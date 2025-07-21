<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.example.entity.Category" %>
<%@ page import="com.example.entity.Item" %>
<%@ page import="java.text.DecimalFormat" %>

<%
    Category category = (Category) request.getAttribute("category");
    String success = (String) request.getAttribute("success");
    String error = (String) request.getAttribute("error");
    Object cartObj = session.getAttribute("cart");
    DecimalFormat df = new DecimalFormat("#.##");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= category != null ? category.getName() : "Category" %> - SpringGrocery</title>
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
                <a href="<%= request.getContextPath() %>/shop" class="btn btn-secondary">← Back to Shop</a>
                <a href="<%= request.getContextPath() %>/cart" class="btn btn-secondary">
                    Cart 
                    <% if (cartObj != null) {
                        try {
                            if (cartObj instanceof java.util.List) {
                                java.util.List cartList = (java.util.List) cartObj;
                                if (!cartList.isEmpty()) { %>
                                    <span class="cart-count"><%= cartList.size() %></span>
                    <%          }
                            } else if (cartObj instanceof java.util.Map) {
                                java.util.Map cartMap = (java.util.Map) cartObj;
                                if (!cartMap.isEmpty()) { %>
                                    <span class="cart-count"><%= cartMap.size() %></span>
                    <%          }
                            }
                        } catch (Exception e) {
                            // Handle any casting errors silently
                        }
                    } %>
                </a>
                <a href="<%= request.getContextPath() %>/order-history" class="btn btn-secondary">Orders</a>
                <a href="<%= request.getContextPath() %>/logout" class="btn btn-logout">Logout</a>
            </div>
        </div>
    </nav>

    <!-- Category Content -->
    <div class="category-container">
        <div class="category-header">
            <h2><%= category != null ? category.getName() : "Category" %></h2>
            <p>Fresh and organic <%= category != null ? category.getName().toLowerCase() : "category" %> products</p>
        </div>
        
        <% if (success != null && !success.isEmpty()) { %>
            <div class="alert alert-success"><%= success %></div>
        <% } %>
        
        <% if (error != null && !error.isEmpty()) { %>
            <div class="alert alert-error"><%= error %></div>
        <% } %>
        
        <div class="products-grid">
            <% 
            if (category != null && category.getItems() != null && !category.getItems().isEmpty()) {
                for (Item item : category.getItems()) { 
            %>
                <div class="product-card">
                    <div class="product-image">
                        <img src="<%= request.getContextPath() %>/<%= item.getImageUrl() %>" alt="<%= item.getName() %>" 
                             onerror="this.src='<%= request.getContextPath() %>/images/placeholder.jpg'">
                    </div>
                    <div class="product-info">
                        <h3><%= item.getName() %></h3>
                        <p class="product-price">₹<%= df.format(item.getPricePerUnit()) %> per <%= item.getUnit() %></p>
                        <p class="product-stock"><%= item.getAvailableStock() %> <%= item.getUnit() %> available</p>
                        
                        <form method="post" action="<%= request.getContextPath() %>/category" class="add-to-cart-form">
                            <input type="hidden" name="itemId" value="<%= item.getId() %>">
                            
                            <div class="quantity-selector">
                                <label for="quantity_<%= item.getId() %>">Quantity:</label>
                                <select id="quantity_<%= item.getId() %>" name="quantity" required>
                                    <option value="" selected>Select</option>
                                    <% 
                                    if (item.getAllowedQuantities() != null) {
                                        for (Object qtyObj : item.getAllowedQuantities()) {
                                            Number qty = (Number) qtyObj;
                                            if (qty.doubleValue() <= item.getAvailableStock()) {
                                    %>
                                                <option value="<%= qty %>"><%= qty %> <%= item.getUnit() %></option>
                                    <%      }
                                        }
                                    }
                                    %>
                                </select>
                            </div>
                            
                            <button type="submit" class="btn btn-primary btn-add-cart">Select Quantity</button>
                        </form>
                    </div>
                </div>
            <% 
                }
            } else { 
            %>
                <div class="empty-category">
                    <h3>No products available in this category yet.</h3>
                    <p>Please check back later for updates.</p>
                    <a href="<%= request.getContextPath() %>/shop" class="btn btn-primary">← Back to Shop</a>
                </div>
            <% } %>
        </div>
    </div>

    <script src="<%= request.getContextPath() %>/js/script.js"></script>
</body>
</html>