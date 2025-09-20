package dao;

import model.Item;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemDAO {
    
    public List<Item> getAllItems() throws SQLException {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items ORDER BY name";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Item item = new Item();
                item.setId(rs.getInt("id"));
                item.setName(rs.getString("name"));
                item.setPrice(rs.getDouble("price"));
                item.setCategory(rs.getString("category"));
                items.add(item);
            }
        }
        return items;
    }
    
    public void addItem(Item item) throws SQLException {
        String sql = "INSERT INTO items (name, price, category) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, item.getName());
            pstmt.setDouble(2, item.getPrice());
            pstmt.setString(3, item.getCategory());
            pstmt.executeUpdate();
        }
    }
    
    public List<Item> getTopItems() throws SQLException {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT i.id, i.name, i.price, " +
                    "COALESCE(SUM(td.quantity), 0) as total_sold, " +
                    "COALESCE(SUM(td.quantity * td.price), 0) as total_revenue " +
                    "FROM items i " +
                    "LEFT JOIN transaction_details td ON i.id = td.item_id " +
                    "LEFT JOIN transactions t ON td.transaction_id = t.id AND t.type = 'SALE' " +
                    "GROUP BY i.id, i.name, i.price " +
                    "ORDER BY total_sold DESC LIMIT 10";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Item item = new Item();
                item.setId(rs.getInt("id"));
                item.setName(rs.getString("name"));
                item.setPrice(rs.getDouble("price"));
                item.setTotalSold(rs.getInt("total_sold"));
                item.setTotalRevenue(rs.getDouble("total_revenue"));
                items.add(item);
            }
        }
        return items;
    }
    
    public Item getItemById(int id) throws SQLException {
        String sql = "SELECT * FROM items WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Item item = new Item();
                    item.setId(rs.getInt("id"));
                    item.setName(rs.getString("name"));
                    item.setPrice(rs.getDouble("price"));
                    return item;
                }
            }
        }
        return null;
    }
    
    ///


    public List<Map<String, Object>> getTopItemsWithRevenue() throws SQLException {
        String sql = "SELECT i.name, SUM(td.quantity) AS total_sold, SUM(td.quantity * td.price) AS total_revenue " +
                     "FROM items i " +
                     "JOIN transaction_details td ON i.id = td.item_id " +
                     "JOIN transactions t ON td.transaction_id = t.id AND t.type = 'SALE' " +
                     "GROUP BY i.name ORDER BY total_sold DESC LIMIT 10";
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", rs.getString("name"));
                map.put("total_sold", rs.getInt("total_sold"));
                map.put("total_revenue", rs.getDouble("total_revenue"));
                list.add(map);
            }
        }
        return list;
    }

    public List<Map<String, Object>> getCustomerCountPerCategory() throws SQLException {
        // Pastikan tabel items memiliki kolom 'category'
        String sql = "SELECT i.category, COUNT(DISTINCT t.customer_id) AS customer_count " +
                     "FROM items i " +
                     "JOIN transaction_details td ON i.id = td.item_id " +
                     "JOIN transactions t ON td.transaction_id = t.id AND t.type = 'SALE' " +
                     "GROUP BY i.category";
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("category", rs.getString("category"));
                map.put("customer_count", rs.getInt("customer_count"));
                list.add(map);
            }
        }
        return list;
    }

}