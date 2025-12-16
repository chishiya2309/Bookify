<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Bookify - Online Bookstore</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
    <jsp:include page="/common/header_sign_in.jsp"></jsp:include>

    <div class="container">
        
        <div style="margin-top: 20px;"></div>

        <div class="book-section">
            <h2>New Books:</h2>
            <div class="book-grid">
                <c:forEach items="${listNewBooks}" var="book">
                    <div class="book-card">
                        <a href="${pageContext.request.contextPath}/view_book?id=${book.bookId}">
                            <img class="book-img" src="${pageContext.request.contextPath}/images/book_icon.png" alt="${book.title}" />
                        </a>
                        
                        <div class="book-title">
                            <a href="${pageContext.request.contextPath}/view_book?id=${book.bookId}">${book.title}</a>
                        </div>
                        
                        <div class="book-author">
                            <c:forEach items="${book.authors}" var="author" varStatus="status">
                                ${author.name}${!status.last ? ',' : ''}
                            </c:forEach>
                        </div>
                        
                        <div class="book-rating">★★★★☆</div>

                        <div class="book-price">
                            <fmt:formatNumber value="${book.price}" type="currency" currencySymbol="$"/>
                        </div>
                    </div>
                </c:forEach>
                
                <c:if test="${empty listNewBooks}">
                    <p class="empty-msg">No new books available at the moment.</p>
                </c:if>
            </div>
        </div>

        <hr>

        <div class="book-section">
            <h2>Best-Selling Books</h2>
            <div class="book-grid">
                <c:forEach items="${listBestSellingBooks}" var="book">
                    <div class="book-card">
                        <a href="${pageContext.request.contextPath}/view_book?id=${book.bookId}">
                            <img class="book-img" src="${pageContext.request.contextPath}/images/book_icon.png" alt="${book.title}" />
                        </a>
                        
                        <div class="book-title">
                            <a href="${pageContext.request.contextPath}/view_book?id=${book.bookId}">${book.title}</a>
                        </div>
                        
                        <div class="book-author">
                            <c:forEach items="${book.authors}" var="author" varStatus="status">
                                ${author.name}${!status.last ? ',' : ''}
                            </c:forEach>
                        </div>

                        <div class="book-rating">★★★★★</div>

                        <div class="book-price">
                            <fmt:formatNumber value="${book.price}" type="currency" currencySymbol="$"/>
                        </div>
                    </div>
                </c:forEach>

                <c:if test="${empty listBestSellingBooks}">
                    <p class="empty-msg">Best-selling data is updating...</p>
                </c:if>
            </div>
        </div>

        <hr>

        <div class="book-section">
            <h2>Most-Favored Books</h2>
            <div class="book-grid">
                <c:forEach items="${listFavoredBooks}" var="book">
                    <div class="book-card">
                        <a href="${pageContext.request.contextPath}/view_book?id=${book.bookId}">
                            <img class="book-img" src="${pageContext.request.contextPath}/images/book_icon.png" alt="${book.title}" />
                        </a>
                        
                        <div class="book-title">
                            <a href="${pageContext.request.contextPath}/view_book?id=${book.bookId}">${book.title}</a>
                        </div>
                        
                        <div class="book-rating">★★★★★</div>

                        <div class="book-price">
                            <fmt:formatNumber value="${book.price}" type="currency" currencySymbol="$"/>
                        </div>
                    </div>
                </c:forEach>

                <c:if test="${empty listFavoredBooks}">
                    <p class="empty-msg">No highly rated books found yet.</p>
                </c:if>
            </div>
        </div>

    </div>

    <jsp:include page="/common/footer_customer.jsp"></jsp:include>
</body>
</html>