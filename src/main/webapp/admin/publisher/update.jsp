<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Publisher Management - Admin</title>
    <link rel="stylesheet" href="../../css/styles.css" type="text/css"/>
</head>
<body>
<h2>Edit Publisher</h2>

<form action="${pageContext.request.contextPath}/admin/publishers" method="post">

    <input type="hidden" name="action" value="update"/>

    <input type="hidden" name="id" value="${publisher.publisherId}"/>

    <label>Full Name:</label>
    <input type="text" name="name" value="<c:out value='${publisher.name}'/>" required style="width: 300px;">
    <br>

    <label>Contact Email:</label>
    <input type="email" name="contactEmail" value="<c:out value='${publisher.contactEmail}'/>" required style="width: 300px;">
    <br>

    <label>Address:</label>
    <input type="text" name="address" value="<c:out value='${publisher.address}'/>" style="width: 300px;">
    <br>

    <label>Website:</label>
    <input type="text" name="website" value="<c:out value='${publisher.website}'/>" style="width: 300px;">
    <br>

    <div class="buttons">
        <button type="submit" class="btn">Save</button>
        <button type="button" class="btn" onclick="history.back()">Cancel</button>
    </div>

</form>

<script src="../../js/script.js"></script>
</body>
</html>