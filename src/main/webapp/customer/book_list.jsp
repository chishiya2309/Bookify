<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${categoryName} - Bookify</title>
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/DuyHung.css">
</head>
<body>

    <c:choose>
        <c:when test="${isLoggedIn}">
            <jsp:include page="/customer/header_customer.jsp" />
        </c:when>
        <c:otherwise>
            <jsp:include page="/customer/header_sign_in.jsp" />
        </c:otherwise>
    </c:choose>

    <div class="container">
        
        <h2 class="category-title">
            Danh mục: <span class="highlight">${categoryName}</span>
        </h2>

        <div class="book-section">
            <div class="book-grid">
                
                <c:forEach items="${listBooks}" var="book">
                    <div class="book-card">
                        
                        <a href="${pageContext.request.contextPath}/view_book?id=${book.bookId}">
                            <c:choose>
                                <c:when test="${not empty book.primaryImageUrl}">
                                    <img class="book-img" 
                                         src="${book.primaryImageUrl}" 
                                         alt="${book.title}" 
                                         onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/images/book_icon.png';" />
                                </c:when>
                                <c:otherwise>
                                    <img class="book-img" 
                                         src="${pageContext.request.contextPath}/images/book_icon.png" 
                                         alt="${book.title}" />
                                </c:otherwise>
                            </c:choose>
                        </a>
                        
                        <div class="book-title">
                            <a href="${pageContext.request.contextPath}/view_book?id=${book.bookId}">
                                ${book.title}
                            </a>
                        </div>
                        
                        <div class="book-author">
                            <c:forEach items="${book.authors}" var="author" varStatus="status">
                                ${author.name}${!status.last ? ',' : ''}
                            </c:forEach>
                        </div>
                        
                        <div class="book-rating">★★★★★</div>
                        <div class="book-price">
                            <fmt:formatNumber value="${book.price}" pattern="#,###"/>₫
                        </div>
                    </div>
                </c:forEach>

                <c:if test="${empty listBooks}">
                    <div class="empty-category-msg">
                        <p>Chưa có sách nào trong danh mục này.</p>
                        <a href="${pageContext.request.contextPath}/">Quay về trang chủ</a>
                    </div>
                </c:if>

            </div>
        </div>
    </div>

    <jsp:include page="/customer/footer_customer.jsp" />

</body>
</html>