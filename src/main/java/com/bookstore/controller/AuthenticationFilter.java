package com.bookstore.controller;

import com.bookstore.service.JwtUtil;
import com.bookstore.model.Customer;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

// Chỉ áp dụng cho các đường dẫn của khách hàng
@WebFilter(urlPatterns = { "/customer/*" })
public class AuthenticationFilter implements Filter {

    // Các đường dẫn không cần đăng nhập (guest có thể truy cập)
    private static final List<String> GUEST_ALLOWED_PATHS = Arrays.asList(
            "/customer/login.jsp",
            "/customer/register.jsp",
            "/customer/cart", // Giỏ hàng Servlet
            "/customer/cart.jsp", // Giỏ hàng JSP
            "/customer/book_detail.jsp",
            "/customer/search_result.jsp",
            "/customer/CustomerHomePage.jsp");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Kiểm tra nếu là đường dẫn cho phép guest truy cập
        String requestPath = req.getRequestURI().substring(req.getContextPath().length());
        if (isGuestAllowedPath(requestPath)) {
            chain.doFilter(request, response);
            return;
        }

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
                    if (session == null)
                        session = req.getSession(true);

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
            res.sendRedirect(req.getContextPath() + "/customer/login.jsp");
            return;
        }

        // Nếu đã login rồi thì cho đi tiếp vào Servlet (OrderServlet)
        chain.doFilter(request, response);
    }

    /**
     * Kiểm tra xem đường dẫn có cho phép guest truy cập không
     */
    private boolean isGuestAllowedPath(String path) {
        for (String allowedPath : GUEST_ALLOWED_PATHS) {
            if (path.equals(allowedPath) || path.startsWith(allowedPath + "?")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}