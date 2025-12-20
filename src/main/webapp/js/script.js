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

// ===== VALIDATION SYSTEM - REUSABLE FOR ALL FORMS =====

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

// Validation Rules - Centralized validation logic
const ValidationRules = {
    required: (value, message = 'Trường này không được để trống.') => {
        return value && value.trim() ? null : message;
    },

    minLength: (value, min, message = `Tối thiểu ${min} ký tự.`) => {
        return value.trim().length >= min ? null : message;
    },

    maxLength: (value, max, message = `Tối đa ${max} ký tự.`) => {
        return value.length <= max ? null : message;
    },

    pattern: (value, regex, message = 'Định dạng không hợp lệ.') => {
        return regex.test(value) ? null : message;
    },

    email: (value, message = 'Email không hợp lệ.') => {
        const emailRegex = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
        return emailRegex.test(value) ? null : message;
    },

    url: (value, message = 'URL không hợp lệ.') => {
        const urlRegex = /^(https?:\/\/)?[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}.*$/;
        return urlRegex.test(value) ? null : message;
    },

    number: (value, message = 'Phải là số hợp lệ.') => {
        return !isNaN(parseFloat(value)) ? null : message;
    },

    min: (value, minValue, message = `Giá trị tối thiểu là ${minValue}.`) => {
        return parseFloat(value) >= minValue ? null : message;
    },

    max: (value, maxValue, message = `Giá trị tối đa là ${maxValue}.`) => {
        return parseFloat(value) <= maxValue ? null : message;
    },

    dateNotFuture: (value, message = 'Ngày không thể là ngày trong tương lai.') => {
        const selectedDate = new Date(value);
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        return selectedDate <= today ? null : message;
    }
};

// Generic Form Validator
class FormValidator {
    constructor(formSelector, validationConfig) {
        this.form = document.querySelector(formSelector);
        this.config = validationConfig;
        this.isValid = true;
        this.firstInvalidField = null;
    }

    // Validate single field
    validateField(field, rules) {
        clearError(field);
        const value = field.value || '';

        for (const rule of rules) {
            const error = rule(value);
            if (error) {
                showError(field, error);
                return false;
            }
        }

        field.classList.add('valid');
        return true;
    }

    // Validate entire form
    validateForm() {
        this.isValid = true;
        this.firstInvalidField = null;

        // Clear all previous errors
        this.form.querySelectorAll('.invalid').forEach(el => el.classList.remove('invalid'));
        this.form.querySelectorAll('.error-message').forEach(el => el.remove());

        // Validate each field according to config
        for (const [fieldId, rules] of Object.entries(this.config)) {
            const field = this.form.querySelector(`#${fieldId}`);
            if (!field) continue;

            if (!this.validateField(field, rules)) {
                if (!this.firstInvalidField) {
                    this.firstInvalidField = field;
                }
                this.isValid = false;
            }
        }

        // Focus on first invalid field
        if (!this.isValid && this.firstInvalidField) {
            this.firstInvalidField.focus();
            this.firstInvalidField.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }

        return this.isValid;
    }

    // Attach blur event listeners
    attachBlurValidation() {
        for (const [fieldId, rules] of Object.entries(this.config)) {
            const field = this.form.querySelector(`#${fieldId}`);
            if (!field) continue;

            field.addEventListener('blur', () => this.validateField(field, rules));
            field.addEventListener('input', () => clearError(field));
        }
    }
}

// ===== FORM VALIDATION CONFIGS =====

