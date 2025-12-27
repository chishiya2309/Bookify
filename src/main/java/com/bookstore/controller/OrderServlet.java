package com.bookstore.controller;

import com.bookstore.model.Customer;
import com.bookstore.model.Order;
import com.bookstore.service.OrderServices;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/customer/orders")
public class OrderServlet extends HttpServlet {
    

    private OrderServices orderServices;

    @Override
    public void init() {
        orderServices = new OrderServices();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
    // Bạn không cần redirect về login ở đây nữa vì Filter đã chặn phía ngoài rồi
    
    Customer customer = (Customer) session.getAttribute("customer");

    // Nếu lỡ như session còn nhưng object customer bị mất (do server restart)
    if (customer == null) {
        // Option 1: Tự nạp lại nếu bạn có Service
        // Option 2: Đẩy về login cho chắc chắn
        response.sendRedirect(request.getContextPath() + "/customer/login.jsp"); 
        return;
    }

        String action = request.getParameter("action");

        // ======================
        // 1. ORDER HISTORY
        // ======================
        if (action == null) {
            List<Order> orders = orderServices.getOrderHistory(customer);
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

            Order order = orderServices.getOrderDetail(orderId, customer);
            if (order == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            request.setAttribute("order", order);
            request.getRequestDispatcher("/customer/order-detail.jsp")
                   .forward(request, response);
        }
    }
}
