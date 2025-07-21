<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error - SpringGrocery</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <!-- Navigation -->
    <nav class="navbar">
        <div class="nav-container">
            <div class="nav-logo">
                <img src="${pageContext.request.contextPath}/images/logo.png" alt="SpringGrocery Logo" class="logo">
                <a href="${pageContext.request.contextPath}/home"><h1>SpringGrocery</h1></a>
            </div>
            <div class="nav-auth">
                <a href="${pageContext.request.contextPath}/home" class="btn btn-primary">Home</a>
                <c:if test="${sessionScope.user != null}">
                    <a href="${pageContext.request.contextPath}/shop" class="btn btn-secondary">Shop</a>
                </c:if>
            </div>
        </div>
    </nav>

    <!-- Error Content -->
    <div class="error-container">
        <div class="error-card">
            <h1>Oops! Something went wrong</h1>
            <p>We're sorry, but something unexpected happened. Please try again.</p>
            
            <div class="error-actions">
                <a href="${pageContext.request.contextPath}/home" class="btn btn-primary">Go to Homepage</a>
                <c:if test="${sessionScope.user != null}">
                    <a href="${pageContext.request.contextPath}/shop" class="btn btn-secondary">Continue Shopping</a>
                </c:if>
                <button onclick="history.back()" class="btn btn-secondary">Go Back</button>
            </div>
            
            <div class="error-help">
                <h4>Need help?</h4>
                <p>Contact our support team:</p>
                <p>📞 +91 9876543210</p>
                <p>📧 support@springgrocery.com</p>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>