package com.bookstore.controller;

import com.bookstore.model.Customer;
import com.bookstore.model.Order;
import com.bookstore.service.CustomerServices;
import com.bookstore.service.JwtAuthHelper;
import com.bookstore.service.OrderServices;
import com.bookstore.data.DBUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet("/customer/orders")
public class OrderServlet extends HttpServlet {

    private OrderServices orderServices;
    private CustomerServices customerServices;

    @Override
    public void init() {
        orderServices = new OrderServices();
        customerServices = new CustomerServices();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            session = request.getSession(true);
        }

        Customer customer = (Customer) session.getAttribute("customer");

        // Nếu session không có customer, kiểm tra JWT cookie để khôi phục
        if (customer == null) {
            customer = JwtAuthHelper.restoreCustomerFromJwt(request, session, DBUtil.getEmFactory());
        }

        // Nếu vẫn không có customer sau khi khôi phục JWT, redirect về login
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/customer/login.jsp?redirect=orders");
            return;
        }

        // Load categories cho header
        request.setAttribute("listCategories", customerServices.listAllCategories());

        // Set userName cho header
        request.setAttribute("userName", customer.getFullName());

        // Debug logging
        System.out.println("[OrderServlet] Customer ID: " + customer.getUserId() + ", Email: " + customer.getEmail());

        String action = request.getParameter("action");

        // ======================
        // 1. ORDER HISTORY
        // ======================
        if (action == null) {
            List<Order> orders = orderServices.getOrderHistory(customer);
            System.out.println(
                    "[OrderServlet] Found " + orders.size() + " orders for customer ID: " + customer.getUserId());
            request.setAttribute("orders", orders);

            request.getRequestDispatcher("/customer/orders.jsp")
                    .forward(request, response);
            return;
        }

        // ======================
        // 2. ORDER DETAIL
        // ======================
        if ("detail".equals(action)) {
            Integer orderId = Integer.valueOf(request.getParameter("id"));

            System.out.println("[OrderServlet] Retrieving order detail - orderId: " + orderId + ", customerId: "
                    + customer.getUserId());

            Order order = orderServices.getOrderDetail(orderId, customer);
            if (order == null) {
                System.out.println("[OrderServlet] Order not found or doesn't belong to customer. orderId: " + orderId
                        + ", customerId: " + customer.getUserId());
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Đơn hàng không tồn tại hoặc không thuộc về bạn");
                return;
            }

            request.setAttribute("order", order);
            request.getRequestDispatcher("/customer/order-detail.jsp")
                    .forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            session = request.getSession(true);
        }

        Customer customer = (Customer) session.getAttribute("customer");

        // Nếu session không có customer, kiểm tra JWT cookie để khôi phục
        if (customer == null) {
            customer = JwtAuthHelper.restoreCustomerFromJwt(request, session, DBUtil.getEmFactory());
        }

        // Nếu vẫn không có customer sau khi khôi phục JWT, redirect về login
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/customer/login.jsp?redirect=orders");
            return;
        }

        String action = request.getParameter("action");

        // ======================
        // MARK ORDER AS DELIVERED
        // ======================
        if ("markDelivered".equals(action)) {
            try {
                Integer orderId = Integer.valueOf(request.getParameter("orderId"));

                // Kiểm tra order có thuộc về customer không
                Order order = orderServices.getOrderDetail(orderId, customer);
                if (order == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND,
                            "Đơn hàng không tồn tại hoặc không thuộc về bạn");
                    return;
                }

                // Chỉ cho phép cập nhật từ SHIPPED sang DELIVERED
                if (order.getOrderStatus() != Order.OrderStatus.SHIPPED) {
                    String errorMessage = URLEncoder.encode("Chỉ có thể xác nhận đã nhận hàng với đơn hàng đang giao",
                            StandardCharsets.UTF_8);
                    response.sendRedirect(request.getContextPath() + "/customer/orders?error=" + errorMessage);
                    return;
                }

                // Cập nhật status
                orderServices.updateOrderStatus(orderId, Order.OrderStatus.DELIVERED);

                // Redirect về trang chi tiết đơn hàng hoặc danh sách đơn hàng
                String successMessage = URLEncoder.encode("Đã xác nhận nhận hàng thành công", StandardCharsets.UTF_8);
                String redirectUrl = request.getParameter("redirect");
                if ("detail".equals(redirectUrl)) {
                    response.sendRedirect(request.getContextPath() + "/customer/orders?action=detail&id=" + orderId
                            + "&success=" + successMessage);
                } else {
                    response.sendRedirect(request.getContextPath() + "/customer/orders?success=" + successMessage);
                }
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Order ID không hợp lệ");
            } catch (Exception e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Đã xảy ra lỗi khi xử lý yêu cầu");
            }
        }
    }
}
