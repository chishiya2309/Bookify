<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>BookDetails</title>
        <link rel="stylesheet" href="../../css/styles.css" type="text/css"/>
    </head>
    <body>
    <div class="container mt-5 mb-5">
        <div class="row">

            <div class="col-md-3 text-center">
                <c:if test="${not empty book.images}">
                    <img src="${book.images[0].url}" alt="${book.title}" class="book-cover-img">
                </c:if>
                <c:if test="${empty book.images}">
                    <img src="https://via.placeholder.com/300x400?text=No+Image" alt="No Image" class="book-cover-img">
                </c:if>
            </div>

            <div class="col-md-6">
                <div class="book-title">${book.title}</div>

                <div class="book-author">
                    by
                    <c:forEach var="author" items="${book.authors}" varStatus="status">
                        ${author.name}<c:if test="${!status.last}">, </c:if>
                    </c:forEach>
                </div>

                <div class="d-flex align-items-center mb-3">
                    <div class="rating-stars">
                        <c:set var="rating" value="${book.averageRating}" />
                        <c:forEach begin="1" end="5" var="i">
                            <c:choose>
                                <c:when test="${rating >= i}">
                                    <i class="fas fa-star"></i> </c:when>
                                <c:when test="${rating > i - 1 && rating < i}">
                                    <i class="fas fa-star-half-alt"></i> </c:when>
                                <c:otherwise>
                                    <i class="far fa-star"></i> </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </div>
                    <a href="#reviews-section" class="review-count">
                        ${book.reviews.size()} Reviews
                    </a>
                </div>

                <div class="description-box">
                    <c:if test="${not empty book.description}">
                        ${book.description}
                    </c:if>
                    <c:if test="${empty book.description}">
                        Book description goes here...
                    </c:if>
                </div>
            </div>

            <div class="col-md-3 text-center">
            <span class="price-tag">
                $${book.price}
            </span>

                <form action="add-to-cart" method="post">
                    <input type="hidden" name="bookId" value="${book.bookId}">
                    <button type="submit" class="btn btn-add-to-cart">
                        Add to Cart
                    </button>
                </form>

                <div class="mt-3 text-muted small">
                    <i class="fas fa-truck"></i> Free Shipping
                </div>
            </div>

        </div>

        <div id="reviews-section" class="row mt-5">
            <div class="col-12">
                <hr>
                <h4>Customer Reviews</h4>
            </div>
        </div>
    </div>
    </body>
</html>
