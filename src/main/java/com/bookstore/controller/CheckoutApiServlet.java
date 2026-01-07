package com.bookstore.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.bookstore.config.ShippingConfig;
import com.bookstore.dao.AddressDAO;
import com.bookstore.dao.ShoppingCartDAO;
import com.bookstore.data.DBUtil;
import com.bookstore.model.Address;
import com.bookstore.model.Customer;
import com.bookstore.model.ShoppingCart;
import com.bookstore.service.JwtAuthHelper;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * CheckoutApiServlet - API endpoints cho trang thanh toán
 * 
 * Endpoints:
 * - GET /api/checkout/shipping?addressId={id} - Tính phí vận chuyển theo địa
 * chỉ
 */
@WebServlet("/api/checkout/*")
public class CheckoutApiServlet extends HttpServlet {

    private AddressDAO addressDAO;
    private ShoppingCartDAO shoppingCartDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        addressDAO = new AddressDAO();
        shoppingCartDAO = new ShoppingCartDAO();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(response, 400, "Invalid endpoint");
            return;
        }

        // Get customer from session
        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");

        // Restore from JWT if needed
        if (customer == null) {
            customer = JwtAuthHelper.restoreCustomerFromJwt(request, session, DBUtil.getEmFactory());
        }

        // Must be authenticated
        if (customer == null) {
            sendError(response, 401, "Unauthorized");
            return;
        }

        String[] pathParts = pathInfo.split("/");
        String action = pathParts.length > 1 ? pathParts[1] : "";

        try {
            switch (action) {
                case "shipping":
                    handleGetShippingFee(customer, request, response);
                    break;
                default:
                    sendError(response, 404, "Endpoint not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, 500, "Server error: " + e.getMessage());
        }
    }

    /**
     * Calculate shipping fee based on selected address
     */
    private void handleGetShippingFee(Customer customer, HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        String addressIdStr = request.getParameter("addressId");
        if (addressIdStr == null || addressIdStr.isEmpty()) {
            sendError(response, 400, "Address ID required");
            return;
        }

        Integer addressId;
        try {
            addressId = Integer.parseInt(addressIdStr);
        } catch (NumberFormatException e) {
            sendError(response, 400, "Invalid address ID");
            return;
        }

        // Get address
        Address address = addressDAO.findById(addressId);

        // Security: Check ownership
        if (address == null || !address.getCustomer().getUserId().equals(customer.getUserId())) {
            sendError(response, 404, "Address not found");
            return;
        }

        // Get cart to check subtotal for free shipping
        ShoppingCart cart = shoppingCartDAO.findByCustomerId(customer.getUserId());
        BigDecimal subtotal = cart != null ? cart.getTotalAmount() : BigDecimal.ZERO;

        // Calculate shipping fee using ShippingConfig
        String province = address.getProvince();
        BigDecimal shippingFee = ShippingConfig.calculateShippingFee(province, subtotal);
        String region = ShippingConfig.getRegionName(province);
        BigDecimal freeShippingNeeded = ShippingConfig.getAmountForFreeShipping(subtotal);

        // If shipping is free, update region text
        if (shippingFee.equals(BigDecimal.ZERO)) {
            region = "Miễn phí";
        }

        // Build response
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("shippingFee", shippingFee.intValue());
        result.put("region", region);
        result.put("freeShippingNeeded", freeShippingNeeded.intValue());
        result.put("freeShippingThreshold", ShippingConfig.FREE_SHIPPING_THRESHOLD.intValue());

        sendJsonResponse(response, result);
    }

    // ==================== UTILITY METHODS ====================

    private void sendJsonResponse(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(data));
        out.flush();
    }

    private void sendError(HttpServletResponse response, int status, String message)
            throws IOException {
        response.setStatus(status);
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", message);
        sendJsonResponse(response, error);
    }
}