// Book Form Validation Config
const bookValidationConfig = {
    categoryId: [
        (value) => ValidationRules.required(value, 'Vui lòng chọn danh mục sách.')
    ],
    title: [
        (value) => ValidationRules.required(value, 'Tiêu đề không được để trống.'),
        (value) => ValidationRules.maxLength(value, 255, 'Tiêu đề không được vượt quá 255 ký tự.')
    ],
    isbn: [
        (value) => ValidationRules.required(value, 'Mã ISBN không được để trống.'),
        (value) => ValidationRules.pattern(value, /^[0-9\-]{10,20}$/, 'ISBN phải có 10-20 ký tự, chỉ bao gồm số và dấu gạch ngang.'),
        (value) => (value.length >= 10 && value.length <= 20) ? null : 'ISBN phải có độ dài từ 10 đến 20 ký tự.'
    ],
    publishDate: [
        (value) => ValidationRules.required(value, 'Vui lòng chọn ngày xuất bản.'),
        (value) => ValidationRules.dateNotFuture(value, 'Ngày xuất bản không thể là ngày trong tương lai.')
    ],
    price: [
        (value) => ValidationRules.required(value, 'Giá sách không được để trống.'),
        (value) => ValidationRules.number(value, 'Giá sách phải là số hợp lệ.'),
        (value) => ValidationRules.min(value, 0.01, 'Giá sách phải lớn hơn 0.'),
        (value) => ValidationRules.max(value, 99999999.99, 'Giá sách không được vượt quá 99,999,999.99.')
    ],
    description: [
        (value) => {
            if (value.trim().length === 0) return null; // Optional field
            if (value.trim().length < 10) return 'Mô tả phải có ít nhất 10 ký tự.';
            if (value.length > 5000) return 'Mô tả không được vượt quá 5000 ký tự.';
            return null;
        }
    ]
};

// Publisher Form Validation Config
const publisherValidationConfig = {
    name: [
        (value) => ValidationRules.required(value, 'Tên nhà xuất bản không được để trống.'),
        (value) => ValidationRules.maxLength(value, 255, 'Tên nhà xuất bản không được vượt quá 255 ký tự.')
    ],
    contactEmail: [
        (value) => ValidationRules.required(value, 'Email liên hệ không được để trống.'),
        (value) => ValidationRules.email(value, 'Email liên hệ không hợp lệ.'),
        (value) => ValidationRules.maxLength(value, 100, 'Email không được vượt quá 100 ký tự.')
    ],
    address: [
        (value) => {
            if (value.trim().length === 0) return null; // Optional
            return ValidationRules.maxLength(value, 500, 'Địa chỉ không được vượt quá 500 ký tự.');
        }
    ],
    website: [
        (value) => {
            if (value.trim().length === 0) return null; // Optional
            const maxLengthError = ValidationRules.maxLength(value, 255, 'Website không được vượt quá 255 ký tự.');
            if (maxLengthError) return maxLengthError;
            return ValidationRules.url(value, 'Website không hợp lệ. Ví dụ: https://example.com');
        }
    ]
};

// Category Form Validation Config
const categoryValidationConfig = {
    name: [
        (value) => ValidationRules.required(value, 'Tên danh mục không được để trống.'),
        (value) => ValidationRules.maxLength(value, 100, 'Tên danh mục không được vượt quá 100 ký tự.')
    ]
};

// ===== LEGACY FUNCTIONS (keep for special cases) =====

