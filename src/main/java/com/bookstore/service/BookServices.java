package com.bookstore.service;

import com.bookstore.dao.BookDAO;
import com.bookstore.model.Book;
import com.bookstore.model.Review;

import java.util.List;

public class BookServices {

    private final BookDAO bookDAO = new BookDAO();

    public Book getBookById(Integer bookId) {
        return bookDAO.findById(bookId);
    }

    public List<Review> getReviews(Integer bookId, int page) {
        return bookDAO.getReviews(bookId, page, 10);
    }

    public long getTotalReviews(Integer bookId) {
        return bookDAO.countReviews(bookId);
    }

    public Double getAverageRating(Integer bookId) {
        return bookDAO.getAverageRating(bookId);
    }
}