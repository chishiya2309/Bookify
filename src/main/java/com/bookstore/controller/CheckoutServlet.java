package com.bookstore.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.bookstore.data.DBUtil;
import com.bookstore.config.ShippingConfig;
import com.bookstore.dao.AddressDAO;
import com.bookstore.dao.BookDAO;
import com.bookstore.model.Address;
import com.bookstore.model.Book;
import com.bookstore.model.CartItem;
import com.bookstore.model.Customer;
import com.bookstore.model.Order;
import com.bookstore.model.Payment;
import com.bookstore.model.ShoppingCart;
import com.bookstore.service.CustomerServices;
import com.bookstore.service.JwtAuthHelper;
import com.bookstore.service.JwtUtil;
import com.bookstore.service.OrderService;
import com.bookstore.service.PaymentService;
import com.bookstore.service.ShoppingCartServices;

import java.math.BigDecimal;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * CheckoutServlet - xử lý quá trình thanh toán
 * GET: Hiển thị trang thanh toán với giỏ hàng và thông tin khách hàng
 * POST: Xử lý đơn hàng và thanh toán
 */
@WebServlet(name = "CheckoutServlet", urlPatterns = { "/customer/checkout" })
public class CheckoutServlet extends HttpServlet {

    private static final String GUEST_CART_KEY = "guestCart";
    private ShoppingCartServices cartService;
    private PaymentService paymentService;
    private OrderService orderService;
    private CustomerServices customerServices;
    private AddressDAO addressDAO;

    @Override
    public void init() throws ServletException {
        cartService = new ShoppingCartServices();
        paymentService = new PaymentService();
        orderService = new OrderService();
        customerServices = new CustomerServices();
        addressDAO = new AddressDAO();
    }

