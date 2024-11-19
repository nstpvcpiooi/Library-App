package Library.backend.bookModel;

import java.util.List;
import Library.backend.bookDao.GoogleBookDao;
import Library.backend.bookDao.MysqlBookDao;

public class Book {
    private String bookID; // Mã tài liệu
    private String title; // Tiêu đề tài liệu
    private String author; // Mã tác giả
    private int publishYear; // Năm xuất bản
    private String category; // Mã danh mục
    private String isbn; // ISBN

    private String coverCode; // Link ảnh bìa
    private int status; // Tình trạng tài liệu (có sẵn, đã mượn)
    private int quantity; // Số lượng sách

    // Constructor
    public Book(String bookID, String title, String author, int publishYear,
                String category, String isbn, String coverCode, int status, int quantity) {
        this.bookID = bookID;
        this.title = title;
        this.author = author;
        this.publishYear = publishYear;
        this.category = category;
        this.isbn = isbn;
        this.coverCode = coverCode;
        this.status = status;
        this.quantity = quantity;
    }

    // Thêm tài liệu
    public void addBook() {
        MysqlBookDao.getInstance().addBook(this);
    }

    // Xóa tài liệu
    public void deleteBook() {
        MysqlBookDao.getInstance().deleteBook(this.bookID);
    }

    // Cập nhật tài liệu
    public void updateBook(String newTitle, String newAuthor, int newPublishYear, String newCategory, String newIsbn,
                           String newCoverCode, int newStatus, int newQuantity) {
        this.title = newTitle;
        this.author = newAuthor;
        this.publishYear = newPublishYear;
        this.category = newCategory;
        this.isbn = newIsbn;
        this.coverCode = newCoverCode;
        this.status = newStatus;
        this.quantity = newQuantity;

        MysqlBookDao.getInstance().updateBook(this);
    }

    // Tìm kiếm tài liệu theo tiêu chí
    public static List<Book> searchBooks(String criteria, String value) {
        return MysqlBookDao.getInstance().searchBooks(criteria, value);
    }

    // Tạo mã QR cho tài liệu, trả về đường dẫn đến file mã qr;
    public String generateQrCodeForBook() {
        GoogleBookDao.getInstance().generateQrCodeForBook(this.isbn);
        return "src/main/resources/Library/" + this.bookID + "_qr.png";
    }

    // Cập nhật tình trạng tài liệu
    public void updateBookStatus(int newStatus) {
        this.status = newStatus;
        MysqlBookDao.getInstance().updateBookStatus(this.bookID, newStatus);
    }

    // Tra cứu thông tin tài liệu từ API theo ISBN
    public static Book fetchBookInfoFromAPI(String isbn) {
        return GoogleBookDao.getInstance().fetchBookInfoFromAPI(isbn);
    }

    // Lấy phần mô tả của sách
    public String fetchBookDescriptionFromAPI() {
        return GoogleBookDao.getInstance().fetchBookDescriptionFromAPI(this);
    }

    @Override
    public String toString() {
        return "Book [bookID=" + bookID + ", title=" + title + ", author=" + author + ", publishYear=" + publishYear
                + ", category=" + category + ", isbn=" + isbn + ", coverCode=" + coverCode + ", status=" + status
                + ", quantity=" + quantity + "]"; // Thêm quantity vào chuỗi trả về
    }

    // Getters and Setters
    public String getBookID() {
        return bookID;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getPublishYear() {
        return publishYear;
    }

    public String getCategory() {
        return category;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getCoverCode() {
        return coverCode;
    }

    public int getStatus() {
        return status;
    }

    public int getQuantity() {
        return quantity; // Getter cho quantity
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setPublishYear(int publishYear) {
        this.publishYear = publishYear;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setCoverCode(String coverCode) {
        this.coverCode = coverCode;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setQuantity(int quantity) { // Setter cho quantity
        this.quantity = quantity;
    }

    public static List<Book> fetchAllBooksFromAPI() {
        return GoogleBookDao.getInstance().fetchAllBooksFromAPI();
    }
}
