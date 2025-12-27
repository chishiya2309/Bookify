package com.bookstore.controller.admin;

import com.bookstore.dao.VoucherDAO;
import com.bookstore.model.Voucher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * VoucherAdminServlet - Quản lý CRUD cho voucher
 */
@WebServlet("/admin/vouchers")
public class VoucherAdminServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(VoucherAdminServlet.class.getName());
    private final VoucherDAO voucherDAO = new VoucherDAO();
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null || action.isEmpty()) {
            action = "list";
        }

        String url = "/admin/voucher/list.jsp";

        try {
            switch (action) {
                case "list":
                    listVouchers(request, response);
                    break;

                case "showCreate":
                    url = "/admin/voucher/create.jsp";
                    break;

                case "create":
                    createVoucher(request, response);
                    url = "/admin/voucher/list.jsp";
                    break;

                case "showUpdate":
                    showUpdateForm(request, response);
                    url = "/admin/voucher/update.jsp";
                    break;

                case "update":
                    updateVoucher(request, response);
                    url = "/admin/voucher/list.jsp";
                    break;

                case "delete":
                    deleteVoucher(request, response);
                    url = "/admin/voucher/list.jsp";
                    break;

                case "toggleStatus":
                    toggleStatus(request, response);
                    url = "/admin/voucher/list.jsp";
                    break;

                default:
                    listVouchers(request, response);
                    break;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in VoucherAdminServlet", e);
            request.setAttribute("errorMessage", "Lỗi: " + e.getMessage());
            listVouchers(request, response);
        }

        getServletContext().getRequestDispatcher(url).forward(request, response);
    }

    private void listVouchers(HttpServletRequest request, HttpServletResponse response) {
        List<Voucher> vouchers = voucherDAO.findAll();
        request.setAttribute("vouchers", vouchers);
    }

    private void showUpdateForm(HttpServletRequest request, HttpServletResponse response) {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            int id = Integer.parseInt(idStr);
            Voucher voucher = voucherDAO.findById(id);
            request.setAttribute("voucher", voucher);
        }
    }

    private void createVoucher(HttpServletRequest request, HttpServletResponse response) {
        Voucher voucher = new Voucher();
        readVoucherFields(voucher, request);
        voucherDAO.save(voucher);
        request.setAttribute("message", "Tạo voucher thành công!");
        listVouchers(request, response);
    }

    private void updateVoucher(HttpServletRequest request, HttpServletResponse response) {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            int id = Integer.parseInt(idStr);
            Voucher voucher = voucherDAO.findById(id);
            if (voucher != null) {
                readVoucherFields(voucher, request);
                voucherDAO.update(voucher);
                request.setAttribute("message", "Cập nhật voucher thành công!");
            }
        }
        listVouchers(request, response);
    }

    private void deleteVoucher(HttpServletRequest request, HttpServletResponse response) {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            int id = Integer.parseInt(idStr);
            Voucher voucher = voucherDAO.findById(id);
            if (voucher != null) {
                voucherDAO.delete(voucher);
                request.setAttribute("message", "Xóa voucher thành công!");
            }
        }
        listVouchers(request, response);
    }

    private void toggleStatus(HttpServletRequest request, HttpServletResponse response) {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            int id = Integer.parseInt(idStr);
            Voucher voucher = voucherDAO.findById(id);
            if (voucher != null) {
                voucher.setActive(!voucher.isActive());
                voucherDAO.update(voucher);
                request.setAttribute("message",
                        voucher.isActive() ? "Đã kích hoạt voucher!" : "Đã vô hiệu hóa voucher!");
            }
        }
        listVouchers(request, response);
    }

    private void readVoucherFields(Voucher voucher, HttpServletRequest request) {
        voucher.setCode(request.getParameter("code").toUpperCase().trim());
        voucher.setDescription(request.getParameter("description"));

        String discountType = request.getParameter("discountType");
        voucher.setDiscountType(Voucher.DiscountType.valueOf(discountType));

        String discountValue = request.getParameter("discountValue");
        if (discountValue != null && !discountValue.isEmpty()) {
            try {
                voucher.setDiscountValue(new BigDecimal(discountValue));
            } catch (NumberFormatException e) {
                Logger.getLogger(VoucherAdminServlet.class.getName())
                        .log(Level.WARNING, "Invalid discountValue: " + discountValue, e);
                voucher.setDiscountValue(null);
            }
        } else {
            voucher.setDiscountValue(null);
        }

        String maxDiscount = request.getParameter("maxDiscount");
        if (maxDiscount != null && !maxDiscount.isEmpty()) {
            try {
                voucher.setMaxDiscount(new BigDecimal(maxDiscount));
            } catch (NumberFormatException e) {
                Logger.getLogger(VoucherAdminServlet.class.getName())
                        .log(Level.WARNING, "Invalid maxDiscount: " + maxDiscount, e);
                voucher.setMaxDiscount(null);
            }
        } else {
            voucher.setMaxDiscount(null);
        }

        String minOrderAmount = request.getParameter("minOrderAmount");
        if (minOrderAmount != null && !minOrderAmount.isEmpty()) {
            try {
                voucher.setMinOrderAmount(new BigDecimal(minOrderAmount));
            } catch (NumberFormatException e) {
                Logger.getLogger(VoucherAdminServlet.class.getName())
                        .log(Level.WARNING, "Invalid minOrderAmount: " + minOrderAmount, e);
                voucher.setMinOrderAmount(BigDecimal.ZERO);
            }
        } else {
            voucher.setMinOrderAmount(BigDecimal.ZERO);
        }

        String maxUses = request.getParameter("maxUses");
        if (maxUses != null && !maxUses.isEmpty()) {
            try {
                voucher.setMaxUses(Integer.parseInt(maxUses));
            } catch (NumberFormatException e) {
                Logger.getLogger(VoucherAdminServlet.class.getName())
                        .log(Level.WARNING, "Invalid maxUses: " + maxUses, e);
                voucher.setMaxUses(null);
            }
        } else {
            voucher.setMaxUses(null);
        }

        String maxUsesPerUser = request.getParameter("maxUsesPerUser");
        if (maxUsesPerUser != null && !maxUsesPerUser.isEmpty()) {
            try {
                voucher.setMaxUsesPerUser(Integer.parseInt(maxUsesPerUser));
            } catch (NumberFormatException e) {
                Logger.getLogger(VoucherAdminServlet.class.getName())
                        .log(Level.WARNING, "Invalid maxUsesPerUser: " + maxUsesPerUser, e);
                voucher.setMaxUsesPerUser(1);
            }
        } else {
            voucher.setMaxUsesPerUser(1);
        }

        String startDate = request.getParameter("startDate");
        if (startDate != null && !startDate.isEmpty()) {
            voucher.setStartDate(LocalDateTime.parse(startDate, DATETIME_FORMATTER));
        }

        String endDate = request.getParameter("endDate");
        if (endDate != null && !endDate.isEmpty()) {
            voucher.setEndDate(LocalDateTime.parse(endDate, DATETIME_FORMATTER));
        }

        String isActive = request.getParameter("isActive");
        voucher.setActive("on".equals(isActive) || "true".equals(isActive));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
