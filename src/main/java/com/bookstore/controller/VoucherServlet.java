package com.bookstore.controller;

import com.bookstore.config.ShippingConfig;
import com.bookstore.model.Customer;
import com.bookstore.model.Voucher;
import com.bookstore.service.JwtAuthHelper;
import com.bookstore.service.VoucherService;
import com.bookstore.service.VoucherService.ValidationResult;
import com.bookstore.data.DBUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonObject;

/**
 * VoucherServlet - API endpoint for voucher validation
 * 
 * POST /api/voucher/validate
 * Request: { code, subtotal, shippingFee }
 * Response: { valid, message, discount, voucherCode, discountType }
 */
@WebServlet("/api/voucher/validate")
public class VoucherServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(VoucherServlet.class.getName());
    private VoucherService voucherService;

    @Override
    public void init() throws ServletException {
        voucherService = new VoucherService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JsonObject jsonResponse = new JsonObject();

        try {
            HttpSession session = request.getSession();
            Customer customer = (Customer) session.getAttribute("customer");

            if (customer == null) {
                customer = JwtAuthHelper.restoreCustomerFromJwt(request, session, DBUtil.getEmFactory());
            }

            String code = request.getParameter("code");
            String subtotalStr = request.getParameter("subtotal");
            String shippingFeeStr = request.getParameter("shippingFee");

            BigDecimal subtotal = BigDecimal.ZERO;
            BigDecimal shippingFee = BigDecimal.ZERO;

            if (subtotalStr != null && !subtotalStr.isEmpty()) {
                subtotal = new BigDecimal(subtotalStr.replace(",", ""));
            }
            if (shippingFeeStr != null && !shippingFeeStr.isEmpty()) {
                shippingFee = new BigDecimal(shippingFeeStr.replace(",", ""));
            }

            ValidationResult result = voucherService.validateVoucher(code, subtotal, shippingFee, customer);

            jsonResponse.addProperty("valid", result.isValid());
            jsonResponse.addProperty("message", result.getMessage());

            if (result.isValid()) {
                Voucher voucher = result.getVoucher();
                jsonResponse.addProperty("discount", result.getDiscount().doubleValue());
                jsonResponse.addProperty("voucherCode", voucher.getCode());
                jsonResponse.addProperty("discountType", voucher.getDiscountType().name());

                BigDecimal newTotal;
                if (voucher.getDiscountType() == Voucher.DiscountType.FREE_SHIPPING) {
                    newTotal = subtotal;
                } else {
                    newTotal = subtotal.add(shippingFee).subtract(result.getDiscount());
                }
                jsonResponse.addProperty("newTotal", newTotal.doubleValue());

                if (voucher.getDiscountType() == Voucher.DiscountType.FREE_SHIPPING) {
                    jsonResponse.addProperty("newShippingFee", 0);
                }
            }

            LOGGER.log(Level.INFO, "Voucher validation: code={0}, valid={1}",
                    new Object[] { code, result.isValid() });

        } catch (NumberFormatException e) {
            jsonResponse.addProperty("valid", false);
            jsonResponse.addProperty("message", "Dữ liệu không hợp lệ");
            LOGGER.log(Level.WARNING, "Invalid number format in voucher request", e);
        } catch (Exception e) {
            jsonResponse.addProperty("valid", false);
            jsonResponse.addProperty("message", "Có lỗi xảy ra. Vui lòng thử lại.");
            LOGGER.log(Level.SEVERE, "Error validating voucher", e);
        }

        PrintWriter out = response.getWriter();
        out.print(jsonResponse.toString());
        out.flush();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
