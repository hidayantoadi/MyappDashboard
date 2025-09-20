<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
    <head>
        <title>Dashboard Analisis Penjualan</title>
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
        <link rel="stylesheet" href="css/style.css">
        <style>
            /* Style sederhana dan responsif */
            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                background: #f0f4f8;
                margin: 0;
                padding: 20px;
                color: #333;
            }
            .container {
                max-width: 1100px;
                margin: 0 auto;
                background: #fff;
                padding: 30px 40px;
                border-radius: 12px;
                box-shadow: 0 8px 20px rgba(0,0,0,0.1);
            }
            h1, h2, h3 {
                color: #222;
                margin-bottom: 20px;
                font-weight: 700;
            }
            .dashboard-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit,minmax(350px,1fr));
                gap: 30px;
                margin-top: 20px;
            }
            .card {
                background: white;
                padding: 20px;
                border-radius: 12px;
                box-shadow: 0 6px 15px rgba(0,0,0,0.1);
            }
            .kpi {
                font-size: 1.5rem;
                font-weight: 700;
                color: #007bff;
            }
            table {
                width: 100%;
                border-collapse: collapse;
                margin-top: 10px;
            }
            th, td {
                padding: 8px 12px;
                border-bottom: 1px solid #ddd;
                text-align: left;
            }
            th {
                background: #007bff;
                color: white;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <h1>Dashboard Analisis Penjualan & Pembelian</h1>
            <nav>

                <a href="dashboard">Dashboard</a> |

                <a href="customer?action=new">Tambah Customer</a> |

                <a href="item?action=new">Tambah Item</a> |

                <a href="transaction?action=new">Tambah Transaksi</a> |

                <a href="customer">Daftar Customer</a> |

                <a href="item">Daftar Item</a> |

                <a href="transaction">Daftar Transaksi</a>

            </nav>
            <!-- KPI -->
            <div class="dashboard-grid">
                <div class="card">
                    <div>Total Penjualan</div>
                    <div class="kpi">Rp ${totalSales}</div>
                </div>
                <div class="card">
                    <div>Total Pembelian</div>
                    <div class="kpi">Rp ${totalPurchases}</div>
                </div>
                <div class="card">
                    <div>Customer Aktif</div>
                    <div class="kpi">${activeCustomers}</div>
                </div>
                <div class="card">
                    <div>Item Terlaris</div>
                    <div class="kpi">${topItem}</div>
                </div>
            </div>

            <!-- Form Filter -->
            <form id="filterForm" method="get" action="dashboard" style="margin-bottom: 20px;">
                <div class="form-group">
                    <label for="filterMonth">Bulan:</label>
                    <select id="filterMonth" name="month" required>
                        <option value="">--Pilih Bulan--</option>
                        <c:forEach var="m" begin="1" end="12">
                            <option value="${m}" ${selectedMonth == m ? 'selected' : ''}>${m}</option>
                        </c:forEach>
                    </select>

                    <label for="filterYear">Tahun:</label>
                    <%
                        java.util.Calendar cal = java.util.Calendar.getInstance();
                        int currentYear = cal.get(java.util.Calendar.YEAR);
                    %>

                    <select id="filterYear" name="year" required>
                        <option value="">--Pilih Tahun--</option>
                        <c:forEach var="y" begin="2000" end="<%= currentYear%>">
                            <option value="${y}" ${selectedYear == y ? 'selected' : ''}>${y}</option>
                        </c:forEach>
                    </select>
                </div>
                <button type="submit">Filter</button>
            </form>        

            <!-- Grafik Tren Bulanan -->
            <div class="card" style="margin-top: 40px;">
                <h2>Tren Penjualan & Pembelian Bulanan</h2>
                <canvas id="monthlyTrendChart" height="150"></canvas>
            </div>

            <!-- Top Customer dan Item -->
            <div class="dashboard-grid" style="margin-top: 40px;">
                <div class="card">
                    <h3>Top 10 Customer (Total Belanja)</h3>
                    <table>
                        <thead><tr><th>Nama</th><th>Transaksi</th><th>Total Belanja</th></tr></thead>
                        <tbody>
                            <c:forEach var="c" items="${topCustomers}">
                                <tr>
                                    <td>${c.name}</td>
                                    <td>${c.transaction_count}</td>
                                    <td>Rp ${c.total_spent}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    <canvas id="topCustomersChart" height="200"></canvas>
                </div>

                <div class="card">
                    <h3>Top 10 Item Terlaris</h3>
                    <table>
                        <thead><tr><th>Nama Item</th><th>Jumlah Terjual</th><th>Pendapatan</th></tr></thead>
                        <tbody>
                            <c:forEach var="i" items="${topItems}">
                                <tr>
                                    <td>${i.name}</td>
                                    <td>${i.total_sold}</td>
                                    <td>Rp ${i.total_revenue}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    <canvas id="topItemsChart" height="200"></canvas>
                </div>
            </div>

            <!-- Customer per Kategori -->
            <div class="card" style="margin-top: 40px;">
                <h3>Distribusi Customer per Kategori Item</h3>
                <canvas id="customerCategoryChart" height="150"></canvas>
            </div>

            <!-- Heatmap Transaksi Harian -->
            <div class="card" style="margin-top: 40px;">
                <h3>Heatmap Transaksi Harian Bulan Ini</h3>
                <canvas id="dailyHeatmapChart" height="100"></canvas>
            </div>
        </div>

        <script>
            // Data dari JSP ke JS
            const monthlyData = [
            <c:forEach var="row" items="${monthlyTrends}">
                {month: '${row.month}', sales: ${row.sales}, purchases: ${row.purchases}},
            </c:forEach>
            ];

            const topCustomers = [
            <c:forEach var="c" items="${topCustomers}">
                {name: '${c.name}', totalSpent: ${c.total_spent}},
            </c:forEach>
            ];

            const topItems = [
            <c:forEach var="i" items="${topItems}">
                {name: '${i.name}', sold: ${i.total_sold}},
            </c:forEach>
            ];

            const customerCategories = [
            <c:forEach var="cat" items="${customerPerCategory}">
                {category: '${cat.category}', count: ${cat.customer_count}},
            </c:forEach>
            ];

            const dailyTransactions = [
            <c:forEach var="d" items="${dailyTransactions}">
                {day: ${d.day}, count: ${d.transaction_count}},
            </c:forEach>
            ];

            // Chart Tren Bulanan
            const ctxMonthly = document.getElementById('monthlyTrendChart').getContext('2d');
            new Chart(ctxMonthly, {
                type: 'line',
                data: {
                    labels: monthlyData.map(d => d.month),
                    datasets: [
                        {
                            label: 'Penjualan',
                            data: monthlyData.map(d => d.sales),
                            borderColor: 'rgba(54, 162, 235, 1)',
                            backgroundColor: 'rgba(54, 162, 235, 0.2)',
                            fill: true,
                            tension: 0.3
                        },
                        {
                            label: 'Pembelian',
                            data: monthlyData.map(d => d.purchases),
                            borderColor: 'rgba(255, 99, 132, 1)',
                            backgroundColor: 'rgba(255, 99, 132, 0.2)',
                            fill: true,
                            tension: 0.3
                        }
                    ]
                },
                options: {responsive: true}
            });

            // Chart Top Customers (Bar Horizontal)
            const ctxTopCustomers = document.getElementById('topCustomersChart').getContext('2d');
            new Chart(ctxTopCustomers, {
                type: 'bar',
                data: {
                    labels: topCustomers.map(c => c.name),
                    datasets: [{
                            label: 'Total Belanja (Rp)',
                            data: topCustomers.map(c => c.totalSpent),
                            backgroundColor: 'rgba(75, 192, 192, 0.7)'
                        }]
                },
                options: {
                    indexAxis: 'y',
                    responsive: true,
                    scales: {x: {beginAtZero: true}}
                }
            });

            // Chart Top Items (Bar Horizontal)
            const ctxTopItems = document.getElementById('topItemsChart').getContext('2d');
            new Chart(ctxTopItems, {
                type: 'bar',
                data: {
                    labels: topItems.map(i => i.name),
                    datasets: [{
                            label: 'Jumlah Terjual',
                            data: topItems.map(i => i.sold),
                            backgroundColor: 'rgba(255, 159, 64, 0.7)'
                        }]
                },
                options: {
                    indexAxis: 'y',
                    responsive: true,
                    scales: {x: {beginAtZero: true}}
                }
            });

            // Pie Chart Customer per Kategori
            const ctxCustomerCat = document.getElementById('customerCategoryChart').getContext('2d');
            new Chart(ctxCustomerCat, {
                type: 'pie',
                data: {
                    labels: customerCategories.map(c => c.category),
                    datasets: [{
                            data: customerCategories.map(c => c.count),
                            backgroundColor: [
                                '#007bff', '#28a745', '#ffc107', '#dc3545', '#6f42c1', '#20c997'
                            ]
                        }]
                },
                options: {responsive: true}
            });

            // Heatmap Transaksi Harian (bar chart dengan opacity)
            const ctxDailyHeatmap = document.getElementById('dailyHeatmapChart').getContext('2d');
            new Chart(ctxDailyHeatmap, {
                type: 'bar',
                data: {
                    labels: dailyTransactions.map(d => d.day),
                    datasets: [{
                            label: 'Jumlah Transaksi',
                            data: dailyTransactions.map(d => d.count),
                            backgroundColor: dailyTransactions.map(d => `rgba(54, 162, 235, ${(d.count / 10) lt 1 ? (d.count / 10) : 1})`)
                        }]
                },
                options: {
                    responsive: true,
                    scales: {
                        y: {beginAtZero: true}
                    },
                    plugins: {
                        legend: {display: false}
                    }
                }
            });
        </script>
    </body>
</html>