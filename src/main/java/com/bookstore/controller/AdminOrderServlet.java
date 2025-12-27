package com.bookstore.controller;

import com.bookstore.model.Address;
import com.bookstore.model.Order;
import com.bookstore.service.AdminServices;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/admin/orders")
public class AdminOrderServlet extends HttpServlet {

    private final AdminServices service = new AdminServices();

    // ================= GET =================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        if (action == null) action = "list";

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
                req.setAttribute("orders", service.listAllOrders());
                req.getRequestDispatcher("/admin/orders.jsp")
                   .forward(req, resp);
        }
    }

    // ================= POST =================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String action = req.getParameter("action");
        int orderId = Integer.parseInt(req.getParameter("orderId"));

        switch (action) {

            case "save": {
                Address address = new Address();
                address.setStreet(req.getParameter("street"));
                address.setWard(req.getParameter("ward"));
                address.setDistrict(req.getParameter("district"));
                address.setProvince(req.getParameter("province"));
                address.setZipCode(req.getParameter("zipCode"));

                service.updateOrder(
                        orderId,
                        req.getParameter("recipientName"),
                        req.getParameter("paymentMethod"),
                        Order.OrderStatus.valueOf(req.getParameter("orderStatus")),
                        address
                );
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
