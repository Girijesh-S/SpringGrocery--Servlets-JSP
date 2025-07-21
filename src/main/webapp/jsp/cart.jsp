<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="com.example.model.CartItem" %>
<%@ page import="com.example.entity.UserDetails" %>

<%
    List<CartItem> cart = (List<CartItem>) request.getAttribute("cart");
    String success = (String) request.getAttribute("success");
    String error = (String) request.getAttribute("error");
    Object totalAmountObj = request.getAttribute("totalAmount");
    Object deliveryChargeObj = request.getAttribute("deliveryCharge");
    Object finalAmountObj = request.getAttribute("finalAmount");
    UserDetails user = (UserDetails) session.getAttribute("user");
    Object cartObj = session.getAttribute("cart");
    DecimalFormat df = new DecimalFormat("#.##");
    
    Number totalAmount = (totalAmountObj != null) ? (Number) totalAmountObj : 0.0;
    Number deliveryCharge = (deliveryChargeObj != null) ? (Number) deliveryChargeObj : 0.0;
    Number finalAmount = (finalAmountObj != null) ? (Number) finalAmountObj : 0.0;
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cart - SpringGrocery</title>
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
                <a href="<%= request.getContextPath() %>/shop" class="btn btn-secondary">← Continue Shopping</a>
                <a href="<%= request.getContextPath() %>/order-history" class="btn btn-secondary">Orders</a>
                <a href="<%= request.getContextPath() %>/logout" class="btn btn-logout">Logout</a>
            </div>
        </div>
    </nav>

    <!-- Cart Content -->
    <div class="cart-container">
        <div class="cart-header">
            <h2>Your Shopping Cart</h2>
        </div>
        
        <% if (success != null && !success.isEmpty()) { %>
            <div class="alert alert-success"><%= success %></div>
        <% } %>
        
        <% if (error != null && !error.isEmpty()) { %>
            <div class="alert alert-error"><%= error %></div>
        <% } %>
        
        <% if (cart != null && !cart.isEmpty()) { %>
            <div class="cart-content">
                <div class="cart-items">
                    <% for (CartItem cartItem : cart) { %>
                        <div class="cart-item">
                            <div class="item-image">
                                <img src="<%= request.getContextPath() %>/<%= cartItem.getItem().getImageUrl() %>" 
                                     alt="<%= cartItem.getItem().getName() %>" 
                                     onerror="this.src='<%= request.getContextPath() %>/images/placeholder.jpg'">
                            </div>
                            <div class="item-details">
                                <h3><%= cartItem.getItem().getName() %></h3>
                                <p class="item-category">
                                    <%
                                    String categoryName = "Unknown Category";
                                    try {
                                        if (cartItem.getItem().getCategory() != null) {
                                            categoryName = cartItem.getItem().getCategory().getName();
                                        }
                                    } catch (org.hibernate.LazyInitializationException e) {
                                        categoryName = "Category not available";
                                    } catch (Exception e) {
                                        categoryName = "Category error";
                                    }
                                    out.print(categoryName);
                                    %>
                                </p>
                                <p class="item-quantity"><%= cartItem.getQuantity() %> <%= cartItem.getItem().getUnit() %></p>
                            </div>
                            <div class="item-price">
                                <p class="unit-price">₹<%= df.format(cartItem.getItem().getPricePerUnit()) %> per <%= cartItem.getItem().getUnit() %></p>
                                <p class="total-price">₹<%= df.format(cartItem.getTotalPrice()) %></p>
                            </div>
                            <div class="item-actions">
                                <form method="post" action="<%= request.getContextPath() %>/cart" style="display: inline;">
                                    <input type="hidden" name="action" value="removeItem">
                                    <input type="hidden" name="itemId" value="<%= cartItem.getItem().getId() %>">
                                    <button type="submit" class="btn btn-remove" onclick="return confirm('Remove this item from cart?')">Remove</button>
                                </form>
                            </div>
                        </div>
                    <% } %>
                </div>
                
                <div class="cart-summary">
                    <div class="summary-card">
                        <h3>Order Summary</h3>
                        <div class="summary-row">
                            <span>Subtotal:</span>
                            <span>₹<%= df.format(totalAmount) %></span>
                        </div>
                        <div class="summary-row">
                            <span>Delivery Charge:</span>
                            <span>
                                <% if (deliveryCharge != null && deliveryCharge.doubleValue() == 0.0) { %>
                                    <span class="free-delivery">FREE</span>
                                <% } else { %>
                                    ₹<%= df.format(deliveryCharge) %>
                                <% } %>
                            </span>
                        </div>
                        <hr>
                        <div class="summary-row total">
                            <span><strong>Total Amount:</strong></span>
                            <span><strong>₹<%= df.format(finalAmount) %></strong></span>
                        </div>
                        
                        <div class="delivery-info">
                            <h4>Delivery Details</h4>
                            <% if (user != null) { %>
                                <p><strong><%= user.getName() %></strong></p>
                                <p><%= user.getPhone() %></p>
                                <p><%= user.getAddressLine1() %></p>
                                <% if (user.getAddressLine2() != null && !user.getAddressLine2().isEmpty()) { %>
                                    <p><%= user.getAddressLine2() %></p>
                                <% } %>
                                <p><%= user.getCity() %>, <%= user.getState() %> - <%= user.getPincode() %></p>
                                <p><strong>Delivery Date:</strong> <%= user.getDeliveryDate() %></p>
                            <% } %>
                        </div>
                        
                        <div class="payment-method">
                            <h4>Payment Method</h4>
                            <div class="payment-option">
                                <input type="radio" id="cod" name="payment" value="cod" checked disabled>
                                <label for="cod">Cash on Delivery (COD)</label>
                            </div>
                        </div>
                        
                        <form method="post" action="<%= request.getContextPath() %>/cart">
                            <input type="hidden" name="action" value="placeOrder">
                            <button type="submit" class="btn btn-primary btn-full" 
                                    onclick="return confirm('Confirm order for ₹<%= df.format(finalAmount) %>?')">
                                Place Order
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        <% } else { %>
            <div class="empty-cart">
                <h3>Your cart is empty</h3>
                <p>Add some products to your cart to see them here.</p>
                <a href="<%= request.getContextPath() %>/shop" class="btn btn-primary">Start Shopping</a>
            </div>
        <% } %>
    </div>

    <script src="<%= request.getContextPath() %>/js/script.js"></script>
</body>
</html>