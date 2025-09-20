<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Tambah Customer</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="container">
        <h1>Tambah Customer Baru</h1>
        
        <nav>
            <a href="dashboard">Dashboard</a> |
            <a href="customer?action=new">Tambah Customer</a> |
            <a href="item?action=new">Tambah Item</a> |
            <a href="transaction?action=new">Tambah Transaksi</a>
        </nav>
        
        <form action="customer" method="post">
            <input type="hidden" name="action" value="insert">
            
            <div class="form-group">
                <label for="name">Nama:</label>
                <input type="text" id="name" name="name" required>
            </div>
            
            <div class="form-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email">
            </div>
            
            <div class="form-group">
                <label for="phone">Telepon:</label>
                <input type="tel" id="phone" name="phone">
            </div>
            
            <button type="submit">Simpan</button>
            <a href="customer" class="button">Batal</a>
        </form>
    </div>
</body>
</html>