<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Daftar Items</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="container">
        <h1>Daftar Items</h1>
        
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
                    <th>Nama Barang</th>
                    <th>Harga Barang</th>
                    <th>Kategori Barang</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="item" items="${items}">
                    <tr>
                        <td>${item.id}</td>
                        <td>${item.name}</td>
                        <td>${item.price}</td>
                        <td>${item.category}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        
        <a href="item?action=new" class="button">Tambah Items Baru</a>
    </div>
</body>
</html>