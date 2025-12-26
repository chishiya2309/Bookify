<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>${book.title} - Bookify</title>
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
    .my-review-box { background: #e3f2fd; padding: 20px; border-radius: 8px; margin-bottom: 30px; border: 2px solid #2196f3; position: relative; }
    .my-review-box .delete-btn { position: absolute; top: 10px; right: 10px; background: #f44336; color: white; border: none; padding: 6px 12px; border-radius: 4px; cursor: pointer; font-size: 14px; }
    .my-review-box .delete-btn:hover { background: #d32f2f; }
    .review-item { background: #f8f9fa; padding: 20px; border-radius: 8px; margin-bottom: 20px; border: 1px solid #eee; }
    .review-item .rating { color: #f39c12; font-size: 20px; margin-left: 8px; }
    .review-item .date { color: #666; font-size: 14px; margin-left: 10px; }
    .review-item .headline { margin: 10px 0; font-weight: bold; color: #2c3e50; }

    #load-more-btn { display: block; margin: 30px auto; padding: 12px 30px; background: #28a745; color: white; border: none; font-size: 16px; cursor: pointer; border-radius: 6px; }
    #load-more-btn:hover { background: #218838; }

    .write-review-box { background:#f0f8ff; padding:20px; border-radius:8px; margin-bottom:30px; border:1px solid #bee5eb; }
  </style>
</head>
<body>

<c:choose>
  <c:when test="${not empty sessionScope.customer or not empty sessionScope.userEmail}">
    <jsp:include page="/customer/header_customer.jsp"/>
  </c:when>
  <c:otherwise>
    <jsp:include page="/customer/header_sign_in.jsp"/>
  </c:otherwise>
</c:choose>

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

      <div class="meta-info"><strong>Author(s):</strong>
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

      <div class="price"><fmt:formatNumber value="${book.price}" type="currency" currencySymbol="$"/></div>

      <!-- Giữ nguyên đơn giản như cũ -->
      <div class="meta-info"><strong>In stock:</strong> ${book.quantityInStock} copies</div>

      <div class="description"><strong>Description:</strong><br><c:out value="${book.description}"/></div>

      <div class="quantity-section">
        <label for="quantity"><strong>Quantity:</strong></label>
        <input type="number" id="quantity" class="quantity-input" min="1" max="${book.quantityInStock}" value="1">
      </div>

      <div class="action-buttons">
        <button class="add-to-cart-btn" onclick="handleAddToCart(${book.bookId})">Add to Cart</button>
        <button class="buy-now-btn" onclick="handleBuyNow(${book.bookId})">Buy Now</button>
      </div>

      <div class="shipping-info">
        <p>Free shipping on orders over $50</p>
        <p>Cash on delivery (COD), credit card, bank transfer</p>
        <p>7-day return if defective or incorrect</p>
        <p>24/7 customer support</p>
      </div>
    </div>
  </div>

  <!-- PHẦN REVIEWS (giữ nguyên như trước) -->
  <div class="reviews-section">
    <h2>Đánh giá sản phẩm (${totalReviews})</h2>

    <c:if test="${not empty currentCustomer && not empty customerReview}">
      <div class="my-review-box">
        <strong>Đánh giá của bạn</strong>
        <button class="delete-btn" onclick="deleteMyReview(${customerReview.reviewId})">Xóa đánh giá</button>
        <br><br>
        <span class="rating">
            <c:forEach begin="1" end="${customerReview.rating}">★</c:forEach>
          </span>
        <span class="date">(${customerReview.reviewDate})</span><br>
        <c:if test="${not empty customerReview.headline}">
          <h4 class="headline"><c:out value="${customerReview.headline}"/></h4>
        </c:if>
        <p><c:out value="${customerReview.comment}"/></p>
      </div>
    </c:if>

    <c:if test="${not empty currentCustomer && empty customerReview}">
      <div class="write-review-box">
        <h3>Viết đánh giá của bạn</h3>
        <form action="${pageContext.request.contextPath}/review" method="post">
          <input type="hidden" name="action" value="create"/>
          <input type="hidden" name="bookId" value="${book.bookId}"/>
          <div style="margin:15px 0;">
            <label><strong>Đánh giá:</strong></label><br>
            <select name="rating" required style="font-size:18px; padding:8px;">
              <option value="">-- Chọn số sao --</option>
              <option value="5">5 ★ Tuyệt vời</option>
              <option value="4">4 ★ Tốt</option>
              <option value="3">3 ★ Trung bình</option>
              <option value="2">2 ★ Tạm được</option>
              <option value="1">1 ★ Kém</option>
            </select>
          </div>
          <div style="margin:15px 0;">
            <label><strong>Tiêu đề:</strong></label><br>
            <input type="text" name="headline" maxlength="255" style="width:100%; padding:10px;" placeholder="Tóm tắt cảm nhận"/>
          </div>
          <div style="margin:15px 0;">
            <label><strong>Nội dung:</strong></label><br>
            <textarea name="comment" rows="6" maxlength="2000" required style="width:100%; padding:10px;" placeholder="Chia sẻ trải nghiệm của bạn..."></textarea>
          </div>
          <button type="submit" style="padding:12px 24px; background:#007bff; color:white; border:none; border-radius:6px;">Gửi đánh giá</button>
        </form>
      </div>
    </c:if>

    <c:if test="${empty currentCustomer}">
      <p style="background:#fff3cd; padding:15px; border-radius:8px; margin-bottom:30px;">
        <a href="${pageContext.request.contextPath}/customer/login.jsp">Đăng nhập</a> để viết đánh giá.
      </p>
    </c:if>

    <h3>Đánh giá từ khách hàng khác</h3>
    <div id="reviews-list">
      <c:forEach items="${reviews}" var="r">
        <div class="review-item">
          <strong><c:out value="${r.customer.fullName}"/></strong>
          <span class="rating">
              <c:forEach begin="1" end="${r.rating}">★</c:forEach>
            </span>
          <span class="date">(${r.reviewDate})</span><br>
          <c:if test="${not empty r.headline}">
            <h4 class="headline"><c:out value="${r.headline}"/></h4>
          </c:if>
          <p><c:out value="${r.comment}"/></p>
        </div>
      </c:forEach>
    </div>

    <c:if test="${loadedCount < totalReviews - (not empty customerReview ? 1 : 0)}">
      <button id="load-more-btn" data-page="1">Xem thêm đánh giá</button>
    </c:if>
  </div>
</div>

<jsp:include page="/customer/footer_customer.jsp"></jsp:include>

<script>
  function getQuantity() {
    const qtyInput = document.getElementById('quantity');
    let qty = parseInt(qtyInput.value) || 1;
    const stock = ${book.quantityInStock};

    if (qty < 1) {
      qty = 1;
      qtyInput.value = 1;
    }
    if (qty > stock) {
      qty = stock;
      qtyInput.value = stock;
      alert('Chỉ còn ' + stock + ' cuốn trong kho. Đã điều chỉnh số lượng về tối đa.');
    }
    return qty;
  }

  function addToCart(bookId, quantity, redirectAfter = false) {
    const stock = ${book.quantityInStock};

    if (quantity > stock) {
      alert('Số lượng yêu cầu (' + quantity + ') vượt quá tồn kho (' + stock + ' cuốn). Vui lòng giảm số lượng.');
      return;
    }

    fetch('${pageContext.request.contextPath}/customer/cart', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: 'action=add&bookId=' + bookId + '&quantity=' + quantity
    })
            .then(response => {
              if (!response.ok) throw new Error('Lỗi server');
            })
            .then(() => {
              alert('Đã thêm "' + '${book.title}' + '" (x' + quantity + ') vào giỏ hàng thành công!');
              if (redirectAfter) {
                window.location.href = '${pageContext.request.contextPath}/customer/cart';
              }
            })
            .catch(err => {
              alert('Không thể thêm vào giỏ hàng: ' + err.message + '\n(Có thể sách đã hết hàng do người khác mua trước đó)');
            });
  }

  function handleAddToCart(bookId) {
    const quantity = getQuantity();
    addToCart(bookId, quantity, false);
  }

  function handleBuyNow(bookId) {
    const quantity = getQuantity();
    addToCart(bookId, quantity, true);
  }

  const loadMoreBtn = document.getElementById('load-more-btn');
  if (loadMoreBtn) {
    loadMoreBtn.addEventListener('click', function() {
      let page = parseInt(this.getAttribute('data-page')) || 1;
      fetch('${pageContext.request.contextPath}/view_book?action=loadMore&id=${book.bookId}&page=' + page)
              .then(response => response.text())
              .then(html => {
                if (html.trim()) {
                  document.getElementById('reviews-list').insertAdjacentHTML('beforeend', html);
                  page++;
                  this.setAttribute('data-page', page);
                } else {
                  this.style.display = 'none';
                }
              });
    });
  }

  function deleteMyReview(reviewId) {
    if (confirm('Bạn có chắc chắn muốn xóa đánh giá này không? Hành động này không thể hoàn tác.')) {
      fetch('${pageContext.request.contextPath}/review', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'action=delete&reviewId=' + reviewId
      }).then(() => location.reload());
    }
  }
</script>

</body>
</html>