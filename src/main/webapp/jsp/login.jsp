<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - SpringGrocery</title>
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
                <a href="<%= request.getContextPath() %>/register" class="btn btn-primary">Register</a>
            </div>
        </div>
    </nav>

    <!-- Login Form -->
    <div class="auth-container">
        <div class="auth-card">
            <h2>Login to SpringGrocery</h2>
            
            <% 
            String error = (String) request.getAttribute("error");
            if (error != null) { 
            %>
                <div class="alert alert-error"><%= error %></div>
            <% } %>
            
            <form method="post" action="<%= request.getContextPath() %>/login" class="auth-form">
                <div class="form-group">
                    <label for="phone">Phone Number</label>
                    <input type="tel" id="phone" name="phone" pattern="[0-9]{10}" maxlength="10" 
                           placeholder="Enter 10-digit phone number" required>
                </div>
                
                <div class="form-group captcha-group">
                    <label for="captcha">Enter Captcha</label>
                    <div class="captcha-container">
                        <div class="captcha-display"><%= request.getAttribute("captcha") != null ? request.getAttribute("captcha") : "ABCD" %></div>
                        <button type="button" onclick="refreshCaptcha()" class="btn btn-refresh">🔄</button>
                    </div>
                    <input type="text" id="captcha" name="captcha" placeholder="Enter captcha" required>
                </div>
                
                <button type="submit" class="btn btn-primary btn-full">Login</button>
            </form>
            
            <div class="auth-footer">
                <p>Don't have an account? <a href="<%= request.getContextPath() %>/register">Register here</a></p>
            </div>
        </div>
    </div>

    <script>
        function refreshCaptcha() {
            window.location.href = "<%= request.getContextPath() %>/login";
        }
    </script>
    <script src="<%= request.getContextPath() %>/js/script.js"></script>
</body>
</html>