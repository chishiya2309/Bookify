package com.bookstore.controller.admin;

import com.bookstore.model.*;
import com.bookstore.model.Publisher;
import com.bookstore.service.BookServices;

import com.bookstore.util.CloudinaryUtil;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@WebServlet("/admin/books")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 100 // 100MB for multiple images
)
public class BookServlet extends HttpServlet {
    private final BookServices bookService = new BookServices();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null || action.isEmpty()) {
            action = "list";
        }

        String url = "/admin/book/show.jsp";

        try {
            switch (action) {
                case "list":
                    listBooks(request, response);
                    break;
                case "showCreate":
                    showCreateForm(request, response);
                    url = "/admin/book/create.jsp";
                    break;
                case "showUpdate":
                    showUpdateForm(request, response);
                    url = "/admin/book/update.jsp";
                    break;
                case "create":
                    createBook(request, response);
                    return;
                case "update":
                    updateBook(request, response);
                    return;
                case "delete":
                    deleteBook(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
        }

        getServletContext().getRequestDispatcher(url).forward(request, response);
    }

    private void listBooks(HttpServletRequest request, HttpServletResponse response) {
        // Pagination parameters
        int page = 0;
        int size = 10; // Default 10 books per page

        String pageParam = request.getParameter("page");
        String sizeParam = request.getParameter("size");

        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 0)
                    page = 0;
            } catch (NumberFormatException e) {
                page = 0;
            }
        }

        if (sizeParam != null && !sizeParam.isEmpty()) {
            try {
                size = Integer.parseInt(sizeParam);
                if (size < 1)
                    size = 10;
                if (size > 100)
                    size = 100; // Max 100 per page
            } catch (NumberFormatException e) {
                size = 10;
            }
        }

        List<Book> books = bookService.getAllBooksPaginated(page, size);
        long totalBooks = bookService.countAllBooks();
        int totalPages = (int) Math.ceil((double) totalBooks / size);

        request.setAttribute("books", books);
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", size);
        request.setAttribute("totalBooks", totalBooks);
        request.setAttribute("totalPages", totalPages);
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("listCategory", bookService.getAllCategories());
        request.setAttribute("listAuthors", bookService.getAllAuthors());
        request.setAttribute("listPublishers", bookService.getAllPublishers());
    }

    private void showUpdateForm(HttpServletRequest request, HttpServletResponse response) {
        try {
            String bookIdStr = request.getParameter("bookId");
            if (bookIdStr != null && !bookIdStr.isEmpty()) {
                int bookId = Integer.parseInt(bookIdStr);
                Book book = bookService.getBookById(bookId);

                if (book == null) {
                    throw new ServletException("Book not found with ID: " + bookId);
                }

                // Sort images by sortOrder
                if (book.getImages() != null) {
                    book.getImages().sort(Comparator.comparing(BookImage::getSortOrder));
                }

                request.setAttribute("book", book);
                request.setAttribute("listCategory", bookService.getAllCategories());
                request.setAttribute("listAuthors", bookService.getAllAuthors());
                request.setAttribute("listPublishers", bookService.getAllPublishers());
            } else {
                throw new ServletException("Book ID is required");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Failed to load book: " + e.getMessage());
        }
    }

    private void createBook(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Book book = new Book();

        // Read basic book fields
        readBookFields(book, request);

        // Validate book data
        String validationError = validateBook(book, request, true);
        if (validationError != null) {
            request.setAttribute("errorMessage", validationError);
            request.setAttribute("listCategory", bookService.getAllCategories());
            request.setAttribute("listAuthors", bookService.getAllAuthors());
            request.setAttribute("listPublishers", bookService.getAllPublishers());
            getServletContext().getRequestDispatcher("/admin/book/create.jsp").forward(request, response);
            return;
        }

        // Handle multiple image uploads for create
        handleMultipleImageUpload(book, request, true);
        bookService.createBook(book);

        listBooks(request, response);
        getServletContext().getRequestDispatcher("/admin/book/show.jsp").forward(request, response);
    }

    private void updateBook(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String bookIdStr = request.getParameter("bookId");
        if (bookIdStr != null) {
            int bookId = Integer.parseInt(bookIdStr);
            // 1. Get existing book from DB
            Book book = bookService.getBookById(bookId);

            // 2. Read basic book fields
            readBookFields(book, request);

            // 3. Validate book data
            String validationError = validateBook(book, request, false);
            if (validationError != null) {
                request.setAttribute("errorMessage", validationError);
                request.setAttribute("book", book);
                request.setAttribute("listCategory", bookService.getAllCategories());
                request.setAttribute("listAuthors", bookService.getAllAuthors());
                request.setAttribute("listPublishers", bookService.getAllPublishers());
                getServletContext().getRequestDispatcher("/admin/book/update.jsp").forward(request, response);
                return;
            }

            // 4. Handle image deletions
            handleImageDeletions(book, request);

            // 5. Handle image reordering and primary setting
            handleImageReordering(book, request);

            // 6. Handle new image uploads
            handleMultipleImageUpload(book, request, false);

            // 7. Save to DB
            bookService.updateBook(book);
        }
        listBooks(request, response);
        getServletContext().getRequestDispatcher("/admin/book/show.jsp").forward(request, response);
    }

    private void handleImageDeletions(Book book, HttpServletRequest request) {
        String[] deleteIds = request.getParameterValues("deleteImageIds");
        if (deleteIds != null && deleteIds.length > 0) {
            Cloudinary cloudinary = CloudinaryUtil.cloudinary;
            Set<Integer> idsToDelete = new HashSet<>();

            for (String idStr : deleteIds) {
                try {
                    idsToDelete.add(Integer.parseInt(idStr));
                } catch (NumberFormatException e) {
                    // Skip invalid IDs
                }
            }

            // Remove images from book and delete from Cloudinary
            Iterator<BookImage> iterator = book.getImages().iterator();
            while (iterator.hasNext()) {
                BookImage img = iterator.next();
                if (idsToDelete.contains(img.getImageId())) {
                    try {
                        String publicId = extractPublicId(img.getUrl());
                        if (publicId != null) {
                            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    iterator.remove();
                }
            }
        }
    }

    private void handleImageReordering(Book book, HttpServletRequest request) {
        // Handle image order updates
        String[] orderEntries = request.getParameterValues("imageOrder");
        if (orderEntries != null) {
            Map<Integer, Integer> orderMap = new HashMap<>();
            for (String entry : orderEntries) {
                String[] parts = entry.split(":");
                if (parts.length == 2) {
                    try {
                        int imageId = Integer.parseInt(parts[0]);
                        int sortOrder = Integer.parseInt(parts[1]);
                        orderMap.put(imageId, sortOrder);
                    } catch (NumberFormatException e) {
                        // Skip invalid entries
                    }
                }
            }

            // Update sort order for each image
            for (BookImage img : book.getImages()) {
                if (orderMap.containsKey(img.getImageId())) {
                    img.setSortOrder(orderMap.get(img.getImageId()));
                }
            }
        }

        // Handle primary image setting
        String primaryIdStr = request.getParameter("primaryImageId");
        if (primaryIdStr != null && !primaryIdStr.isEmpty()) {
            try {
                int primaryId = Integer.parseInt(primaryIdStr);
                for (BookImage img : book.getImages()) {
                    img.setIsPrimary(img.getImageId().equals(primaryId));
                }
            } catch (NumberFormatException e) {
                // Ignore invalid primary ID
            }
        }
    }

    private void handleMultipleImageUpload(Book book, HttpServletRequest request, boolean isCreate)
            throws IOException, ServletException {

        Cloudinary cloudinary = CloudinaryUtil.cloudinary;

        // Get file input name based on context
        String inputName = isCreate ? "bookImages" : "newBookImages";
        Collection<Part> parts = request.getParts();

        // Get primary image index for create
        int primaryIndex = 0;
        if (isCreate) {
            String primaryIndexStr = request.getParameter("primaryImageIndex");
            if (primaryIndexStr != null) {
                try {
                    primaryIndex = Integer.parseInt(primaryIndexStr);
                } catch (NumberFormatException e) {
                    primaryIndex = 0;
                }
            }
        }

        // Calculate starting sort order
        int sortOrder = 0;
        if (!isCreate && book.getImages() != null) {
            for (BookImage img : book.getImages()) {
                if (img.getSortOrder() >= sortOrder) {
                    sortOrder = img.getSortOrder() + 1;
                }
            }
        }

        // Check if there's at least one primary image for existing images
        boolean hasPrimary = false;
        if (!isCreate && book.getImages() != null) {
            for (BookImage img : book.getImages()) {
                if (Boolean.TRUE.equals(img.getIsPrimary())) {
                    hasPrimary = true;
                    break;
                }
            }
        }

        // Ensure images list is initialized
        if (book.getImages() == null) {
            book.setImages(new ArrayList<>());
        }

        int uploadedIndex = 0;
        for (Part part : parts) {
            if (inputName.equals(part.getName()) && part.getSize() > 0 &&
                    part.getContentType() != null && part.getContentType().startsWith("image/")) {

                try {
                    // Read image bytes
                    byte[] imageBytes = part.getInputStream().readAllBytes();

                    // Generate unique public ID
                    String uniqueId = book.getIsbn() + "_" + System.currentTimeMillis() + "_" + uploadedIndex;

                    // Upload to Cloudinary
                    Map uploadResult = cloudinary.uploader().upload(
                            imageBytes,
                            ObjectUtils.asMap(
                                    "folder", "bookstore/books",
                                    "public_id", "book_" + uniqueId,
                                    "overwrite", true,
                                    "resource_type", "image"));

                    String imageUrl = uploadResult.get("secure_url").toString();

                    // Create BookImage entity
                    BookImage image = new BookImage();
                    image.setUrl(imageUrl);
                    image.setSortOrder(sortOrder++);
                    image.setBook(book);

                    // Set primary image
                    if (isCreate) {
                        image.setIsPrimary(uploadedIndex == primaryIndex);
                    } else {
                        // For update, only set as primary if no existing primary
                        image.setIsPrimary(!hasPrimary && uploadedIndex == 0);
                        if (image.getIsPrimary()) {
                            hasPrimary = true;
                        }
                    }

                    book.getImages().add(image);
                    uploadedIndex++;

                } catch (Exception e) {
                    throw new ServletException("Failed to upload image: " + e.getMessage(), e);
                }
            }
        }

        // Ensure at least one primary image exists
        if (!book.getImages().isEmpty()) {
            boolean anyPrimary = book.getImages().stream().anyMatch(img -> Boolean.TRUE.equals(img.getIsPrimary()));
            if (!anyPrimary) {
                book.getImages().get(0).setIsPrimary(true);
            }
        }
    }

    private void deleteBook(HttpServletRequest request, HttpServletResponse response) {
        String bookIdStr = request.getParameter("bookId");

        if (bookIdStr != null) {
            int bookId = Integer.parseInt(bookIdStr);

            // Kiểm tra xem sách có trong đơn hàng không
            boolean hasOrders = com.bookstore.dao.BookDAO.hasOrders(bookId);
            if (hasOrders) {
                request.setAttribute("errorMessage", 
                    "Không thể xoá sách này vì đã có đơn hàng liên kết. Sách đã được đặt mua bởi khách hàng.");
                listBooks(request, response);
                return;
            }

            Book book = bookService.getBookById(bookId);
            Cloudinary cloudinary = CloudinaryUtil.cloudinary;

            for (BookImage img : book.getImages()) {
                try {
                    String publicId = extractPublicId(img.getUrl());
                    cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            bookService.deleteBook(bookId);
            request.setAttribute("message", "Xoá sách thành công!");
            listBooks(request, response);
        }
    }

    private String extractPublicId(String imageUrl) {
        if (imageUrl == null)
            return null;

        String[] parts = imageUrl.split("/upload/");
        if (parts.length < 2)
            return null;

        String publicPath = parts[1];
        publicPath = publicPath.replaceFirst("^v\\d+/", "");

        int lastDot = publicPath.lastIndexOf('.');
        if (lastDot > 0) {
            return publicPath.substring(0, lastDot);
        }
        return publicPath;
    }

    private String validateBook(Book book, HttpServletRequest request, boolean isCreate)
            throws ServletException, IOException {
        // 1. Validate Title
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            return "Title is required.";
        }
        if (book.getTitle().length() > 255) {
            return "Title must not exceed 255 characters.";
        }

        // 2. Validate ISBN
        if (book.getIsbn() == null || book.getIsbn().trim().isEmpty()) {
            return "ISBN is required.";
        }
        if (book.getIsbn().length() < 10 || book.getIsbn().length() > 20) {
            return "ISBN must be between 10 and 20 characters.";
        }
        if (!book.getIsbn().matches("[0-9\\-]{10,20}")) {
            return "ISBN must contain only digits and hyphens.";
        }

        // 3. Validate Price
        if (book.getPrice() == null) {
            return "Price is required.";
        }
        if (book.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return "Price must be greater than 0.";
        }
        if (book.getPrice().compareTo(new BigDecimal("99999999.99")) > 0) {
            return "Price must not exceed 99,999,999.99.";
        }

        // 4. Validate Publish Date
        if (book.getPublishDate() == null) {
            return "Publish date is required.";
        }
        if (book.getPublishDate().isAfter(LocalDate.now())) {
            return "Publish date cannot be in the future.";
        }

        // 5. Validate Category
        if (book.getCategory() == null) {
            return "Category is required.";
        }

        // 6. Validate Authors
        if (book.getAuthors() == null || book.getAuthors().isEmpty()) {
            return "At least one author is required.";
        }

        // 7. Validate Description
        if (book.getDescription() != null && !book.getDescription().trim().isEmpty()) {
            if (book.getDescription().trim().length() < 10) {
                return "Description must be at least 10 characters if provided.";
            }
            if (book.getDescription().length() > 5000) {
                return "Description must not exceed 5000 characters.";
            }
        }

        // 8. Validate Book Images (required for create)
        if (isCreate) {
            String inputName = "bookImages";
            boolean hasImages = false;
            Collection<Part> parts = request.getParts();
            for (Part part : parts) {
                if (inputName.equals(part.getName()) && part.getSize() > 0) {
                    hasImages = true;

                    // Validate file type
                    String contentType = part.getContentType();
                    if (contentType == null || !contentType.startsWith("image/")) {
                        return "Invalid image format. Only JPEG, PNG, JPG, WEBP allowed.";
                    }

                    // Validate file size (max 5MB)
                    if (part.getSize() > 5 * 1024 * 1024) {
                        return "Image file size must not exceed 5MB.";
                    }
                }
            }
            if (!hasImages) {
                return "At least one book image is required.";
            }
        }

        return null;
    }

    private void readBookFields(Book book, HttpServletRequest request) throws IOException, ServletException {
        book.setTitle(request.getParameter("title"));
        book.setIsbn(request.getParameter("isbn"));
        book.setDescription(request.getParameter("description"));

        String priceStr = request.getParameter("price");
        if (priceStr != null && !priceStr.isEmpty()) {
            book.setPrice(new BigDecimal(priceStr));
        }

        String qtyStr = request.getParameter("quantity");
        if (qtyStr != null && !qtyStr.isEmpty()) {
            book.setQuantityInStock(Integer.parseInt(qtyStr));
        }

        // Handle publish date
        String dateStr = request.getParameter("publishDate");
        if (dateStr != null && !dateStr.isEmpty()) {
            book.setPublishDate(LocalDate.parse(dateStr));
        }
        book.setLastUpdated(LocalDate.now());

        // Handle category
        String catIdStr = request.getParameter("categoryId");
        if (catIdStr != null) {
            Category cat = bookService.findCategoryById(Integer.parseInt(catIdStr));
            book.setCategory(cat);
        }

        String[] authorIds = request.getParameterValues("authorIds");
        List<Author> authors = new ArrayList<>();
        if (authorIds != null) {
            for (String authId : authorIds) {
                Author author = bookService.findAuthorById(Integer.parseInt(authId));
                if (author != null)
                    authors.add(author);
            }
            book.setAuthors(authors);
        }

        // Handle publisher
        String publisherIdStr = request.getParameter("publisherId");
        if (publisherIdStr != null && !publisherIdStr.isEmpty()) {
            Publisher publisher = bookService.findPublisherById(Integer.parseInt(publisherIdStr));
            book.setPublisher(publisher);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}