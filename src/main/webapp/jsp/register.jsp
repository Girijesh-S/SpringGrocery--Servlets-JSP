<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - SpringGrocery</title>
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
                <a href="<%= request.getContextPath() %>/home" class="btn btn-secondary">Home</a>
                <a href="<%= request.getContextPath() %>/login" class="btn btn-primary">Login</a>
            </div>
        </div>
    </nav>

    <!-- Register Form -->
    <div class="auth-container">
        <div class="auth-card register-card">
            <h2>Register with SpringGrocery</h2>
            
            <% 
            String error = (String) request.getAttribute("error");
            if (error != null) { 
            %>
                <div class="alert alert-error"><%= error %></div>
            <% } %>
            
            <form method="post" action="<%= request.getContextPath() %>/register" class="auth-form">
                <div class="form-row">
                    <div class="form-group">
                        <label for="name">Full Name *</label>
                        <input type="text" id="name" name="name" placeholder="Enter your full name" required>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="phone">Phone Number *</label>
                        <input type="tel" id="phone" name="phone" pattern="[0-9]{10}" maxlength="10" 
                               placeholder="10-digit phone number" required>
                    </div>
                    <div class="form-group">
                        <label for="altPhone">Alternative Phone *</label>
                        <input type="tel" id="altPhone" name="altPhone" pattern="[0-9]{10}" maxlength="10" 
                               placeholder="10-digit alt phone number" required>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="pincode">Pincode *</label>
                        <input type="text" id="pincode" name="pincode" pattern="[0-9]{6}" maxlength="6" 
                               placeholder="6-digit pincode" required>
                    </div>
                    <div class="form-group">
                        <label for="city">City *</label>
                        <select id="city" name="city" required>
                            <option value="">Select City</option>
                            <% 
                            List<String> cities = (List<String>) request.getAttribute("cities");
                            if (cities != null) {
                                for (String city : cities) {
                            %>
                                <option value="<%= city %>"><%= city %></option>
                            <% 
                                }
                            } else {
                            %>
                                <option value="Chennai">Chennai</option>
                                <option value="Chengalpattu">Chengalpattu</option>
                                <option value="Kancheepuram">Kancheepuram</option>
                            <% } %>
                        </select>
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="addressLine1">Address Line 1 *</label>
                    <input type="text" id="addressLine1" name="addressLine1" 
                           placeholder="House/Flat No, Street" required>
                </div>
                
                <div class="form-group">
                    <label for="addressLine2">Address Line 2</label>
                    <input type="text" id="addressLine2" name="addressLine2" 
                           placeholder="Landmark, Area">
                </div>
                
                <div class="form-group">
                    <label for="deliveryDate">Preferred Delivery Date</label>
                    <select id="deliveryDate" name="deliveryDate">
                        <option value="Today">Today</option>
                        <option value="Tomorrow">Tomorrow</option>
                    </select>
                </div>
                
                <button type="submit" class="btn btn-primary btn-full">Register & Continue</button>
            </form>
            
            <div class="auth-footer">
                <p>Already have an account? <a href="<%= request.getContextPath() %>/login">Login here</a></p>
            </div>
        </div>
    </div>

    <script src="<%= request.getContextPath() %>/js/script.js"></script>
</body>
</html>