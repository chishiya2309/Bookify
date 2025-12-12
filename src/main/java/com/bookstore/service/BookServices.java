/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookstore.service;

import com.bookstore.dao.BookDAO;
import com.bookstore.model.Author;
import com.bookstore.model.Book;
import com.bookstore.model.Category;

import java.util.List;

public class BookServices {
    public List<Book> getALlBooks() {
        return BookDAO.getAllBooks();
    }
    public Book getBookById(Integer id) {
        return BookDAO.getBookById(id);
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
