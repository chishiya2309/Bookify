function showPreview(fileInput) {
    var file = fileInput.files[0];
    if (file) {
        // Validate file type
        const allowedTypes = ['image/jpeg', 'image/png', 'image/jpg', 'image/webp'];
        if (!allowedTypes.includes(file.type)) {
            alert('Invalid file type. Only JPEG, PNG, JPG, WEBP images are allowed.');
            fileInput.value = '';
            return;
        }

        // Validate file size (max 5MB)
        const maxSize = 5 * 1024 * 1024; // 5MB in bytes
        if (file.size > maxSize) {
            alert('File size too large. Maximum size is 5MB.');
            fileInput.value = '';
            return;
        }

        var reader = new FileReader();
        reader.onload = function(e) {
            var img = document.getElementById('thumbnail');
            img.src = e.target.result;
            img.style.display = 'block';
        };
        reader.readAsDataURL(file);
    }
}

// Helper: Show error message under input
function showError(input, message) {
    // Remove existing error
    clearError(input);

    // Add invalid class
    input.classList.add('invalid');
    input.classList.remove('valid');

    // Create error message element
    const errorDiv = document.createElement('div');
    errorDiv.className = 'error-message';
    errorDiv.textContent = message;

    // Insert after input's parent form-row
    const formRow = input.closest('.form-row');
    if (formRow) {
        formRow.appendChild(errorDiv);
    }
}

// Helper: Clear error message
function clearError(input) {
    input.classList.remove('invalid');

    const formRow = input.closest('.form-row');
    if (formRow) {
        const existingError = formRow.querySelector('.error-message');
        if (existingError) {
            existingError.remove();
        }
    }
}

// Helper: Show error for Tom Select
function showErrorTomSelect(selectElement, message) {
    // Try to find wrapper next to select element (Tom Select creates wrapper as sibling)
    let wrapper = selectElement.nextElementSibling;
    if (!wrapper || !wrapper.classList.contains('ts-wrapper')) {
        // Fallback: search in parent
        wrapper = selectElement.parentElement.querySelector('.ts-wrapper');
    }

    if (wrapper) {
        wrapper.classList.add('invalid');

        const formRow = wrapper.closest('.form-row');
        if (formRow) {
            // Remove existing error
            const existingError = formRow.querySelector('.error-message');
            if (existingError) existingError.remove();

            // Create new error
            const errorDiv = document.createElement('div');
            errorDiv.className = 'error-message';
            errorDiv.textContent = message;
            formRow.appendChild(errorDiv);
        }
    }
}

// Helper: Clear error for Tom Select
function clearErrorTomSelect(selectElement) {
    // Try to find wrapper next to select element
    let wrapper = selectElement.nextElementSibling;
    if (!wrapper || !wrapper.classList.contains('ts-wrapper')) {
        wrapper = selectElement.parentElement.querySelector('.ts-wrapper');
    }

    if (wrapper) {
        wrapper.classList.remove('invalid');

        const formRow = wrapper.closest('.form-row');
        if (formRow) {
            const existingError = formRow.querySelector('.error-message');
            if (existingError) existingError.remove();
        }
    }
}

