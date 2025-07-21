<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.example.entity.UserDetails" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SpringGrocery - Organic Store</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <!-- Navigation -->
    <nav class="navbar">
        <div class="nav-container">
            <div class="nav-logo">
                <img src="<%= request.getContextPath() %>/images/logo.png" alt="SpringGrocery Logo" class="logo">
                <h1>SpringGrocery</h1>
            </div>
            <div class="nav-auth">
                <% 
                UserDetails user = (UserDetails) session.getAttribute("user");
                if (user != null) { 
                %>
                    <span class="welcome-msg">Welcome, <%= user.getName() %>!</span>
                    <a href="<%= request.getContextPath() %>/shop" class="btn btn-primary">Shop Now</a>
                    <a href="<%= request.getContextPath() %>/cart" class="btn btn-secondary">
                        Cart
                        <% 
                        java.util.List cartList = (java.util.List) session.getAttribute("cart");
                        if (cartList != null && !cartList.isEmpty()) { 
                        %>
                            <span class="cart-count"><%= cartList.size() %></span>
                        <% } %>
                    </a>
                    <a href="<%= request.getContextPath() %>/order-history" class="btn btn-secondary">Orders</a>
                    <a href="<%= request.getContextPath() %>/logout" class="btn btn-logout">Logout</a>
                <% } else { %>
                    <a href="<%= request.getContextPath() %>/login" class="btn btn-primary">Login</a>
                    <a href="<%= request.getContextPath() %>/register" class="btn btn-secondary">Register</a>
                <% } %>
            </div>
        </div>
    </nav>

    <!-- Hero Section -->
    <section class="hero">
        <div class="hero-content">
            <h2>Fresh Organic Groceries Delivered to Your Doorstep</h2>
            <p class="hero-subtitle">Premium quality fruits, vegetables, and daily essentials from nature's best</p>
            <div class="hero-features">
                <div class="feature">
                    <h3>🌱 100% Organic</h3>
                    <p>Certified organic products from trusted farmers</p>
                </div>
                <div class="feature">
                    <h3>🚚 Free Delivery</h3>
                    <p>Free home delivery on orders above Rs.500</p>
                </div>
                <div class="feature">
                    <h3>⚡ Same Day Delivery</h3>
                    <p>Order today, get it delivered today!</p>
                </div>
            </div>
            <div class="hero-cta">
                <% if (user != null) { %>
                    <a href="<%= request.getContextPath() %>/shop" class="btn btn-large btn-primary">Start Shopping</a>
                <% } else { %>
                    <a href="<%= request.getContextPath() %>/register" class="btn btn-large btn-primary">Get Started</a>
                <% } %>
            </div>
        </div>
    </section>

    <!-- Service Areas -->
    <section class="service-areas">
        <div class="container">
            <h2>We Deliver To</h2>
            <div class="areas-grid">
                <div class="area-card">
                    <h3>Chennai</h3>
                    <p>All areas within Chennai city limits</p>
                </div>
                <div class="area-card">
                    <h3>Chengalpattu</h3>
                    <p>Complete Chengalpattu district coverage</p>
                </div>
                <div class="area-card">
                    <h3>Kancheepuram</h3>
                    <p>All major areas in Kancheepuram</p>
                </div>
            </div>
        </div>
    </section>

    <!-- Footer -->
    <footer class="footer">
        <div class="footer-content">
            <div class="footer-section">
                <h3>Contact Info</h3>
                <p>📧 Email: support@springgrocery.com</p>
                <p>📞 Phone: +91 9876543210</p>
                <p>📍 Address: 123 Organic Street, Chennai, Tamil Nadu 600001</p>
            </div>
            <div class="footer-section">
                <h3>Quick Links</h3>
                <ul>
                    <li><a href="<%= request.getContextPath() %>/home">Home</a></li>
                    <li><a href="<%= request.getContextPath() %>/shop">Shop</a></li>
                    <li><a href="<%= request.getContextPath() %>/login">Login</a></li>
                    <li><a href="<%= request.getContextPath() %>/register">Register</a></li>
                </ul>
            </div>
        </div>
        <div class="footer-bottom">
            <p>&copy; 2025 SpringGrocery. All rights reserved.</p>
        </div>
    </footer>

    <script src="<%= request.getContextPath() %>/js/script.js"></script>
</body>
</html>