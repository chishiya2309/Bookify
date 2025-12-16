<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Books Management - Admin</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
    <link rel="stylesheet" href="../../css/styles.css" type="text/css"/>
    <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
    <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
</head>
<body>
<h2>Edit Book</h2>

<form action="${pageContext.request.contextPath}/admin/books" method="post" enctype="multipart/form-data">

    <input type="hidden" name="action" value="update"/>
    <input type="hidden" name="bookId" value="${book.bookId}"/>

    <label>Category:</label>
    <select name="categoryId" required>
        <c:forEach items="${listCategory}" var="cat">
            <option value="${cat.categoryId}"
                    <c:if test="${cat.categoryId == book.category.categoryId}">selected</c:if>>
                    ${cat.name}
            </option>
        </c:forEach>
    </select>
    <br>

    <label>Title:</label>
    <input type="text" name="title" value="${book.title}" required>
    <br>

    <label>Author:</label>
    <select name="authorIds" id="authorSelect" multiple="multiple" required>
        <c:forEach items="${listAuthors}" var="allAuth">
            <c:set var="isSelected" value="" />

            <c:if test="${not empty book.authors}">
                <c:forEach items="${book.authors}" var="bookAuth">
                    <c:if test="${bookAuth.authorId == allAuth.authorId}">
                        <c:set var="isSelected" value="selected" />
                    </c:if>
                </c:forEach>
            </c:if>
            <option value="${allAuth.authorId}" ${isSelected}>
                    ${allAuth.name}
            </option>
        </c:forEach>
    </select>
    <br>

    <label>ISBN:</label>
    <input type="text" name="isbn" value="${book.isbn}" required>
    <br>

    <label>Publish Date:</label>
    <input type="date" name="publishDate" value="${book.publishDate}" required>
    <br>

    <label>Book image:</label>
    <input type="file" name="bookImage" accept="image/*" onchange="showPreview(this);" style="width: auto;">
    <br>

<%--    <c:if test="${not empty book.images}">--%>
<%--        <img id="thumbnail" src="${pageContext.request.contextPath}/images/${book.images[0].fileName}" alt="Book Cover"/>--%>
<%--    </c:if>--%>
<%--    <c:if test="${empty book.images}">--%>
<%--        <img id="thumbnail" alt="Preview" style="display: none;"/>--%>
<%--    </c:if>--%>

    <label>Price:</label>
    <input type="number" name="price" value="${book.price}" step="0.01" style="width: 100px;"> $
    <br>

    <label>Description:</label>
    <textarea name="description" rows="5" required>${book.description}</textarea>
    <br>

    <div class="buttons">
        <button type="submit" class="btn">Save</button>
        <button type="button" class="btn" onclick="history.back()">Cancel</button>
    </div>

</form>
<script src="../../js/script.js"></script>
</body>
</html>