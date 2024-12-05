package Library.backend.bookModel;

import java.util.ArrayList;
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
    private int quantity; // Số lượng sách

    // Constructor
    public Book(String bookID, String title, String author, int publishYear,
                String category, String isbn, String coverCode, int quantity) {
        this.bookID = bookID;
        this.title = title;
        this.author = author;
        this.publishYear = publishYear;
        this.category = category;
        this.isbn = isbn;
        this.coverCode = coverCode;
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
                           String newCoverCode, int newQuantity) {
        this.title = newTitle;
        this.author = newAuthor;
        this.publishYear = newPublishYear;
        this.category = newCategory;
        this.isbn = newIsbn;
        this.coverCode = newCoverCode;
        this.quantity = newQuantity;

        MysqlBookDao.getInstance().updateBook(this);
    }

    // Tìm kiếm tài liệu theo tiêu chí
    public static ArrayList<Book> searchBooks(String criteria, String value) {
        return (ArrayList<Book>) MysqlBookDao.getInstance().searchBooks(criteria, value);
    }

    public static ArrayList<Book> searchBooksValue(String value) {
        return (ArrayList<Book>) MysqlBookDao.getInstance().searchBooksValue(value);
    }
    public static ArrayList<Book> advancedSearchBooks(String value, int limit, int offset) {
        return (ArrayList<Library.backend.bookModel.Book>) MysqlBookDao.getInstance().advancedSearchBooks(value, limit, offset);
    }
    // Tạo mã QR cho tài liệu, trả về đường dẫn đến file mã qr;
    public String generateQrCodeForBook() {
        GoogleBookDao.getInstance().generateQrCodeForBook(this.isbn);
        return "src/main/resources/Library/" + this.isbn + "_qr.png";
    }

    // Tra cứu thông tin tài liệu từ API theo ISBN
    public static Book fetchBookInfoFromAPI(String isbn) {
        return GoogleBookDao.getInstance().fetchBookInfoFromAPI(isbn);
    }

    // Lấy phần mô tả của sách
    public String fetchBookDescriptionFromAPI() {
        return GoogleBookDao.getInstance().fetchBookDescriptionFromAPI(this);
    }

    public void updateQuantity(int n){
        MysqlBookDao.getInstance().updateQuantity(this.bookID,n);
    }

    public static Book getBookById(String strid){
        ArrayList<Book> b= (ArrayList<Book>) searchBooks("bookID",strid);
        return b.get(0);

    }

    public static Book getBookByIsbn(String strid){
        ArrayList<Book> b= (ArrayList<Book>) searchBooks("isbn",strid);
        return b.get(0);

    }

    @Override
    public String toString() {
        return "Book [bookID=" + bookID + ", title=" + title + ", author=" + author + ", publishYear=" + publishYear
                + ", category=" + category + ", isbn=" + isbn + ", coverCode=" + coverCode + ", quantity=" + quantity + "]"; // Cập nhật lại chuỗi trả về
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

    public int getQuantity() {
        return quantity; // Getter cho quantity
    }

    public void setCoverCode(String s){
        this.coverCode=s;
    }



    public static List<Book> fetchAllBooksFromAPI() {
        return GoogleBookDao.getInstance().fetchAllBooksFromAPI();
    }

    public static void main(String[] args) {
      /*  List<Book> L = Book.fetchAllBooksFromAPI();
        int i=0;
        for(Book b : L){
            b.addBook();
            if(i==30)break;
            i++;
        }
*//*
        Book b=Book.getBookById("-90ewuAkZUsC");
        b.generateQrCodeForBook();

*/
        Book b= new Book("123","123","123",123,"123","123","123",123);
        b.addBook();
    }
}
