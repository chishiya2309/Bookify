package com.bookstore.controller;

import com.bookstore.dao.BookDAO;
import com.bookstore.data.DBUtil;
import com.bookstore.model.*;
import com.bookstore.service.JwtAuthHelper;
import com.bookstore.service.ShoppingCartServices;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CartApiServlet - API AJAX cho các thao tác với giỏ hàng
 * * Các endpoint:
 * - POST /api/cart/add - Thêm sản phẩm vào giỏ hàng (trả về định dạng JSON)
 * - GET /api/cart/count - Lấy số lượng sản phẩm hiện có trong giỏ
 * - GET /api/cart/items - Lấy danh sách sản phẩm để hiển thị trên mini-cart
 */
@WebServlet("/api/cart/*")
public class CartApiServlet extends HttpServlet {

    private ShoppingCartServices cartService;
    private BookDAO bookDAO;
    private Gson gson;

    private static final String GUEST_CART_KEY = "guestCart";
    private static final int MAX_CART_ITEMS = 50;
    private static final int MAX_QUANTITY_PER_ITEM = 10;

    @Override
    public void init() throws ServletException {
        cartService = new ShoppingCartServices();
        bookDAO = new BookDAO();
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

        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");

        // Khôi phục khách hàng từ JWT nếu cần
        if (customer == null) {
            customer = JwtAuthHelper.restoreCustomerFromJwt(request, session, DBUtil.getEmFactory());
        }

        String[] pathParts = pathInfo.split("/");
        String action = pathParts[1];

        try {
            switch (action) {
                case "count":
                    handleGetCount(session, customer, response);
                    break;

                case "items":
                    handleGetItems(session, customer, response);
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

        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");

        // Khôi phục khách hàng từ JWT nếu cần
        if (customer == null) {
            customer = JwtAuthHelper.restoreCustomerFromJwt(request, session, DBUtil.getEmFactory());
        }

        String[] pathParts = pathInfo.split("/");
        String action = pathParts[1];

        try {
            switch (action) {
                case "add":
                    handleAddToCart(session, customer, request, response);
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
     * Lấy số lượng sản phẩm trong giỏ hàng
     */
    private void handleGetCount(HttpSession session, Customer customer,
            HttpServletResponse response) throws IOException {

        ShoppingCart cart = getCart(session, customer);
        int count = cart != null ? cart.getTotalItems() : 0;

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("count", count);

        sendJsonResponse(response, result);
    }

    /**
     * Lấy danh sách sản phẩm trong giỏ hàng để hiển thị trên mini-cart
     */
    private void handleGetItems(HttpSession session, Customer customer,
            HttpServletResponse response) throws IOException {

        ShoppingCart cart = getCart(session, customer);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);

        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            result.put("items", new ArrayList<>());
            result.put("count", 0);
            result.put("subtotal", 0);
        } else {
            cartService.calculateCartTotals(cart);

            List<Map<String, Object>> items = new ArrayList<>();
            for (CartItem item : cart.getItems()) {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("cartItemId", item.getCartItemId());
                itemMap.put("bookId", item.getBook().getBookId());
                itemMap.put("title", item.getBook().getTitle());
                itemMap.put("price", item.getBook().getPrice());
                itemMap.put("quantity", item.getQuantity());
                itemMap.put("subtotal",
                        item.getBook().getPrice().multiply(new java.math.BigDecimal(item.getQuantity())));

                // Lấy primary image URL
                String imageUrl = item.getBook().getPrimaryImageUrl();
                if (imageUrl == null || imageUrl.isEmpty()) {
                    imageUrl = "/images/no-image.jpg";
                }
                itemMap.put("imageUrl", imageUrl);

                items.add(itemMap);
            }

            result.put("items", items);
            result.put("count", cart.getTotalItems());
            result.put("subtotal", cart.getTotalAmount());
        }

        sendJsonResponse(response, result);
    }

    /**
     * Thêm sản phẩm vào giỏ hàng thông qua AJAX
     */
    private void handleAddToCart(HttpSession session, Customer customer,
            HttpServletRequest request, HttpServletResponse response) throws IOException {

        String bookIdParam = request.getParameter("bookId");
        String quantityParam = request.getParameter("quantity");

        if (bookIdParam == null || quantityParam == null) {
            sendError(response, 400, "Missing parameters");
            return;
        }

        try {
            int bookId = Integer.parseInt(bookIdParam);
            int quantity = Integer.parseInt(quantityParam);

            // Kiểm tra sách tồn tại và có trong kho
            Book book = bookDAO.findById(bookId);
            if (book == null) {
                sendError(response, 404, "Sách không tồn tại");
                return;
            }

            int currentStock = book.getQuantityInStock();
            if (currentStock <= 0) {
                sendError(response, 400, "Sách đã hết hàng");
                return;
            }

            // Lấy hoặc tạo giỏ hàng
            ShoppingCart cart;
            if (customer == null) {
                cart = getGuestCart(session);
            } else {
                cart = cartService.getOrCreateCartForCustomer(customer);
            }

            // Kiểm tra số lượng tối đa cho mỗi sản phẩm
            if (quantity > MAX_QUANTITY_PER_ITEM) {
                sendError(response, 400, "Số lượng tối đa cho mỗi sản phẩm là " + MAX_QUANTITY_PER_ITEM);
                return;
            }

            // Kiểm tra số lượng tồn tại trong giỏ hàng
            int existingInCart = 0;
            boolean isNewItem = true;
            if (cart.getItems() != null) {
                for (CartItem item : cart.getItems()) {
                    if (item.getBook().getBookId().equals(bookId)) {
                        existingInCart = item.getQuantity();
                        isNewItem = false;
                        break;
                    }
                }
            }

            // Kiểm tra số lượng tối đa cho giỏ hàng
            if (isNewItem && cart.getItems() != null && cart.getItems().size() >= MAX_CART_ITEMS) {
                sendError(response, 400, "Giỏ hàng đã đạt giới hạn tối đa " + MAX_CART_ITEMS + " sản phẩm");
                return;
            }

            // Kiểm tra tổng số lượng
            int totalQuantity = existingInCart + quantity;
            if (totalQuantity > MAX_QUANTITY_PER_ITEM) {
                sendError(response, 400, "Tổng số lượng không được vượt quá " + MAX_QUANTITY_PER_ITEM + " (hiện có: "
                        + existingInCart + ")");
                return;
            }

            if (totalQuantity > currentStock) {
                sendError(response, 400, "Không đủ hàng. Chỉ còn " + currentStock + " sản phẩm");
                return;
            }

            // Thêm vào giỏ hàng
            cartService.addItemToCart(cart, bookId, quantity);

            // Lưu giỏ hàng khách hàng vào session
            if (customer == null) {
                session.setAttribute(GUEST_CART_KEY, cart);
            }

            // Tính toán tổng số lượng
            cartService.calculateCartTotals(cart);

            // Trả về thông tin giỏ hàng
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Đã thêm \"" + book.getTitle() + "\" vào giỏ hàng");
            result.put("count", cart.getTotalItems());
            result.put("subtotal", cart.getTotalAmount());

            sendJsonResponse(response, result);

        } catch (NumberFormatException e) {
            sendError(response, 400, "Invalid parameters");
        }
    }

    /**
     * Lấy giỏ hàng cho khách hàng hiện tại
     */
    private ShoppingCart getCart(HttpSession session, Customer customer) {
        if (customer == null) {
            return getGuestCart(session);
        } else {
            ShoppingCart cart = cartService.getCartByCustomer(customer);
            if (cart != null) {
                cartService.calculateCartTotals(cart);
            }
            return cart;
        }
    }

    /**
     * Lấy giỏ hàng khách hàng từ session
     */
    private ShoppingCart getGuestCart(HttpSession session) {
        ShoppingCart guestCart = (ShoppingCart) session.getAttribute(GUEST_CART_KEY);
        if (guestCart == null) {
            guestCart = cartService.getOrCreateGuestCart();
            session.setAttribute(GUEST_CART_KEY, guestCart);
        }
        return guestCart;
    }

    private void sendJsonResponse(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(data));
        out.flush();
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", message);
        sendJsonResponse(response, error);
    }
}
