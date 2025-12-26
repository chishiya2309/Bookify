<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<style>
    .modal {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        z-index: 9999;
        display: flex;
        align-items: center;
        justify-content: center;
    }
    
    .modal-overlay {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0, 0, 0, 0.5);
        backdrop-filter: blur(4px);
    }
    
    .modal-content {
        position: relative;
        background: white;
        border-radius: 12px;
        max-width: 600px;
        width: 90%;
        max-height: 90vh;
        overflow-y: auto;
        box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
        animation: modalSlideIn 0.3s ease-out;
    }
    
    @keyframes modalSlideIn {
        from {
            opacity: 0;
            transform: translateY(-50px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
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
            <h3><i class="fas fa-map-marker-alt"></i> Thêm địa chỉ mới</h3>
            <button type="button" class="modal-close" onclick="closeAddressModal()">
                <i class="fas fa-times"></i>
            </button>
        </header>
        
        <section class="modal-body">
            <form id="addAddressForm">
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

// Open modal
function openAddressModal() {
    document.getElementById('addressModal').style.display = 'flex';
    loadProvinces();
}

// Close modal
function closeAddressModal() {
    document.getElementById('addressModal').style.display = 'none';
    document.getElementById('addAddressForm').reset();
    selectedProvinceName = '';
    selectedDistrictName = '';
    selectedWardName = '';
}

// Load provinces
function loadProvinces() {
    const provinceSelect = document.getElementById('provinceSelect');
    
    fetch(contextPath + '/api/address/provinces')
        .then(res => res.json())
        .then(provinces => {
            provinceSelect.innerHTML = '<option value="">-- Chọn Tỉnh/Thành phố --</option>';
            provinces.forEach(p => {
                const option = new Option(p.name, p.code);
                option.dataset.name = p.name;
                provinceSelect.add(option);
            });
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

// Form submit
document.getElementById('addAddressForm').addEventListener('submit', function(e) {
    e.preventDefault();
    
    const formData = new FormData();
    formData.append('recipientName', document.getElementById('newRecipientName').value);
    formData.append('phoneNumber', document.getElementById('newPhoneNumber').value);
    formData.append('street', document.getElementById('newStreet').value);
    formData.append('ward', selectedWardName);
    formData.append('district', selectedDistrictName);
    formData.append('province', selectedProvinceName);
    formData.append('zipCode', '');
    formData.append('isDefault', document.getElementById('setDefaultCheckbox').checked);
    
    fetch(contextPath + '/api/address/create', {
        method: 'POST',
        body: new URLSearchParams(formData)
    })
    .then(res => res.json())
    .then(data => {
        if (data.success) {
            alert('Địa chỉ đã được thêm thành công!');
            closeAddressModal();
            // Reload addresses
            if (typeof loadCustomerAddresses === 'function') {
                loadCustomerAddresses();
            } else {
                location.reload();
            }
        } else {
            alert('Lỗi: ' + (data.error || 'Không thể thêm địa chỉ'));
        }
    })
    .catch(err => {
        console.error('Failed to create address:', err);
        alert('Đã xảy ra lỗi khi thêm địa chỉ');
    });
});
</script>
