package com.bookstore.service;
import com.bookstore.dao.BookDAO;
import com.bookstore.model.Book;
import com.bookstore.model.Review;
import com.bookstore.model.Author;
import com.bookstore.model.Book;
import com.bookstore.model.Category;
import java.util.List;

public class BookServices {

    private final BookDAO bookDAO = new BookDAO();

    public Book getBookById(Integer bookId) {
        return BookDAO.getBookById(bookId);
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

    public List<Book> getAllBooks() {
        return BookDAO.getAllBooks();
    }
    
    public void updateBook(Book book) {
        BookDAO.updateBook(book);
    }
    public void deleteBook(Integer id) {
        BookDAO.deleteBook(id);
    }
    public void createBook(Book book) {
        BookDAO.createBook(book);
    }
    public List<Category> getAllCategories() {
        return BookDAO.getAllCategories();
    }
    public List<Author> getAllAuthors() {
        return BookDAO.getAllAuthors();
    }
    public Category findCategoryById(Integer id) {
        return BookDAO.findCategoryById(id);
    }
    public Author findAuthorById(Integer id) {
        return BookDAO.findAuthorById(id);
    }
}