// Validate single field (for blur event)
function validateSingleField(field) {
    clearError(field);

    const fieldId = field.id;
    const value = field.value ? field.value.trim() : '';

    switch(fieldId) {
        case 'title':
            if (!value) {
                showError(field, 'Tiêu đề không được để trống.');
            } else if (value.length > 255) {
                showError(field, 'Tiêu đề không được vượt quá 255 ký tự.');
            } else {
                field.classList.add('valid');
            }
            break;

        case 'isbn':
            const isbnPattern = /^[0-9\-]{10,20}$/;
            if (!value) {
                showError(field, 'Mã ISBN không được để trống.');
            } else if (!isbnPattern.test(value)) {
                showError(field, 'ISBN phải có 10-20 ký tự, chỉ bao gồm số và dấu gạch ngang.');
            } else if (value.length < 10 || value.length > 20) {
                showError(field, 'ISBN phải có độ dài từ 10 đến 20 ký tự.');
            } else {
                field.classList.add('valid');
            }
            break;

        case 'publishDate':
            if (!value) {
                showError(field, 'Vui lòng chọn ngày xuất bản.');
            } else {
                const selectedDate = new Date(value);
                const today = new Date();
                today.setHours(0, 0, 0, 0);
                if (selectedDate > today) {
                    showError(field, 'Ngày xuất bản không thể là ngày trong tương lai.');
                } else {
                    field.classList.add('valid');
                }
            }
            break;

        case 'price':
            const priceValue = parseFloat(value);
            if (!value) {
                showError(field, 'Giá sách không được để trống.');
            } else if (isNaN(priceValue) || priceValue <= 0) {
                showError(field, 'Giá sách phải lớn hơn 0.');
            } else if (priceValue > 99999999.99) {
                showError(field, 'Giá sách không được vượt quá 99,999,999.99.');
            } else {
                field.classList.add('valid');
            }
            break;

        case 'description':
            if (value.length > 0) {
                if (value.length < 10) {
                    showError(field, 'Mô tả phải có ít nhất 10 ký tự.');
                } else if (value.length > 5000) {
                    showError(field, 'Mô tả không được vượt quá 5000 ký tự.');
                } else {
                    field.classList.add('valid');
                }
            }
            break;
    }
}

// Validate form before submit
function validateBookForm(form) {
    let isValid = true;
    let firstInvalidField = null;

    // Clear all previous errors
    form.querySelectorAll('.invalid').forEach(el => el.classList.remove('invalid'));
    form.querySelectorAll('.error-message').forEach(el => el.remove());

    // 1. Validate Category
    const category = form.querySelector('#categoryId');
    if (category && !category.value) {
        showError(category, 'Vui lòng chọn danh mục sách.');
        if (!firstInvalidField) firstInvalidField = category;
        isValid = false;
    }

    // 2. Validate Title
    const title = form.querySelector('#title');
    if (!title.value.trim()) {
        showError(title, 'Tiêu đề không được để trống.');
        if (!firstInvalidField) firstInvalidField = title;
        isValid = false;
    } else if (title.value.length > 255) {
        showError(title, 'Tiêu đề không được vượt quá 255 ký tự.');
        if (!firstInvalidField) firstInvalidField = title;
        isValid = false;
    } else {
        title.classList.add('valid');
    }

    // 3. Validate Authors (Tom Select)
    const authorSelect = form.querySelector('#authorSelect');
    if (authorSelect) {
        // Check using Tom Select instance if available
        let hasAuthors = false;
        if (window.authorTomSelect && window.authorTomSelect.items) {
            hasAuthors = window.authorTomSelect.items.length > 0;
        } else {
            // Fallback to selectedOptions
            const selectedOptions = Array.from(authorSelect.selectedOptions);
            hasAuthors = selectedOptions.length > 0;
        }

        if (!hasAuthors) {
            showErrorTomSelect(authorSelect, 'Vui lòng chọn ít nhất một tác giả.');
            if (!firstInvalidField) firstInvalidField = authorSelect;
            isValid = false;
        } else {
            // Clear error if authors are selected
            clearErrorTomSelect(authorSelect);
        }
    }

    // 4. Validate ISBN
    const isbn = form.querySelector('#isbn');
    const isbnPattern = /^[0-9\-]{10,20}$/;
    if (!isbn.value.trim()) {
        showError(isbn, 'Mã ISBN không được để trống.');
        if (!firstInvalidField) firstInvalidField = isbn;
        isValid = false;
    } else if (!isbnPattern.test(isbn.value)) {
        showError(isbn, 'ISBN phải có 10-20 ký tự, chỉ bao gồm số và dấu gạch ngang.');
        if (!firstInvalidField) firstInvalidField = isbn;
        isValid = false;
    } else if (isbn.value.length < 10 || isbn.value.length > 20) {
        showError(isbn, 'ISBN phải có độ dài từ 10 đến 20 ký tự.');
        if (!firstInvalidField) firstInvalidField = isbn;
        isValid = false;
    } else {
        isbn.classList.add('valid');
    }

    // 5. Validate Publish Date
    const publishDate = form.querySelector('#publishDate');
    if (!publishDate.value) {
        showError(publishDate, 'Vui lòng chọn ngày xuất bản.');
        if (!firstInvalidField) firstInvalidField = publishDate;
        isValid = false;
    } else {
        const selectedDate = new Date(publishDate.value);
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        if (selectedDate > today) {
            showError(publishDate, 'Ngày xuất bản không thể là ngày trong tương lai.');
            if (!firstInvalidField) firstInvalidField = publishDate;
            isValid = false;
        } else {
            publishDate.classList.add('valid');
        }
    }

    // 6. Validate Book Image (for create form only)
    const bookImage = form.querySelector('#bookImage');
    if (bookImage && bookImage.hasAttribute('required')) {
        if (!bookImage.files || bookImage.files.length === 0) {
            showError(bookImage, 'Vui lòng chọn hình ảnh cho sách.');
            if (!firstInvalidField) firstInvalidField = bookImage;
            isValid = false;
        }
    }

    // 7. Validate Price
    const price = form.querySelector('#price');
    const priceValue = parseFloat(price.value);
    if (!price.value) {
        showError(price, 'Giá sách không được để trống.');
        if (!firstInvalidField) firstInvalidField = price;
        isValid = false;
    } else if (isNaN(priceValue) || priceValue <= 0) {
        showError(price, 'Giá sách phải lớn hơn 0.');
        if (!firstInvalidField) firstInvalidField = price;
        isValid = false;
    } else if (priceValue > 99999999.99) {
        showError(price, 'Giá sách không được vượt quá 99,999,999.99.');
        if (!firstInvalidField) firstInvalidField = price;
        isValid = false;
    } else {
        price.classList.add('valid');
    }

    // 8. Validate Description (optional but if provided, check length)
    const description = form.querySelector('#description');
    if (description.value.trim().length > 0) {
        if (description.value.trim().length < 10) {
            showError(description, 'Mô tả phải có ít nhất 10 ký tự.');
            if (!firstInvalidField) firstInvalidField = description;
            isValid = false;
        } else if (description.value.length > 5000) {
            showError(description, 'Mô tả không được vượt quá 5000 ký tự.');
            if (!firstInvalidField) firstInvalidField = description;
            isValid = false;
        } else {
            description.classList.add('valid');
        }
    }

    // Focus on first invalid field
    if (!isValid && firstInvalidField) {
        // Special handling for Tom Select
        if (firstInvalidField.id === 'authorSelect') {
            const wrapper = firstInvalidField.nextElementSibling;
            if (wrapper && wrapper.classList.contains('ts-wrapper')) {
                const input = wrapper.querySelector('.ts-control input');
                if (input) {
                    input.focus();
                }
            }
        } else {
            firstInvalidField.focus();
        }
        firstInvalidField.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }

    return isValid;
}

