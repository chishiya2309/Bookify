package com.bookstore.service;

import com.bookstore.dao.CategoryDAO;
import com.bookstore.model.Category;

import java.util.List;

public class CategoryService {

    public void createCategory(Category category) {
        CategoryDAO.createCategory(category);
    }

    public void updateCategory(Category category) {
        CategoryDAO.updateCategory(category);
    }

    public void deleteCategory(Integer categoryId) {
        CategoryDAO.deleteCategory(categoryId);
    }

    public List<Category> getAllCategories() {
        return CategoryDAO.getAllCategories();
    }

    public Category getCategoryById(Integer categoryId) {
        return CategoryDAO.getCategoryById(categoryId);
    }
}