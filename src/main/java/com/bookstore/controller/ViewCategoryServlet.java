package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.model.Category; // Nhớ import model Category
import com.bookstore.service.BookServices;
import com.bookstore.service.CustomerServices;
import com.bookstore.service.JwtAuthHelper;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/view_category")
public class ViewCategoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private BookServices bookServices;
    private CustomerServices customerServices;

    @Override
    public void init() throws ServletException {
        super.init();
        this.bookServices = new BookServices();
        this.customerServices = new CustomerServices();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        // 1. Giữ trạng thái đăng nhập (để Header không bị lỗi)
        JwtAuthHelper.checkLoginStatus(request);
        
        // 2. Lấy danh sách Categories cho Menu Header
        List<Category> listCats = customerServices.listAllCategories();
        request.setAttribute("listCategories", listCats);

        // 3. Xử lý lấy sách theo danh mục
        String categoryIdParam = request.getParameter("id");
        String categoryName = "Unknown Category"; // Tên mặc định

        if (categoryIdParam != null) {
            try {
                int categoryId = Integer.parseInt(categoryIdParam);
                
                // Lấy danh sách sách
                List<Book> listBooks = bookServices.listBooksByCategory(categoryId);
                request.setAttribute("listBooks", listBooks);

                // Tìm tên danh mục để hiển thị tiêu đề cho đẹp
                // (Duyệt qua listCats đã lấy ở bước 2 để tìm tên)
                for (Category c : listCats) {
                    if (c.getCategoryId() == categoryId) {
                        categoryName = c.getName();
                        break;
                    }
                }
            } catch (NumberFormatException e) {
                log("Invalid category id: " + categoryIdParam, e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid category id.");
                return;
            } catch (Exception e) {
                log("Error while loading category view", e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load category at this time.");
                return;
            }
        }
        
        request.setAttribute("categoryName", categoryName);

        // 4. Chuyển sang file giao diện
        RequestDispatcher dispatcher = request.getRequestDispatcher("customer/book_list.jsp");
        dispatcher.forward(request, response);
    }
}