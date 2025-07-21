package com.example.servlet;

import com.example.dao.UserDAO;
import com.example.entity.UserDetails;
import com.example.util.CaptchaUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class LoginServlet extends HttpServlet {
    
    private UserDAO userDAO = new UserDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Generate new captcha
        String captcha = CaptchaUtil.generateCaptcha();
        HttpSession session = request.getSession();
        session.setAttribute("captcha", captcha);
        
        request.setAttribute("captcha", captcha);
        request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String phone = request.getParameter("phone");
        String captchaInput = request.getParameter("captcha");
        
        HttpSession session = request.getSession();
        String sessionCaptcha = (String) session.getAttribute("captcha");
        
        // Validate captcha
        if (!captchaInput.equalsIgnoreCase(sessionCaptcha)) {
            request.setAttribute("error", "Invalid captcha. Please try again.");
            doGet(request, response);
            return;
        }
        
        // Validate phone number
        if (phone == null || !phone.matches("\\d{10}")) {
            request.setAttribute("error", "Please enter a valid 10-digit phone number.");
            doGet(request, response);
            return;
        }
        
        // Check if user exists
        UserDetails user = userDAO.findByPhone(phone);
        if (user == null) {
            request.setAttribute("error", "Phone number not registered. Please register first.");
            doGet(request, response);
            return;
        }
        
        // Login successful
        session.setAttribute("user", user);
        session.removeAttribute("captcha");
        response.sendRedirect(request.getContextPath() + "/shop");
    }
}
