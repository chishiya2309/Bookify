package com.bookstore.controller;

import com.bookstore.data.DBUtil;
import com.bookstore.model.Customer;
import com.bookstore.model.Order;
import com.bookstore.config.SepayConfig;
import com.bookstore.service.JwtAuthHelper;
import com.bookstore.service.OrderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * BankTransferPaymentServlet - Display bank transfer payment page with QR code
 * Shows VietQR code and polls for payment confirmation via Sepay IPN
 */
@WebServlet(name = "BankTransferPaymentServlet", urlPatterns = { "/customer/bank-transfer-payment" })
public class BankTransferPaymentServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(BankTransferPaymentServlet.class.getName());
    private OrderService orderService;

    @Override
    public void init() throws ServletException {
        orderService = new OrderService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");

        // Restore customer from JWT if not in session
        if (customer == null) {
            customer = JwtAuthHelper.restoreCustomerFromJwt(request, session, DBUtil.getEmFactory());
        }

        // Redirect to login if not authenticated
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/customer/login");
            return;
        }

        try {
            // Get order ID from parameter
            String orderIdStr = request.getParameter("orderId");

            if (orderIdStr == null || orderIdStr.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/customer/order-history");
                return;
            }

            int orderId = Integer.parseInt(orderIdStr);

            // Load order
            Order order = orderService.getOrderByIdWithDetails(orderId);

            if (order == null) {
                response.sendRedirect(request.getContextPath() + "/customer/order-history");
                return;
            }

            // Verify order belongs to customer
            if (!order.getCustomer().getUserId().equals(customer.getUserId())) {
                LOGGER.log(Level.WARNING, "Customer {0} attempted to access order {1}",
                        new Object[] { customer.getUserId(), orderId });
                response.sendRedirect(request.getContextPath() + "/customer/order-history");
                return;
            }

            // Check if already paid - redirect to confirmation
            if (order.getPaymentStatus() == Order.PaymentStatus.PAID) {
                response.sendRedirect(request.getContextPath() + "/customer/order-confirmation?orderId=" + orderId);
                return;
            }

            // Check if payment method is BANK_TRANSFER
            if (!"BANK_TRANSFER".equals(order.getPaymentMethod())) {
                response.sendRedirect(request.getContextPath() + "/customer/order-confirmation?orderId=" + orderId);
                return;
            }

            // Generate VietQR URL
            String vietQRUrl = SepayConfig.generateVietQRUrl(
                    order.getOrderId(),
                    order.getTotalAmount().longValue());

            // Set attributes for JSP
            request.setAttribute("order", order);
            request.setAttribute("vietQRUrl", vietQRUrl);
            request.setAttribute("bankName", SepayConfig.getFullBankName());
            request.setAttribute("accountNumber", SepayConfig.getAccountNumber());
            request.setAttribute("transferContent", SepayConfig.generateTransferContent(order.getOrderId()));

            // Forward to payment page
            request.getRequestDispatcher("/customer/bank-transfer-payment.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid order ID format", e);
            response.sendRedirect(request.getContextPath() + "/customer/order-history");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading bank transfer payment page", e);
            response.sendRedirect(request.getContextPath() + "/customer/order-history");
        }
    }

    @Override
    public String getServletInfo() {
        return "Bank Transfer Payment Servlet - Display QR code for bank transfer";
    }
}
