package com.bookstore.controller.admin;

import java.io.IOException;
import java.util.List;

import com.bookstore.dao.AdminDAO;
import com.bookstore.model.Customer;
import com.bookstore.service.CustomerServices;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/customers")
public class CustomerServlet extends HttpServlet {
    
    private static final int PAGE_SIZE = 10;
    private final CustomerServices customerServices;
    private final AdminDAO adminDAO;
    
    public CustomerServlet() {
        this.customerServices = new CustomerServices();
        this.adminDAO = new AdminDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String action = request.getParameter("action");
        if (action == null || action.isEmpty()) action = "list";
        
        try {
            switch (action) {
                case "list" -> listCustomers(request, response);
                case "create" -> showCreateForm(request, response);
                case "edit" -> showEditForm(request, response);
                default -> listCustomers(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
            listCustomers(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String action = request.getParameter("action");
        if (action == null || action.isEmpty()) action = "list";
        
        try {
            switch (action) {
                case "create" -> createCustomer(request, response);
                case "update" -> updateCustomer(request, response);
                case "delete" -> deleteCustomer(request, response);
                default -> listCustomers(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("error", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/customers?action=list");
        }
    }
    
    private void listCustomers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String search = request.getParameter("search");
        int page = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try { page = Integer.parseInt(pageParam); if (page < 1) page = 1; } catch (NumberFormatException e) { page = 1; }
        }
        
        List<Customer> customerList = customerServices.listCustomers(page, PAGE_SIZE, search);
        long totalItems = customerServices.countCustomers(search);
        int totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
        
        request.setAttribute("customerList", customerList);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalItems", totalItems);
        request.setAttribute("search", search);
        request.setAttribute("hasNext", page < totalPages);
        request.setAttribute("hasPrevious", page > 1);
        
        HttpSession session = request.getSession();
        if (session.getAttribute("success") != null) {
            request.setAttribute("success", session.getAttribute("success"));
            session.removeAttribute("success");
        }
        if (session.getAttribute("error") != null) {
            request.setAttribute("error", session.getAttribute("error"));
            session.removeAttribute("error");
        }
        
        getServletContext().getRequestDispatcher("/admin/customer/list_customer.jsp").forward(request, response);
    }
    
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("isEdit", false);
        getServletContext().getRequestDispatcher("/admin/customer/form_customer.jsp").forward(request, response);
    }
    
    private void createCustomer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String fullName = request.getParameter("fullName");
        String phoneNumber = request.getParameter("phoneNumber");
        
        if (password == null || !password.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp");
            request.setAttribute("email", email);
            request.setAttribute("fullName", fullName);
            request.setAttribute("phoneNumber", phoneNumber);
            request.setAttribute("isEdit", false);
            getServletContext().getRequestDispatcher("/admin/customer/form_customer.jsp").forward(request, response);
            return;
        }
        
        try {
            customerServices.createCustomer(email, password, fullName, phoneNumber);
            HttpSession session = request.getSession();
            session.setAttribute("success", "Thêm khách hàng thành công");
            response.sendRedirect(request.getContextPath() + "/admin/customers?action=list");
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("email", email);
            request.setAttribute("fullName", fullName);
            request.setAttribute("phoneNumber", phoneNumber);
            request.setAttribute("isEdit", false);
            getServletContext().getRequestDispatcher("/admin/customer/form_customer.jsp").forward(request, response);
        }
    }
    
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "ID khách hàng không hợp lệ");
            response.sendRedirect(request.getContextPath() + "/admin/customers?action=list");
            return;
        }
        
        try {
            Integer id = Integer.parseInt(idParam);
            Customer customer = customerServices.getCustomerById(id);
            if (customer == null) {
                HttpSession session = request.getSession();
                session.setAttribute("error", "Khách hàng không tồn tại");
                response.sendRedirect(request.getContextPath() + "/admin/customers?action=list");
                return;
            }
            request.setAttribute("customer", customer);
            request.setAttribute("isEdit", true);
            getServletContext().getRequestDispatcher("/admin/customer/form_customer.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "ID khách hàng không hợp lệ");
            response.sendRedirect(request.getContextPath() + "/admin/customers?action=list");
        }
    }
    
    private void updateCustomer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String fullName = request.getParameter("fullName");
        String phoneNumber = request.getParameter("phoneNumber");
        
        if (idParam == null || idParam.isEmpty()) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "ID khách hàng không hợp lệ");
            response.sendRedirect(request.getContextPath() + "/admin/customers?action=list");
            return;
        }
        
        // Validate password confirmation if password is provided
        if (password != null && !password.isEmpty() && !password.equals(confirmPassword)) {
            try {
                int id = Integer.parseInt(idParam);
                Customer formCustomer = new Customer();
                formCustomer.setUserId(id);
                formCustomer.setEmail(email);
                formCustomer.setFullName(fullName);
                formCustomer.setPhoneNumber(phoneNumber);
                request.setAttribute("customer", formCustomer);
                request.setAttribute("error", "Mật khẩu xác nhận không khớp");
                request.setAttribute("isEdit", true);
                getServletContext().getRequestDispatcher("/admin/customer/form_customer.jsp").forward(request, response);
                return;
            } catch (NumberFormatException e) {
                HttpSession session = request.getSession();
                session.setAttribute("error", "ID khách hàng không hợp lệ");
                response.sendRedirect(request.getContextPath() + "/admin/customers?action=list");
                return;
            }
        }
        
        try {
            int id = Integer.parseInt(idParam);
            customerServices.updateCustomer(id, fullName, password, phoneNumber);
            HttpSession session = request.getSession();
            session.setAttribute("success", "Cập nhật khách hàng thành công");
            response.sendRedirect(request.getContextPath() + "/admin/customers?action=list");
        } catch (NumberFormatException e) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "ID khách hàng không hợp lệ");
            response.sendRedirect(request.getContextPath() + "/admin/customers?action=list");
        } catch (Exception e) {
            try {
                Integer id = Integer.parseInt(idParam);
                Customer formCustomer = new Customer();
                formCustomer.setUserId(id);
                formCustomer.setEmail(email);
                formCustomer.setFullName(fullName);
                formCustomer.setPhoneNumber(phoneNumber);
                request.setAttribute("customer", formCustomer);
                request.setAttribute("error", e.getMessage());
                request.setAttribute("isEdit", true);
                getServletContext().getRequestDispatcher("/admin/customer/form_customer.jsp").forward(request, response);
            } catch (Exception ex) {
                HttpSession session = request.getSession();
                session.setAttribute("error", e.getMessage());
                response.sendRedirect(request.getContextPath() + "/admin/customers?action=list");
            }
        }
    }
    
    private void deleteCustomer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "ID khách hàng không hợp lệ");
            response.sendRedirect(request.getContextPath() + "/admin/customers?action=list");
            return;
        }
        
        try {
            Integer id = Integer.parseInt(idParam);
            customerServices.deleteCustomer(id);
            HttpSession session = request.getSession();
            session.setAttribute("success", "Xóa khách hàng thành công");
            response.sendRedirect(request.getContextPath() + "/admin/customers?action=list");
        } catch (NumberFormatException e) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "ID khách hàng không hợp lệ");
            response.sendRedirect(request.getContextPath() + "/admin/customers?action=list");
        } catch (Exception e) {
            HttpSession session = request.getSession();
            session.setAttribute("error", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/customers?action=list");
        }
    }
}
