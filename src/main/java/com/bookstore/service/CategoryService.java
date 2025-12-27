package com.bookstore.service;

import com.bookstore.dao.CategoryDAO;
import com.bookstore.model.Category;

import java.util.List;

public class CategoryService {

    public List<Category> listAll() {
        return CategoryDAO.findAll();
    }

    public Category findById(Integer id) {
        return CategoryDAO.findById(id);
    }

    public Category findByName(String name) {
        if (name == null) {
            return null;
        }
        return CategoryDAO.findByName(name.trim());
    }

    public String validateName(String name, Integer existingId) {
        if (name == null || name.trim().isEmpty()) {
            return "Tên danh mục không được để trống";
        }
        String trimmed = name.trim();
        if (trimmed.length() > 100) {
            return "Tên danh mục tối đa 100 ký tự";
        }
        Category found = findByName(trimmed);
        if (found != null && (existingId == null || !found.getCategoryId().equals(existingId))) {
            return "Tên danh mục đã tồn tại";
        }
        return null;
    }

    public boolean create(String name) {
        String error = validateName(name, null);
        if (error != null) {
            throw new IllegalArgumentException(error);
        }
        Category category = new Category();
        category.setName(name.trim());
        CategoryDAO.create(category);
        return true;
    }

    public boolean update(Integer id, String name) {
        if (id == null) {
            throw new IllegalArgumentException("Thiếu ID danh mục");
        }
        String error = validateName(name, id);
        if (error != null) {
            throw new IllegalArgumentException(error);
        }
        Category category = findById(id);
        if (category == null) {
            throw new IllegalArgumentException("Danh mục không tồn tại");
        }
        category.setName(name.trim());
        CategoryDAO.update(category);
        return true;
    }

    public boolean delete(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Thiếu ID danh mục");
        }
        long linkedBooks = CategoryDAO.countBooks(id);
        if (linkedBooks > 0) {
            throw new IllegalStateException("Không thể xóa danh mục đang có sách");
        }
        CategoryDAO.delete(id);
        return true;
    }
}
