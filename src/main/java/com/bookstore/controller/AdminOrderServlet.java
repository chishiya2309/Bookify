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
        // Tham số phân trang
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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String action = req.getParameter("action");
        int orderId = Integer.parseInt(req.getParameter("orderId"));

        // Dịch vụ Email
        com.bookstore.service.EmailService emailService = new com.bookstore.service.EmailService();

        switch (action) {
            case "save": {
                // 1. Lấy trạng thái đơn hàng CŨ trước khi cập nhật
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

                // 2. Thực hiện cập nhật
                service.updateOrder(
                        orderId,
                        req.getParameter("recipientName"),
                        req.getParameter("paymentMethod"),
                        newStatus,
                        address);

                // 3. Kiểm tra thay đổi trạng thái & gửi Email
                if (newStatus != oldStatus) {
                    // Lấy đơn hàng cập nhật để lấy thông tin mới nhất
                    Order updatedOrder = service.getOrderForEdit(orderId);

                    // Case A: Trạng thái thay đổi thành SHIPPED
                    if (newStatus == Order.OrderStatus.SHIPPED) {
                        // Hiện tại mô phỏng.
                        emailService.sendShippingNotification(updatedOrder, "BOOKIFY-" + orderId);
                    }

                    // Case B: Trạng thái thay đổi thành DELIVERED
                    else if (newStatus == Order.OrderStatus.DELIVERED) {
                        emailService.sendDeliveryConfirmation(updatedOrder);
                    }

                    // Case C: Trạng thái thay đổi thành CANCELLED
                    else if (newStatus == Order.OrderStatus.CANCELLED) {
                        emailService.sendOrderCancellation(updatedOrder, "Đơn hàng bị hủy bởi Admin");

                        // Special Case: Yêu cầu hoàn tiền?
                        // Nếu đơn hàng đã thanh toán và phương thức thanh toán không phải là COD (Bank
                        // Transfer, Sepay, v.v.)
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
