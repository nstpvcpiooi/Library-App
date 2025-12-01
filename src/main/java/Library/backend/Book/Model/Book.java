package Library.backend.Book.Model;

public class Book {
    private String bookID;
    private String title;
    private String author;
    private int publishYear;
    private String category;
    private String isbn;
    private String coverCode;
    private int quantity;

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

    @Override
    public String toString() {
        return "Book [bookID=" + bookID + ", title=" + title + ", author=" + author + ", publishYear=" + publishYear
                + ", category=" + category + ", isbn=" + isbn + ", coverCode=" + coverCode + ", quantity=" + quantity + "]";
    }

    public String getBookID() {
        return bookID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(int publishYear) {
        this.publishYear = publishYear;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getCoverCode() {
        return coverCode;
    }

    public void setCoverCode(String coverCode) {
        this.coverCode = coverCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
