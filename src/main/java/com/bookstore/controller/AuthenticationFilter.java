package com.bookstore.controller;

import com.bookstore.service.JwtUtil;
import com.bookstore.model.Customer;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

// Chỉ áp dụng cho các đường dẫn của khách hàng
@WebFilter(urlPatterns = {"/customer/*"})
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false); // Không tự ý tạo session mới ở đây

        // 1. Kiểm tra xem đã có đối tượng customer trong session chưa
        boolean isLoggedIn = (session != null && session.getAttribute("customer") != null);

        if (!isLoggedIn) {
            // 2. Nếu chưa có session, thử kiểm tra JWT trong Cookie
            String token = null;
            Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if ("jwt_token".equals(c.getName())) {
                        token = c.getValue();
                        break;
                    }
                }
            }

            // 3. Xác thực Token
            if (token != null && JwtUtil.validateToken(token)) {
                String role = JwtUtil.extractRole(token);
                
                // Nếu là khách hàng, bạn có thể cho qua hoặc nạp lại thông tin tối thiểu
                if ("CUSTOMER".equals(role)) {
                    // Tạo lại session nếu nó đã mất
                    if (session == null) session = req.getSession(true);
                    
                    // Lưu ý quan trọng: Tại đây bạn nên nạp lại email vào session 
                    // để Servlet con (OrderServlet) có thể dùng email này load lại Customer từ DB
                    session.setAttribute("userEmail", JwtUtil.extractEmail(token));
                    session.setAttribute("userRole", "CUSTOMER");
                    
                    chain.doFilter(request, response);
                    return;
                }
            }

            // 4. Nếu không có token hoặc token sai -> Chuyển hướng về trang login
            // Lưu ý: Dùng đường dẫn đầy đủ từ contextPath
            res.sendRedirect(req.getContextPath() + "/login.jsp"); 
            return;
        }

        // Nếu đã login rồi thì cho đi tiếp vào Servlet (OrderServlet)
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}