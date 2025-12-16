/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookstore.service;

import com.bookstore.dao.BookDAO;
import com.bookstore.dao.CartItemDAO;
import java.math.BigDecimal;
import com.bookstore.model.Book;
import com.bookstore.dao.ShoppingCartDAO;
import com.bookstore.model.CartItem;
import com.bookstore.model.Customer;
import com.bookstore.model.ShoppingCart;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author lequa
 */
public class ShoppingCartServices {
    private ShoppingCartDAO cartDAO;
    private CartItemDAO cartItemDAO;
    private BookDAO bookDAO;
    
    public ShoppingCartServices() {
        this.cartDAO = new ShoppingCartDAO();
        this.cartItemDAO = new CartItemDAO();
        this.bookDAO = new BookDAO();
    }
    
    public ShoppingCart getCartByCustomer(Customer customer) {
        if (customer == null || customer.getUserId() == null) {
            return null;
        }
        return cartDAO.findByCustomerId(customer.getUserId());
    }
    
    public ShoppingCart getCartByCustomerId(Integer customerId) {
        return cartDAO.findByCustomerId(customerId);
    }
    
    public ShoppingCart createCart(Customer customer) {
        ShoppingCart cart = new ShoppingCart(customer);
        cartDAO.save(cart);
        // Reload cart từ DB để có managed entity
        return cartDAO.findByCustomerId(customer.getUserId());
    }
    
    /**
     * Lấy giỏ hàng hiện có của khách hàng hoặc tạo mới nếu chưa tồn tại.
     * 
     * <p>Phương thức này kết hợp logic của {@link #getCartByCustomer(Customer)} và 
     * {@link #createCart(Customer)} để đảm bảo rằng luôn có một giỏ hàng hợp lệ
     * được trả về cho khách hàng. Nó sẽ tự động tạo giỏ hàng mới và lưu vào 
     * cơ sở dữ liệu nếu khách hàng chưa có giỏ hàng.</p>
     * 
     * <p><strong>Khi nào nên sử dụng:</strong></p>
     * <ul>
     *   <li>Sử dụng phương thức này khi cần đảm bảo khách hàng luôn có giỏ hàng 
     *       (ví dụ: khi thêm sản phẩm, xem giỏ hàng, hoặc merge giỏ hàng)</li>
     *   <li>Sử dụng {@link #getCartByCustomer(Customer)} khi chỉ cần kiểm tra xem 
     *       khách hàng có giỏ hàng hay không mà không muốn tạo mới</li>
     *   <li>Sử dụng {@link #createCart(Customer)} khi cần tạo giỏ hàng mới một cách 
     *       rõ ràng (ví dụ: sau khi đơn hàng được hoàn tất)</li>
     * </ul>
     * 
     * @param customer Đối tượng khách hàng cần lấy hoặc tạo giỏ hàng. Không được null.
     * @return Giỏ hàng hiện có hoặc mới tạo của khách hàng, đã được persist vào database.
     *         Trả về managed entity từ EntityManager để có thể tiếp tục thao tác.
     * @see #getCartByCustomer(Customer)
     * @see #createCart(Customer)
     */
    public ShoppingCart getOrCreateCartForCustomer(Customer customer) {
        ShoppingCart cart = getCartByCustomer(customer);
        if (cart == null) {
            cart = createCart(customer);
        }
        return cart;
    }
    
    public ShoppingCart getOrCreateGuestCart() {
        return new ShoppingCart();
    }
    
    public void addItemToCart(ShoppingCart cart, Integer bookId, Integer quantity) {
        Book book = bookDAO.findById(bookId);
        
        if(book == null) {
            throw new IllegalArgumentException("Không tìm thấy sách");
        }
        
        if (!book.isAvailable(quantity)) {
            throw new IllegalArgumentException("Không đủ hàng");
        }
        
        //Kiểm tra sách đã có trong giỏ hàng chưa?
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getBook().getBookId().equals(bookId))
                .findFirst();
        
        if(existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemDAO.update(item);
        } else {
            CartItem newItem = new CartItem(cart, book, quantity);
            cart.getItems().add(newItem);
            cartItemDAO.save(newItem);
        }
        
