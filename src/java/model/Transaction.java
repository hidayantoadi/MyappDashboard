package model;

import java.util.Date;
import java.util.List;

public class Transaction {
    private int id;
    private int customerId;
    private String customerName;
    private Date transactionDate;
    private String type; // SALE or PURCHASE
    private double totalAmount;
    private List<TransactionDetail> details;

    public Transaction() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public Date getTransactionDate() { return transactionDate; }
    public void setTransactionDate(Date transactionDate) { this.transactionDate = transactionDate; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public List<TransactionDetail> getDetails() { return details; }
    public void setDetails(List<TransactionDetail> details) { this.details = details; }
}