// ===== PUBLISHER FORM VALIDATION =====

// Validate single publisher field (for blur event)
function validatePublisherField(field) {
    clearError(field);

    const fieldId = field.id;
    const value = field.value ? field.value.trim() : '';

    switch(fieldId) {
        case 'name':
            if (!value) {
                showError(field, 'Tên nhà xuất bản không được để trống.');
            } else if (value.length > 255) {
                showError(field, 'Tên nhà xuất bản không được vượt quá 255 ký tự.');
            } else {
                field.classList.add('valid');
            }
            break;

        case 'contactEmail':
            const emailRegex = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
            if (!value) {
                showError(field, 'Email liên hệ không được để trống.');
            } else if (!emailRegex.test(value)) {
                showError(field, 'Email liên hệ không hợp lệ.');
            } else if (value.length > 100) {
                showError(field, 'Email không được vượt quá 100 ký tự.');
            } else {
                field.classList.add('valid');
            }
            break;

        case 'address':
            if (value.length > 0 && value.length > 500) {
                showError(field, 'Địa chỉ không được vượt quá 500 ký tự.');
            } else if (value.length > 0) {
                field.classList.add('valid');
            }
            break;

        case 'website':
            const urlRegex = /^(https?:\/\/)?[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}.*$/;
            if (value.length > 0) {
                if (value.length > 255) {
                    showError(field, 'Website không được vượt quá 255 ký tự.');
                } else if (!urlRegex.test(value)) {
                    showError(field, 'Website không hợp lệ. Ví dụ: https://example.com');
                } else {
                    field.classList.add('valid');
                }
            }
            break;
    }
}

