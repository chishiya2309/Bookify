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
        return cartDAO.findByCustomer(customer);
    }
    
    public ShoppingCart createCart(Customer customer) {
        ShoppingCart cart = new ShoppingCart(customer);
        cartDAO.save(cart);
        return cart;
    }
    
//    public MergeResult mergeGuestCartToUserCart(ShoppingCart guestCart, Customer customer) {
//        
//    }
    
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
        }else {
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
        CartItem item = cartItemDAO.findById(itemId);
        if (item != null && item.getCart().getCartId().equals(cart.getCartId())) {
            cart.getItems().remove(item);
            cartItemDAO.delete(itemId);
            
            calculateCartTotals(cart);
            cartDAO.update(cart);
        }
    }
    
    public void addItem(Book book, int quantity) {
        
    }
    
    public void removeItem(Integer cartItemId) {
        
    }
    
    public void updateQuantity(Integer cartItemId, int newQuantity) {
        
    }
    
    public void clearCart(ShoppingCart cart) {
        cartItemDAO.deleteByCart(cart.getCartId());
        cart.getItems().clear();
        cart.setTotalAmount(BigDecimal.ZERO);
        cart.setTotalItems(0);
        cartDAO.update(cart);
    }
    
    private void calculateTotals() {
        
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
            limitedItems.add(String.format("'%s': Đã giới hạn đến %d cuốn (theo yêu cầu là %d, không đủ số lượng hàng trong kho)", bookTitle, availableQty, requestedQty));
        }
    }
}
