<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Books Management - Admin</title>
    <link rel="stylesheet" href="../../css/styles.css" type="text/css"/>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
    <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
    <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
</head>
<body>
    <h2>Create New Book</h2>
    <form action="${pageContext.request.contextPath}/admin/books" method="post" enctype="multipart/form-data">

        <input type="hidden" name="action" value="create"/>

        <label>Category:</label>
        <select name="categoryId" required>
            <c:forEach items="${listCategory}" var="cat">
                <option value="${cat.categoryId}">${cat.name}</option>
            </c:forEach>
        </select>
        <br><br>

        <label>Title:</label>
        <input type="text" name="title" required>
        <br><br>

        <label>Author:</label>
        <select name="authorIds" id="authorSelect" multiple="multiple" style="width: 300px" required >
            <c:forEach items="${listAuthors}" var="author">
                <option value="${author.authorId}"><c:out value="${author.name}"/></option>
            </c:forEach>
        </select>
        <br><br>

        <label>ISBN:</label>
        <input type="text" name="isbn" required>
        <br><br>

        <label>Publish Date:</label>
        <input type="date" name="publishDate" required>
        <br><br>

        <label>Book Image:</label>
        <input type="file" name="bookImage" accept="image/*" onchange="showPreview(this);" required>
        <br>
        <img style="max-height: 250px; display: none" id="thumbnail" alt="Image Preview"/>
        <br>

        <label>Price:</label>
        <input type="number" name="price" required>
        <br><br>

        <label>Description:</label><br>
        <textarea name="description" rows="5" cols="40" required></textarea>
        <br><br>

        <input type="submit" value="Save">
        <input type="button" value="Cancel" onclick="history.back()">

    </form>
<script src="../../js/script.js"></script>
</body>
</html>
