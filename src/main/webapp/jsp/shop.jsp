<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.entity.Category" %>
<%@ page import="com.example.entity.UserDetails" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Shop - SpringGrocery</title>
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
                <% 
                UserDetails user = (UserDetails) session.getAttribute("user");
                if (user != null) { 
                %>
                    <span class="welcome-msg">Welcome, <%= user.getName() %>!</span>
                <% } %>
                <a href="<%= request.getContextPath() %>/cart" class="btn btn-secondary">
                    Cart 
                    <% 
                    java.util.List cartList = (java.util.List) session.getAttribute("cart");
                    if (cartList != null && !cartList.isEmpty()) { 
                    %>
                        <span class="cart-count">(<%= cartList.size() %>)</span>
                    <% } %>
                </a>
                <a href="<%= request.getContextPath() %>/order-history" class="btn btn-secondary">Orders</a>
                <a href="<%= request.getContextPath() %>/logout" class="btn btn-logout">Logout</a>
            </div>
        </div>
    </nav>

    <!-- Shop Content -->
    <div class="shop-container">
        <div class="shop-header">
            <h2>Shop by Categories</h2>
            <p>Choose from our fresh and organic product categories</p>
        </div>
        
        <div class="categories-grid">
            <% 
            List<Category> categories = (List<Category>) request.getAttribute("categories");
            if (categories != null && !categories.isEmpty()) {
                for (Category category : categories) {
            %>
                <div class="category-card" onclick="location.href='<%= request.getContextPath() %>/category?id=<%= category.getId() %>'">
                    <div class="category-image">
                        <img src="<%= request.getContextPath() %>/<%= category.getImageUrl() != null ? category.getImageUrl() : "images/placeholder.jpg" %>" 
                             alt="<%= category.getName() %>" 
                             onerror="this.src='<%= request.getContextPath() %>/images/placeholder.jpg'">
                    </div>
                    <div class="category-info">
                        <h3><%= category.getName() %></h3>
                        <p>Fresh <%= category.getName().toLowerCase() %> delivered daily</p>
                        <div class="category-cta">
                            <span class="btn btn-primary">Shop Now</span>
                        </div>
                    </div>
                </div>
            <% 
                }
            } else {
            %>
                <div class="empty-shop">
                    <h3>No categories available yet</h3>
                    <p>Please check back later for updates.</p>
                </div>
            <% } %>
        </div>
    </div>

    <script src="<%= request.getContextPath() %>/js/script.js"></script>
</body>
</html>