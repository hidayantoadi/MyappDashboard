package controller;

import dao.ItemDAO;
import model.Item;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "ItemServlet", value = "/item")
public class ItemServlet extends HttpServlet {
    private ItemDAO itemDAO;
    
    @Override
    public void init() {
        itemDAO = new ItemDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        try {
            if ("new".equals(action)) {
                showNewForm(request, response);
            } else {
                listItems(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Cannot retrieve items", e);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        try {
            if ("insert".equals(action)) {
                insertItem(request, response);
            } else {
                listItems(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Cannot process item", e);
        }
    }
    
    private void listItems(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        List<Item> items = itemDAO.getAllItems();
        request.setAttribute("items", items);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/item/list.jsp");
        dispatcher.forward(request, response);
    }
    
    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/item/form.jsp");
        dispatcher.forward(request, response);
    }
    
    private void insertItem(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        String name = request.getParameter("name");
        double price = Double.parseDouble(request.getParameter("price"));
        String category = request.getParameter("category");
        Item newItem = new Item();
        newItem.setName(name);
        newItem.setPrice(price);
        newItem.setCategory(category);
        
        itemDAO.addItem(newItem);
        response.sendRedirect("item");
    }
}