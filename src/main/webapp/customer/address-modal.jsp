<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<style>
    /* Address Modal - Fullscreen */
    #addressModal.modal {
        position: fixed !important;
        top: 0 !important;
        left: 0 !important;
        right: 0 !important;
        bottom: 0 !important;
        width: 100vw !important;
        height: 100vh !important;
        z-index: 99999 !important;
        display: none; /* Hidden by default */
        align-items: center !important;
        justify-content: center !important;
        margin: 0 !important;
        padding: 20px !important;
    }
    
    /* Show modal when has .show class */
    #addressModal.modal.show {
        display: flex !important;
    }
    
    #addressModal .modal-overlay {
        position: fixed;
        inset: 0;
        background: rgba(0, 0, 0, 0.7);
        backdrop-filter: blur(6px);
    }
    
    #addressModal .modal-content {
        position: relative;
        z-index: 2;
        background: #fff;
        width: 100%;
        max-width: 550px;
        max-height: calc(100vh - 40px);
        overflow-y: auto;
        border-radius: 16px;
        box-shadow: 0 25px 80px rgba(0, 0, 0, 0.4);
        animation: modalFadeIn 0.3s ease-out;
    }
    
    /* Mobile: fullscreen */
    @media (max-width: 767px) {
        #addressModal {
            padding: 0;
        }
        
        #addressModal .modal-content {
            width: 100%;
            height: 100%;
            max-width: 100%;
            max-height: 100%;
            border-radius: 0;
            box-shadow: none;
        }
    }
    
    @keyframes modalFadeIn {
        from {
            opacity: 0;
            transform: scale(0.95);
        }
        to {
            opacity: 1;
            transform: scale(1);
        }
    }
    
    .modal-header {
        padding: 20px 24px;
        border-bottom: 1px solid #e0e0e0;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }
    
    .modal-header h3 {
        margin: 0;
        font-size: 20px;
        color: var(--text-main);
        display: flex;
        align-items: center;
        gap: 10px;
    }
    
    .modal-close {
        background: none;
        border: none;
        font-size: 24px;
        cursor: pointer;
        color: var(--text-light);
        padding: 4px 8px;
        transition: color 0.2s;
    }
    
    .modal-close:hover {
        color: var(--color-error);
    }
    
    .modal-body {
        padding: 24px;
    }
    
    .form-row {
        margin-bottom: 16px;
        border: none;
        padding: 0;
        margin-left: 0;
        margin-right: 0;
    }
    
    .form-row label {
        display: block;
        margin-bottom: 6px;
        font-weight: 500;
        color: var(--text-main);
    }
    
    .required {
        color: var(--color-error);
    }
    
    .form-row input[type="text"],
    .form-row input[type="tel"],
    .form-row select {
        width: 100%;
        padding: 10px 12px;
        border: 1px solid var(--input-border);
        border-radius: 6px;
        font-size: 14px;
        transition: border-color 0.2s;
    }
    
    .form-row input:focus,
    .form-row select:focus {
        outline: none;
        border-color: var(--color-primary);
        box-shadow: 0 0 0 3px rgba(13, 110, 253, 0.1);
    }
    
    .form-row select:disabled {
        background-color: #f5f5f5;
        cursor: not-allowed;
    }
    
    .modal-footer {
        display: flex;
        gap: 12px;
        justify-content: flex-end;
        margin-top: 24px;
    }
    
    .btn-cancel, .btn-save {
        padding: 10px 20px;
        border-radius: 6px;
        font-size: 14px;
        font-weight: 500;
        cursor: pointer;
        transition: all 0.2s;
        border: none;
    }
    
    .btn-cancel {
        background: #f5f5f5;
        color: var(--text-main);
    }
    
    .btn-cancel:hover {
        background: #e0e0e0;
    }
    
    .btn-save {
        background: var(--color-primary);
        color: white;
        display: flex;
        align-items: center;
        gap: 8px;
    }
    
    .btn-save:hover {
        background: #0b5ed7;
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(13, 110, 253, 0.3);
    }
</style>

<!-- Address Selection Modal -->
<div id="addressModal" class="modal" style="display: none;">
    <div class="modal-overlay" onclick="closeAddressModal()"></div>
    <section class="modal-content">
        <header class="modal-header">
            <h3 id="modalTitle"><i class="fas fa-map-marker-alt"></i> <span id="modalTitleText">Thêm địa chỉ mới</span></h3>
            <button type="button" class="modal-close" onclick="closeAddressModal()">
                <i class="fas fa-times"></i>
            </button>
        </header>
        
        <section class="modal-body">
            <form id="addAddressForm">
                <input type="hidden" id="editAddressId" value="">
                <fieldset class="form-row">
                    <label>Người nhận <span class="required">*</span></label>
                    <input type="text" id="newRecipientName" name="recipientName" 
                           placeholder="Họ và tên" required>
                </fieldset>
                
                <fieldset class="form-row">
                    <label>Số điện thoại <span class="required">*</span></label>
                    <input type="tel" id="newPhoneNumber" name="phoneNumber" 
                           placeholder="0123456789" required>
                </fieldset>
                
                <fieldset class="form-row">
                    <label>Tỉnh/Thành phố <span class="required">*</span></label>
                    <select id="provinceSelect" name="provinceCode" required>
                        <option value="">-- Chọn Tỉnh/Thành phố --</option>
                    </select>
                </fieldset>
                
                <fieldset class="form-row">
                    <label>Quận/Huyện <span class="required">*</span></label>
                    <select id="districtSelect" name="districtCode" required disabled>
                        <option value="">-- Chọn Quận/Huyện --</option>
                    </select>
                </fieldset>
                
                <fieldset class="form-row">
                    <label>Xã/Phường <span class="required">*</span></label>
                    <select id="wardSelect" name="wardCode" required disabled>
                        <option value="">-- Chọn Xã/Phường --</option>
                    </select>
                </fieldset>
                
                <fieldset class="form-row">
                    <label>Địa chỉ cụ thể <span class="required">*</span></label>
                    <input type="text" id="newStreet" name="street" 
                           placeholder="Số nhà, tên đường" required>
                </fieldset>
                
                <fieldset class="form-row">
                    <label>
                        <input type="checkbox" id="setDefaultCheckbox" name="isDefault">
                        Đặt làm địa chỉ mặc định
                    </label>
                </fieldset>
                
                <footer class="modal-footer">
                    <button type="button" class="btn-cancel" onclick="closeAddressModal()">
                        Hủy
                    </button>
                    <button type="submit" class="btn-save">
                        <i class="fas fa-save"></i> Lưu địa chỉ
                    </button>
                </footer>
            </form>
        </section>
    </section>
</div>

<script>
const contextPath = '${pageContext.request.contextPath}';

// Province, District, Ward data
let selectedProvinceName = '';
let selectedDistrictName = '';
let selectedWardName = '';

// Move modal to body to avoid CSS constraints from parent elements
(function() {
    document.addEventListener('DOMContentLoaded', function() {
        const modal = document.getElementById('addressModal');
        if (modal && modal.parentElement !== document.body) {
            document.body.appendChild(modal);
        }
    });
})();

// Open modal
function openAddressModal() {
    const modal = document.getElementById('addressModal');
    // Ensure modal is in body for proper positioning
    if (modal.parentElement !== document.body) {
        document.body.appendChild(modal);
    }
    // Add show class to display modal (CSS handles centering)
    modal.classList.add('show');
    document.body.style.overflow = 'hidden'; // Prevent background scroll
    loadProvinces();
}

// Close modal
function closeAddressModal() {
    document.getElementById('addressModal').classList.remove('show');
    document.body.style.overflow = ''; // Restore background scroll
    document.getElementById('addAddressForm').reset();
    document.getElementById('editAddressId').value = '';
    document.getElementById('modalTitleText').textContent = 'Thêm địa chỉ mới';
    
    // Remove edit location info if exists
    const existingInfo = document.querySelector('.edit-location-info');
    if (existingInfo) existingInfo.remove();
    
    selectedProvinceName = '';
    selectedDistrictName = '';
    selectedWardName = '';
}

// Load provinces
function loadProvinces() {
    const provinceSelect = document.getElementById('provinceSelect');
    
    return fetch(contextPath + '/api/address/provinces')
        .then(res => res.json())
        .then(provinces => {
            provinceSelect.innerHTML = '<option value="">-- Chọn Tỉnh/Thành phố --</option>';
            provinces.forEach(p => {
                const option = new Option(p.name, p.code);
                option.dataset.name = p.name;
                provinceSelect.add(option);
            });
            return provinces;
        })
        .catch(err => {
            console.error('Failed to load provinces:', err);
            alert('Không thể tải danh sách tỉnh/thành phố');
        });
}

// Province change handler
document.getElementById('provinceSelect').addEventListener('change', function() {
    const districtSelect = document.getElementById('districtSelect');
    const wardSelect = document.getElementById('wardSelect');
    const selectedOption = this.options[this.selectedIndex];
    
    selectedProvinceName = selectedOption.dataset.name || '';
    
    if (this.value) {
        districtSelect.disabled = false;
        districtSelect.innerHTML = '<option value="">Đang tải...</option>';
        
        fetch(contextPath + '/api/address/districts/' + this.value)
            .then(res => res.json())
            .then(districts => {
                districtSelect.innerHTML = '<option value="">-- Chọn Quận/Huyện --</option>';
                districts.forEach(d => {
                    const option = new Option(d.name, d.code);
                    option.dataset.name = d.name;
                    districtSelect.add(option);
                });
            })
            .catch(err => {
                console.error('Failed to load districts:', err);
                districtSelect.innerHTML = '<option value="">Lỗi tải dữ liệu</option>';
            });
        
        // Reset ward
        wardSelect.disabled = true;
        wardSelect.innerHTML = '<option value="">-- Chọn Xã/Phường --</option>';
        selectedDistrictName = '';
        selectedWardName = '';
    } else {
        districtSelect.disabled = true;
        wardSelect.disabled = true;
        districtSelect.innerHTML = '<option value="">-- Chọn Quận/Huyện --</option>';
        wardSelect.innerHTML = '<option value="">-- Chọn Xã/Phường --</option>';
    }
});

// District change handler
document.getElementById('districtSelect').addEventListener('change', function() {
    const wardSelect = document.getElementById('wardSelect');
    const selectedOption = this.options[this.selectedIndex];
    
    selectedDistrictName = selectedOption.dataset.name || '';
    
    if (this.value) {
        wardSelect.disabled = false;
        wardSelect.innerHTML = '<option value="">Đang tải...</option>';
        
        fetch(contextPath + '/api/address/wards/' + this.value)
            .then(res => res.json())
            .then(wards => {
                wardSelect.innerHTML = '<option value="">-- Chọn Xã/Phường --</option>';
                wards.forEach(w => {
                    const option = new Option(w.name, w.code);
                    option.dataset.name = w.name;
                    wardSelect.add(option);
                });
            })
            .catch(err => {
                console.error('Failed to load wards:', err);
                wardSelect.innerHTML = '<option value="">Lỗi tải dữ liệu</option>';
            });
        
        selectedWardName = '';
    } else {
        wardSelect.disabled = true;
        wardSelect.innerHTML = '<option value="">-- Chọn Xã/Phường --</option>';
    }
});

// Ward change handler
document.getElementById('wardSelect').addEventListener('change', function() {
    const selectedOption = this.options[this.selectedIndex];
    selectedWardName = selectedOption.dataset.name || '';
});

// Form submit - handles both create and update
document.getElementById('addAddressForm').addEventListener('submit', function(e) {
    e.preventDefault();
    
    const addressId = document.getElementById('editAddressId').value;
    const isEdit = addressId && addressId !== '';
    
    const formData = new FormData();
    if (isEdit) {
        formData.append('addressId', addressId);
    }
    formData.append('recipientName', document.getElementById('newRecipientName').value);
    formData.append('phoneNumber', document.getElementById('newPhoneNumber').value);
    formData.append('street', document.getElementById('newStreet').value);
    formData.append('ward', selectedWardName);
    formData.append('district', selectedDistrictName);
    formData.append('province', selectedProvinceName);
    formData.append('zipCode', '');
    formData.append('isDefault', document.getElementById('setDefaultCheckbox').checked);
    
    const endpoint = isEdit ? '/api/address/update' : '/api/address/create';
    
    fetch(contextPath + endpoint, {
        method: 'POST',
        body: new URLSearchParams(formData)
    })
    .then(res => res.json())
    .then(data => {
        if (data.success) {
            alert(isEdit ? 'Địa chỉ đã được cập nhật!' : 'Địa chỉ đã được thêm thành công!');
            closeAddressModal();
            location.reload();
        } else {
            alert('Lỗi: ' + (data.error || 'Không thể lưu địa chỉ'));
        }
    })
    .catch(err => {
        console.error('Failed to save address:', err);
        alert('Đã xảy ra lỗi khi lưu địa chỉ');
    });
});

// Edit address - fetch data and open modal
function editAddress(addressId) {
    fetch(contextPath + '/api/address/get/' + addressId)
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                // Set modal to edit mode
                document.getElementById('editAddressId').value = addressId;
                document.getElementById('modalTitleText').textContent = 'Chỉnh sửa địa chỉ';
                
                // Populate form fields
                document.getElementById('newRecipientName').value = data.recipientName || '';
                document.getElementById('newPhoneNumber').value = data.phoneNumber || '';
                document.getElementById('newStreet').value = data.street || '';
                document.getElementById('setDefaultCheckbox').checked = data.isDefault || false;
                
                // Store names for edit mode (since we can't easily reload selects)
                selectedProvinceName = data.province || '';
                selectedDistrictName = data.district || '';
                selectedWardName = data.ward || '';
                
                // Show modal
                document.getElementById('addressModal').classList.add('show');
                document.body.style.overflow = 'hidden';
                
                // Load provinces and try to select correct values
                loadProvinces().then(() => {
                    // Add info message about location
                    const infoMsg = document.createElement('div');
                    infoMsg.className = 'edit-location-info';
                    infoMsg.style.cssText = 'background: #fff3cd; padding: 10px; border-radius: 6px; margin-bottom: 15px; font-size: 13px; color: #856404;';
                    infoMsg.innerHTML = '<i class="fas fa-info-circle"></i> Địa chỉ hiện tại: <strong>' + data.ward + ', ' + data.district + ', ' + data.province + '</strong><br><small>Chọn lại tỉnh/thành phố nếu muốn thay đổi địa chỉ.</small>';
                    
                    const existingInfo = document.querySelector('.edit-location-info');
                    if (existingInfo) existingInfo.remove();
                    
                    const formBody = document.querySelector('#addAddressForm');
                    formBody.insertBefore(infoMsg, formBody.firstChild.nextSibling);
                });
            } else {
                alert('Không thể tải thông tin địa chỉ');
            }
        })
        .catch(err => {
            console.error('Failed to fetch address:', err);
            alert('Đã xảy ra lỗi');
        });
}

