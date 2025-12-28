package com.bookstore.service;

import com.bookstore.dao.AuthorDAO;
import com.bookstore.model.Author;

import java.util.List;

public class AuthorServices {

    private final AuthorDAO authorDAO = new AuthorDAO();
    private static final int ADMIN_PAGE_SIZE = 20;

    public void createAuthor(Author author) {
        authorDAO.createAuthor(author);
    }

    public void updateAuthor(Author author) {
        authorDAO.updateAuthor(author);
    }

    public void deleteAuthor(Integer authorId) {
        authorDAO.deleteAuthor(authorId);
    }

    public Author getAuthorById(Integer authorId) {
        return authorDAO.getAuthorById(authorId);
    }

    public List<Author> getAuthorsForAdmin(String name, int page, int size) {
        return authorDAO.getAuthorsForAdmin(name, page, size);
    }

    public List<Author> getAuthorsForAdmin(String name, int page) {
        return getAuthorsForAdmin(name, page, ADMIN_PAGE_SIZE);
    }

    public long countAuthorsForAdmin(String name) {
        return authorDAO.countAuthorsForAdmin(name);
    }

    /**
     * Đếm số sách của tác giả để kiểm tra trước khi xoá
     */
    public long countBooksByAuthor(Integer authorId) {
        return authorDAO.countBooksByAuthor(authorId);
    }
}