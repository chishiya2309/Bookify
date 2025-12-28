<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>${book.title} - Bookify</title>
  <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
  <style>
    .container { max-width: 1200px; margin: 0 auto; padding: 20px; }
    .book-detail { display: flex; flex-wrap: wrap; gap: 40px; margin-top: 20px; }
    .book-image { flex: 1; min-width: 300px; }

    /* IMAGE CAROUSEL */
    .carousel-container {
      position: relative;
      width: 100%;
      max-width: 500px;
      margin: 0 auto;
    }
    .carousel-main {
      position: relative;
      width: 100%;
      height: 500px;
      overflow: hidden;
      border: 1px solid #ddd;
      border-radius: 8px;
      background: #fff;
    }
    .carousel-main img {
      width: 100%;
      height: 100%;
      object-fit: contain;
      display: none;
    }
    .carousel-main img.active {
      display: block;
    }
    .carousel-btn {
      position: absolute;
      top: 50%;
      transform: translateY(-50%);
      background: rgba(0,0,0,0.5);
      color: white;
      border: none;
      padding: 15px;
      cursor: pointer;
      font-size: 20px;
      border-radius: 4px;
      z-index: 10;
    }
    .carousel-btn:hover {
      background: rgba(0,0,0,0.8);
    }
    .carousel-btn.prev { left: 10px; }
    .carousel-btn.next { right: 10px; }
    .carousel-thumbnails {
      display: flex;
      gap: 10px;
      margin-top: 15px;
      overflow-x: auto;
      padding: 5px 0;
    }
    .carousel-thumbnails img {
      width: 80px;
      height: 80px;
      object-fit: cover;
      border: 2px solid #ddd;
      border-radius: 4px;
      cursor: pointer;
      transition: all 0.3s;
    }
    .carousel-thumbnails img:hover {
      border-color: #007bff;
      transform: scale(1.05);
    }
    .carousel-thumbnails img.active {
      border-color: #007bff;
      box-shadow: 0 0 8px rgba(0,123,255,0.5);
    }

    .no-image-detail {
      width: 300px;
      height: 400px;
      display: flex;
      align-items: center;
      justify-content: center;
      background-color: #f0f0f0;
      color: #999;
      font-size: 18px;
      font-weight: 500;
      border: 1px solid #ddd;
      border-radius: 8px;
    }
    .book-info { flex: 2; min-width: 300px; text-align: left; }
    .book-info h1 { font-size: 32px; margin-bottom: 15px; text-align: left; }
    .price { font-size: 28px; color: #b12704; font-weight: bold; margin: 20px 0; text-align: left; }
    .meta-info { margin: 12px 0; font-size: 16px; color: #333; text-align: left; }
    .description { margin-top: 25px; line-height: 1.8; font-size: 16px; text-align: left; }
    .average-rating { font-size: 18px; color: #f39c12; margin: 15px 0; font-weight: bold; text-align: left; }
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

    .reviews-section { margin-top: 60px; border-top: 2px solid #eee; padding-top: 30px; text-align: left; }
    .reviews-section h2 { margin-bottom: 25px; color: #333; text-align: left; }
    .reviews-section h3 { text-align: left; }
    .my-review-box { background: #e3f2fd; padding: 20px; border-radius: 8px; margin-bottom: 30px; border: 2px solid #2196f3; position: relative; text-align: left; }
    .my-review-box .delete-btn { position: absolute; top: 10px; right: 10px; background: #f44336; color: white; border: none; padding: 6px 12px; border-radius: 4px; cursor: pointer; font-size: 14px; }
    .my-review-box .delete-btn:hover { background: #d32f2f; }
    .review-item { background: #f8f9fa; padding: 20px; border-radius: 8px; margin-bottom: 20px; border: 1px solid #eee; text-align: left; }
    .review-item .rating { color: #f39c12; font-size: 20px; margin-left: 8px; }
    .review-item .date { color: #666; font-size: 14px; margin-left: 10px; }
    .review-item .headline { margin: 10px 0; font-weight: bold; color: #2c3e50; }

    #load-more-btn { display: block; margin: 30px auto; padding: 12px 30px; background: #28a745; color: white; border: none; font-size: 16px; cursor: pointer; border-radius: 6px; }
    #load-more-btn:hover { background: #218838; }

    .write-review-box { background:#f0f8ff; padding:20px; border-radius:8px; margin-bottom:30px; border:1px solid #bee5eb; text-align: left; }

    /* THÔNG BÁO CHƯA MUA SÁCH */
    .purchase-required-notice {
      background: #fff3cd;
      border: 1px solid #ffc107;
      padding: 15px 20px;
      border-radius: 8px;
      margin-bottom: 30px;
      color: #856404;
      font-size: 15px;
      text-align: left;
    }
    .purchase-required-notice strong {
      color: #d39e00;
    }
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
    <!-- IMAGE CAROUSEL -->
    <div class="book-image">
      <c:choose>
        <c:when test="${not empty book.images && book.images.size() > 0}">
          <div class="carousel-container">
            <div class="carousel-main">
              <!-- Sắp xếp: ảnh primary đầu tiên, rồi đến các ảnh khác -->
              <c:set var="imageIndex" value="0" />
              <c:forEach items="${book.images}" var="img" varStatus="status">
                <c:if test="${img.isPrimary}">
                  <img src="${img.url}" alt="${book.title}" class="${imageIndex == 0 ? 'active' : ''}" data-index="${imageIndex}" onerror="this.style.display='none'" />
                  <c:set var="imageIndex" value="${imageIndex + 1}" />
                </c:if>
              </c:forEach>
              <c:forEach items="${book.images}" var="img" varStatus="status">
                <c:if test="${!img.isPrimary}">
                  <img src="${img.url}" alt="${book.title}" class="${imageIndex == 0 ? 'active' : ''}" data-index="${imageIndex}" onerror="this.style.display='none'" />
                  <c:set var="imageIndex" value="${imageIndex + 1}" />
                </c:if>
              </c:forEach>

              <c:if test="${book.images.size() > 1}">
                <button class="carousel-btn prev" onclick="changeSlide(-1)">❮</button>
                <button class="carousel-btn next" onclick="changeSlide(1)">❯</button>
              </c:if>
            </div>

            <c:if test="${book.images.size() > 1}">
              <div class="carousel-thumbnails">
                <c:set var="thumbIndex" value="0" />
                <c:forEach items="${book.images}" var="img" varStatus="status">
                  <c:if test="${img.isPrimary}">
                    <img src="${img.url}" alt="Thumbnail" class="${thumbIndex == 0 ? 'active' : ''}" onclick="goToSlide(${thumbIndex})" onerror="this.style.display='none'" />
                    <c:set var="thumbIndex" value="${thumbIndex + 1}" />
                  </c:if>
                </c:forEach>
                <c:forEach items="${book.images}" var="img" varStatus="status">
                  <c:if test="${!img.isPrimary}">
                    <img src="${img.url}" alt="Thumbnail" class="${thumbIndex == 0 ? 'active' : ''}" onclick="goToSlide(${thumbIndex})" onerror="this.style.display='none'" />
                    <c:set var="thumbIndex" value="${thumbIndex + 1}" />
                  </c:if>
                </c:forEach>
              </div>
            </c:if>
          </div>
        </c:when>
        <c:otherwise>
          <div class="no-image-detail">No Image Available</div>
        </c:otherwise>
      </c:choose>
    </div>

    <div class="book-info">
      <h1>${book.title}</h1>
      <div class="meta-info">
        <strong>Tác giả:</strong>
        <c:forEach items="${book.authors}" var="author" varStatus="status">
          ${author.name}<c:if test="${!status.last}">, </c:if>
        </c:forEach>
      </div>
      <div class="meta-info"><strong>Thể loại:</strong> ${book.category.name}</div>
      <div class="meta-info"><strong>Nhà xuất bản:</strong> ${book.publisher.name}</div>

      <div class="average-rating">
        <c:set var="fullStars" value="${avgRating.intValue()}"/>
        <c:set var="hasHalfStar" value="${avgRating - fullStars >= 0.3}"/>
        <c:forEach begin="1" end="${fullStars}">★</c:forEach>
        <c:if test="${hasHalfStar}">½</c:if>
        (${avgRating} / 5) - ${totalReviews} đánh giá
      </div>

      <div class="price">
        <fmt:formatNumber value="${book.price}" pattern="#,###"/>₫
      </div>

      <div class="meta-info"><strong>Còn lại:</strong> ${book.quantityInStock} cuốn</div>

      <div class="description">
        <strong>Mô tả:</strong><br>
        <c:out value="${book.description}"/>
      </div>

      <div class="quantity-section">
        <label for="quantity"><strong>Số lượng:</strong></label>
        <input type="number" id="quantity" class="quantity-input" min="1"
               max="${book.quantityInStock}" value="1">
      </div>

      <div class="action-buttons">
        <button class="add-to-cart-btn" onclick="addToCart(${book.bookId})">
          🛒 Thêm vào giỏ hàng
        </button>
        <button class="buy-now-btn" onclick="buyNow(${book.bookId})">
          ⚡ Mua ngay
        </button>
      </div>

      <div class="shipping-info">
        <p>🚚 Miễn phí vận chuyển cho đơn hàng từ 300.000₫</p>
        <p>💳 Thanh toán: COD, thẻ tín dụng, chuyển khoản</p>
        <p>↩️ Đổi trả trong 7 ngày nếu lỗi hoặc sai sản phẩm</p>
        <p>📞 Hỗ trợ khách hàng 24/7</p>
      </div>
    </div>
  </div>

  <!-- PHẦN REVIEWS -->
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

    <c:if test="${empty currentCustomer}">
      <div class="purchase-required-notice">
        <strong>📝 Viết đánh giá:</strong> Vui lòng <a href="${pageContext.request.contextPath}/customer/login.jsp" style="color:#007bff; text-decoration:underline;">đăng nhập</a> để viết đánh giá cho sản phẩm này.
      </div>
    </c:if>

    <%-- Đã đăng nhập + chưa đánh giá + đã mua sách với đơn DELIVERED --%>
    <c:if test="${not empty currentCustomer && empty customerReview && canReview}">
      <%-- Xóa error cũ nếu có vì user đã đủ điều kiện review --%>
      <c:remove var="error" scope="session" />

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

    <%-- Đã đăng nhập + chưa đánh giá + CHƯA mua sách hoặc đơn chưa giao --%>
    <c:if test="${not empty currentCustomer && empty customerReview && !canReview}">
      <div class="purchase-required-notice">
        <strong>📝 Viết đánh giá:</strong> Bạn cần mua sách này và đợi đơn hàng được giao thành công (trạng thái DELIVERED) trước khi có thể viết đánh giá.
      </div>
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

    <c:if test="${loadedCount < totalReviews}">
      <button id="load-more-btn" data-page="1">Xem thêm đánh giá</button>
    </c:if>
  </div>
</div>

<jsp:include page="/customer/footer_customer.jsp"></jsp:include>

<script>
  // IMAGE CAROUSEL
  let currentSlide = 0;
  const slides = document.querySelectorAll('.carousel-main img');
  const thumbnails = document.querySelectorAll('.carousel-thumbnails img');

  function showSlide(index) {
    if (slides.length === 0) return;

    if (index >= slides.length) currentSlide = 0;
    if (index < 0) currentSlide = slides.length - 1;

    slides.forEach((slide, i) => {
      slide.classList.remove('active');
      if (thumbnails[i]) thumbnails[i].classList.remove('active');
    });

    slides[currentSlide].classList.add('active');
    if (thumbnails[currentSlide]) thumbnails[currentSlide].classList.add('active');
  }

  function changeSlide(direction) {
    currentSlide += direction;
    showSlide(currentSlide);
  }

  function goToSlide(index) {
    currentSlide = index;
    showSlide(currentSlide);
  }

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

                  const loadedNow = ${loadedCount} + (page * 5);
                  if (loadedNow >= ${totalReviews}) {
                    this.style.display = 'none';
                  }
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

  function addToCart(bookId) {
    const quantity = document.getElementById('quantity').value;
    const btn = event.target.closest('button');

    if (btn) {
      btn.disabled = true;
      btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang thêm...';
    }

    fetch('${pageContext.request.contextPath}/api/cart/add', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      },
      body: 'bookId=' + bookId + '&quantity=' + quantity
    })
            .then(res => res.json())
            .then(data => {
              if (data.success) {
                showToast(data.message, 'success');

                if (typeof refreshMiniCart === 'function') {
                  refreshMiniCart();
                }
                if (typeof updateCartBadge === 'function') {
                  updateCartBadge();
                }
              } else {
                showToast(data.error || 'Không thể thêm vào giỏ hàng', 'error');
              }
            })
            .catch(err => {
              console.error('Add to cart error:', err);
              showToast('Đã xảy ra lỗi', 'error');
            })
            .finally(() => {
              if (btn) {
                btn.disabled = false;
                btn.innerHTML = '<i class="fas fa-cart-plus"></i> Thêm vào giỏ';
              }
            });
  }

  function showToast(message, type) {
    const existingToast = document.querySelector('.cart-toast');
    if (existingToast) existingToast.remove();

    const toast = document.createElement('div');
    toast.className = 'cart-toast ' + type;
    toast.innerHTML = (type === 'success' ? '<i class="fas fa-check-circle"></i> ' : '<i class="fas fa-exclamation-circle"></i> ') + message;
    toast.style.cssText = 'position: fixed; top: 80px; right: 20px; padding: 14px 20px; border-radius: 8px; z-index: 10000; color: white; font-weight: 500; box-shadow: 0 4px 20px rgba(0,0,0,0.2); animation: slideIn 0.3s ease;';
    toast.style.background = type === 'success' ? '#28a745' : '#dc3545';

    document.body.appendChild(toast);

    setTimeout(() => {
      toast.style.animation = 'slideOut 0.3s ease';
      setTimeout(() => toast.remove(), 300);
    }, 3000);
  }

  function buyNow(bookId) {
    const quantity = document.getElementById('quantity').value;

    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '${pageContext.request.contextPath}/customer/cart';

    const actionInput = document.createElement('input');
    actionInput.type = 'hidden';
    actionInput.name = 'action';
    actionInput.value = 'add';
    form.appendChild(actionInput);

    const bookInput = document.createElement('input');
    bookInput.type = 'hidden';
    bookInput.name = 'bookId';
    bookInput.value = bookId;
    form.appendChild(bookInput);

    const qtyInput = document.createElement('input');
    qtyInput.type = 'hidden';
    qtyInput.name = 'quantity';
    qtyInput.value = quantity;
    form.appendChild(qtyInput);

    const redirectInput = document.createElement('input');
    redirectInput.type = 'hidden';
    redirectInput.name = 'redirect';
    redirectInput.value = 'checkout';
    form.appendChild(redirectInput);

    document.body.appendChild(form);
    form.submit();
  }
</script>

</body>
</html>