// Delete address with confirmation and order check
function deleteAddress(addressId) {
    // First check if address can be deleted
    fetch(contextPath + '/api/address/canDelete/' + addressId)
        .then(res => res.json())
        .then(data => {
            if (!data.canDelete) {
                alert('❌ ' + (data.reason || 'Không thể xóa địa chỉ này'));
                return;
            }
            
            if (confirm('Bạn có chắc chắn muốn xóa địa chỉ này?')) {
                // Use XMLHttpRequest for DELETE method
                const xhr = new XMLHttpRequest();
                xhr.open('DELETE', contextPath + '/api/address/delete/' + addressId, true);
                xhr.onreadystatechange = function() {
                    if (xhr.readyState === 4) {
                        const response = JSON.parse(xhr.responseText);
                        if (response.success) {
                            alert('✅ Địa chỉ đã được xóa');
                            location.reload();
                        } else {
                            alert('❌ ' + (response.error || 'Không thể xóa địa chỉ'));
                        }
                    }
                };
                xhr.send();
            }
        })
        .catch(err => {
            console.error('Failed to check delete:', err);
            alert('Đã xảy ra lỗi');
        });
}

// Set address as default
function setDefaultAddress(addressId) {
    fetch(contextPath + '/api/address/set-default/' + addressId, {
        method: 'POST'
    })
    .then(res => res.json())
    .then(data => {
        if (data.success) {
            alert('✅ Đã đặt làm địa chỉ mặc định');
            location.reload();
        } else {
            alert('❌ ' + (data.error || 'Không thể đặt làm địa chỉ mặc định'));
        }
    })
    .catch(err => {
        console.error('Failed to set default:', err);
        alert('Đã xảy ra lỗi');
    });
}
</script>
