package com.bookstore.controller.admin;

import com.bookstore.model.Order;
import com.bookstore.service.StatisticsService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/admin/metrics")
public class StatisticsServlet extends HttpServlet {

    private final StatisticsService statisticsService = new StatisticsService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String type = request.getParameter("type");
        if (type == null)
            type = "dashboard";

        switch (type) {
            case "book":
                request.setAttribute("data", statisticsService.bookRevenue());
                request.getRequestDispatcher("/admin/metric/book_metric.jsp").forward(request, response);
                break;

            case "monthly":
                request.setAttribute("data", statisticsService.monthlyRevenue());
                request.getRequestDispatcher("/admin/metric/monthly_revenue.jsp").forward(request, response);
                break;

            case "category":
                request.setAttribute("data", statisticsService.categoryRevenue());
                request.getRequestDispatcher("/admin/metric/category_metric.jsp").forward(request, response);
                break;

            default:
                // Dashboard view - load all statistics
                loadDashboardMetrics(request);
                request.getRequestDispatcher("/admin/statistics.jsp").forward(request, response);
        }
    }

    private void loadDashboardMetrics(HttpServletRequest request) {
        // ===== KPI Metrics =====
        BigDecimal totalRevenue = statisticsService.getTotalRevenue();
        long totalOrders = statisticsService.getTotalOrderCount();
        long totalCustomers = statisticsService.getTotalCustomerCount();
        long totalBooks = statisticsService.getTotalBookCount();
        BigDecimal avgOrderValue = statisticsService.getAverageOrderValue();

        request.setAttribute("totalRevenue", totalRevenue);
        request.setAttribute("totalOrders", totalOrders);
        request.setAttribute("totalCustomers", totalCustomers);
        request.setAttribute("totalBooks", totalBooks);
        request.setAttribute("avgOrderValue", avgOrderValue);

        // ===== Revenue Summary =====
        BigDecimal todayRevenue = statisticsService.getTodayRevenue();
        BigDecimal thisMonthRevenue = statisticsService.getThisMonthRevenue();
        BigDecimal thisYearRevenue = statisticsService.getThisYearRevenue();

        request.setAttribute("todayRevenue", todayRevenue);
        request.setAttribute("thisMonthRevenue", thisMonthRevenue);
        request.setAttribute("thisYearRevenue", thisYearRevenue);

        // ===== Charts Data =====
        // Order by status (for pie chart)
        List<Object[]> ordersByStatus = statisticsService.getOrderCountByStatus();
        request.setAttribute("ordersByStatus", ordersByStatus);

        // Order by payment method (for pie chart)
        List<Object[]> ordersByPayment = statisticsService.getOrderCountByPaymentMethod();
        request.setAttribute("ordersByPayment", ordersByPayment);

        // Revenue by month (for bar chart)
        int currentYear = LocalDate.now().getYear();
        List<Object[]> revenueByMonth = statisticsService.getRevenueByMonth(currentYear);
        request.setAttribute("revenueByMonth", revenueByMonth);
        request.setAttribute("currentYear", currentYear);

        // ===== Tables Data =====
        // Top 10 selling books
        List<Object[]> topBooks = statisticsService.getTopSellingBooks(10);
        request.setAttribute("topBooks", topBooks);

        // Top 10 customers
        List<Object[]> topCustomers = statisticsService.getTopCustomers(10);
        request.setAttribute("topCustomers", topCustomers);

        // Low stock books (threshold = 10)
        List<Object[]> lowStockBooks = statisticsService.getLowStockBooks(10);
        request.setAttribute("lowStockBooks", lowStockBooks);

        // Recent orders
        List<Order> recentOrders = statisticsService.getRecentOrders(5);
        request.setAttribute("recentOrders", recentOrders);
    }
}
