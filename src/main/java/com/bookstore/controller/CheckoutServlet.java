package com.bookstore.controller;

import java.io.IOException;

import com.bookstore.data.DBUtil;
import com.bookstore.model.Customer;
import com.bookstore.model.Order;
import com.bookstore.model.Payment;
import com.bookstore.model.ShoppingCart;
import com.bookstore.service.CustomerServices;
import com.bookstore.service.JwtAuthHelper;
import com.bookstore.service.JwtUtil;
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
    private CustomerServices customerServices;

    @Override
    public void init() throws ServletException {
        cartService = new ShoppingCartServices();
        paymentService = new PaymentService();
        customerServices = new CustomerServices();
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

            // Get shipping information from form
            String fullName = request.getParameter("fullName");
            String phoneNumber = request.getParameter("phoneNumber");
            String streetLine = request.getParameter("streetLine");
            String ward = request.getParameter("ward");
            String district = request.getParameter("district");
            String province = request.getParameter("province");
            String zipCode = request.getParameter("zipCode");
            String country = request.getParameter("country");

            // Get payment method
            String paymentMethodStr = request.getParameter("paymentMethod");
            Payment.PaymentMethod paymentMethod = Payment.PaymentMethod.valueOf(paymentMethodStr);

            // Validate required fields
            if (fullName == null || fullName.trim().isEmpty() ||
                    phoneNumber == null || phoneNumber.trim().isEmpty() ||
                    streetLine == null || streetLine.trim().isEmpty()) {
                request.setAttribute("error", "Vui lòng điền đầy đủ thông tin giao hàng");
                request.setAttribute("cart", cart);
                request.setAttribute("user", customer);
                request.getRequestDispatcher("/customer/checkout.jsp").forward(request, response);
                return;
            }

            // Build shipping address string (for display purposes)
            String shippingAddressStr = buildShippingAddress(streetLine, ward, district, province, country, zipCode);

            // TODO: Create Order with proper Address object
            // For now, we'll create a mock order
            // In production, you should create an Address object and save it to database
            Order order = createMockOrder(customer, cart, shippingAddressStr, fullName, phoneNumber);

            // Create payment
            String gateway = paymentMethod == Payment.PaymentMethod.SEPAY ? "Sepay" : null;
            Payment payment = paymentService.createPayment(order, paymentMethod, gateway);

            // Process payment
            PaymentService.PaymentResult result = paymentService.processPayment(payment, new java.util.HashMap<>());

            if (result.isSuccess()) {
                if (result.requiresRedirect()) {
                    // Redirect to payment gateway
                    response.sendRedirect(result.getRedirectUrl());
                } else {
                    // Payment completed (COD) - clear cart and redirect to confirmation
                    cartService.clearCart(cart);

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

    /**
     * Build shipping address string
     */
    private String buildShippingAddress(String streetLine, String ward, String district,
            String province, String country, String zipCode) {
        StringBuilder address = new StringBuilder();

        if (streetLine != null && !streetLine.trim().isEmpty()) {
            address.append(streetLine.trim());
        }

        if (ward != null && !ward.trim().isEmpty()) {
            if (address.length() > 0)
                address.append(", ");
            address.append(ward.trim());
        }

        if (district != null && !district.trim().isEmpty()) {
            if (address.length() > 0)
                address.append(", ");
            address.append(district.trim());
        }

        if (province != null && !province.trim().isEmpty()) {
            if (address.length() > 0)
                address.append(", ");
            address.append(province.trim());
        }

        if (country != null && !country.trim().isEmpty()) {
            if (address.length() > 0)
                address.append(", ");
            address.append(country.trim());
        }

        if (zipCode != null && !zipCode.trim().isEmpty()) {
            address.append(" ").append(zipCode.trim());
        }

        return address.toString();
    }

    /**
     * Create mock order (temporary until OrderDAO is implemented)
     * Note: This creates a minimal Order object without Address entity
     * In production, create proper Address entity and save to database
     */
    private Order createMockOrder(Customer customer, ShoppingCart cart, String shippingAddressStr,
            String recipientName, String recipientPhone) {
        Order order = new Order();
        order.setOrderId(generateMockOrderId());
        order.setCustomer(customer);
        order.setTotalAmount(cart.getTotalAmount());
        order.setOrderStatus(Order.OrderStatus.PENDING);
        order.setPaymentStatus(Order.PaymentStatus.UNPAID);
        order.setRecipientName(recipientName);
        order.setOrderDate(java.time.LocalDateTime.now());

        // Note: shippingAddress requires Address entity
        // For now, we skip setting it (will be null)
        // TODO: Create Address entity and set it properly
        // Address address = new Address();
        // address.setStreetLine(streetLine);
        // ... set other fields
        // order.setShippingAddress(address);

        return order;
    }

    /**
     * Generate mock order ID
     */
    private Integer generateMockOrderId() {
        return (int) (System.currentTimeMillis() % 100000);
    }

    @Override
    public String getServletInfo() {
        return "Checkout Servlet - Handle checkout process";
    }
}
