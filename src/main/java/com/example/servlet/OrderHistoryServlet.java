package com.example.servlet;

import com.example.dao.OrderDAO;
import com.example.entity.Order;
import com.example.entity.UserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class OrderHistoryServlet extends HttpServlet {
    
    private OrderDAO orderDAO = new OrderDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("=== OrderHistoryServlet.doGet() called ===");
        
        HttpSession session = request.getSession();
        UserDetails user = (UserDetails) session.getAttribute("user");
        
        if (user == null) {
            System.out.println("User is null - redirecting to login");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        System.out.println("Loading order history for user: " + user.getName() + " (ID: " + user.getId() + ")");
        
        try {
            // Get orders for the current user
            List<Order> orders = orderDAO.findByUserId(user.getId());
            System.out.println("Found " + orders.size() + " orders for user " + user.getName());
            
            if (!orders.isEmpty()) {
                System.out.println("Order details:");
                for (Order order : orders) {
                    System.out.println("  - Order ID: " + order.getId());
                    System.out.println("  - Item: " + order.getItemName());
                    System.out.println("  - Quantity: " + order.getQuantity());
                    System.out.println("  - Date: " + order.getOrderDate());
                    System.out.println("  ---");
                }
            } else {
                System.out.println("No orders found for this user");
            }
            
            request.setAttribute("orders", orders);
            request.getRequestDispatcher("/jsp/order-history.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("Error loading order history: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Failed to load order history. Please try again.");
            request.getRequestDispatcher("/jsp/order-history.jsp").forward(request, response);
        }
    }
}
