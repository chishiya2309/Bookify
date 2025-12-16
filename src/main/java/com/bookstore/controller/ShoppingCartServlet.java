package com.bookstore.controller;

import com.bookstore.model.*;
import com.bookstore.service.ShoppingCartServices;
import com.bookstore.service.ShoppingCartServices.MergeResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet("/customer/cart")
public class ShoppingCartServlet extends HttpServlet {

    private ShoppingCartServices cartService;
    private static final String GUEST_CART_KEY = "guestCart";
    private static final String MERGE_MESSAGE_KEY = "mergeMessage";
    private static final String SUCCESS_MESSAGE_KEY = "successMessage";

    @Override
    public void init() throws ServletException {
        cartService = new ShoppingCartServices();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");
        ShoppingCart cart;
        
        if (customer == null) {
            // Chưa đăng nhập - Lấy giỏ hàng guest từ session
            cart = getGuestCart(session);
            System.out.println("[DEBUG] Guest cart loaded");
        } else {
            // Đã đăng nhập - Lấy giỏ hàng user từ DB
            try {
                System.out.println("[DEBUG] Loading cart for customer: " + customer.getUserId() + " - " + customer.getEmail());
                
                cart = cartService.getCartByCustomer(customer);
                
                if (cart == null) {
                    System.out.println("[DEBUG] No cart found, creating new cart...");
                    cart = cartService.createCart(customer);
                } else {
                    System.out.println("[DEBUG] Cart found: ID=" + cart.getCartId() + 
                                     ", Items=" + (cart.getItems() != null ? cart.getItems().size() : "NULL") +
                                     ", TotalItems=" + cart.getTotalItems());
                    
                    // Debug: Print each item
                    if (cart.getItems() != null) {
                        for (var item : cart.getItems()) {
                            System.out.println("[DEBUG] Item: BookID=" + item.getBook().getBookId() + 
                                             ", Title=" + item.getBook().getTitle() + 
                                             ", Qty=" + item.getQuantity());
                        }
                    }
                }
                
                cartService.calculateCartTotals(cart);
                
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "Unable to load cart: " + e.getMessage());
                request.getRequestDispatcher("/customer/error.jsp").forward(request, response);
                return;
            }
        }
        
        request.setAttribute("cart", cart);
        request.setAttribute("isGuest", customer == null);
        
        // Hiển thị thông báo merge nếu có
        String mergeMessage = (String) session.getAttribute(MERGE_MESSAGE_KEY);
        if (mergeMessage != null) {
            request.setAttribute("mergeMessage", mergeMessage);
            session.removeAttribute(MERGE_MESSAGE_KEY);
        }
        
        // Hiển thị thông báo thành công nếu có
        String successMessage = (String) session.getAttribute(SUCCESS_MESSAGE_KEY);
        if (successMessage != null) {
            request.setAttribute("successMessage", successMessage);
            session.removeAttribute(SUCCESS_MESSAGE_KEY);
        }
        
        request.getRequestDispatcher("/customer/cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");
        String action = request.getParameter("action");
        
