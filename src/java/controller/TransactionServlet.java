package controller;

import dao.CustomerDAO;
import dao.ItemDAO;
import dao.TransactionDAO;
import model.Transaction;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "TransactionServlet", value = "/transaction")
public class TransactionServlet extends HttpServlet {
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
        String action = request.getParameter("action");
        
        try {
            if ("new".equals(action)) {
                showNewForm(request, response);
            } else {
                listTransactions(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Cannot retrieve data", e);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        try {
            if ("insert".equals(action)) {
                insertTransaction(request, response);
            } else {
                listTransactions(request, response);
            }
        } catch (Exception e) {
            throw new ServletException("Cannot process transaction", e);
        }
    }
    
    private void listTransactions(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        List<Transaction> transactions = transactionDAO.getAllTransactions();
        request.setAttribute("transactions", transactions);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/transaction/list.jsp");
        dispatcher.forward(request, response);
    }
    
    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        List customers = customerDAO.getAllCustomers();
        List items = itemDAO.getAllItems();
        
        request.setAttribute("customers", customers);
        request.setAttribute("items", items);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/transaction/form.jsp");
        dispatcher.forward(request, response);
    }
    
    private void insertTransaction(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        int customerId = Integer.parseInt(request.getParameter("customer_id"));
        String type = request.getParameter("type");
        String[] itemIds = request.getParameterValues("item_id");
        String[] quantities = request.getParameterValues("quantity");
        String[] prices = request.getParameterValues("price");
        
        Transaction transaction = new Transaction();
        transaction.setCustomerId(customerId);
        transaction.setType(type);
        
        transactionDAO.saveTransactionWithDetails(transaction, itemIds, quantities, prices);
        response.sendRedirect("transaction");
    }
}