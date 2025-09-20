<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Tambah Items</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="container">
        <h1>Tambah Items Baru</h1>
        
        <nav>
            <a href="dashboard">Dashboard</a> |
            <a href="customer?action=new">Tambah Customer</a> |
            <a href="item?action=new">Tambah Item</a> |
            <a href="transaction?action=new">Tambah Transaksi</a>
        </nav>
        
        <form action="item" method="post">
            <input type="hidden" name="action" value="insert">
            
            <div class="form-group">
                <label for="name">Nama Barang:</label>
                <input type="text" id="name" name="name" required>
            </div>
            
            <div class="form-group">
                <label for="number">Harga Barang Rp :</label>
                <input type="number" id="price" name="price">
            </div>
            
            <div class="form-group">
                <label for="name">Kategori Barang:</label>
                <input type="text" id="category" name="category" required>
            </div>            
            
            <button type="submit">Simpan</button>
            <a href="item" class="button">Batal</a>
        </form>
    </div>
</body>
</html>