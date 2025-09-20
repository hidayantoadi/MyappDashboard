package controller;

import dao.CustomerDAO;
import dao.ItemDAO;
import dao.TransactionDAO;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@WebServlet(name = "DashboardServlet", value = "/dashboard")
public class DashboardServlet extends HttpServlet {

    private CustomerDAO customerDAO;
    private ItemDAO itemDAO;
    private TransactionDAO transactionDAO;

    @Override
    public void init() {
        customerDAO = new CustomerDAO();
        itemDAO = new ItemDAO();
        transactionDAO = new TransactionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {

            String monthParam = request.getParameter("month");
            String yearParam = request.getParameter("year");

            int month = (monthParam != null && !monthParam.isEmpty()) ? Integer.parseInt(monthParam) : LocalDate.now().getMonthValue();
            int year = (yearParam != null && !yearParam.isEmpty()) ? Integer.parseInt(yearParam) : LocalDate.now().getYear();

// Kirim ke DAO untuk query dengan filter bulan & tahun
            
            

            // KPI
            double totalSales = transactionDAO.getTotalSales();
            double totalPurchases = transactionDAO.getTotalPurchases();
            int activeCustomers = transactionDAO.getActiveCustomerCount();
            String topItem = transactionDAO.getTopItemName();

            // Tren bulanan
            List<Map<String, Object>> monthlyTrends = transactionDAO.getMonthlySalesPurchases(year, month);

            // Top 10 customer
            List<Map<String, Object>> topCustomers = customerDAO.getTopCustomersWithSpending();

            // Top 10 item
            List<Map<String, Object>> topItems = itemDAO.getTopItemsWithRevenue();

            // Customer per kategori
            List<Map<String, Object>> customerPerCategory = itemDAO.getCustomerCountPerCategory();

            // Heatmap transaksi harian
            List<Map<String, Object>> dailyTransactions = transactionDAO.getDailyTransactionCounts(year, month);

            // Set attribute untuk JSP
            request.setAttribute("selectedMonth", month);
            request.setAttribute("selectedYear", year);         
            request.setAttribute("totalSales", totalSales);
            request.setAttribute("totalPurchases", totalPurchases);
            request.setAttribute("activeCustomers", activeCustomers);
            request.setAttribute("topItem", topItem);
            request.setAttribute("monthlyTrends", monthlyTrends);
            request.setAttribute("topCustomers", topCustomers);
            request.setAttribute("topItems", topItems);
            request.setAttribute("customerPerCategory", customerPerCategory);
            request.setAttribute("dailyTransactions", dailyTransactions);

            request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
