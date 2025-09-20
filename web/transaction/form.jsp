<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Tambah Transaksi</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="container">
        <h1>Tambah Transaksi</h1>
        
        <nav>
            <a href="dashboard">Dashboard</a> |
            <a href="customer?action=new">Tambah Customer</a> |
            <a href="item?action=new">Tambah Item</a> |
            <a href="transaction?action=new">Tambah Transaksi</a>
        </nav>
        
        <form action="transaction" method="post">
            <input type="hidden" name="action" value="insert">
            
            <div class="form-group">
                <label for="customer_id">Customer:</label>
                <select id="customer_id" name="customer_id" required>
                    <option value="">Pilih Customer</option>
                    <c:forEach var="customer" items="${customers}">
                        <option value="${customer.id}">${customer.name}</option>
                    </c:forEach>
                </select>
            </div>
            
            <div class="form-group">
                <label for="type">Jenis Transaksi:</label>
                <select id="type" name="type" required>
                    <option value="SALE">Penjualan</option>
                    <option value="PURCHASE">Pembelian</option>
                </select>
            </div>
            
            <h3>Item Transaksi</h3>
            <table id="items-table">
                <thead>
                    <tr>
                        <th>Item</th>
                        <th>Quantity</th>
                        <th>Harga</th>
                        <th>Aksi</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>
                            <select name="item_id" required>
                                <option value="">Pilih Item</option>
                                <c:forEach var="item" items="${items}">
                                    <option value="${item.id}" data-price="${item.price}">${item.name} (Rp ${item.price})</option>
                                </c:forEach>
                            </select>
                        </td>
                        <td><input type="number" name="quantity" min="1" value="1" required></td>
                        <td><input type="number" name="price" step="0.01" required></td>
                        <td><button type="button" onclick="addRow()">+</button></td>
                    </tr>
                </tbody>
            </table>
            
            <button type="submit">Simpan Transaksi</button>
            <a href="transaction" class="button">Batal</a>
        </form>
    </div>

    <script>
        function addRow() {
            const tbody = document.querySelector('#items-table tbody');
            const newRow = tbody.rows[0].cloneNode(true);
            
            // Reset values
            newRow.querySelector('select').selectedIndex = 0;
            newRow.querySelector('input[name="quantity"]').value = 1;
            newRow.querySelector('input[name="price"]').value = '';
            
            tbody.appendChild(newRow);
        }
        
        // Auto-fill price when item is selected
        document.addEventListener('change', function(e) {
            if (e.target.name === 'item_id') {
                const selectedOption = e.target.options[e.target.selectedIndex];
                const price = selectedOption.getAttribute('data-price');
                if (price) {
                    const row = e.target.closest('tr');
                    row.querySelector('input[name="price"]').value = price;
                }
            }
        });
    </script>
</body>
</html>