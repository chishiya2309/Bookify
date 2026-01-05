<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${empty author ? 'Thêm tác giả mới' : 'Chỉnh sửa tác giả'} - Admin Bookify</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background: #f4f4f4; }
        .container { max-width: 900px; margin: 0 auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
        h1 { text-align: center; color: #333; }
        .form-group { margin-bottom: 20px; }
        label { display: block; margin-bottom: 8px; font-weight: bold; }
        input[type="text"], input[type="url"], input[type="file"], textarea {
            width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px;
            font-size: 16px; box-sizing: border-box;
        }
        textarea { height: 200px; resize: vertical; }
        .btn-group { text-align: center; margin-top: 30px; }
        .btn { padding: 12px 24px; font-size: 16px; border: none; border-radius: 4px; cursor: pointer; margin: 0 10px; text-decoration: none; display: inline-block; }
        .btn-save { background: #28a745; color: white; }
        .btn-cancel { background: #6c757d; color: white; }
        .current-photo { margin-top: 10px; padding: 15px; background: #f8f9fa; border-radius: 8px; }
        .current-photo img { max-width: 200px; height: auto; border-radius: 8px; border: 1px solid #ddd; display: block; margin-top: 10px; }

        /* Upload method selector */
        .upload-methods { display: flex; gap: 15px; flex-wrap: wrap; margin-bottom: 15px; }
        .upload-method-option {
            flex: 1;
            min-width: 200px;
            padding: 12px;
            border: 2px solid #ddd;
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.3s;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        .upload-method-option:hover { border-color: #007bff; background: #f8f9fa; }
        .upload-method-option input[type="radio"] { cursor: pointer; }
        .upload-method-option.active { border-color: #007bff; background: #e7f3ff; }

        .upload-section { display: none; padding: 15px; background: #f8f9fa; border-radius: 8px; margin-top: 10px; }
        .upload-section.active { display: block; }

        .file-info { margin-top: 10px; font-size: 14px; color: #666; }
        .preview-image { max-width: 300px; margin-top: 15px; border-radius: 8px; border: 2px solid #ddd; }

        /* Thông báo success / error - ĐÃ ĐƯA VÀO ĐÂY ĐỂ KHÔNG BỊ HIỂN THỊ NHƯ TEXT */
        .message {
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 4px;
            font-weight: bold;
        }
        .success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        .error {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
    </style>
</head>
<body>
<%@ include file="/admin/header_admin.jsp" %>
<div class="container">
    <h1>${empty author ? 'Thêm tác giả mới' : 'Chỉnh sửa tác giả: '}${not empty author ? fn:escapeXml(author.name) : ''}</h1>

    <!-- HIỂN THỊ THÔNG BÁO -->
    <c:if test="${not empty sessionScope.errorMessage}">
        <div class="message error">${sessionScope.errorMessage}</div>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.successMessage}">
        <div class="message success">${sessionScope.successMessage}</div>
        <c:remove var="successMessage" scope="session"/>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/admin/authors" enctype="multipart/form-data">
        <input type="hidden" name="action" value="save"/>

        <c:if test="${not empty author}">
            <input type="hidden" name="authorId" value="${author.authorId}"/>
        </c:if>

        <!-- GIỮ TRẠNG THÁI TÌM KIẾM -->
        <c:if test="${not empty searchName}">
            <input type="hidden" name="searchName" value="${fn:escapeXml(searchName)}"/>
        </c:if>
        <c:if test="${not empty searchPage}">
            <input type="hidden" name="searchPage" value="${searchPage}"/>
        </c:if>

        <div class="form-group">
            <label for="name">Tên tác giả <span style="color:red;">*</span></label>
            <input type="text" id="name" name="name" value="${fn:escapeXml(author.name)}" required maxlength="100"/>
        </div>

        <div class="form-group">
            <label>Ảnh đại diện</label>

            <!-- PHƯƠNG THỨC UPLOAD -->
            <div class="upload-methods">
                <label class="upload-method-option ${empty author || param.uploadMethod == 'file' ? 'active' : ''}" id="method-file-label">
                    <input type="radio" name="uploadMethod" value="file" id="method-file"
                    ${empty author || param.uploadMethod == 'file' ? 'checked' : ''}>
                    <span>📁 Upload file mới</span>
                </label>

                <label class="upload-method-option ${param.uploadMethod == 'url' ? 'active' : ''}" id="method-url-label">
                    <input type="radio" name="uploadMethod" value="url" id="method-url"
                    ${param.uploadMethod == 'url' ? 'checked' : ''}>
                    <span>🔗 Dùng URL</span>
                </label>

                <c:if test="${not empty author.photoUrl}">
                    <label class="upload-method-option ${param.uploadMethod == 'keep' || empty param.uploadMethod ? 'active' : ''}" id="method-keep-label">
                        <input type="radio" name="uploadMethod" value="keep" id="method-keep"
                            ${param.uploadMethod == 'keep' || empty param.uploadMethod ? 'checked' : ''}>
                        <span>✅ Giữ ảnh hiện tại</span>
                    </label>

                    <label class="upload-method-option ${param.uploadMethod == 'remove' ? 'active' : ''}" id="method-remove-label">
                        <input type="radio" name="uploadMethod" value="remove" id="method-remove"
                            ${param.uploadMethod == 'remove' ? 'checked' : ''}>
                        <span>❌ Xóa ảnh</span>
                    </label>
                </c:if>
            </div>

            <!-- UPLOAD FILE -->
            <div class="upload-section ${empty author || param.uploadMethod == 'file' ? 'active' : ''}" id="section-file">
                <input type="file" id="photoFile" name="photoFile" accept="image/*" onchange="previewImage(this)"/>
                <div class="file-info">
                    Chấp nhận: JPG, PNG, WEBP. Tối đa: 5MB
                </div>
                <img id="preview" class="preview-image" style="display:none;"/>
            </div>

            <!-- NHẬP URL -->
            <div class="upload-section ${param.uploadMethod == 'url' ? 'active' : ''}" id="section-url">
                <input type="url" id="photoUrl" name="photoUrl" placeholder="https://example.com/photo.jpg" value="${fn:escapeXml(param.photoUrl)}"/>
            </div>

            <!-- ẢNH HIỆN TẠI -->
            <c:if test="${not empty author.photoUrl}">
                <div class="current-photo">
                    <p><strong>Ảnh hiện tại:</strong></p>
                    <img src="${author.photoUrl}" alt="Ảnh tác giả" loading="lazy"
                         onerror="this.src='https://via.placeholder.com/200?text=Error+Loading'"/>
                </div>
            </c:if>
        </div>

        <div class="form-group">
            <label for="biography">Tiểu sử</label>
            <textarea id="biography" name="biography" maxlength="10000">${fn:escapeXml(author.biography)}</textarea>
        </div>

        <div class="btn-group">
            <button type="submit" class="btn btn-save" onclick="return validateForm()">💾 Lưu lại</button>
            <a href="${pageContext.request.contextPath}/admin/authors?action=list<c:if test="${not empty searchName}">&name=${fn:escapeXml(searchName)}</c:if><c:if test="${not empty searchPage}">&page=${searchPage}</c:if>"
               class="btn btn-cancel">↩️ Hủy bỏ</a>
        </div>
    </form>
</div>

<script>
    // Validate form trước khi submit
    function validateForm() {
        const name = document.getElementById('name').value.trim();
        if (!name) {
            alert('Vui lòng nhập tên tác giả!');
            document.getElementById('name').focus();
            return false;
        }

        if (name.length > 100) {
            alert('Tên tác giả không được vượt quá 100 ký tự!');
            document.getElementById('name').focus();
            return false;
        }

        const biography = document.getElementById('biography').value;
        if (biography && biography.length > 10000) {
            alert('Tiểu sử không được vượt quá 10.000 ký tự!');
            document.getElementById('biography').focus();
            return false;
        }

        const uploadMethod = document.querySelector('input[name="uploadMethod"]:checked');
        if (!uploadMethod) {
            alert('Vui lòng chọn phương thức upload ảnh!');
            return false;
        }

        // Validate file nếu chọn upload file
        if (uploadMethod.value === 'file') {
            const fileInput = document.getElementById('photoFile');
            if (fileInput.files.length > 0) {
                const file = fileInput.files[0];
                const maxSize = 5 * 1024 * 1024; // 5MB

                if (file.size > maxSize) {
                    alert('Kích thước file không được vượt quá 5MB!');
                    return false;
                }

                if (!file.type.startsWith('image/')) {
                    alert('Chỉ chấp nhận file ảnh (JPEG, PNG, WEBP)!');
                    return false;
                }
            }
        }

        // Validate URL nếu chọn dùng URL
        if (uploadMethod.value === 'url') {
            const photoUrl = document.getElementById('photoUrl').value.trim();
            if (photoUrl) {
                if (photoUrl.length > 500) {
                    alert('URL ảnh không được vượt quá 500 ký tự!');
                    return false;
                }

                if (!photoUrl.match(/^https?:\/\/.+/i)) {
                    alert('URL ảnh không hợp lệ (phải bắt đầu bằng http:// hoặc https://)!');
                    return false;
                }
            }
        }

        return true;
    }

    // Xử lý chuyển đổi phương thức upload
    const methods = document.querySelectorAll('input[name="uploadMethod"]');
    const sections = {
        'file': document.getElementById('section-file'),
        'url': document.getElementById('section-url')
    };
    const labels = {
        'file': document.getElementById('method-file-label'),
        'url': document.getElementById('method-url-label'),
        'keep': document.getElementById('method-keep-label'),
        'remove': document.getElementById('method-remove-label')
    };

    methods.forEach(method => {
        method.addEventListener('change', function() {
            // Ẩn tất cả sections
            Object.values(sections).forEach(section => {
                if (section) section.classList.remove('active');
            });

            // Xóa active từ tất cả labels
            Object.values(labels).forEach(label => {
                if (label) label.classList.remove('active');
            });

            // Hiển thị section tương ứng (chỉ file và url có section)
            if (sections[this.value]) {
                sections[this.value].classList.add('active');
            }

            // Thêm active vào label được chọn
            if (labels[this.value]) {
                labels[this.value].classList.add('active');
            }
        });
    });

    // Preview ảnh khi chọn file
    function previewImage(input) {
        const preview = document.getElementById('preview');
        if (input.files && input.files[0]) {
            const reader = new FileReader();
            reader.onload = function(e) {
                preview.src = e.target.result;
                preview.style.display = 'block';
            };
            reader.readAsDataURL(input.files[0]);
        } else {
            preview.style.display = 'none';
        }
    }

    // Trigger change ban đầu để đồng bộ trạng thái khi reload trang (do lỗi validate)
    document.querySelector('input[name="uploadMethod"]:checked')?.dispatchEvent(new Event('change'));
</script>

</body>
</html>