<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // Server-side authentication check
    String username = (String) request.getAttribute("username");
    String userRole = (String) request.getAttribute("userRole");
    
    // Verify user is authenticated
    if (username == null || username.isEmpty()) {
        response.sendRedirect(request.getContextPath() + "/admin/AdminLogin.jsp");
        return;
    }
    
    // Verify user has admin privileges
    if (!"ADMIN".equals(userRole)) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.sendRedirect(request.getContextPath() + "/customer/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <title>Admin Dashboard</title>
    </head>
    <body>
        <h1>Chào mừng Admin!</h1>
        <p>Bạn đã đăng nhập thành công.</p>
        <a href="${pageContext.request.contextPath}/auth/logout">Đăng xuất</a>
    </body>
</html>