package com.bookstore.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.bookstore.data.DBUtil;
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

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * CheckoutServlet - Handle checkout process
 * GET: Display checkout page with cart and customer info
 * POST: Process order and payment
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
     * Handles the HTTP GET method - Display checkout page
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check if admin - redirect to admin page
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

        // Restore customer from JWT if not in session
        if (customer == null) {
            customer = JwtAuthHelper.restoreCustomerFromJwt(request, session, DBUtil.getEmFactory());
        }

        // If still no customer, redirect to login with redirect parameter
        if (customer == null) {
            // Save message to session
            session.setAttribute("checkoutMessage", "Vui lòng đăng nhập để tiếp tục thanh toán");

            // Redirect to login with redirect parameter
            response.sendRedirect(request.getContextPath() + "/customer/login.jsp?redirect=" +
                    request.getContextPath() + "/customer/checkout");
            return;
        }

        // Load cart
        ShoppingCart cart = loadCart(session, customer);

        // Check if cart is empty
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            request.setAttribute("cart", cart);
            request.setAttribute("isGuest", false);
            request.getRequestDispatcher("/customer/checkout.jsp").forward(request, response);
            return;
        }

        // Calculate cart totals
        cartService.calculateCartTotals(cart);

        // Pre-validate stock (non-blocking warning for user)
        List<String> stockWarnings = validateCartStock(cart);
        if (!stockWarnings.isEmpty()) {
            request.setAttribute("stockWarnings", stockWarnings);
        }

        // Set cart and customer info to request
        request.setAttribute("cart", cart);
        request.setAttribute("isGuest", false);
        request.setAttribute("user", customer);
        request.setAttribute("userEmail", customer.getEmail());

        // Load customer addresses
        com.bookstore.dao.AddressDAO addressDAO = new com.bookstore.dao.AddressDAO();
        java.util.List<com.bookstore.model.Address> addresses = addressDAO.findByCustomerId(customer.getUserId());
        request.setAttribute("customerAddresses", addresses);

        // Set categories for header
        request.setAttribute("listCategories", customerServices.listAllCategories());

        // Display checkout message if exists
        String checkoutMessage = (String) session.getAttribute("checkoutMessage");
        if (checkoutMessage != null) {
            request.setAttribute("message", checkoutMessage);
            session.removeAttribute("checkoutMessage");
        }

        // Forward to checkout page
        request.getRequestDispatcher("/customer/checkout.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP POST method - Process checkout
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");

        // Restore customer from JWT if not in session
        if (customer == null) {
            customer = JwtAuthHelper.restoreCustomerFromJwt(request, session, DBUtil.getEmFactory());
        }

        // Guest users must login to checkout
        if (customer == null) {
            session.setAttribute("checkoutMessage", "Vui lòng đăng nhập để tiếp tục thanh toán");
            response.sendRedirect(request.getContextPath() + "/customer/login?redirect=checkout");
            return;
        }

        try {
            // Load cart
            ShoppingCart cart = loadCart(session, customer);

            if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
                request.setAttribute("error", "Giỏ hàng trống. Vui lòng thêm sản phẩm trước khi thanh toán.");
                request.getRequestDispatcher("/customer/checkout.jsp").forward(request, response);
                return;
            }

            // Get selected address ID
            String selectedAddressIdStr = request.getParameter("selectedAddressId");
            if (selectedAddressIdStr == null || selectedAddressIdStr.trim().isEmpty()) {
                request.setAttribute("error", "Vui lòng chọn địa chỉ giao hàng");
                request.setAttribute("cart", cart);
                request.setAttribute("user", customer);
                request.getRequestDispatcher("/customer/checkout.jsp").forward(request, response);
                return;
            }

            // Get payment method
            String paymentMethodStr = request.getParameter("paymentMethod");
            if (paymentMethodStr == null || paymentMethodStr.trim().isEmpty()) {
                request.setAttribute("error", "Vui lòng chọn phương thức thanh toán");
                request.setAttribute("cart", cart);
                request.setAttribute("user", customer);
                request.getRequestDispatcher("/customer/checkout.jsp").forward(request, response);
                return;
            }

            Payment.PaymentMethod paymentMethod = Payment.PaymentMethod.valueOf(paymentMethodStr);

            // Load selected address
            Integer selectedAddressId = Integer.parseInt(selectedAddressIdStr);
            Address shippingAddress = addressDAO.findById(selectedAddressId);

            if (shippingAddress == null) {
                request.setAttribute("error", "Địa chỉ giao hàng không tồn tại");
                request.setAttribute("cart", cart);
                request.setAttribute("user", customer);
                request.getRequestDispatcher("/customer/checkout.jsp").forward(request, response);
                return;
            }

            // Validate address belongs to customer (security check)
            if (!shippingAddress.getCustomer().getUserId().equals(customer.getUserId())) {
                request.setAttribute("error", "Địa chỉ không hợp lệ");
                request.setAttribute("cart", cart);
                request.setAttribute("user", customer);
                request.getRequestDispatcher("/customer/checkout.jsp").forward(request, response);
                return;
            }

            // Create order from cart using OrderService
            Order order = orderService.createOrderFromCart(
                    customer,
                    shippingAddress,
                    cart,
                    paymentMethodStr);

            // Create payment
            String gateway = paymentMethod == Payment.PaymentMethod.SEPAY ? "Sepay" : null;
            Payment payment = paymentService.createPayment(order, paymentMethod, gateway);

            // Process payment
            PaymentService.PaymentResult result = paymentService.processPayment(payment, new java.util.HashMap<>());

            if (result.isSuccess()) {
                // Chỉ xóa cart ngay cho COD (thanh toán được coi là "hoàn tất" về mặt logic)
                // BANK_TRANSFER: cart sẽ được xóa khi webhook xác nhận đã nhận tiền
                if (paymentMethod == Payment.PaymentMethod.COD) {
                    cartService.clearCart(cart);
                    session.setAttribute("cart", cart);
                }

                if (result.requiresRedirect()) {
                    // Redirect to payment gateway
                    response.sendRedirect(result.getRedirectUrl());
                } else if (paymentMethod == Payment.PaymentMethod.BANK_TRANSFER) {
                    // Bank transfer - redirect to QR payment page
                    // Cart will be cleared after payment confirmed via webhook
                    session.setAttribute("orderConfirmation",
                            "Vui lòng quét mã QR để thanh toán đơn hàng #" + order.getOrderId());
                    response.sendRedirect(
                            request.getContextPath() + "/customer/bank-transfer-payment?orderId=" + order.getOrderId());
                } else {
                    // Payment completed (COD) - redirect to confirmation
                    session.setAttribute("orderConfirmation",
                            "Đặt hàng thành công! Mã đơn hàng: " + order.getOrderId());
                    response.sendRedirect(
                            request.getContextPath() + "/customer/order-confirmation?orderId=" + order.getOrderId());
                }
            } else {
                // Payment failed
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

    /**
     * Load cart from session or database
     */
    private ShoppingCart loadCart(HttpSession session, Customer customer) {
        ShoppingCart cart;

        if (customer == null) {
            // Guest user - load from session
            cart = (ShoppingCart) session.getAttribute(GUEST_CART_KEY);
            if (cart == null) {
                cart = cartService.getOrCreateGuestCart();
            }
        } else {
            // Logged in user - load from database
            cart = cartService.getOrCreateCartForCustomer(customer);
        }

        return cart;
    }

    @Override
    public String getServletInfo() {
        return "Checkout Servlet - Handle checkout process";
    }

    /**
     * Pre-validate cart stock before checkout (non-blocking check).
     * Returns list of warning messages for items with insufficient stock.
     * This is a soft check - actual enforcement happens in OrderService with
     * locking.
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

            // Get current stock from database
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
}
