package dao;

import model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerDAO {
    
    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY name";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setId(rs.getInt("id"));
                customer.setName(rs.getString("name"));
                customer.setEmail(rs.getString("email"));
                customer.setPhone(rs.getString("phone"));
                customers.add(customer);
            }
        }
        return customers;
    }
    
    public void addCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (name, email, phone) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, customer.getPhone());
            pstmt.executeUpdate();
        }
    }
    
    public List<Customer> getTopCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT c.id, c.name, c.email, c.phone, " +
                    "COUNT(t.id) as transaction_count, " +
                    "COALESCE(SUM(td.quantity * td.price), 0) as total_spent " +
                    "FROM customers c " +
                    "LEFT JOIN transactions t ON c.id = t.customer_id AND t.type = 'SALE' " +
                    "LEFT JOIN transaction_details td ON t.id = td.transaction_id " +
                    "GROUP BY c.id, c.name, c.email, c.phone " +
                    "ORDER BY total_spent DESC LIMIT 10";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setId(rs.getInt("id"));
                customer.setName(rs.getString("name"));
                customer.setEmail(rs.getString("email"));
                customer.setPhone(rs.getString("phone"));
                customer.setTransactionCount(rs.getInt("transaction_count"));
                customer.setTotalSpent(rs.getDouble("total_spent"));
                customers.add(customer);
            }
        }
        return customers;
    }
    
    public Customer getCustomerById(int id) throws SQLException {
        String sql = "SELECT * FROM customers WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer();
                    customer.setId(rs.getInt("id"));
                    customer.setName(rs.getString("name"));
                    customer.setEmail(rs.getString("email"));
                    customer.setPhone(rs.getString("phone"));
                    return customer;
                }
            }
        }
        return null;
    }
    ///

    public List<Map<String, Object>> getTopCustomersWithSpending() throws SQLException {
        String sql = "SELECT c.name, COUNT(t.id) AS transaction_count, SUM(td.quantity * td.price) AS total_spent " +
                     "FROM customers c " +
                     "JOIN transactions t ON c.id = t.customer_id AND t.type = 'SALE' " +
                     "JOIN transaction_details td ON t.id = td.transaction_id " +
                     "GROUP BY c.name ORDER BY total_spent DESC LIMIT 10";
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", rs.getString("name"));
                map.put("transaction_count", rs.getInt("transaction_count"));
                map.put("total_spent", rs.getDouble("total_spent"));
                list.add(map);
            }
        }
        return list;
    }
    
}