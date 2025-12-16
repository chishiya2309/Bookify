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
        placeholder: "Select authors...",
        allowClear: true,
        closeOnSelect: false // Giữ menu mở để chọn nhiều người nhanh hơn
    });
});