// Validate publisher form before submit
function validatePublisherForm(form) {
    let isValid = true;
    let firstInvalidField = null;

    // Clear all previous errors
    form.querySelectorAll('.invalid').forEach(el => el.classList.remove('invalid'));
    form.querySelectorAll('.error-message').forEach(el => el.remove());

    // 1. Validate Name
    const name = form.querySelector('#name');
    if (!name.value.trim()) {
        showError(name, 'Tên nhà xuất bản không được để trống.');
        if (!firstInvalidField) firstInvalidField = name;
        isValid = false;
    } else if (name.value.length > 255) {
        showError(name, 'Tên nhà xuất bản không được vượt quá 255 ký tự.');
        if (!firstInvalidField) firstInvalidField = name;
        isValid = false;
    } else {
        name.classList.add('valid');
    }

    // 2. Validate Contact Email
    const email = form.querySelector('#contactEmail');
    const emailRegex = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
    if (!email.value.trim()) {
        showError(email, 'Email liên hệ không được để trống.');
        if (!firstInvalidField) firstInvalidField = email;
        isValid = false;
    } else if (!emailRegex.test(email.value)) {
        showError(email, 'Email liên hệ không hợp lệ.');
        if (!firstInvalidField) firstInvalidField = email;
        isValid = false;
    } else if (email.value.length > 100) {
        showError(email, 'Email không được vượt quá 100 ký tự.');
        if (!firstInvalidField) firstInvalidField = email;
        isValid = false;
    } else {
        email.classList.add('valid');
    }

    // 3. Validate Address (optional)
    const address = form.querySelector('#address');
    if (address.value.trim().length > 0 && address.value.length > 500) {
        showError(address, 'Địa chỉ không được vượt quá 500 ký tự.');
        if (!firstInvalidField) firstInvalidField = address;
        isValid = false;
    } else if (address.value.trim().length > 0) {
        address.classList.add('valid');
    }

    // 4. Validate Website (optional)
    const website = form.querySelector('#website');
    const urlRegex = /^(https?:\/\/)?[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}.*$/;
    if (website.value.trim().length > 0) {
        if (website.value.length > 255) {
            showError(website, 'Website không được vượt quá 255 ký tự.');
            if (!firstInvalidField) firstInvalidField = website;
            isValid = false;
        } else if (!urlRegex.test(website.value)) {
            showError(website, 'Website không hợp lệ. Ví dụ: https://example.com');
            if (!firstInvalidField) firstInvalidField = website;
            isValid = false;
        } else {
            website.classList.add('valid');
        }
    }

    // Focus on first invalid field
    if (!isValid && firstInvalidField) {
        firstInvalidField.focus();
        firstInvalidField.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }

    return isValid;
}

// Global reference to Tom Select instance
let authorTomSelect = null;
let authorFieldTouched = false; // Flag to track if user has interacted with author field