        try {
            ShoppingCart cart;
            
            if (customer == null) {
                // Guest user
                cart = getGuestCart(session);
            } else {
                // Logged in user
                cart = cartService.getCartByCustomer(customer);
                if (cart == null) {
                    cart = cartService.createCart(customer);
                }
            }
            
            String successMessage = null;
            
            switch (action) {
                case "add":
                    addToCart(request, cart);
                    successMessage = "Đã thêm sách vào giỏ hàng thành công!";
                    break;
                case "update":
                    updateCart(request, cart);
                    // Reload cart từ database để đảm bảo dữ liệu được sync
                    if (customer != null) {
                        cart = cartService.getCartByCustomer(customer);
                        if (cart != null) {
                            cartService.calculateCartTotals(cart);
                        }
                    }
                    successMessage = "Giỏ hàng đã cập nhật thành công!";
                    break;
                case "remove":
                    removeItem(request, cart);
                    // Reload cart từ database sau khi remove
                    if (customer != null) {
                        cart = cartService.getCartByCustomer(customer);
                        if (cart != null) {
                            cartService.calculateCartTotals(cart);
                        }
                    }
                    successMessage = "Đã xoá item được chọn khỏi giỏ hàng!";
                    break;
                case "clear":
                    cartService.clearCart(cart);
                    // Reload cart từ database sau khi clear
                    if (customer != null) {
                        cart = cartService.getCartByCustomer(customer);
                        if (cart != null) {
                            cartService.calculateCartTotals(cart);
                        }
                    }
                    successMessage = "Đã xoá giỏ hàng!";
                    break;
                case "updateQuantity":
                    updateQuantity(request, cart);
                    // Reload cart từ database sau khi update quantity
                    if (customer != null) {
                        cart = cartService.getCartByCustomer(customer);
                        if (cart != null) {
                            cartService.calculateCartTotals(cart);
                        }
                    }
                    successMessage = "Đã cập nhật số lượng!";
                    break;
            }
            
            // Lưu lại guest cart vào session nếu là guest
            if (customer == null) {
                saveGuestCart(session, cart);
            }
            
            // Lưu thông báo thành công vào session
            if (successMessage != null) {
                session.setAttribute(SUCCESS_MESSAGE_KEY, successMessage);
            }
            
            response.sendRedirect("cart");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Cart operation failed: " + e.getMessage());
            request.getRequestDispatcher("/customer/error.jsp").forward(request, response);
        }
    }

    /**
     * Lấy giỏ hàng guest từ session
     */
    private ShoppingCart getGuestCart(HttpSession session) {
        ShoppingCart guestCart = (ShoppingCart) session.getAttribute(GUEST_CART_KEY);
        
        if (guestCart == null) {
            guestCart = cartService.getOrCreateGuestCart();
            session.setAttribute(GUEST_CART_KEY, guestCart);
        }
        
        return guestCart;
    }
    
    /**
     * Lưu giỏ hàng guest vào session
     */
    private void saveGuestCart(HttpSession session, ShoppingCart cart) {
        session.setAttribute(GUEST_CART_KEY, cart);
    }
    
    /**
     * CART MERGING - Được gọi khi user đăng nhập thành công
     * Hàm này nên được gọi từ LoginServlet
     */
    public static void mergeCartOnLogin(HttpSession session, Customer customer, 
                                       ShoppingCartServices cartService) {
        ShoppingCart guestCart = (ShoppingCart) session.getAttribute(GUEST_CART_KEY);
        
        if (guestCart != null && !guestCart.getItems().isEmpty()) {
            // Thực hiện merge
            MergeResult result = cartService.mergeGuestCartToUserCart(guestCart, customer);
            
            // Xóa guest cart khỏi session
            session.removeAttribute(GUEST_CART_KEY);
            
            // Lưu thông báo merge để hiển thị
            if (result.hasChanges()) {
                session.setAttribute(MERGE_MESSAGE_KEY, result.getSummaryMessage());
            }
        }
    }

    private void addToCart(HttpServletRequest request, ShoppingCart cart) {
        Integer bookId = Integer.parseInt(request.getParameter("bookId"));
        Integer quantity = Integer.parseInt(request.getParameter("quantity"));
        
        cartService.addItemToCart(cart, bookId, quantity);
    }

    /**
     * Update Cart - Cập nhật số lượng tất cả items trong giỏ hàng
     * Đọc các parameter: quantity_[cartItemId] từ form
     */
    private void updateCart(HttpServletRequest request, ShoppingCart cart) {
        System.out.println("[UPDATE CART] Starting update for cart ID: " + cart.getCartId());
        
        int updatedCount = 0;
        int removedCount = 0;
        
        // Tạo copy của list để tránh ConcurrentModificationException
        for (CartItem item : new ArrayList<>(cart.getItems())) {
            String qtyParam = request.getParameter("quantity_" + item.getCartItemId());
            
            if (qtyParam != null && !qtyParam.trim().isEmpty()) {
                try {
                    int newQuantity = Integer.parseInt(qtyParam.trim());
                    int currentQuantity = item.getQuantity();
                    
                    System.out.println("[UPDATE CART] Item " + item.getCartItemId() + 
                                     " (" + item.getBook().getTitle() + "): " + 
                                     currentQuantity + " → " + newQuantity);
                    
                    if (newQuantity <= 0) {
                        // Xóa item nếu quantity <= 0
                        cartService.removeItemFromCart(cart, item.getCartItemId());
                        removedCount++;
                        System.out.println("[UPDATE CART] Removed item: " + item.getBook().getTitle());
                    } else if (newQuantity != currentQuantity) {
                        // Chỉ update nếu quantity thực sự thay đổi
                        cartService.updateItemQuantity(cart, item.getCartItemId(), newQuantity);
                        updatedCount++;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("[UPDATE CART] Invalid quantity format for item " + item.getCartItemId());
                }
            }
        }
        
        System.out.println("[UPDATE CART] Complete! Updated: " + updatedCount + ", Removed: " + removedCount);
    }

    private void removeItem(HttpServletRequest request, ShoppingCart cart) {
        Integer itemId = Integer.parseInt(request.getParameter("itemId"));
        cartService.removeItemFromCart(cart, itemId);
    }

    private void updateQuantity(HttpServletRequest request, ShoppingCart cart) {
        Integer itemId = Integer.parseInt(request.getParameter("itemId"));
        Integer quantity = Integer.parseInt(request.getParameter("quantity"));
        cartService.updateItemQuantity(cart, itemId, quantity);
    }
}