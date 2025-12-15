<%@page contentType="text/html" pageEncoding="UTF-8"%>
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