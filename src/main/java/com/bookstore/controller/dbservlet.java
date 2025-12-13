package com.bookstore.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/test-db")
public class dbservlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<html><body>");
        out.println("<h2>Kiểm tra kết nối Database & Hibernate Mapping</h2>");

        EntityManagerFactory emf = null;
        EntityManager em = null;

        try {
            out.println("<p>Dang khoi tao Persistence Unit 'bookify_pu'...</p>");

            // 1. Dòng này sẽ kích hoạt Hibernate đọc file persistence.xml và map các Entity
            // Lưu ý: "bookify_pu" là tên lấy từ log bạn gửi trước đó.
            emf = Persistence.createEntityManagerFactory("bookify_pu");

            // 2. Thử tạo EntityManager
            em = emf.createEntityManager();

            // 3. Thử chạy một câu query đơn giản nhất để test kết nối vật lý
            out.println("<p>Dang test query 'SELECT 1'...</p>");
            em.getTransaction().begin();
            Object result = em.createNativeQuery("SELECT 1").getSingleResult();
            em.getTransaction().commit();

            out.println("<h3 style='color:green'>✅ KẾT NỐI THÀNH CÔNG!</h3>");
            out.println("<p>Database đang hoạt động tốt. Kết quả test: " + result + "</p>");

        } catch (Exception e) {
            out.println("<h3 style='color:red'>❌ KẾT NỐI THẤT BẠI!</h3>");
            out.println("<div style='background:#f8d7da; padding:10px; border:1px solid red; white-space: pre-wrap;'>");
            e.printStackTrace(out); // In lỗi chi tiết ra màn hình để đọc
            out.println("</div>");
        } finally {
            // Dọn dẹp
            if (em != null && em.isOpen()) em.close();
            if (emf != null && emf.isOpen()) emf.close();
        }

        out.println("</body></html>");
    }
}