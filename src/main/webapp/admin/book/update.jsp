<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Books Management - Admin</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
    <link rel="stylesheet" href="../../css/styles.css" type="text/css"/>
    <link href="https://cdn.jsdelivr.net/npm/tom-select@2.3.1/dist/css/tom-select.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/tom-select@2.3.1/dist/js/tom-select.complete.min.js"></script>
</head>
<body>
<jsp:include page="../header_admin.jsp" />

<div class="container">
    <h2>Edit Book</h2>

    <form action="${pageContext.request.contextPath}/admin/books" method="post" enctype="multipart/form-data" class="form-card" novalidate>

        <input type="hidden" name="action" value="update"/>
        <input type="hidden" name="bookId" value="${book.bookId}"/>

        <div class="form-row">
            <label for="categoryId">Category:</label>
            <select id="categoryId" name="categoryId" required>
                <c:forEach items="${listCategory}" var="cat">
                    <option value="${cat.categoryId}"
                            <c:if test="${cat.categoryId == book.category.categoryId}">selected</c:if>>
                            ${cat.name}
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="form-row">
            <label for="title">Title:</label>
            <input id="title" type="text" name="title" value="${book.title}" required
                   maxlength="255"
                   minlength="1"
                   placeholder="Enter book title (max 255 characters)">
        </div>

        <div class="form-row">
            <label for="authorSelect">Author:</label>
            <select name="authorIds" id="authorSelect" multiple="multiple" required class="tomselect-authors">
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
        </div>

        <div class="form-row">
            <label for="isbn">ISBN:</label>
            <input id="isbn" type="text" name="isbn" value="${book.isbn}" required
                   maxlength="20"
                   minlength="10"
                   pattern="[0-9\-]{10,20}"
                   placeholder="Enter ISBN (10-20 digits)"
                   title="ISBN must be 10-20 characters, digits and hyphens only">
        </div>

        <div class="form-row">
            <label for="publishDate">Publish Date:</label>
            <input id="publishDate" type="date" name="publishDate" value="${book.publishDate}" required
                   max="${java.time.LocalDate.now()}"
                   title="Publish date cannot be in the future">
        </div>

        <div class="form-row">
            <label for="bookImage">Book Image:</label>
            <input id="bookImage" type="file" name="bookImage"
                   accept="image/jpeg,image/png,image/jpg,image/webp"
                   onchange="showPreview(this);"
                   title="Only JPEG, PNG, JPG, WEBP images are allowed (optional for update)">
        </div>

        <div class="form-row" style="grid-template-columns: 160px 1fr;">
            <label></label>
            <img style="max-height: 250px; display: none" id="thumbnail" alt="Image Preview"/>
        </div>

        <div class="form-row">
            <label for="price">Price:</label>
            <div class="input-prefix">
                <span class="prefix">$</span>
                <input id="price" type="number" name="price" value="${book.price}" required
                       min="0.01"
                       max="99999999.99"
                       step="0.01"
                       placeholder="0.00"
                       title="Price must be greater than 0">
            </div>
        </div>

        <div class="form-row" style="align-items: start;">
            <label for="description">Description:</label>
            <textarea id="description" name="description" rows="5"
                      minlength="10"
                      maxlength="5000"
                      placeholder="Enter book description (10-5000 characters)">${book.description}</textarea>
        </div>

        <div class="buttons">
            <button type="submit" class="btn">Save</button>
            <a class="btn" href="${pageContext.request.contextPath}/admin/books">Cancel</a>
        </div>

    </form>
</div>

<jsp:include page="../footer_admin.jsp" />
<script src="../../js/script.js"></script>
</body>
</html>