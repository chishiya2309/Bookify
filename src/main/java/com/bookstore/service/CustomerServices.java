package com.bookstore.service;

import com.bookstore.dao.CustomerHomePageDAO;
import com.bookstore.model.Book;
import java.util.List;

public class CustomerServices {


    public List<Book> listNewBooks() {
        return CustomerHomePageDAO.listNewBooks();
    }

    public List<Book> listBestSellingBooks() {
        return CustomerHomePageDAO.listBestSellingBooks();
    }

    public List<Book> listMostFavoredBooks() {
        return CustomerHomePageDAO.listMostFavoredBooks();
    }
    
}