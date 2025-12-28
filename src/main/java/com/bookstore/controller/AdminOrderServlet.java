package com.bookstore.controller;

import com.bookstore.model.Address;
import com.bookstore.model.Order;
import com.bookstore.service.AdminServices;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/orders")
public class AdminOrderServlet extends HttpServlet {

    private final AdminServices service = new AdminServices();
    private static final int DEFAULT_PAGE_SIZE = 10;

    // ================= GET =================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        if (action == null)
            action = "list";

        switch (action) {

            case "edit": {
                int id = Integer.parseInt(req.getParameter("id"));

                req.setAttribute("order", service.getOrderForEdit(id));
                req.setAttribute("statuses", Order.OrderStatus.values());

                req.getRequestDispatcher("/admin/EditOrder.jsp")
                        .forward(req, resp);
                break;
            }

            case "delete": {
                int id = Integer.parseInt(req.getParameter("id"));
                service.deleteOrder(id);
                resp.sendRedirect(req.getContextPath() + "/admin/orders");
                break;
            }

            default:
                listOrders(req, resp);
        }
    }

    private void listOrders(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Pagination parameters
        int page = 0;
        int size = DEFAULT_PAGE_SIZE;

        String pageParam = req.getParameter("page");
        String sizeParam = req.getParameter("size");

        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 0)
                    page = 0;
            } catch (NumberFormatException e) {
                page = 0;
            }
        }

        if (sizeParam != null && !sizeParam.isEmpty()) {
            try {
                size = Integer.parseInt(sizeParam);
                if (size < 1)
                    size = DEFAULT_PAGE_SIZE;
                if (size > 100)
                    size = 100;
            } catch (NumberFormatException e) {
                size = DEFAULT_PAGE_SIZE;
            }
        }

        List<Order> orders = service.listAllOrdersPaginated(page, size);
        long totalOrders = service.countOrders();
        int totalPages = (int) Math.ceil((double) totalOrders / size);

        req.setAttribute("orders", orders);
        req.setAttribute("currentPage", page);
        req.setAttribute("pageSize", size);
        req.setAttribute("totalOrders", totalOrders);
        req.setAttribute("totalPages", totalPages);

        req.getRequestDispatcher("/admin/orders.jsp").forward(req, resp);
    }

    // ================= POST =================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String action = req.getParameter("action");
        int orderId = Integer.parseInt(req.getParameter("orderId"));

        // Email Service
        com.bookstore.service.EmailService emailService = new com.bookstore.service.EmailService();

        switch (action) {

            case "save": {
                // 1. Fetch current order status BEFORE update
                Order existingOrder = service.getOrderForEdit(orderId);
                Order.OrderStatus oldStatus = existingOrder.getOrderStatus();
                String paymentMethod = existingOrder.getPaymentMethod();
                Order.PaymentStatus paymentStatus = existingOrder.getPaymentStatus();

                Address address = new Address();
                address.setStreet(req.getParameter("street"));
                address.setWard(req.getParameter("ward"));
                address.setDistrict(req.getParameter("district"));
                address.setProvince(req.getParameter("province"));
                address.setZipCode(req.getParameter("zipCode"));

                Order.OrderStatus newStatus = Order.OrderStatus.valueOf(req.getParameter("orderStatus"));

                // 2. Perform Update
                service.updateOrder(
                        orderId,
                        req.getParameter("recipientName"),
                        req.getParameter("paymentMethod"),
                        newStatus,
                        address);

                // 3. Check for Status Changes & Send Emails
                if (newStatus != oldStatus) {
                    // Fetch updated order to get latest details
                    Order updatedOrder = service.getOrderForEdit(orderId);

                    // Case A: Status changed to SHIPPED
                    if (newStatus == Order.OrderStatus.SHIPPED) {
                        // In a real app, you'd get tracking number from input.
                        // For now we simulate or leave placeholder.
                        emailService.sendShippingNotification(updatedOrder, "BOOKIFY-" + orderId);
                    }

                    // Case B: Status changed to CANCELLED
                    else if (newStatus == Order.OrderStatus.CANCELLED) {
                        emailService.sendOrderCancellation(updatedOrder, "Đơn hàng bị hủy bởi Admin");

                        // Special Case: Refund required?
                        // If order was PAID and method was NOT COD (Bank Transfer, Sepay, etc.)
                        boolean isPrePaid = paymentMethod != null &&
                                !paymentMethod.equalsIgnoreCase("COD");

                        if (paymentStatus == Order.PaymentStatus.PAID && isPrePaid) {
                            String subject = "Yêu cầu hoàn tiền thủ công - Đơn hàng #" + orderId;
                            String message = String.format(
                                    "Đơn hàng #%d đã bị hủy bởi Admin.\n" +
                                            "Khách hàng đã thanh toán qua: %s\n" +
                                            "Số tiền cần hoàn: %s\n" +
                                            "Vui lòng kiểm tra và hoàn tiền thủ công cho khách hàng.",
                                    orderId,
                                    paymentMethod,
                                    java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"))
                                            .format(updatedOrder.getTotalAmount()));
                            emailService.sendAdminNotification(subject, message);
                        }
                    }
                }
                break;
            }

            case "updateQty": {
                int detailId = Integer.parseInt(req.getParameter("detailId"));
                int qty = Integer.parseInt(req.getParameter("quantity"));

                service.updateOrderDetailQty(detailId, qty);
                break;
            }

            case "removeBook": {
                int detailId = Integer.parseInt(req.getParameter("detailId"));
                service.removeOrderDetail(detailId);
                break;
            }
        }

        resp.sendRedirect(req.getContextPath()
                + "/admin/orders?action=edit&id=" + orderId);
    }
}
