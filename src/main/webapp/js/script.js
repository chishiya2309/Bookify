function showPreview(fileInput) {
    var file = fileInput.files[0];
    if (file) {
        var reader = new FileReader();
        reader.onload = function(e) {
            var img = document.getElementById('thumbnail');
            img.src = e.target.result;
            img.style.display = 'block';
        };
        reader.readAsDataURL(file);
    }
}
    $(document).ready(function() {
    $('#authorSelect').select2({
        placeholder: "Select author...",
        allowClear: true,
        closeOnSelect: false
    });
});
// 1. Kích hoạt Select2 cho ô Author
$(document).ready(function() {
    $('#authorSelect').select2({
        placeholder: "Select authors...",
        allowClear: true,
        closeOnSelect: false // Giữ menu mở để chọn nhiều người nhanh hơn
    });
});

// 2. Hàm Preview ảnh ngay lập tức
function showPreview(fileInput) {
    var file = fileInput.files[0];
    if (file) {
        var reader = new FileReader();
        reader.onload = function(e) {
            var img = document.getElementById('thumbnail');
            img.src = e.target.result;
            img.style.display = 'block';
        };
        reader.readAsDataURL(file);
    }
}