// Validate book form with special handling for authors and image
function validateBookForm(form) {
    const validator = new FormValidator('.form-card:not(.publisher-form):not(.category-form)', bookValidationConfig);
    let isValid = validator.validateForm();
    let firstInvalidField = validator.firstInvalidField;

    // Special validation for Authors (Tom Select)
    const authorSelect = form.querySelector('#authorSelect');
    if (authorSelect) {
        let hasAuthors = false;
        if (window.authorTomSelect && window.authorTomSelect.items) {
            hasAuthors = window.authorTomSelect.items.length > 0;
        } else {
            const selectedOptions = Array.from(authorSelect.selectedOptions);
            hasAuthors = selectedOptions.length > 0;
        }

        if (!hasAuthors) {
            showErrorTomSelect(authorSelect, 'Vui lòng chọn ít nhất một tác giả.');
            if (!firstInvalidField) firstInvalidField = authorSelect;
            isValid = false;
        } else {
            clearErrorTomSelect(authorSelect);
        }
    }

    // Special validation for Book Image (create form only)
    const bookImage = form.querySelector('#bookImage');
    if (bookImage && bookImage.hasAttribute('required')) {
        if (!bookImage.files || bookImage.files.length === 0) {
            showError(bookImage, 'Vui lòng chọn hình ảnh cho sách.');
            if (!firstInvalidField) firstInvalidField = bookImage;
            isValid = false;
        }
    }

    // Focus on first invalid field (including special fields)
    if (!isValid && firstInvalidField) {
        if (firstInvalidField.id === 'authorSelect') {
            const wrapper = firstInvalidField.nextElementSibling;
            if (wrapper && wrapper.classList.contains('ts-wrapper')) {
                const input = wrapper.querySelector('.ts-control input');
                if (input) input.focus();
            }
        } else {
            firstInvalidField.focus();
        }
        firstInvalidField.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }

    return isValid;
}

// Validate single field (for blur event)
function validateSingleField(field) {
    const fieldId = field.id;
    if (bookValidationConfig[fieldId]) {
        const validator = new FormValidator('.form-card:not(.publisher-form):not(.category-form)', bookValidationConfig);
        return validator.validateField(field, bookValidationConfig[fieldId]);
    }
    return true;
}

// Helper: Show error for Tom Select
function showErrorTomSelect(selectElement, message) {
    let wrapper = selectElement.nextElementSibling;
    if (!wrapper || !wrapper.classList.contains('ts-wrapper')) {
        wrapper = selectElement.parentElement.querySelector('.ts-wrapper');
    }

    if (wrapper) {
        wrapper.classList.add('invalid');
        const formRow = wrapper.closest('.form-row');
        if (formRow) {
            const existingError = formRow.querySelector('.error-message');
            if (existingError) existingError.remove();

            const errorDiv = document.createElement('div');
            errorDiv.className = 'error-message';
            errorDiv.textContent = message;
            formRow.appendChild(errorDiv);
        }
    }
}

// Helper: Clear error for Tom Select
function clearErrorTomSelect(selectElement) {
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

// Publisher form validation using FormValidator
function validatePublisherForm(form) {
    const validator = new FormValidator('.form-card.publisher-form', publisherValidationConfig);
    return validator.validateForm();
}

function validatePublisherField(field) {
    const fieldId = field.id;
    if (publisherValidationConfig[fieldId]) {
        const validator = new FormValidator('.form-card.publisher-form', publisherValidationConfig);
        return validator.validateField(field, publisherValidationConfig[fieldId]);
    }
    return true;
}

// Category form validation using FormValidator
function validateCategoryForm(form) {
    const validator = new FormValidator('.form-card.category-form', categoryValidationConfig);
    return validator.validateForm();
}

function validateCategoryField(field) {
    const fieldId = field.id;
    if (categoryValidationConfig[fieldId]) {
        const validator = new FormValidator('.form-card.category-form', categoryValidationConfig);
        return validator.validateField(field, categoryValidationConfig[fieldId]);
    }
    return true;
}

// Global reference to Tom Select instance
let authorTomSelect = null;
let authorFieldTouched = false;

$(document).ready(function() {
    // Form validation on submit - check if it's Book form, Publisher form, or Category form
    const bookForm = document.querySelector('.form-card:not(.publisher-form):not(.category-form)');
    const publisherForm = document.querySelector('.form-card.publisher-form');
    const categoryForm = document.querySelector('.form-card.category-form');

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

    // Category form validation
    if (categoryForm) {
        categoryForm.addEventListener('submit', function(e) {
            if (!validateCategoryForm(this)) {
                e.preventDefault();
                return false;
            }
        });

        // Add blur validation for real-time feedback
        categoryForm.querySelectorAll('input').forEach(function(field) {
            field.addEventListener('blur', function() {
                validateCategoryField(this);
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