package com.bookstore.service;

import com.bookstore.dao.StatisticsDAO;
import com.bookstore.model.Order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class StatisticsService {

    private final StatisticsDAO statisticsDAO;

    public StatisticsService() {
        this.statisticsDAO = new StatisticsDAO();
    }

    // ================= REVENUE METRICS =================

    public BigDecimal getTotalRevenue() {
        return statisticsDAO.getTotalRevenue();
    }

    public BigDecimal getTodayRevenue() {
        return statisticsDAO.getTodayRevenue();
    }

    public BigDecimal getThisMonthRevenue() {
        return statisticsDAO.getThisMonthRevenue();
    }

    public BigDecimal getThisYearRevenue() {
        return statisticsDAO.getThisYearRevenue();
    }

    public BigDecimal getAverageOrderValue() {
        return statisticsDAO.getAverageOrderValue();
    }

    // ================= ORDER STATISTICS =================

    public List<Object[]> getOrderCountByStatus() {
        return statisticsDAO.getOrderCountByStatus();
    }

    public List<Object[]> getOrderCountByPaymentMethod() {
        return statisticsDAO.getOrderCountByPaymentMethod();
    }

    public long getTotalOrderCount() {
        return statisticsDAO.getTotalOrderCount();
    }

    public long getTotalCustomerCount() {
        return statisticsDAO.getTotalCustomerCount();
    }

    public long getTotalBookCount() {
        return statisticsDAO.getTotalBookCount();
    }

    // ================= TOP ANALYTICS =================

    public List<Object[]> getTopSellingBooks(int limit) {
        return statisticsDAO.getTopSellingBooks(limit);
    }

    public List<Object[]> getTopCustomers(int limit) {
        return statisticsDAO.getTopCustomers(limit);
    }

    // ================= REVENUE TRENDS =================

    public List<Object[]> getRevenueByMonth(int year) {
        return statisticsDAO.getRevenueByMonth(year);
    }

    public List<Object[]> getRevenueByCurrentYear() {
        return statisticsDAO.getRevenueByMonth(LocalDate.now().getYear());
    }

    // ================= INVENTORY ALERTS =================

    public List<Object[]> getLowStockBooks(int threshold) {
        return statisticsDAO.getLowStockBooks(threshold);
    }

    public List<Object[]> getLowStockBooks() {
        return getLowStockBooks(10); // Default threshold
    }

    // ================= RECENT ORDERS =================

    public List<Order> getRecentOrders(int limit) {
        return statisticsDAO.getRecentOrders(limit);
    }

    public List<Order> getRecentOrders() {
        return getRecentOrders(5); // Default limit
    }

    // ================= LEGACY METHODS =================

    public List<Object[]> categoryRevenue() {
        return statisticsDAO.getCategoryRevenue();
    }

    public List<Object[]> bookRevenue() {
        return statisticsDAO.getBookRevenue();
    }

    public List<Object[]> monthlyRevenue() {
        return statisticsDAO.getMonthlyRevenue();
    }
}
