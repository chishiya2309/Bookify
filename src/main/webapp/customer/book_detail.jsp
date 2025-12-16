<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>${book.title} - Bookify</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
  <style>
    .container { max-width: 1200px; margin: 0 auto; padding: 20px; }
    .book-detail { display: flex; flex-wrap: wrap; gap: 40px; margin-top: 20px; }
    .book-image { flex: 1; min-width: 300px; }
    .book-image img { max-width: 100%; border: 1px solid #ddd; border-radius: 8px; }
    .book-info { flex: 2; min-width: 300px; }
    .book-info h1 { font-size: 32px; margin-bottom: 15px; }
    .price { font-size: 28px; color: #b12704; font-weight: bold; margin: 20px 0; }
    .meta-info { margin: 12px 0; font-size: 16px; color: #333; }
    .description { margin-top: 25px; line-height: 1.8; font-size: 16px; }
    .average-rating { font-size: 18px; color: #f39c12; margin: 15px 0; font-weight: bold; }
    .quantity-section { margin: 20px 0; display: flex; align-items: center; gap: 15px; }
    .quantity-input { width: 70px; padding: 8px; text-align: center; border: 1px solid #ccc; border-radius: 4px; }
    .action-buttons { display: flex; gap: 20px; margin-top: 20px; }
    .add-to-cart-btn, .buy-now-btn { padding: 14px 32px; font-size: 18px; border: none; border-radius: 6px; cursor: pointer; font-weight: bold; }
    .add-to-cart-btn { background: #ffc107; color: #333; }
    .add-to-cart-btn:hover { background: #e0a800; }
    .buy-now-btn { background: #dc3545; color: white; }
    .buy-now-btn:hover { background: #c82333; }
    .shipping-info { margin-top: 40px; padding: 20px; background: #f8f9fa; border-radius: 8px; border: 1px solid #eee; }
    .shipping-info p { margin: 8px 0; font-size: 15px; color: #555; }

    .reviews-section { margin-top: 60px; border-top: 2px solid #eee; padding-top: 30px; }
    .reviews-section h2 { margin-bottom: 25px; color: #333; }
    .review-item { background: #f8f9fa; padding: 20px; border-radius: 8px; margin-bottom: 20px; border: 1px solid #eee; }
    .review-item .rating { color: #f39c12; font-size: 20px; margin-left: 8px; }
    .review-item .date { color: #666; font-size: 14px; margin-left: 10px; }
    .review-item .headline { margin: 10px 0; font-weight: bold; color: #2c3e50; }

    #load-more-btn { display: block; margin: 30px auto; padding: 12px 30px; background: #28a745; color: white; border: none; font-size: 16px; cursor: pointer; border-radius: 6px; }
    #load-more-btn:hover { background: #218838; }
  </style>
</head>
<body>

<jsp:include page="/common/header_sign_in.jsp"></jsp:include>

<div class="container">
  <div class="book-detail">
    <div class="book-image">
      <c:choose>
        <c:when test="${not empty book.images}">
          <c:forEach items="${book.images}" var="img" varStatus="status">
            <c:if test="${img.isPrimary || status.first}">
              <img src="${img.url}" alt="${book.title}" />
            </c:if>
          </c:forEach>
        </c:when>
        <c:otherwise>
          <img src="${pageContext.request.contextPath}/images/book_icon.png" alt="${book.title}" />
        </c:otherwise>
      </c:choose>
    </div>

    <div class="book-info">
      <h1>${book.title}</h1>

      <div class="meta-info">
        <strong>Author(s):</strong>
        <c:forEach items="${book.authors}" var="author" varStatus="status">
          ${author.name}<c:if test="${!status.last}">, </c:if>
        </c:forEach>
      </div>

      <div class="meta-info"><strong>Category:</strong> ${book.category.name}</div>
      <div class="meta-info"><strong>Publisher:</strong> ${book.publisher.name}</div>

      <div class="average-rating">
        <c:set var="fullStars" value="${avgRating.intValue()}"/>
        <c:set var="hasHalfStar" value="${avgRating - fullStars >= 0.3}"/>

        <c:forEach begin="1" end="${fullStars}">★</c:forEach>
        <c:if test="${hasHalfStar}">½</c:if>
        (${avgRating} / 5) - ${totalReviews} reviews
      </div>

      <div class="price">
        <fmt:formatNumber value="${book.price}" type="currency" currencySymbol="$"/>
      </div>

      <div class="meta-info"><strong>In stock:</strong> ${book.quantityInStock} copies</div>

      <div class="description">
        <strong>Description:</strong><br>
        ${book.description}
      </div>

      <div class="quantity-section">
        <label for="quantity"><strong>Quantity:</strong></label>
        <input type="number" id="quantity" class="quantity-input" min="1"
               max="${book.quantityInStock}" value="1">
      </div>

      <div class="action-buttons">
        <button class="add-to-cart-btn" onclick="addToCart(${book.bookId})">
          Add to Cart
        </button>
        <button class="buy-now-btn" onclick="buyNow(${book.bookId})">
          Buy Now
        </button>
      </div>

      <div class="shipping-info">
        <p>Free shipping on orders over $50</p>
        <p>Cash on delivery (COD), credit card, bank transfer</p>
        <p>7-day return if defective or incorrect</p>
        <p>24/7 customer support</p>
      </div>
    </div>
  </div>

  <div class="reviews-section">
    <h2>Customer Reviews (${totalReviews})</h2>

    <div id="reviews-list">
      <c:forEach items="${reviews}" var="r">
        <div class="review-item">
          <strong>${r.customer.fullName}</strong>
          <span class="rating">
                            <c:forEach begin="1" end="${r.rating}">★</c:forEach>
                        </span>
          <span class="date">(${r.reviewDate})</span><br>
          <c:if test="${not empty r.headline}">
            <h4 class="headline">${r.headline}</h4>
          </c:if>
          <p>${r.comment}</p>
        </div>
      </c:forEach>
    </div>

    <c:if test="${loadedCount < totalReviews}">
      <button id="load-more-btn" data-page="1">Load more reviews</button>
    </c:if>
  </div>
</div>

<jsp:include page="/common/footer_customer.jsp"></jsp:include>

<script>
  const btn = document.getElementById('load-more-btn');
  if (btn) {
    btn.addEventListener('click', function() {
      let page = parseInt(this.getAttribute('data-page')) || 1;

      // ← ĐÃ SỬA: dùng /view_book và id=
      fetch('${pageContext.request.contextPath}/view_book?action=loadMore&id=${book.bookId}&page=' + page)
              .then(response => response.text())
              .then(html => {
                document.getElementById('reviews-list').insertAdjacentHTML('beforeend', html);
                page++;
                this.setAttribute('data-page', page);

                const loaded = document.querySelectorAll('#reviews-list .review-item').length;
                if (loaded >= ${totalReviews}) {
                  this.style.display = 'none';
                }
              });
    });
  }

  function addToCart(bookId) {
    const quantity = document.getElementById('quantity').value;
    alert('Added book ID ' + bookId + ' (quantity: ' + quantity + ') to cart!');
  }

  function buyNow(bookId) {
    const quantity = document.getElementById('quantity').value;
    alert('Proceeding to checkout: book ID ' + bookId + ' (quantity: ' + quantity + ')');
  }
</script>

</body>
</html>