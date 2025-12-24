package com.bookstore.controller;

import com.bookstore.dao.AddressDAO;
import com.bookstore.data.DBUtil;
import com.bookstore.model.Address;
import com.bookstore.model.Customer;
import com.bookstore.service.JwtAuthHelper;
import com.bookstore.service.VietnamAddressService;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AddressServlet - Handle address management and Vietnam Address API
 * 
 * Endpoints:
 * - GET /api/address/list - Get customer addresses
 * - POST /api/address/create - Create new address
 * - PUT /api/address/update - Update address
 * - DELETE /api/address/delete/{id} - Delete address
 * - POST /api/address/set-default/{id} - Set default address
 * 
 * Vietnam API Proxy:
 * - GET /api/address/provinces - Get all provinces
 * - GET /api/address/districts/{provinceCode} - Get districts
 * - GET /api/address/wards/{districtCode} - Get wards
 */
@WebServlet("/api/address/*")
public class AddressServlet extends HttpServlet {

    private AddressDAO addressDAO;
    private VietnamAddressService vietnamAddressService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        addressDAO = new AddressDAO();
        vietnamAddressService = new VietnamAddressService();
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

        String[] pathParts = pathInfo.split("/");
        String action = pathParts[1];

        try {
            switch (action) {
                case "list":
                    handleGetAddressList(customer, response);
                    break;

                case "provinces":
                    handleGetProvinces(response);
                    break;

                case "districts":
                    if (pathParts.length < 3) {
                        sendError(response, 400, "Province code required");
                        return;
                    }
                    handleGetDistricts(pathParts[2], response);
                    break;

                case "wards":
                    if (pathParts.length < 3) {
                        sendError(response, 400, "District code required");
                        return;
                    }
                    handleGetWards(pathParts[2], response);
                    break;

                default:
                    sendError(response, 404, "Endpoint not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, 500, "Server error: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
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
        String action = pathParts[1];

        try {
            switch (action) {
                case "create":
                    handleCreateAddress(customer, request, response);
                    break;

                case "update":
                    handleUpdateAddress(customer, request, response);
                    break;

                case "set-default":
                    if (pathParts.length < 3) {
                        sendError(response, 400, "Address ID required");
                        return;
                    }
                    handleSetDefault(customer, Integer.parseInt(pathParts[2]), response);
                    break;

                default:
                    sendError(response, 404, "Endpoint not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, 500, "Server error: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
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
        String action = pathParts[1];

        try {
            if (action.equals("delete") && pathParts.length >= 3) {
                handleDeleteAddress(customer, Integer.parseInt(pathParts[2]), response);
            } else {
                sendError(response, 404, "Endpoint not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, 500, "Server error: " + e.getMessage());
        }
    }

    // ==================== HANDLER METHODS ====================

    /**
     * Get customer address list
     */
    private void handleGetAddressList(Customer customer, HttpServletResponse response)
            throws IOException {
        if (customer == null) {
            sendError(response, 401, "Unauthorized");
            return;
        }

        List<Address> addresses = addressDAO.findByCustomerId(customer.getUserId());
        sendJsonResponse(response, addresses);
    }

    /**
     * Get all provinces
     */
    private void handleGetProvinces(HttpServletResponse response) throws IOException {
        List<VietnamAddressService.Province> provinces = vietnamAddressService.getAllProvinces();
        sendJsonResponse(response, provinces);
    }

    /**
     * Get districts by province code
     */
    private void handleGetDistricts(String provinceCode, HttpServletResponse response)
            throws IOException {
        List<VietnamAddressService.District> districts = vietnamAddressService.getDistrictsByProvince(provinceCode);
        sendJsonResponse(response, districts);
    }

    /**
     * Get wards by district code
     */
    private void handleGetWards(String districtCode, HttpServletResponse response)
            throws IOException {
        List<VietnamAddressService.Ward> wards = vietnamAddressService.getWardsByDistrict(districtCode);
        sendJsonResponse(response, wards);
    }

    /**
     * Create new address
     */
    private void handleCreateAddress(Customer customer, HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        // Get parameters
        String recipientName = request.getParameter("recipientName");
        String phoneNumber = request.getParameter("phoneNumber");
        String street = request.getParameter("street");
        String ward = request.getParameter("ward");
        String district = request.getParameter("district");
        String province = request.getParameter("province");
        String zipCode = request.getParameter("zipCode");
        boolean isDefault = Boolean.parseBoolean(request.getParameter("isDefault"));

        // Validate required fields
        if (street == null || ward == null || district == null || province == null) {
            sendError(response, 400, "Missing required fields");
            return;
        }

        // Create address
        Address address = new Address();
        address.setCustomer(customer);
        address.setRecipientName(recipientName);
        address.setPhoneNumber(phoneNumber);
        address.setStreet(street);
        address.setWard(ward);
        address.setDistrict(district);
        address.setProvince(province);
        address.setZipCode(zipCode != null ? zipCode : "");
        address.setCountry("Vietnam");
        address.setIsDefault(isDefault);

        // Save address
        addressDAO.save(address);

        // If set as default, update others
        if (isDefault) {
            addressDAO.setDefaultAddress(address.getAddressId(), customer.getUserId());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Địa chỉ đã được tạo thành công");
        result.put("addressId", address.getAddressId());

        sendJsonResponse(response, result);
    }

    /**
     * Update existing address
     */
    private void handleUpdateAddress(Customer customer, HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        String addressIdParam = request.getParameter("addressId");
        if (addressIdParam == null) {
            sendError(response, 400, "Address ID required");
            return;
        }

        Integer addressId = Integer.parseInt(addressIdParam);
        Address address = addressDAO.findById(addressId);

        // Security: Check ownership
        if (address == null || !address.getCustomer().getUserId().equals(customer.getUserId())) {
            sendError(response, 403, "Forbidden");
            return;
        }

        // Update fields
        String street = request.getParameter("street");
        if (street != null)
            address.setStreet(street);

        String ward = request.getParameter("ward");
        if (ward != null)
            address.setWard(ward);

        String district = request.getParameter("district");
        if (district != null)
            address.setDistrict(district);

        String province = request.getParameter("province");
        if (province != null)
            address.setProvince(province);

        String zipCode = request.getParameter("zipCode");
        if (zipCode != null)
            address.setZipCode(zipCode);

        // Save
        addressDAO.update(address);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Địa chỉ đã được cập nhật");

        sendJsonResponse(response, result);
    }

    /**
     * Delete address
     */
    private void handleDeleteAddress(Customer customer, Integer addressId,
            HttpServletResponse response) throws IOException {
        Address address = addressDAO.findById(addressId);

        // Security: Check ownership
        if (address == null || !address.getCustomer().getUserId().equals(customer.getUserId())) {
            sendError(response, 403, "Forbidden");
            return;
        }

        addressDAO.delete(addressId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Địa chỉ đã được xóa");

        sendJsonResponse(response, result);
    }

    /**
     * Set default address
     */
    private void handleSetDefault(Customer customer, Integer addressId,
            HttpServletResponse response) throws IOException {
        Address address = addressDAO.findById(addressId);

        // Security: Check ownership
        if (address == null || !address.getCustomer().getUserId().equals(customer.getUserId())) {
            sendError(response, 403, "Forbidden");
            return;
        }

        addressDAO.setDefaultAddress(addressId, customer.getUserId());

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Đã đặt làm địa chỉ mặc định");

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
