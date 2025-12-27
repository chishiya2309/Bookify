package com.bookstore.controller.admin;

import com.bookstore.model.Category;
import com.bookstore.service.CategoryService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/categories")
public class CategoryServlet extends HttpServlet {

    private final CategoryService categoryService = new CategoryService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null || action.isEmpty()) {
            action = "list";
        }

        String url = "/admin/category/show.jsp";

        try {
            switch (action) {
                case "list":
                    listCategories(request);
                    break;
                case "showCreate":
                    url = "/admin/category/create.jsp";
                    break;
                case "create":
                    createCategory(request);
                    listCategories(request);
                    break;
                case "showUpdate":
                    showUpdateForm(request);
                    url = "/admin/category/update.jsp";
                    break;
                case "update":
                    updateCategory(request);
                    listCategories(request);
                    break;
                case "delete":
                    deleteCategory(request);
                    listCategories(request);
                    break;
                default:
                    listCategories(request);
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            request.setAttribute("errorMessage", e.getMessage());
            if ("showCreate".equals(action)) {
                url = "/admin/category/create.jsp";
            } else if ("showUpdate".equals(action) || "update".equals(action)) {
                url = "/admin/category/update.jsp";
            }
        }

        getServletContext().getRequestDispatcher(url).forward(request, response);
    }

    private void listCategories(HttpServletRequest request) {
        List<Category> categories = categoryService.listAll();
        request.setAttribute("categories", categories);
    }

    private void showUpdateForm(HttpServletRequest request) {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            try {
                Category category = categoryService.findById(Integer.parseInt(idStr));
                request.setAttribute("category", category);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("ID danh mục không hợp lệ");
            }
        }
    }

    private void createCategory(HttpServletRequest request) {
        String name = request.getParameter("name");
        categoryService.create(name);
        request.setAttribute("message", "Thêm danh mục thành công");
    }

    private void updateCategory(HttpServletRequest request) {
        try {
            Integer id = Integer.parseInt(request.getParameter("id"));
            String name = request.getParameter("name");
            categoryService.update(id, name);
            request.setAttribute("message", "Cập nhật danh mục thành công");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID danh mục không hợp lệ");
        }
    }

    private void deleteCategory(HttpServletRequest request) {
        try {
            Integer id = Integer.parseInt(request.getParameter("id"));
            categoryService.delete(id);
            request.setAttribute("message", "Đã xóa danh mục");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID danh mục không hợp lệ");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}

