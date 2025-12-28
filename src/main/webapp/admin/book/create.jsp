<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thêm Sách mới - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/tom-select@2.3.1/dist/css/tom-select.bootstrap5.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" type="text/css"/>
    <style>
        /* Multi-image upload styles */
        .image-upload-section {
            border: 2px dashed #ccc;
            border-radius: 8px;
            padding: 20px;
            text-align: center;
            transition: all 0.3s ease;
            background: #fafafa;
            cursor: pointer;
        }
        .image-upload-section:hover,
        .image-upload-section.dragover {
            border-color: #007bff;
            background: #f0f7ff;
        }
        .image-upload-section input[type="file"] {
            display: none;
        }
        .upload-placeholder {
            color: #666;
            padding: 30px 0;
        }
        .upload-placeholder i {
            font-size: 48px;
            color: #ccc;
            margin-bottom: 10px;
        }
        .upload-placeholder p {
            margin: 10px 0 5px;
            font-size: 16px;
        }
        .upload-placeholder span {
            font-size: 12px;
            color: #999;
        }
        
        /* Image preview gallery */
        .image-preview-gallery {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
            gap: 15px;
            margin-top: 15px;
        }
        .image-preview-item {
            position: relative;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            background: #fff;
            transition: transform 0.2s, box-shadow 0.2s;
        }
        .image-preview-item:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 15px rgba(0,0,0,0.15);
        }
        .image-preview-item img {
            width: 100%;
            height: 150px;
            object-fit: cover;
            display: block;
        }
        .image-preview-item .remove-btn {
            position: absolute;
            top: 5px;
            right: 5px;
            width: 28px;
            height: 28px;
            background: rgba(220, 53, 69, 0.9);
            color: white;
            border: none;
            border-radius: 50%;
            cursor: pointer;
            font-size: 18px;
            line-height: 28px;
            text-align: center;
            transition: background 0.2s;
        }
        .image-preview-item .remove-btn:hover {
            background: #c82333;
        }
        .image-preview-item .primary-badge {
            position: absolute;
            top: 5px;
            left: 5px;
            background: #28a745;
            color: white;
            padding: 3px 8px;
            border-radius: 4px;
            font-size: 11px;
            font-weight: bold;
        }
        .image-preview-item .set-primary-btn {
            position: absolute;
            bottom: 0;
            left: 0;
            right: 0;
            background: rgba(0,0,0,0.7);
            color: white;
            border: none;
            padding: 8px;
            cursor: pointer;
            font-size: 12px;
            opacity: 0;
            transition: opacity 0.2s;
        }
        .image-preview-item:hover .set-primary-btn {
            opacity: 1;
        }
        .image-preview-item.is-primary .set-primary-btn {
            display: none;
        }
        .image-info {
            padding: 8px;
            font-size: 11px;
            color: #666;
            background: #f8f9fa;
            text-overflow: ellipsis;
            overflow: hidden;
            white-space: nowrap;
        }
    </style>
</head>
<body>
<jsp:include page="../header_admin.jsp" />

<div class="container">
    <h2>Thêm Sách mới</h2>

    <form action="${pageContext.request.contextPath}/admin/books" method="post" enctype="multipart/form-data" class="form-card" novalidate id="bookForm">

        <input type="hidden" name="action" value="create"/>

        <div class="form-row">
            <label for="categoryId">Danh mục:</label>
            <select id="categoryId" name="categoryId" required>
                <c:forEach items="${listCategory}" var="cat">
                    <option value="${cat.categoryId}">${cat.name}</option>
                </c:forEach>
            </select>
        </div>

        <div class="form-row">
            <label for="publisherId">Nhà xuất bản:</label>
            <select id="publisherId" name="publisherId">
                <option value="">-- Chọn NXB --</option>
                <c:forEach items="${listPublishers}" var="pub">
                    <option value="${pub.publisherId}">${pub.name}</option>
                </c:forEach>
            </select>
        </div>

        <div class="form-row">
            <label for="title">Tiêu đề:</label>
            <input id="title" type="text" name="title" required
                   maxlength="255"
                   minlength="1"
                   placeholder="Nhập tiêu đề sách (tối đa 255 ký tự)">
        </div>

        <div class="form-row">
            <label for="authorSelect">Tác giả:</label>
            <select name="authorIds" id="authorSelect" multiple="multiple" required class="tomselect-authors">
                <c:forEach items="${listAuthors}" var="author">
                    <option value="${author.authorId}">${author.name}</option>
                </c:forEach>
            </select>
        </div>

        <div class="form-row">
            <label for="isbn">ISBN:</label>
            <input id="isbn" type="text" name="isbn" required
                   maxlength="20"
                   minlength="10"
                   pattern="[0-9\-]{10,20}"
                   placeholder="Nhập ISBN (10-20 ký tự)"
                   title="ISBN phải có 10-20 ký tự, chỉ gồm số và dấu gạch ngang">
        </div>

        <div class="form-row">
            <label for="publishDate">Ngày xuất bản:</label>
            <input id="publishDate" type="date" name="publishDate" required
                   title="Ngày xuất bản không được trong tương lai">
        </div>

        <div class="form-row" style="align-items: start;">
            <label>Hình ảnh sách:</label>
            <div style="width: 100%;">
                <div class="image-upload-section" id="dropZone">
                    <input type="file" id="bookImages" name="bookImages" multiple
                           accept="image/jpeg,image/png,image/jpg,image/webp">
                    <div class="upload-placeholder">
                        <div style="font-size: 48px; color: #ccc;">📷</div>
                        <p><strong>Click để tải lên</strong> hoặc kéo thả</p>
                        <span>JPEG, PNG, JPG, WEBP (Tối đa 5MB mỗi ảnh)</span>
                    </div>
                </div>
                <div class="image-preview-gallery" id="imagePreviewGallery"></div>
                <input type="hidden" name="primaryImageIndex" id="primaryImageIndex" value="0">
            </div>
        </div>

        <div class="form-row">
            <label for="price">Giá:</label>
            <div class="input-prefix">
                <span class="prefix">VND</span>
                <input id="price" type="number" name="price" required
                       min="0.01"
                       max="99999999.99"
                       step="0.01"
                       placeholder="0.00"
                       title="Giá phải lớn hơn 0">
            </div>
        </div>

        <div class="form-row">
            <label for="quantity">Số lượng trong kho:</label>
            <input id="quantity" type="number" name="quantity" required
                   min="0"
                   max="999999"
                   step="1"
                   value="0"
                   placeholder="Nhập số lượng"
                   title="Số lượng phải từ 0 trở lên">
        </div>

        <div class="form-row" style="align-items: start;">
            <label for="description">Mô tả:</label>
            <textarea id="description" name="description" rows="5"
                      minlength="10"
                      maxlength="5000"
                      placeholder="Nhập mô tả sách (10-5000 ký tự)"></textarea>
        </div>

        <div class="buttons">
            <button type="submit" class="btn">Lưu</button>
            <a class="btn" href="${pageContext.request.contextPath}/admin/books">Huỷ</a>
        </div>

    </form>
