<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SpringGrocery Test</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; background: #f8f9fa; }
        .container { max-width: 800px; margin: 0 auto; }
        .header { background: #4CAF50; color: white; padding: 30px; border-radius: 10px; text-align: center; }
        .content { background: white; margin: 20px 0; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .btn { display: inline-block; background: #4CAF50; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; margin: 5px; }
        .btn:hover { background: #45a049; }
        .status { padding: 15px; margin: 10px 0; border-radius: 5px; }
        .success { background: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .info { background: #d1ecf1; color: #0c5460; border: 1px solid #b8daff; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🌱 SpringGrocery - Test Page</h1>
            <p>Fresh Organic Groceries Delivered to Your Doorstep</p>
        </div>
        
        <div class="content">
            <div class="status success">
                <h2>✅ Application is Working!</h2>
                <p>Your SpringGrocery application has been successfully deployed and is running on Tomcat 10.</p>
            </div>
            
            <h3>📊 System Information:</h3>
            <div class="status info">
                <p><strong>Server Time:</strong> <%= new java.util.Date() %></p>
                <p><strong>Context Path:</strong> <%= request.getContextPath() %></p>
                <p><strong>Session ID:</strong> <%= session.getId() %></p>
                <p><strong>Server Info:</strong> <%= application.getServerInfo() %></p>
            </div>
            
            <h3>🧪 Navigation Test:</h3>
            <a href="<%= request.getContextPath() %>/" class="btn">🏠 Home Page</a>
            <a href="<%= request.getContextPath() %>/login" class="btn">🔑 Login Page</a>
            <a href="<%= request.getContextPath() %>/register" class="btn">📝 Register Page</a>
            
            <h3>👤 Session Information:</h3>
            <div class="status info">
                <% if (session.getAttribute("user") != null) { %>
                    <p>✅ User is logged in</p>
                    <a href="<%= request.getContextPath() %>/shop" class="btn">🛒 Go to Shop</a>
                    <a href="<%= request.getContextPath() %>/logout" class="btn">🚪 Logout</a>
                <% } else { %>
                    <p>ℹ️ No user session (not logged in)</p>
                    <p>Try logging in to test the full application functionality.</p>
                <% } %>
            </div>
        </div>
    </div>
</body>
</html>