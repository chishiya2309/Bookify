<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Search Results - Bookify</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/DuyHung.css">
</head>
<body>
    <%-- Hiển thị header phù hợp với trạng thái đăng nhập --%>
    <c:choose>
        <c:when test="${isLoggedIn}">
            <jsp:include page="/customer/header_customer.jsp"></jsp:include>
        </c:when>
        <c:otherwise>
            <jsp:include page="/customer/header_sign_in.jsp"></jsp:include>
        </c:otherwise>
    </c:choose>

    <div class="container">
        
        <div class="search-header">
            <c:if test="${not empty keyword}">
                <h2>Search Results for <span class="highlight">"${keyword}"</span></h2>
            </c:if>
            <c:if test="${empty keyword}">
                <h2>Search Results</h2>
            </c:if>
        </div>

        <div class="search-list">
            <c:forEach items="${listResult}" var="book">
                <div class="search-item">
                    <div class="search-item-img">
                        <a href="${pageContext.request.contextPath}/view_book?id=${book.bookId}">
                            <c:choose>
                                <%-- Trường hợp 1: Có đường dẫn ảnh --%>
                                <c:when test="${not empty book.primaryImageUrl}">
                                    <img src="${book.primaryImageUrl}" alt="${book.title}"
                                         style="width: 120px; height: 180px; object-fit: cover; border-radius: 4px; border: 1px solid #ddd;"
                                         onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/images/book_icon.png';" />
                                </c:when>
                                
                                <%-- Trường hợp 2: Không có ảnh -> Dùng ảnh mặc định --%>
                                <c:otherwise>
                                    <img src="${pageContext.request.contextPath}/images/book_icon.png" alt="${book.title}"
                                         style="width: 120px; height: 180px; object-fit: cover; border-radius: 4px; border: 1px solid #ddd;" />
                                </c:otherwise>
                            </c:choose>
                        </a>
                    </div>

                    <div class="search-item-info">
                        <div class="item-title">
                            <a href="${pageContext.request.contextPath}/view_book?id=${book.bookId}">${book.title}</a>
                        </div>
                        
                        <div class="item-rating">★★★★☆</div> 
                        
                        <div class="item-author">
                            by 
                            <c:forEach items="${book.authors}" var="author" varStatus="status">
                                ${author.name}${!status.last ? ',' : ''}
                            </c:forEach>
                        </div>
                        
                        <div class="item-desc">
                            <c:if test="${not empty book.description}">
                                <c:choose>
                                    <c:when test="${book.description.length() > 150}">
                                        <c:out value="${book.description.substring(0, 150)}..." />
                                    </c:when>
                                    <c:otherwise>
                                        <c:out value="${book.description}" />
                                    </c:otherwise>
                                </c:choose>
                            </c:if>
                            
                            <c:if test="${empty book.description}">
                                <span style="font-style: italic; color: #999;">No description available for this book.</span>
                            </c:if>
                        </div>
                    </div>

                    <div class="search-item-action">
                        <div class="item-price">
                            <fmt:formatNumber value="${book.price}" pattern="#,###"/>₫
                        </div>
                        <a href="${pageContext.request.contextPath}/add_to_cart?bookId=${book.bookId}" class="btn-add-cart">Add To Cart</a>
                    </div>
                </div>
            </c:forEach>

            <c:if test="${empty listResult}">
                <div class="empty-msg">
                    <p>No books found matching your criteria.</p>
                    <a href="${pageContext.request.contextPath}/" style="color: var(--color-primary);">Back to Homepage</a>
                </div>
            </c:if>
        </div>
    </div>

    <jsp:include page="/customer/footer_customer.jsp"></jsp:include>
</body>
</html>