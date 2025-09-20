<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Daftar Customer</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="container">
        <h1>Daftar Customer</h1>
        
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
                    <th>Nama</th>
                    <th>Email</th>
                    <th>Telepon</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="customer" items="${customers}">
                    <tr>
                        <td>${customer.id}</td>
                        <td>${customer.name}</td>
                        <td>${customer.email}</td>
                        <td>${customer.phone}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        
        <a href="customer?action=new" class="button">Tambah Customer Baru</a>
    </div>
</body>
</html>