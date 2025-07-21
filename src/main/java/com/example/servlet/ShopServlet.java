package com.example.servlet;

import com.example.dao.CategoryDAO;
import com.example.entity.Category;
import com.example.entity.UserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class ShopServlet extends HttpServlet {
    
    private CategoryDAO categoryDAO = new CategoryDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        UserDetails user = (UserDetails) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Initialize categories if they don't exist
        List<Category> categories = categoryDAO.findAll();
        if (categories.isEmpty()) {
            initializeData();
            categories = categoryDAO.findAll();
        }
        
        request.setAttribute("categories", categories);
        request.getRequestDispatcher("/jsp/shop.jsp").forward(request, response);
    }
    
    private void initializeData() {
        try {
            // Create categories with sample data
            Category fruits = new Category("Fruits", "images/categories/fruits.jpg");
            Category vegetables = new Category("Vegetables", "images/categories/vegetables.jpg");
            Category riceCereals = new Category("Rice & Cereals", "images/categories/rice-cereals.jpg");
            Category oilsGhee = new Category("Oils & Ghee", "images/categories/oils-ghee.jpg");
            
            categoryDAO.save(fruits);
            categoryDAO.save(vegetables);
            categoryDAO.save(riceCereals);
            categoryDAO.save(oilsGhee);
            
            // Items will be added when categories are viewed
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}