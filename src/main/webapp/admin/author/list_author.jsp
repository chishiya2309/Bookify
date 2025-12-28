<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Tác giả - Admin Bookify</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background: #f4f4f4; }
        .container { max-width: 1400px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
        h1 { text-align: center; color: #333; margin-bottom: 10px; }
        .header-actions { text-align: right; margin-bottom: 20px; }
        .btn { padding: 10px 16px; background: #28a745; color: white; text-decoration: none; border-radius: 4px; font-size: 16px; }
        .btn:hover { background: #218838; }
        .search-filter { background: #f8f9fa; padding: 20px; border-radius: 8px; margin-bottom: 20px; display: flex; flex-wrap: wrap; gap: 15px; align-items: end; }
        .search-filter input, .search-filter button { padding: 10px; font-size: 16px; border-radius: 4px; border: 1px solid #ccc; }
        .search-filter button { background: #007bff; color: white; border: none; cursor: pointer; min-width: 120px; }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
            table-layout: auto;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 12px;
            text-align: center;     /* Căn giữa toàn bộ nội dung */
            vertical-align: middle; /* Căn giữa theo chiều dọc */
            word-wrap: break-word;
            word-break: break-word;
        }
        th { background: #007bff; color: white; }
        td:nth-child(6) { text-align: left; } /* Chỉ cột Hành động căn trái cho đẹp */

        /* Chiều rộng các cột */
        th:nth-child(1) { width: 60px; }   /* ID */
        th:nth-child(2) { width: 100px; }  /* Ảnh */
        th:nth-child(3) { width: 180px; }  /* Tên tác giả */
        th:nth-child(4) { width: auto; }   /* Tiểu sử - chiếm phần còn lại */
        th:nth-child(5) { width: 90px; }   /* Số sách */
        th:nth-child(6) { width: 160px; }  /* Hành động */

        tr:nth-child(even) { background: #f9f9f9; }
        .author-photo { width: 80px; height: 80px; object-fit: cover; border-radius: 8px; border: 1px solid #eee; }

        .bio-preview {
            overflow: hidden;
            line-height: 1.5;
            white-space: nowrap;           /* Không xuống dòng */
            text-align: left;
            padding: 0 8px;
            text-overflow: ellipsis;       /* Hiển thị ... khi text dài */
            max-width: 400px;              /* Giới hạn chiều rộng */
        }

        .bio-preview:hover {
            cursor: help;
        }

        .action-btn {
            padding: 6px 12px;
            margin: 0 4px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            color: white;
            text-decoration: none;
            display: inline-block;
            font-size: 14px;
        }
        .edit-btn { background: #ffc107; color: black; }
        .delete-btn { background: #dc3545; }

        .pagination { text-align: center; margin: 30px 0; }
        .pagination a { padding: 10px 15px; margin: 0 5px; background: #007bff; color: white; text-decoration: none; border-radius: 4px; }
        .pagination .current { padding: 10px 15px; background: #0056b3; color: white; border-radius: 4px; }

        .message { padding: 15px; margin-bottom: 20px; border-radius: 4px; font-weight: bold; }
        .success { background: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .error { background: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }

        @media (max-width: 768px) {
            .search-filter { flex-direction: column; align-items: stretch; }
            table, thead, tbody, th, td, tr { display: block; }
            thead tr { position: absolute; top: -9999px; left: -9999px; }
            tr { border: 1px solid #ccc; margin-bottom: 15px; border-radius: 8px; overflow: hidden; background: #fff; }
            td { border: none; border-bottom: 1px solid #eee; position: relative; padding-left: 50%; text-align: right; }
            td:before { content: attr(data-label); position: absolute; left: 12px; width: 45%; font-weight: bold; white-space: nowrap; text-align: left; }
            td:nth-child(6) { text-align: center; } /* Hành động trên mobile căn giữa */
            .action-btn { display: block; margin: 5px 0; text-align: center; width: 100%; }
            .bio-preview { text-align: left; padding: 0; }
        }
    </style>
</head>
<body>
<%@ include file="/admin/header_admin.jsp" %>
<div class="container">
    <h1>Quản lý Tác giả (${totalAuthors} tác giả)</h1>
    <div class="header-actions">
        <a href="${pageContext.request.contextPath}/admin/authors?action=create<c:if test="${not empty name}">&searchName=${fn:escapeXml(name)}</c:if><c:if test="${currentPage > 1}">&searchPage=${currentPage}</c:if>"
           class="btn">+ Thêm tác giả mới</a>
    </div>

    <c:if test="${not empty sessionScope.successMessage}">
        <div class="message success">${sessionScope.successMessage}</div>
        <c:remove var="successMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage}">
        <div class="message error">${sessionScope.errorMessage}</div>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>

    <form method="get" action="${pageContext.request.contextPath}/admin/authors" class="search-filter">
        <input type="hidden" name="action" value="list"/>
        <div>
            <label>Tên tác giả</label><br>
            <input type="text" name="name" value="${fn:escapeXml(name)}" placeholder="Nhập tên tác giả" style="width:300px;"/>
        </div>
        <button type="submit">Tìm kiếm</button>
    </form>

    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>Ảnh</th>
            <th>Tên tác giả</th>
            <th>Tiểu sử</th>
            <th>Số sách</th>
            <th>Hành động</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${authorList}" var="author">
            <tr>
                <td data-label="ID">${author.authorId}</td>
                <td data-label="Ảnh">
                    <c:choose>
                        <c:when test="${not empty author.photoUrl}">
                            <img src="${author.photoUrl}" alt="${fn:escapeXml(author.name)}" class="author-photo"
                                 onerror="this.onerror=null; this.src='data:image/svg+xml,<svg xmlns=%22http://www.w3.org/2000/svg%22 width=%2280%22 height=%2280%22><rect fill=%22%23eee%22 width=%22100%%22 height=%22100%%22/><text x=%2250%%22 y=%2255%%22 font-size=%2210%22 text-anchor=%22middle%22 fill=%22%23999%22>No Image</text></svg>';">
                        </c:when>
                        <c:otherwise>
                            <img src="data:image/svg+xml,<svg xmlns=%22http://www.w3.org/2000/svg%22 width=%2280%22 height=%2280%22><rect fill=%22%23eee%22 width=%22100%%22 height=%22100%%22/><text x=%2250%%22 y=%2255%%22 font-size=%2210%22 text-anchor=%22middle%22 fill=%22%23999%22>No Image</text></svg>" alt="Không có ảnh" class="author-photo">
                        </c:otherwise>
                    </c:choose>
                </td>
                <td data-label="Tên"><strong>${fn:escapeXml(author.name)}</strong></td>
                <td data-label="Tiểu sử" class="bio-preview" title="${fn:escapeXml(author.biography)}">
                    <c:choose>
                        <c:when test="${not empty author.biography}">
                            <c:out value="${author.biography}" escapeXml="true"/>
                        </c:when>
                        <c:otherwise>
                            <em>Chưa có tiểu sử</em>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td data-label="Số sách">${author.books.size()}</td>
                <td data-label="Hành động">
                    <a href="${pageContext.request.contextPath}/admin/authors?action=edit&authorId=${author.authorId}<c:if test="${not empty name}">&searchName=${fn:escapeXml(name)}</c:if><c:if test="${currentPage > 1}">&searchPage=${currentPage}</c:if>"
                       class="action-btn edit-btn">Sửa</a>
                    <form method="post" action="${pageContext.request.contextPath}/admin/authors" style="display:inline;">
                        <input type="hidden" name="action" value="delete"/>
                        <input type="hidden" name="authorId" value="${author.authorId}"/>
                        <input type="hidden" name="searchName" value="${fn:escapeXml(name)}"/>
                        <input type="hidden" name="searchPage" value="${currentPage}"/>
                        <button type="submit" class="action-btn delete-btn"
                                onclick="return confirm('Bạn có chắc chắn muốn xóa tác giả này?')">Xóa</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <c:if test="${totalPages > 1}">
        <div class="pagination">
            <c:if test="${currentPage > 1}">
                <a href="${pageContext.request.contextPath}/admin/authors?action=list&page=${currentPage - 1}<c:if test="${not empty name}">&name=${fn:escapeXml(name)}</c:if>">« Trước</a>
            </c:if>
            <span class="current">Trang ${currentPage} / ${totalPages}</span>
            <c:if test="${currentPage < totalPages}">
                <a href="${pageContext.request.contextPath}/admin/authors?action=list&page=${currentPage + 1}<c:if test="${not empty name}">&name=${fn:escapeXml(name)}</c:if>">Sau »</a>
            </c:if>
        </div>
    </c:if>
</div>
</body>
</html>