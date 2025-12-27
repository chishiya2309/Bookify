<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Book - Admin</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" type="text/css"/>
    <link href="https://cdn.jsdelivr.net/npm/tom-select@2.3.1/dist/css/tom-select.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/tom-select@2.3.1/dist/js/tom-select.complete.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/sortablejs@1.15.0/Sortable.min.js"></script>
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
            margin-bottom: 15px;
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
            padding: 15px 0;
        }
        .upload-placeholder p {
            margin: 8px 0 5px;
            font-size: 14px;
        }
        .upload-placeholder span {
            font-size: 12px;
            color: #999;
        }
        
        /* Image gallery */
        .image-gallery {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
            gap: 15px;
            margin-top: 10px;
        }
        .image-item {
            position: relative;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            background: #fff;
            transition: transform 0.2s, box-shadow 0.2s;
            cursor: grab;
        }
        .image-item:active {
            cursor: grabbing;
        }
        .image-item:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 15px rgba(0,0,0,0.15);
        }
        .image-item.sortable-ghost {
            opacity: 0.4;
        }
        .image-item.sortable-chosen {
            box-shadow: 0 8px 25px rgba(0,0,0,0.2);
        }
        .image-item img {
            width: 100%;
            height: 150px;
            object-fit: cover;
            display: block;
        }
        .image-item .remove-btn {
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
        .image-item .remove-btn:hover {
            background: #c82333;
        }
        .image-item .primary-badge {
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
        .image-item .set-primary-btn {
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
        .image-item:hover .set-primary-btn {
            opacity: 1;
        }
        .image-item.is-primary .set-primary-btn {
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
        .drag-handle {
            position: absolute;
            top: 5px;
            left: 5px;
            background: rgba(0,0,0,0.5);
            color: white;
            padding: 4px 6px;
            border-radius: 4px;
            font-size: 12px;
            cursor: grab;
        }
        .image-item.is-primary .drag-handle {
            left: auto;
            right: 40px;
        }
        
        .section-title {
            font-weight: 600;
            color: #333;
            margin: 15px 0 10px;
            padding-bottom: 5px;
            border-bottom: 1px solid #eee;
        }
        
        .new-images-preview {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
            gap: 10px;
            margin-top: 10px;
        }
    </style>
</head>
<body>
<jsp:include page="../header_admin.jsp" />

<div class="container">
    <h2>Edit Book</h2>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-error" style="background-color: #fee; border: 1px solid #fcc; padding: 15px; margin-bottom: 20px; border-radius: 4px; color: #c33;">
            <strong>Error:</strong> ${errorMessage}
            <br>
            <a href="${pageContext.request.contextPath}/admin/books" style="color: #c33; text-decoration: underline;">Back to Books List</a>
        </div>
    </c:if>
    <c:if test="${empty book}">
        <div class="alert alert-error" style="background-color: #fee; border: 1px solid #fcc; padding: 15px; margin-bottom: 20px; border-radius: 4px; color: #c33;">
            <strong>Error:</strong> Book not found!
            <br>
            <a href="${pageContext.request.contextPath}/admin/books" style="color: #c33; text-decoration: underline;">Back to Books List</a>
        </div>
    </c:if>

    <c:if test="${not empty book}">
    <form action="${pageContext.request.contextPath}/admin/books" method="post" enctype="multipart/form-data" class="form-card" novalidate id="bookForm">

        <input type="hidden" name="action" value="update"/>
        <input type="hidden" name="bookId" value="${book.bookId}"/>

        <div class="form-row">
            <label for="categoryId">Category:</label>
            <select id="categoryId" name="categoryId" required>
                <c:forEach items="${listCategory}" var="cat">
                    <option value="${cat.categoryId}"
                            <c:if test="${cat.categoryId == book.category.categoryId}">selected</c:if>>
                            ${cat.name}
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="form-row">
            <label for="publisherId">Publisher:</label>
            <select id="publisherId" name="publisherId">
                <option value="">-- Select Publisher --</option>
                <c:forEach items="${listPublishers}" var="pub">
                    <option value="${pub.publisherId}"
                            <c:if test="${not empty book.publisher && pub.publisherId == book.publisher.publisherId}">selected</c:if>>
                            ${pub.name}
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="form-row">
            <label for="title">Title:</label>
            <input id="title" type="text" name="title" value="${book.title}" required
                   maxlength="255"
                   minlength="1"
                   placeholder="Enter book title (max 255 characters)">
        </div>

        <div class="form-row">
            <label for="authorSelect">Author:</label>
            <select name="authorIds" id="authorSelect" multiple="multiple" required class="tomselect-authors">
                <c:forEach items="${listAuthors}" var="allAuth">
                    <c:set var="isSelected" value="" />

                    <c:if test="${not empty book.authors}">
                        <c:forEach items="${book.authors}" var="bookAuth">
                            <c:if test="${bookAuth.authorId == allAuth.authorId}">
                                <c:set var="isSelected" value="selected" />
                            </c:if>
                        </c:forEach>
                    </c:if>
                    <option value="${allAuth.authorId}" ${isSelected}>
                            ${allAuth.name}
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="form-row">
            <label for="isbn">ISBN:</label>
            <input id="isbn" type="text" name="isbn" value="${book.isbn}" required
                   maxlength="20"
                   minlength="10"
                   pattern="[0-9\-]{10,20}"
                   placeholder="Enter ISBN (10-20 digits)"
                   title="ISBN must be 10-20 characters, digits and hyphens only">
        </div>

        <div class="form-row">
            <label for="publishDate">Publish Date:</label>
            <input id="publishDate" type="date" name="publishDate" value="${book.publishDate}" required
                   title="Publish date cannot be in the future">
        </div>

        <div class="form-row" style="align-items: start;">
            <label>Book Images:</label>
            <div style="width: 100%;">
                <!-- Existing Images Section -->
                <c:if test="${not empty book.images}">
                    <div class="section-title">Current Images (Drag to reorder)</div>
                    <div class="image-gallery" id="existingImagesGallery">
                        <c:forEach items="${book.images}" var="img" varStatus="status">
                            <div class="image-item ${img.isPrimary ? 'is-primary' : ''}" 
                                 data-image-id="${img.imageId}" 
                                 data-sort-order="${img.sortOrder}">
                                <c:if test="${img.isPrimary}">
                                    <span class="primary-badge">Primary</span>
                                </c:if>
                                <img src="${img.url}" alt="Book Image">
                                <button type="button" class="remove-btn" onclick="markForDeletion(${img.imageId}, this)">&times;</button>
                                <button type="button" class="set-primary-btn" onclick="setPrimaryExisting(${img.imageId})">Set as Primary</button>
                            </div>
                        </c:forEach>
                    </div>
                </c:if>
                
                <!-- Hidden inputs for tracking changes -->
                <div id="imageTrackingInputs">
                    <input type="hidden" name="primaryImageId" id="primaryImageId" value="">
                    <!-- deleteImageIds will be added dynamically -->
                </div>
                
                <!-- Add New Images Section -->
                <div class="section-title" style="margin-top: 20px;">Add New Images</div>
                <div class="image-upload-section" id="dropZone">
                    <input type="file" id="newBookImages" name="newBookImages" multiple
                           accept="image/jpeg,image/png,image/jpg,image/webp">
                    <div class="upload-placeholder">
                        <div style="font-size: 36px; color: #ccc;">📷</div>
                        <p><strong>Click to upload</strong> or drag and drop</p>
                        <span>JPEG, PNG, JPG, WEBP (Max 5MB each)</span>
                    </div>
                </div>
                <div class="new-images-preview image-gallery" id="newImagesPreview"></div>
            </div>
        </div>

        <div class="form-row">
            <label for="price">Price:</label>
            <div class="input-prefix">
                <span class="prefix">VND</span>
                <input id="price" type="number" name="price" value="${book.price}" required
                       min="0.01"
                       max="99999999.99"
                       step="0.01"
                       placeholder="0.00"
                       title="Price must be greater than 0">
            </div>
        </div>

        <div class="form-row" style="align-items: start;">
            <label for="description">Description:</label>
            <textarea id="description" name="description" rows="5"
                      minlength="10"
                      maxlength="5000"
                      placeholder="Enter book description (10-5000 characters)">${book.description}</textarea>
        </div>

        <div class="buttons">
            <button type="submit" class="btn">Save</button>
            <a class="btn" href="${pageContext.request.contextPath}/admin/books">Cancel</a>
        </div>

    </form>
    </c:if>
</div>

<jsp:include page="../footer_admin.jsp" />
<script src="${pageContext.request.contextPath}/js/script.js"></script>
<script>
    // Delete tracking
    const deleteImageIds = new Set();
    let primaryImageId = null;
    
    // Initialize sortable for existing images
    const existingGallery = document.getElementById('existingImagesGallery');
    if (existingGallery) {
        new Sortable(existingGallery, {
            animation: 150,
            ghostClass: 'sortable-ghost',
            chosenClass: 'sortable-chosen',
            onEnd: updateImageOrder
        });
        
        // Get initial primary image
        const primaryItem = existingGallery.querySelector('.is-primary');
        if (primaryItem) {
            primaryImageId = parseInt(primaryItem.dataset.imageId);
        }
    }
    
    function updateImageOrder() {
        const trackingDiv = document.getElementById('imageTrackingInputs');
        // Remove old order inputs
        trackingDiv.querySelectorAll('input[name="imageOrder"]').forEach(el => el.remove());
        
        // Add new order inputs
        const items = document.querySelectorAll('#existingImagesGallery .image-item:not(.deleted)');
        items.forEach((item, index) => {
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'imageOrder';
            input.value = item.dataset.imageId + ':' + index;
            trackingDiv.appendChild(input);
        });
    }
    
    function markForDeletion(imageId, btn) {
        if (!confirm('Are you sure you want to delete this image?')) return;
        
        const item = btn.closest('.image-item');
        item.style.display = 'none';
        item.classList.add('deleted');
        deleteImageIds.add(imageId);
        
        // Add hidden input for deletion
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = 'deleteImageIds';
        input.value = imageId;
        document.getElementById('imageTrackingInputs').appendChild(input);
        
        // If deleted was primary, set first remaining as primary
        if (item.classList.contains('is-primary')) {
            const remaining = document.querySelector('#existingImagesGallery .image-item:not(.deleted)');
            if (remaining) {
                setPrimaryExisting(parseInt(remaining.dataset.imageId));
            }
        }
        
        updateImageOrder();
    }
    
    function setPrimaryExisting(imageId) {
        primaryImageId = imageId;
        document.getElementById('primaryImageId').value = imageId;
        
        // Update UI
        document.querySelectorAll('#existingImagesGallery .image-item').forEach(item => {
            if (parseInt(item.dataset.imageId) === imageId) {
                item.classList.add('is-primary');
                if (!item.querySelector('.primary-badge')) {
                    const badge = document.createElement('span');
                    badge.className = 'primary-badge';
                    badge.textContent = 'Primary';
                    item.insertBefore(badge, item.firstChild);
                }
            } else {
                item.classList.remove('is-primary');
                const badge = item.querySelector('.primary-badge');
                if (badge) badge.remove();
            }
        });
    }
    
    // New images upload handling
    const dropZone = document.getElementById('dropZone');
    const fileInput = document.getElementById('newBookImages');
    const newPreview = document.getElementById('newImagesPreview');
    let newFiles = [];

    dropZone.addEventListener('click', () => fileInput.click());

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
        handleNewFiles(e.dataTransfer.files);
    });

    fileInput.addEventListener('change', () => {
        handleNewFiles(fileInput.files);
    });

    function handleNewFiles(files) {
        for (let file of files) {
            if (file.type.startsWith('image/') && file.size <= 5 * 1024 * 1024) {
                newFiles.push(file);
            } else if (file.size > 5 * 1024 * 1024) {
                alert('File "' + file.name + '" exceeds 5MB limit');
            }
        }
        updateNewPreview();
        updateNewFileInput();
    }

    function updateNewPreview() {
        newPreview.innerHTML = '';
        newFiles.forEach((file, index) => {
            const reader = new FileReader();
            reader.onload = (e) => {
                const div = document.createElement('div');
                div.className = 'image-item';
                let html = '<img src="' + e.target.result + '" alt="Preview">';
                html += '<button type="button" class="remove-btn" onclick="removeNewImage(' + index + ')">&times;</button>';
                html += '<div class="image-info">' + file.name + '</div>';
                div.innerHTML = html;
                newPreview.appendChild(div);
            };
            reader.readAsDataURL(file);
        });
    }

    function removeNewImage(index) {
        newFiles.splice(index, 1);
        updateNewPreview();
        updateNewFileInput();
    }

    function updateNewFileInput() {
        const dt = new DataTransfer();
        newFiles.forEach(file => dt.items.add(file));
        fileInput.files = dt.files;
    }
    
    // Initialize order on page load
    if (existingGallery) {
        updateImageOrder();
    }
    
    // Set max date for publishDate to today
    document.getElementById('publishDate').max = new Date().toISOString().split('T')[0];
</script>
</body>
</html>