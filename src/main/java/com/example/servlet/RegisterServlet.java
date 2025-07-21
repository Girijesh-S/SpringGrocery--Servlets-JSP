package com.example.servlet;

import com.example.dao.UserDAO;
import com.example.entity.UserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RegisterServlet extends HttpServlet {
    
    private UserDAO userDAO = new UserDAO();
    private static final List<String> ALLOWED_CITIES = Arrays.asList("Chennai", "Chengalpattu", "Kancheepuram");
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setAttribute("cities", ALLOWED_CITIES);
        request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String name = request.getParameter("name");
        String phone = request.getParameter("phone");
        String altPhone = request.getParameter("altPhone");
        String pincode = request.getParameter("pincode");
        String city = request.getParameter("city");
        String addressLine1 = request.getParameter("addressLine1");
        String addressLine2 = request.getParameter("addressLine2");
        String deliveryDate = request.getParameter("deliveryDate");
        
        // Validation
        StringBuilder errors = new StringBuilder();
        
        if (name == null || name.trim().isEmpty()) {
            errors.append("Name is required. ");
        }
        if (phone == null || !phone.matches("\\d{10}")) {
            errors.append("Valid 10-digit phone number is required. ");
        }
        if (altPhone == null || !altPhone.matches("\\d{10}")) {
            errors.append("Valid 10-digit alternative phone number is required. ");
        }
        if (pincode == null || !pincode.matches("\\d{6}")) {
            errors.append("Valid 6-digit pincode is required. ");
        }
        if (city == null || !ALLOWED_CITIES.contains(city)) {
            errors.append("Please select a valid city. ");
        }
        if (addressLine1 == null || addressLine1.trim().isEmpty()) {
            errors.append("Address Line 1 is required. ");
        }
        
        if (errors.length() > 0) {
            request.setAttribute("error", errors.toString());
            request.setAttribute("cities", ALLOWED_CITIES);
            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
            return;
        }
        
        // Check if phone already exists
        if (userDAO.findByPhone(phone) != null) {
            request.setAttribute("error", "Phone number already registered. Please login instead.");
            request.setAttribute("cities", ALLOWED_CITIES);
            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
            return;
        }
        
        try {
            // Create new user
            UserDetails user = new UserDetails();
            user.setName(name.trim());
            user.setPhone(phone);
            user.setAltPhone(altPhone);
            user.setPincode(pincode);
            user.setCity(city);
            user.setState("TamilNadu");
            user.setAddressLine1(addressLine1.trim());
            user.setAddressLine2(addressLine2 != null ? addressLine2.trim() : "");
            user.setDeliveryDate(deliveryDate != null ? deliveryDate : "Today");
            
            userDAO.save(user);
            
            // Auto login
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            
            response.sendRedirect(request.getContextPath() + "/shop");
            
        } catch (Exception e) {
            request.setAttribute("error", "Registration failed. Please try again.");
            request.setAttribute("cities", ALLOWED_CITIES);
            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
        }
    }
}