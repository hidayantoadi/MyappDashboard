package dao;

import model.Transaction;
import model.TransactionDetail;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionDAO {

    public void saveTransactionWithDetails(Transaction transaction, String[] itemIds,
            String[] quantities, String[] prices) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Insert transaction
            String transSql = "INSERT INTO transactions (customer_id, type) VALUES (?, ?)";
            int transactionId;

            try ( PreparedStatement transStmt = conn.prepareStatement(transSql, Statement.RETURN_GENERATED_KEYS)) {
                transStmt.setInt(1, transaction.getCustomerId());
                transStmt.setString(2, transaction.getType());
                transStmt.executeUpdate();

                try ( ResultSet generatedKeys = transStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        transactionId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Creating transaction failed, no ID obtained.");
                    }
                }
            }

            // Insert transaction details
            String detailSql = "INSERT INTO transaction_details (transaction_id, item_id, quantity, price) VALUES (?, ?, ?, ?)";
            try ( PreparedStatement detailStmt = conn.prepareStatement(detailSql)) {
                double totalAmount = 0;

                for (int i = 0; i < itemIds.length; i++) {
                    if (itemIds[i] != null && !itemIds[i].isEmpty()
                            && quantities[i] != null && !quantities[i].isEmpty()
                            && prices[i] != null && !prices[i].isEmpty()) {

                        int itemId = Integer.parseInt(itemIds[i]);
                        int quantity = Integer.parseInt(quantities[i]);
                        double price = Double.parseDouble(prices[i]);
                        double subtotal = quantity * price;
                        totalAmount += subtotal;

                        detailStmt.setInt(1, transactionId);
                        detailStmt.setInt(2, itemId);
                        detailStmt.setInt(3, quantity);
                        detailStmt.setDouble(4, price);
                        detailStmt.addBatch();
                    }
                }
                detailStmt.executeBatch();

                // Update total amount in transaction
                String updateSql = "UPDATE transactions SET total_amount = ? WHERE id = ?";
                try ( PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setDouble(1, totalAmount);
                    updateStmt.setInt(2, transactionId);
                    updateStmt.executeUpdate();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
            }
        }
    }

    public List<Transaction> getAllTransactions() throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, c.name as customer_name "
                + "FROM transactions t "
                + "JOIN customers c ON t.customer_id = c.id "
                + "ORDER BY t.transaction_date DESC";

        try ( Connection conn = DBConnection.getConnection();  Statement stmt = conn.createStatement();  ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setId(rs.getInt("id"));
                transaction.setCustomerId(rs.getInt("customer_id"));
                transaction.setCustomerName(rs.getString("customer_name"));
                transaction.setTransactionDate(rs.getTimestamp("transaction_date"));
                transaction.setType(rs.getString("type"));
                transaction.setTotalAmount(rs.getDouble("total_amount"));

                // Get transaction details
                transaction.setDetails(getTransactionDetails(transaction.getId()));
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    public List<TransactionDetail> getTransactionDetails(int transactionId) throws SQLException {
        List<TransactionDetail> details = new ArrayList<>();
        String sql = "SELECT td.*, i.name as item_name "
                + "FROM transaction_details td "
                + "JOIN items i ON td.item_id = i.id "
                + "WHERE td.transaction_id = ?";

        try ( Connection conn = DBConnection.getConnection();  PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, transactionId);
            try ( ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    TransactionDetail detail = new TransactionDetail();
                    detail.setId(rs.getInt("id"));
                    detail.setTransactionId(rs.getInt("transaction_id"));
                    detail.setItemId(rs.getInt("item_id"));
                    detail.setItemName(rs.getString("item_name"));
                    detail.setQuantity(rs.getInt("quantity"));
                    detail.setPrice(rs.getDouble("price"));
                    detail.setSubtotal(rs.getDouble("quantity") * rs.getDouble("price"));
                    details.add(detail);
                }
            }
        }
        return details;
    }

    public List<Object[]> getSalesDataByMonth() throws SQLException {
        List<Object[]> data = new ArrayList<>();
        String sql = "SELECT TO_CHAR(transaction_date, 'YYYY-MM') as month, "
                + "SUM(CASE WHEN type = 'SALE' THEN total_amount ELSE 0 END) as sales, "
                + "SUM(CASE WHEN type = 'PURCHASE' THEN total_amount ELSE 0 END) as purchases "
                + "FROM transactions "
                + "WHERE transaction_date >= CURRENT_DATE - INTERVAL '12 months' "
                + "GROUP BY TO_CHAR(transaction_date, 'YYYY-MM') "
                + "ORDER BY month";

        try ( Connection conn = DBConnection.getConnection();  Statement stmt = conn.createStatement();  ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Object[] row = new Object[3];
                row[0] = rs.getString("month");
                row[1] = rs.getDouble("sales");
                row[2] = rs.getDouble("purchases");
                data.add(row);
            }
        }
        return data;
    }

    ///
    public double getTotalSales() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_amount),0) FROM transactions WHERE type = 'SALE'";
        try ( Connection conn = DBConnection.getConnection();  Statement stmt = conn.createStatement();  ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0;
        }
    }

    public double getTotalPurchases() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_amount),0) FROM transactions WHERE type = 'PURCHASE'";
        try ( Connection conn = DBConnection.getConnection();  Statement stmt = conn.createStatement();  ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0;
        }
    }

    public int getActiveCustomerCount() throws SQLException {
        String sql = "SELECT COUNT(DISTINCT customer_id) FROM transactions WHERE type = 'SALE'";
        try ( Connection conn = DBConnection.getConnection();  Statement stmt = conn.createStatement();  ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    public String getTopItemName() throws SQLException {
        String sql = "SELECT i.name FROM items i "
                + "JOIN transaction_details td ON i.id = td.item_id "
                + "JOIN transactions t ON td.transaction_id = t.id AND t.type = 'SALE' "
                + "GROUP BY i.name ORDER BY SUM(td.quantity) DESC LIMIT 1";
        try ( Connection conn = DBConnection.getConnection();  Statement stmt = conn.createStatement();  ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getString(1);
            }
            return "N/A";
        }
    }

    public List<Map<String, Object>> getMonthlySalesPurchases(int year, int month) throws SQLException {
        String sql = "SELECT TO_CHAR(transaction_date, 'YYYY-MM') AS month, "
                + "SUM(CASE WHEN type = 'SALE' THEN total_amount ELSE 0 END) AS sales, "
                + "SUM(CASE WHEN type = 'PURCHASE' THEN total_amount ELSE 0 END) AS purchases "
                + "FROM transactions "
                + "WHERE EXTRACT(YEAR FROM transaction_date) = ? "
                + "AND EXTRACT(MONTH FROM transaction_date) = ? "
                + "GROUP BY month ORDER BY month";
        List<Map<String, Object>> list = new ArrayList<>();
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, month);
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("month", rs.getString("month"));
                    map.put("sales", rs.getDouble("sales"));
                    map.put("purchases", rs.getDouble("purchases"));
                    list.add(map);
                }
            }
        }
        return list;
    }

    public List<Map<String, Object>> getDailyTransactionCounts(int year, int month) throws SQLException {
        String sql = "SELECT EXTRACT(DAY FROM transaction_date) AS day, COUNT(*) AS transaction_count "
                + "FROM transactions "
                + "WHERE EXTRACT(YEAR FROM transaction_date) = ? "
                + "AND EXTRACT(MONTH FROM transaction_date) = ? "
                + "GROUP BY day ORDER BY day";
        List<Map<String, Object>> list = new ArrayList<>();
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, month);
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("day", rs.getInt("day"));
                    map.put("transaction_count", rs.getInt("transaction_count"));
                    list.add(map);
                }
            }
        }
        return list;
    }
}
