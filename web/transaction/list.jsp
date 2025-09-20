<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Daftar Transaksi</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="container">
        <h1>Daftar Trannsaksi</h1>
        
        <nav>
            <a href="dashboard">Dashboard</a> |
            <a href="customer?action=new">Tambah Customer</a> |
            <a href="item?action=new">Tambah Item</a> |
            <a href="transaction?action=new">Tambah Transaksi</a>
        </nav>
        
        <table class="data-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Customer ID</th>
                    <th>Customer Name</th>
                    <th>Transaction Date</th>
                    <th>Transaction Type</th>
                    <th>Total Transaction</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="transaction" items="${transactions}">
                    <tr>
                        <td>${transaction.id}</td>
                        <td>${transaction.customerId}</td>
                        <td>${transaction.customerName}</td>
                        <td>${transaction.transactionDate}</td>
                        <td>${transaction.type}</td>
                        <td>${transaction.totalAmount}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        
        <a href="transaction?action=new" class="button">Tambah Transaksi Baru</a>
    </div>
</body>
</html>