    /**
     * Hiển thị trang thanh toán
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Nếu là admin thì redirect về trang admin
        String token = JwtAuthHelper.extractJwtToken(request);
        if (token != null && JwtUtil.validateToken(token)) {
            String role = JwtUtil.extractRole(token);
            if ("ADMIN".equals(role)) {
                response.sendRedirect(request.getContextPath() + "/admin/");
                return;
            }
        }

        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");

        // Khôi phục khách hàng từ JWT nếu không có trong phiên
        if (customer == null) {
            customer = JwtAuthHelper.restoreCustomerFromJwt(request, session, DBUtil.getEmFactory());
        }

        // Nếu không có khách hàng, redirect đến trang login với tham số redirect
        if (customer == null) {
            session.setAttribute("checkoutMessage", "Vui lòng đăng nhập để tiếp tục thanh toán");

            response.sendRedirect(request.getContextPath() + "/customer/login.jsp?redirect=" +
                    request.getContextPath() + "/customer/checkout");
            return;
        }

        // Tải giỏ hàng
        ShoppingCart cart = loadCart(session, customer);

        // Kiểm tra giỏ hàng có rỗng không
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            request.setAttribute("cart", cart);
            request.setAttribute("isGuest", false);
            request.getRequestDispatcher("/customer/checkout.jsp").forward(request, response);
            return;
        }

        // Tính toán tổng tiền giỏ hàng
        cartService.calculateCartTotals(cart);

        // Kiểm tra tồn kho (không chặn, chỉ cảnh báo cho người dùng)
        List<String> stockWarnings = validateCartStock(cart);
        if (!stockWarnings.isEmpty()) {
            request.setAttribute("stockWarnings", stockWarnings);
        }

        // Xác thực và cập nhật giá (phát hiện sự thay đổi giá kể từ khi mặt hàng được
        // thêm vào)
        List<String> priceChanges = validateAndUpdateCartPrices(cart);
        if (!priceChanges.isEmpty()) {
            request.setAttribute("priceChanges", priceChanges);
        }

        request.setAttribute("cart", cart);
        request.setAttribute("isGuest", false);
        request.setAttribute("user", customer);
        request.setAttribute("userEmail", customer.getEmail());

        // Tải số địa chỉ của khách hàng
        com.bookstore.dao.AddressDAO addressDAO = new com.bookstore.dao.AddressDAO();
        java.util.List<com.bookstore.model.Address> addresses = addressDAO.findByCustomerId(customer.getUserId());
        request.setAttribute("customerAddresses", addresses);

        // ========== TÍNH TIỀN GIAO HÀNG ==========
        // Tính phí giao hàng dựa trên địa chỉ mặc định hoặc địa chỉ đầu tiên
        BigDecimal subtotal = cart.getTotalAmount();
        BigDecimal shippingFee = BigDecimal.ZERO;
        String shippingRegion = "";

        Address defaultAddress = null;
        if (addresses != null && !addresses.isEmpty()) {
            // Tìm địa chỉ mặc định hoặc sử dụng địa chỉ đầu tiên
            defaultAddress = addresses.stream()
                    .filter(a -> Boolean.TRUE.equals(a.getIsDefault()))
                    .findFirst()
                    .orElse(addresses.get(0));

            shippingFee = ShippingConfig.calculateShippingFee(
                    defaultAddress.getProvince(), subtotal);
            shippingRegion = ShippingConfig.getRegionName(defaultAddress.getProvince());
        }

        BigDecimal grandTotal = subtotal.add(shippingFee);
        BigDecimal freeShippingNeeded = ShippingConfig.getAmountForFreeShipping(subtotal);

        request.setAttribute("subtotal", subtotal);
        request.setAttribute("shippingFee", shippingFee);
        request.setAttribute("shippingRegion", shippingRegion);
        request.setAttribute("grandTotal", grandTotal);
        request.setAttribute("freeShippingThreshold", ShippingConfig.FREE_SHIPPING_THRESHOLD);
        request.setAttribute("freeShippingNeeded", freeShippingNeeded);
        // ========== TÍNH TIỀN GIAO HÀNG ==========

        // Tải danh mục cho header
        request.setAttribute("listCategories", customerServices.listAllCategories());

        // Hiển thị thông báo thanh toán nếu có
        String checkoutMessage = (String) session.getAttribute("checkoutMessage");
        if (checkoutMessage != null) {
            request.setAttribute("message", checkoutMessage);
            session.removeAttribute("checkoutMessage");
        }

        // Forward đến trang thanh toán
        request.getRequestDispatcher("/customer/checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");

        if (customer == null) {
            customer = JwtAuthHelper.restoreCustomerFromJwt(request, session, DBUtil.getEmFactory());
        }

        // Khách hàng phải đăng nhập mới có thể thực hiện thanh toán
        if (customer == null) {
            session.setAttribute("checkoutMessage", "Vui lòng đăng nhập để tiếp tục thanh toán");
            response.sendRedirect(request.getContextPath() + "/customer/login?redirect=checkout");
            return;
        }

        try {
            ShoppingCart cart = loadCart(session, customer);

            if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
                request.setAttribute("error", "Giỏ hàng trống. Vui lòng thêm sản phẩm trước khi thanh toán.");
                request.getRequestDispatcher("/customer/checkout.jsp").forward(request, response);
                return;
            }

            // Lấy ID địa chỉ đã chọn
            String selectedAddressIdStr = request.getParameter("selectedAddressId");
            if (selectedAddressIdStr == null || selectedAddressIdStr.trim().isEmpty()) {
                request.setAttribute("error", "Vui lòng chọn địa chỉ giao hàng");
                request.setAttribute("cart", cart);
                request.setAttribute("user", customer);
                request.getRequestDispatcher("/customer/checkout.jsp").forward(request, response);
                return;
            }

            // Lấy phương thức thanh toán
            String paymentMethodStr = request.getParameter("paymentMethod");
            if (paymentMethodStr == null || paymentMethodStr.trim().isEmpty()) {
                request.setAttribute("error", "Vui lòng chọn phương thức thanh toán");
                request.setAttribute("cart", cart);
                request.setAttribute("user", customer);
                request.getRequestDispatcher("/customer/checkout.jsp").forward(request, response);
                return;
            }

            Payment.PaymentMethod paymentMethod = Payment.PaymentMethod.valueOf(paymentMethodStr);

            // Lấy thông tin địa chỉ giao hàng
            Integer selectedAddressId = Integer.parseInt(selectedAddressIdStr);
            Address shippingAddress = addressDAO.findById(selectedAddressId);

            if (shippingAddress == null) {
                request.setAttribute("error", "Địa chỉ giao hàng không tồn tại");
                request.setAttribute("cart", cart);
                request.setAttribute("user", customer);
                request.getRequestDispatcher("/customer/checkout.jsp").forward(request, response);
                return;
            }

            // Kiểm tra địa chỉ thuộc về khách hàng (kiểm tra an toàn)
            if (!shippingAddress.getCustomer().getUserId().equals(customer.getUserId())) {
                request.setAttribute("error", "Địa chỉ không hợp lệ");
                request.setAttribute("cart", cart);
                request.setAttribute("user", customer);
                request.getRequestDispatcher("/customer/checkout.jsp").forward(request, response);
                return;
            }

            // Lấy mã giảm giá nếu có
            String voucherCode = request.getParameter("voucherCode");

            // Tạo đơn hàng từ giỏ hàng
            Order order = orderService.createOrderFromCart(
                    customer,
                    shippingAddress,
                    cart,
                    paymentMethodStr,
                    voucherCode);

            // Tạo thanh toán
            String gateway = paymentMethod == Payment.PaymentMethod.SEPAY ? "Sepay" : null;
            Payment payment = paymentService.createPayment(order, paymentMethod, gateway);

            // Thanh toán
            PaymentService.PaymentResult result = paymentService.processPayment(payment, new java.util.HashMap<>());

            if (result.isSuccess()) {
                // Chỉ xóa cart ngay cho COD (thanh toán được coi là "hoàn tất" về mặt logic)
                // BANK_TRANSFER: cart sẽ được xóa khi webhook xác nhận đã nhận tiền
                if (paymentMethod == Payment.PaymentMethod.COD) {
                    cartService.clearCart(cart);
                    session.setAttribute("cart", cart);
                }

                if (result.requiresRedirect()) {
                    // Redirect đến cổng thanh toán
                    response.sendRedirect(result.getRedirectUrl());
                } else if (paymentMethod == Payment.PaymentMethod.BANK_TRANSFER) {
                    // Chuyển khoản ngân hàng - redirect đến trang thanh toán QRCode
                    // Cart sẽ được xóa khi webhook xác nhận đã nhận tiền
                    session.setAttribute("orderConfirmation",
                            "Vui lòng quét mã QR để thanh toán đơn hàng #" + order.getOrderId());
                    response.sendRedirect(
                            request.getContextPath() + "/customer/bank-transfer-payment?orderId=" + order.getOrderId());
                } else {
                    // Thanh toán thành công (COD) - redirect đến trang xác nhận
                    session.setAttribute("orderConfirmation",
                            "Đặt hàng thành công! Mã đơn hàng: " + order.getOrderId());
                    response.sendRedirect(
                            request.getContextPath() + "/customer/order-confirmation?orderId=" + order.getOrderId());
                }
            } else {
                // Thanh toán thất bại
                request.setAttribute("error", "Thanh toán thất bại: " + result.getMessage());
                request.setAttribute("cart", cart);
                request.setAttribute("user", customer);
                request.getRequestDispatcher("/customer/checkout.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Đã xảy ra lỗi trong quá trình thanh toán: " + e.getMessage());
            request.getRequestDispatcher("/customer/checkout.jsp").forward(request, response);
        }
    }

    private ShoppingCart loadCart(HttpSession session, Customer customer) {
        ShoppingCart cart;

        if (customer == null) {
            // Khách hàng không đăng nhập - load từ session
            cart = (ShoppingCart) session.getAttribute(GUEST_CART_KEY);
            if (cart == null) {
                cart = cartService.getOrCreateGuestCart();
            }
        } else {
            // Khách hàng đã đăng nhập - load từ database
            cart = cartService.getOrCreateCartForCustomer(customer);
        }

        return cart;
    }

    @Override
    public String getServletInfo() {
        return "Checkout Servlet - Handle checkout process";
    }

    /**
     * Kiểm tra số lượng sách trong kho trước khi thanh toán (kiểm tra không đồng
     * bộ).
     * Trả về danh sách thông báo cảnh báo cho các mục có số lượng sách không đủ.
     * Đây là một kiểm tra mềm - thực thi thực sự xảy ra trong OrderService với
     * khóa.
     */
    private List<String> validateCartStock(ShoppingCart cart) {
        List<String> warnings = new ArrayList<>();
        BookDAO bookDAO = new BookDAO();

        if (cart == null || cart.getItems() == null) {
            return warnings;
        }

        for (CartItem item : cart.getItems()) {
            Book book = item.getBook();
            if (book == null)
                continue;

            // Lấy số lượng sách trong kho từ database
            Book currentBook = bookDAO.findById(book.getBookId());
            if (currentBook == null) {
                warnings.add("Sản phẩm \"" + book.getTitle() + "\" không còn tồn tại");
                continue;
            }

            int currentStock = currentBook.getQuantityInStock();
            int requestedQty = item.getQuantity();

            if (currentStock <= 0) {
                warnings.add("Sản phẩm \"" + book.getTitle() + "\" đã hết hàng");
            } else if (currentStock < requestedQty) {
                warnings.add("Sản phẩm \"" + book.getTitle() + "\" chỉ còn "
                        + currentStock + " sản phẩm (bạn yêu cầu: " + requestedQty + ")");
            }
        }

        return warnings;
    }