        calculateCartTotals(cart);
        cartDAO.update(cart);
    }
    
    public void updateItemQuantity(ShoppingCart cart, Integer itemId, Integer quantity) {
        CartItem item = cartItemDAO.findById(itemId);
        
        if(item == null || !item.getCart().getCartId().equals(cart.getCartId())) {
            throw new IllegalArgumentException("Không tìm thấy món hàng này trong giỏ hàng!");
        }
        
        if(!item.getBook().isAvailable(quantity)) {
            throw new IllegalArgumentException("Số lượng hàng còn lại không đủ!");
        }
        
        item.setQuantity(quantity);
        cartItemDAO.update(item);
        
        // Sync item trong cart.getItems() với giá trị đã update
        cart.getItems().stream()
            .filter(cartItem -> cartItem.getCartItemId().equals(itemId))
            .findFirst()
            .ifPresent(cartItem -> cartItem.setQuantity(quantity));
        
        calculateCartTotals(cart);
        cartDAO.update(cart);
    }
    
    public void calculateCartTotals(ShoppingCart cart) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalItems = 0;
        
        for(CartItem item : cart.getItems()) {
            BigDecimal itemTotal = item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
            totalItems += item.getQuantity();
        }
        
        cart.setTotalAmount(totalAmount);
        cart.setTotalItems(totalItems);
    }
    
    public void removeItemFromCart(ShoppingCart cart, Integer itemId) {
        // Kiểm tra item có tồn tại và thuộc về cart này không
        boolean itemExists = cart.getItems().stream()
                .anyMatch(item -> item.getCartItemId().equals(itemId));
        
        if (itemExists) {
            // Sử dụng removeIf với lambda so sánh theo ID thay vì object reference
            // vì CartItem không override equals()/hashCode()
            // orphanRemoval = true sẽ tự động xóa item từ DB khi update cart
            cart.getItems().removeIf(cartItem -> cartItem.getCartItemId().equals(itemId));
            
            calculateCartTotals(cart);
            cartDAO.update(cart);
        }
    }
    
    /**
     * Xóa tất cả items trong giỏ hàng
     * orphanRemoval = true sẽ tự động xóa các CartItem từ DB khi update cart
     */
    public void clearCart(ShoppingCart cart) {
        // Clear collection trước - orphanRemoval sẽ xóa từ DB khi update
        cart.getItems().clear();
        cart.setTotalAmount(BigDecimal.ZERO);
        cart.setTotalItems(0);
        cartDAO.update(cart);
    }
    
    public boolean validateCart(ShoppingCart cart) {
        for(CartItem item : cart.getItems()){
            if (!item.getBook().isAvailable(item.getQuantity())) {
                return false;
            }
        }
        return !cart.getItems().isEmpty();
    }
    
    public BigDecimal getItemSubtotal(CartItem item) {
        return item.getBook().getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));
    }
    
    public MergeResult mergeGuestCartToUserCart(ShoppingCart guestCart, Customer customer) {
        MergeResult result = new MergeResult();
        
        // Lấy hoặc tạo giỏ hàng của user từ DB (sử dụng method mới để xử lý detached entity)
        ShoppingCart userCart = getOrCreateCartForCustomer(customer);
        
        // Nếu giỏ hàng guest rỗng, không cần merge
        if (guestCart == null || guestCart.getItems().isEmpty()) {
            result.setMergedCart(userCart);
            return result;
        }
        
        // Bắt đầu merge
        for (CartItem guestItem : guestCart.getItems()) {
            Book book = guestItem.getBook();
            Integer guestQuantity = guestItem.getQuantity();
            
            // Tìm xem sách này đã có trong giỏ hàng user chưa
            Optional<CartItem> existingItem = userCart.getItems().stream()
                    .filter(item -> item.getBook().getBookId().equals(book.getBookId()))
                    .findFirst();
            
            if (existingItem.isPresent()) {
                // TRƯỜNG HỢP 2: Sản phẩm trùng lặp - Cộng dồn số lượng
                CartItem userItem = existingItem.get();
                int currentQty = userItem.getQuantity();
                int requestedQty = currentQty + guestQuantity;
                int availableStock = book.getQuantityInStock();
                
                if (requestedQty <= availableStock) {
                    // Đủ hàng - cập nhật số lượng mới
                    userItem.setQuantity(requestedQty);
                    cartItemDAO.update(userItem);
                    result.addMergedItem(book.getTitle(), guestQuantity, requestedQty);
                } else {
                    // Không đủ hàng - set về mức tối đa
                    userItem.setQuantity(availableStock);
                    cartItemDAO.update(userItem);
                    result.addLimitedItem(book.getTitle(), requestedQty, availableStock);
                }
            } else {
                // TRƯỜNG HỢP 1: Sản phẩm mới - Thêm vào giỏ hàng user
                if (guestQuantity <= book.getQuantityInStock()) {
                    CartItem newItem = new CartItem(userCart, book, guestQuantity);
                    userCart.getItems().add(newItem);
                    cartItemDAO.save(newItem);
                    result.addNewItem(book.getTitle(), guestQuantity);
                } else {
                    // Số lượng vượt quá tồn kho
                    CartItem newItem = new CartItem(userCart, book, book.getQuantityInStock());
                    userCart.getItems().add(newItem);
                    cartItemDAO.save(newItem);
                    result.addLimitedItem(book.getTitle(), guestQuantity, book.getQuantityInStock());
                }
            }
        }
        
        // Tính toán lại tổng tiền
        calculateCartTotals(userCart);
        cartDAO.update(userCart);
        
        result.setMergedCart(userCart);
        return result;
    }
    
    public static class MergeResult {
        private ShoppingCart mergedCart;
        private List<String> newItems = new ArrayList<>();
        private List<String> mergedItems = new ArrayList<>();
        private List<String> limitedItems = new ArrayList<>();
        
        public void addNewItem(String bookTitle, int quantity) {
            newItems.add(String.format("Đã thêm '%s' (x%d) vào giỏ hàng của bạn", bookTitle, quantity));
        }
        
        public void addMergedItem(String bookTitle, int addedQty, int totalQty) {
            mergedItems.add(String.format("'%s': Đã tăng số lượng từ %d lên thành %d cuốn", 
                bookTitle, totalQty - addedQty, totalQty));
        }
        
        public void addLimitedItem(String bookTitle, int requestedQty, int availableQty) {
            limitedItems.add(String.format("'%s': đã giới hạn đến %d cuốn (theo yêu cầu là %d, không đủ số lượng hàng trong kho)", bookTitle, availableQty, requestedQty));
        }
        
        public boolean hasWarnings() {
            return !limitedItems.isEmpty();
        }
        
        public boolean hasChanges() {
            return !newItems.isEmpty() || !mergedItems.isEmpty() || !limitedItems.isEmpty();
        }
        
        public String getSummaryMessage() {
            StringBuilder sb = new StringBuilder();
            
            if (!newItems.isEmpty()) {
                sb.append("<strong>Đã thêm vào quyển sách mới:</strong><br>");
                newItems.forEach(msg -> sb.append("• ").append(msg).append("<br>"));
            }
            
            if (!mergedItems.isEmpty()) {
                sb.append("<strong>Đã cập nhật số lượng:</strong><br>");
                mergedItems.forEach(msg -> sb.append("• ").append(msg).append("<br>"));
            }
            
            if (!limitedItems.isEmpty()) {
                sb.append("<strong>Giới hạn số lượng trong kho:</strong><br>");
                limitedItems.forEach(msg -> sb.append("⚠ ").append(msg).append("<br>"));
            }
            
            return sb.toString();
        }
        
        // Getters and Setters
        public ShoppingCart getMergedCart() {
            return mergedCart;
        }
        
        public void setMergedCart(ShoppingCart mergedCart) {
            this.mergedCart = mergedCart;
        }
        
        public List<String> getNewItems() {
            return newItems;
        }
        
        public List<String> getMergedItems() {
            return mergedItems;
        }
        
        public List<String> getLimitedItems() {
            return limitedItems;
        }
    }  
}