</div>

<jsp:include page="../footer_admin.jsp" />

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/tom-select@2.3.1/dist/js/tom-select.complete.min.js"></script>
<script>const contextPath = '${pageContext.request.contextPath}';</script>
<script src="${pageContext.request.contextPath}/js/script.js"></script>
<script>
    // Multi-image upload handling
    const dropZone = document.getElementById('dropZone');
    const fileInput = document.getElementById('bookImages');
    const gallery = document.getElementById('imagePreviewGallery');
    const primaryIndexInput = document.getElementById('primaryImageIndex');
    let selectedFiles = [];

    // Click to open file dialog
    dropZone.addEventListener('click', () => fileInput.click());

    // Drag and drop handlers
    dropZone.addEventListener('dragover', (e) => {
        e.preventDefault();
        dropZone.classList.add('dragover');
    });

    dropZone.addEventListener('dragleave', () => {
        dropZone.classList.remove('dragover');
    });

    dropZone.addEventListener('drop', (e) => {
        e.preventDefault();
        dropZone.classList.remove('dragover');
        handleFiles(e.dataTransfer.files);
    });

    // File input change
    fileInput.addEventListener('change', () => {
        handleFiles(fileInput.files);
    });

    function handleFiles(files) {
        for (let file of files) {
            if (file.type.startsWith('image/') && file.size <= 5 * 1024 * 1024) {
                selectedFiles.push(file);
            } else if (file.size > 5 * 1024 * 1024) {
                alert('File "' + file.name + '" vượt quá giới hạn 5MB');
            }
        }
        updateGallery();
        updateFileInput();
    }

    function updateGallery() {
        gallery.innerHTML = '';
        selectedFiles.forEach((file, index) => {
            const reader = new FileReader();
            reader.onload = (e) => {
                const div = document.createElement('div');
                const isPrimary = index === parseInt(primaryIndexInput.value);
                div.className = 'image-preview-item' + (isPrimary ? ' is-primary' : '');
                
                let html = '';
                if (isPrimary) {
                    html += '<span class="primary-badge">Ảnh chính</span>';
                }
                html += '<img src="' + e.target.result + '" alt="Preview">';
                html += '<button type="button" class="remove-btn" onclick="removeImage(' + index + ')">&times;</button>';
                html += '<button type="button" class="set-primary-btn" onclick="setPrimary(' + index + ')">Đặt làm ảnh chính</button>';
                html += '<div class="image-info">' + file.name + '</div>';
                div.innerHTML = html;
                gallery.appendChild(div);
            };
            reader.readAsDataURL(file);
        });
    }

    function removeImage(index) {
        selectedFiles.splice(index, 1);
        // Update primary index if needed
        if (parseInt(primaryIndexInput.value) >= selectedFiles.length) {
            primaryIndexInput.value = Math.max(0, selectedFiles.length - 1);
        } else if (parseInt(primaryIndexInput.value) > index) {
            primaryIndexInput.value = parseInt(primaryIndexInput.value) - 1;
        }
        updateGallery();
        updateFileInput();
    }

    function setPrimary(index) {
        primaryIndexInput.value = index;
        updateGallery();
    }

    function updateFileInput() {
        // Create a new DataTransfer object to update file input
        const dt = new DataTransfer();
        selectedFiles.forEach(file => dt.items.add(file));
        fileInput.files = dt.files;
    }

    // Form validation
    document.getElementById('bookForm').addEventListener('submit', function(e) {
        if (selectedFiles.length === 0) {
            e.preventDefault();
            alert('Vui lòng tải lên ít nhất một hình ảnh sách');
            return false;
        }
    });

    // Set max date for publishDate to today
    document.getElementById('publishDate').max = new Date().toISOString().split('T')[0];
</script>
</body>
</html>