    /**
     * Kiểm tra và cập nhật giá của các mục trong giỏ hàng đến giá hiện tại trong
     * database.
     * Trả về danh sách thông báo nếu giá đã thay đổi kể từ khi các mục được thêm
     * vào.
     * 
     * @param cart Giỏ hàng cần kiểm tra
     * @return Danh sách thông báo về thay đổi giá (trống nếu không có thay đổi)
     */
    private List<String> validateAndUpdateCartPrices(ShoppingCart cart) {
        List<String> priceChanges = new ArrayList<>();
        BookDAO bookDAO = new BookDAO();
        java.text.NumberFormat currencyFormat = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));

        if (cart == null || cart.getItems() == null) {
            return priceChanges;
        }

        java.math.BigDecimal totalDifference = java.math.BigDecimal.ZERO;

        for (CartItem item : cart.getItems()) {
            Book cartBook = item.getBook();
            if (cartBook == null)
                continue;

            // Lấy giá hiện tại từ database
            Book currentBook = bookDAO.findById(cartBook.getBookId());
            if (currentBook == null)
                continue;

            java.math.BigDecimal oldPrice = cartBook.getPrice();
            java.math.BigDecimal currentPrice = currentBook.getPrice();

            if (oldPrice == null || currentPrice == null)
                continue;

            // Kiểm tra nếu giá đã thay đổi
            if (oldPrice.compareTo(currentPrice) != 0) {
                java.math.BigDecimal priceDiff = currentPrice.subtract(oldPrice);
                java.math.BigDecimal itemDiff = priceDiff.multiply(java.math.BigDecimal.valueOf(item.getQuantity()));
                totalDifference = totalDifference.add(itemDiff);

                String changeType = priceDiff.compareTo(java.math.BigDecimal.ZERO) > 0 ? "tăng" : "giảm";
                String diffFormatted = currencyFormat.format(priceDiff.abs()) + "₫";

                priceChanges.add(String.format("Giá sản phẩm \"%s\" đã %s %s (từ %s₫ → %s₫)",
                        cartBook.getTitle(),
                        changeType,
                        diffFormatted,
                        currencyFormat.format(oldPrice),
                        currencyFormat.format(currentPrice)));

                // Cập nhật giá của mục trong giỏ hàng
                cartBook.setPrice(currentPrice);
            }
        }

        // Thêm tổng thay đổi nếu có thay đổi
        if (!priceChanges.isEmpty()) {
            String totalChangeType = totalDifference.compareTo(java.math.BigDecimal.ZERO) > 0 ? "tăng" : "giảm";
            priceChanges.add(String.format("📊 Tổng thay đổi: %s %s₫",
                    totalChangeType,
                    currencyFormat.format(totalDifference.abs())));

            cartService.calculateCartTotals(cart);
        }

        return priceChanges;
    }
}
