<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Shopping Cart</title>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <link rel="stylesheet" href="styles/cart.css"/>
    </head>
    <body>
        <main>
            <article>
                <header>
                    <h1>
                        <i class="fas fa-shopping-cart"></i>
                        Giỏ hàng của bạn
                        <c:if test="${not empty cart and not empty cart.items}">
                            <span class="cart-badge">${cart.totalItems} items</span>
                        </c:if>
                    </h1>
                    <c:if test="${not empty cart and not empty cart.items}">
                        <form action="cart" method="post">
                            <input type="hidden" name="action" value="clear">
                            <button type="submit" class="btn btn-clear" 
                                    onclick="return confirm('Bạn có chắc là muốn xoá giỏ hàng không?')">
                                <i class="fas fa-trash-alt"></i>
                                Clear Cart
                            </button>
                        </form>
                    </c:if>
                </header>
                
                <c:choose>
                    <c:when test="${empty cart or empty cart.items}">
                        <section class="empty-cart">
                            <i class="fas fa-shopping-cart"></i>
                            <p style="font-size: 20px; margin-bottom: 8px; font-weight: 600; color: #212529;">Giỏ hàng của bạn đang trống</p>
                            <p style="margin-bottom: 24px; color: #6C757D;">Add some books to get started!</p>
                            <a href="books" class="btn btn-primary">
                                <i class="fas fa-book"></i>
                                Tiếp tục mua sắm
                            </a>
                        </section>
                    </c:when>
                    <c:otherwise>
                        <form action="cart" method="post" id="cartForm">
                            <input type="hidden" name="action" value="update">
                            <table>
                                <thead>
                                    <tr>
                                        <th class="text-center">No</th>
                                        <th>Book</th>
                                        <th class="text-center">Quantity</th>
                                        <th class="text-right">Price</th>
                                        <th class="text-right">Subtotal</th>
                                        <th class="text-center">Action</th>
                                    </tr>
                                </thead>
                            </table>
                        </form>
                    </c:otherwise>
                </c:choose>
            </article>
        </main>
    </body>
</html>
