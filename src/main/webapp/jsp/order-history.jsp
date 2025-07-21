<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.entity.Order" %>
<%@ page import="com.example.entity.UserDetails" %>

<%
    List<Order> orders = (List<Order>) request.getAttribute("orders");
    String error = (String) request.getAttribute("error");
    UserDetails user = (UserDetails) session.getAttribute("user");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Order History - SpringGrocery</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <!-- Navigation -->
    <nav class="navbar">
        <div class="nav-container">
            <div class="nav-logo">
                <img src="<%= request.getContextPath() %>/images/logo.png" alt="SpringGrocery Logo" class="logo">
                <a href="<%= request.getContextPath() %>/home"><h1>SpringGrocery</h1></a>
            </div>
            <div class="nav-auth">
                <a href="<%= request.getContextPath() %>/shop" class="btn btn-secondary">Shop</a>
                <a href="<%= request.getContextPath() %>/cart" class="btn btn-secondary">Cart</a>
                <a href="<%= request.getContextPath() %>/logout" class="btn btn-logout">Logout</a>
            </div>
        </div>
    </nav>

    <!-- Orders Container -->
    <div class="orders-container">
        <div class="orders-header">
            <h2>Your Order History</h2>
            <% if (user != null) { %>
                <p>Welcome back, <%= user.getName() %>! Here are your recent orders.</p>
            <% } %>
        </div>

        <!-- Debug Information (remove in production) -->
        <div style="background: #f0f0f0; padding: 15px; border-radius: 8px; margin-bottom: 20px; font-family: monospace; font-size: 12px;">
            <strong>Debug Info:</strong><br>
            User: <%= user != null ? user.getName() + " (ID: " + user.getId() + ")" : "Not logged in" %><br>
            Orders List: <%= orders != null ? "Found (" + orders.size() + " items)" : "NULL" %><br>
            Error: <%= error != null ? error : "None" %><br>
            Request Path: <%= request.getRequestURI() %><br>
            Context Path: <%= request.getContextPath() %>
        </div>

        <!-- Error Message -->
        <% if (error != null && !error.isEmpty()) { %>
            <div class="alert alert-error">
                <%= error %>
            </div>
        <% } %>

        <!-- Orders Content -->
        <% if (orders != null && !orders.isEmpty()) { %>
            <div class="orders-list">
                <% for (Order order : orders) { %>
                    <div class="order-item">
                        <div class="order-header">
                            <div class="order-id">Order #<%= order.getId() %></div>
                            <div class="order-date">
                                <%= order.getOrderDate() != null ? order.getOrderDate().toString() : "Date not available" %>
                            </div>
                            <div class="order-status delivered">Delivered</div>
                        </div>
                        
                        <div class="order-details">
                            <div class="order-info">
                                <h4><%= order.getItemName() %></h4>
                                <p class="order-quantity">Quantity: <%= order.getQuantity() %></p>
                            </div>
                            <div class="order-total">
                                <div class="order-amount">₹<%= String.format("%.2f", order.getQuantity() * 90) %></div>
                            </div>
                        </div>
                        
                        <div class="order-actions">
                            <button class="btn-details" onclick="viewOrderDetails('<%= order.getId() %>')">
                                View Details
                            </button>
                            <button class="btn-reorder" onclick="reorderItem('<%= order.getItemName() %>', 'Fruits')">
                                Reorder
                            </button>
                        </div>
                    </div>
                <% } %>
            </div>
        <% } else { %>
            <!-- Enhanced Empty State -->
            <div class="empty-orders">
                <div class="empty-orders-icon">🛒</div>
                <h3>No orders yet</h3>
                <p>You haven't placed any orders yet. Start shopping to see your order history here!</p>
                <div class="empty-orders-actions">
                    <a href="<%= request.getContextPath() %>/shop" class="btn btn-primary btn-large">
                        Start Shopping
                    </a>
                    <button class="btn btn-secondary" onclick="location.reload()">
                        Refresh Page
                    </button>
                </div>
            </div>
        <% } %>
    </div>

    <script src="<%= request.getContextPath() %>/js/script.js"></script>
</body>
</html>