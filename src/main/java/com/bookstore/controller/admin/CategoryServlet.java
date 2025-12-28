package com.bookstore.controller.admin;

import com.bookstore.model.Category;
import com.bookstore.service.CategoryService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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
                    listCategories(request, response);
                    break;

                case "showCreate":
                    url = "/admin/category/create.jsp";
                    break;

                case "create":
                    createCategory(request, response);
                    url = "/admin/category/show.jsp";
                    break;

                case "showUpdate":
                    showUpdateForm(request, response);
                    url = "/admin/category/update.jsp";
                    break;

                case "update":
                    updateCategory(request, response);
                    url = "/admin/category/show.jsp";
                    break;

                case "delete":
                    deleteCategory(request, response);
                    url = "/admin/category/show.jsp";
                    break;

                default:
                    listCategories(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
        }

        getServletContext().getRequestDispatcher(url).forward(request, response);
    }

    private void listCategories(HttpServletRequest request, HttpServletResponse response) {
        List<Category> listCategories = categoryService.getAllCategories();
        request.setAttribute("listCategories", listCategories);
    }

    private void showUpdateForm(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            int id = Integer.parseInt(idStr);
            Category category = categoryService.getCategoryById(id);
            session.setAttribute("category", category);
        }
    }

    private void createCategory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Category newCategory = new Category();
        readCategoryFields(newCategory, request);

        String validationError = validateCategory(newCategory);
        if (validationError != null) {
            request.setAttribute("errorMessage", validationError);
            getServletContext().getRequestDispatcher("/admin/category/create.jsp").forward(request, response);
            return;
        }

        categoryService.createCategory(newCategory);
        request.setAttribute("message", "Created successfully!");
        listCategories(request, response);
    }

    private void updateCategory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");

        if (idStr != null && !idStr.isEmpty()) {
            int id = Integer.parseInt(idStr);
            Category category = categoryService.getCategoryById(id);

            if (category != null) {
                readCategoryFields(category, request);

                String validationError = validateCategory(category);
                if (validationError != null) {
                    request.setAttribute("errorMessage", validationError);
                    request.setAttribute("category", category);
                    getServletContext().getRequestDispatcher("/admin/category/update.jsp").forward(request, response);
                    return;
                }

                categoryService.updateCategory(category);
                request.setAttribute("message", "Updated successfully!");
            }
        }

        listCategories(request, response);
    }

    private void deleteCategory(HttpServletRequest request, HttpServletResponse response) {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            int id = Integer.parseInt(idStr);
            
            // Kiểm tra xem danh mục có sách liên kết không
            long bookCount = com.bookstore.dao.CategoryDAO.countBooksByCategory(id);
            if (bookCount > 0) {
                request.setAttribute("errorMessage", 
                    "Không thể xoá danh mục này vì có " + bookCount + " sách thuộc danh mục. Vui lòng chuyển hoặc xoá các sách trước.");
            } else {
                categoryService.deleteCategory(id);
                request.setAttribute("message", "Xoá danh mục thành công!");
            }
        }
        listCategories(request, response);
    }

    private void readCategoryFields(Category category, HttpServletRequest request) {
        String name = request.getParameter("name");
        category.setName(name);
    }

    private String validateCategory(Category category) {
        // Validate Name
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            return "Tên danh mục không được để trống.";
        }
        if (category.getName().length() > 100) {
            return "Tên danh mục không được vượt quá 100 ký tự.";
        }

        return null;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}