$(document).ready(function() {
    // Form validation on submit - check if it's Book form or Publisher form
    const bookForm = document.querySelector('.form-card:not(.publisher-form)');
    const publisherForm = document.querySelector('.form-card.publisher-form');

    if (bookForm) {
        bookForm.addEventListener('submit', function(e) {
            if (!validateBookForm(this)) {
                e.preventDefault();
                return false;
            }
        });

        // Add blur validation for real-time feedback
        bookForm.querySelectorAll('input, select, textarea').forEach(function(field) {
            // Skip Tom Select (handled separately)
            if (field.id === 'authorSelect') return;

            field.addEventListener('blur', function() {
                validateSingleField(this);
            });

            // Clear error on input
            field.addEventListener('input', function() {
                clearError(this);
            });
        });
    }

    // Publisher form validation
    if (publisherForm) {
        publisherForm.addEventListener('submit', function(e) {
            if (!validatePublisherForm(this)) {
                e.preventDefault();
                return false;
            }
        });

        // Add blur validation for real-time feedback
        publisherForm.querySelectorAll('input').forEach(function(field) {
            field.addEventListener('blur', function() {
                validatePublisherField(this);
            });

            // Clear error on input
            field.addEventListener('input', function() {
                clearError(this);
            });
        });
    }

    const authorSelectElement = document.getElementById('authorSelect');

    // Author multi-select (Tom Select)
    if (authorSelectElement && typeof TomSelect !== 'undefined') {
        // Remove any existing invalid state before initialization
        authorSelectElement.classList.remove('invalid');
        const existingWrapper = authorSelectElement.parentElement.querySelector('.ts-wrapper');
        if (existingWrapper) {
            existingWrapper.classList.remove('invalid');
        }

        authorTomSelect = new TomSelect(authorSelectElement, {
            plugins: ['remove_button', 'clear_button'],
            persist: false,
            create: false,
            maxItems: null,
            placeholder: 'Type to search authors...',
            searchField: ['text'],
            sortField: {
                field: 'text',
                direction: 'asc'
            },
            render: {
                no_results: function(data, escape) {
                    return '<div class="no-results">No authors found for "' + escape(data.input) + '"</div>';
                }
            },
            onInitialize: function() {
                // Ensure no invalid state after initialization
                const wrapper = this.wrapper;
                if (wrapper) {
                    wrapper.classList.remove('invalid');
                }
            }
        });

        // Check if there are pre-selected items (for update form)
        // If yes, mark as touched but don't show error
        setTimeout(function() {
            if (authorTomSelect.items.length > 0) {
                authorFieldTouched = true; // Mark as touched since there's data
                clearErrorTomSelect(authorSelectElement); // Ensure no error
            }
        }, 50);

        // Track first interaction
        authorTomSelect.on('focus', function() {
            authorFieldTouched = true;
        });

        // Validate on change
        authorTomSelect.on('change', function() {
            if (authorTomSelect.items.length > 0) {
                clearErrorTomSelect(authorSelectElement);
            } else if (authorFieldTouched) {
                // Only show error if user has interacted and now has no items
                showErrorTomSelect(authorSelectElement, 'Vui lòng chọn ít nhất một tác giả.');
            }
        });

        // Validate on blur ONLY if user has interacted
        authorTomSelect.on('blur', function() {
            if (authorFieldTouched && authorTomSelect.items.length === 0) {
                showErrorTomSelect(authorSelectElement, 'Vui lòng chọn ít nhất một tác giả.');
            }
        });

        // Make globally accessible for validation
        window.authorTomSelect = authorTomSelect;

        // Clear any invalid state that might have been added
        setTimeout(function() {
            const wrapper = authorSelectElement.nextElementSibling;
            if (wrapper && wrapper.classList.contains('ts-wrapper')) {
                wrapper.classList.remove('invalid');
                const formRow = wrapper.closest('.form-row');
                if (formRow) {
                    const errorMsg = formRow.querySelector('.error-message');
                    if (errorMsg) errorMsg.remove();
                }
            }
        }, 100);
    }

    // Custom delete confirmation modal
    let pendingDeleteHref = null;
    const $modal = $('#confirmModal');
    const $confirmText = $('#confirmText');

    // Click Delete -> only open modal (do NOT delete yet)
    $(document).on('click', 'a.btn-action.delete', function(e) {
        e.preventDefault();

        pendingDeleteHref = $(this).attr('href');
        const id = $(this).data('id');

        $confirmText.text(`Are you sure you want to delete ID = ${id}?`);
        $modal.addClass('show');
    });

    // Cancel -> close modal, clear pending
    $('#btnCancel').on('click', function() {
        pendingDeleteHref = null;
        $modal.removeClass('show');
    });

    // Confirm -> navigate to delete url (same behavior style as Edit)
    $('#btnConfirm').on('click', function() {
        if (!pendingDeleteHref) return;

        const href = pendingDeleteHref;
        pendingDeleteHref = null;
        $modal.removeClass('show');

        window.location.href = href;
    });
});