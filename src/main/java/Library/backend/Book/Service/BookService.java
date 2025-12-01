package Library.backend.Book.Service;

import Library.backend.Book.DAO.BookDAO;
import Library.backend.Book.DAO.BookDAOImpl;
import Library.backend.Book.Model.Book;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Facade that orchestrates all book-related operations.
 * Controllers/models now depend on this testable service instead of concrete DAO singletons.
 */
public class BookService {

    private static volatile BookService instance;

    private final BookDAO bookDAO;

    public BookService(BookDAO bookDAO) {
        this.bookDAO = Objects.requireNonNull(bookDAO);
    }

    public static BookService getInstance() {
        if (instance == null) {
            synchronized (BookService.class) {
                if (instance == null) {
                    instance = new BookService(BookDAOImpl.getInstance());
                }
            }
        }
        return instance;
    }

    /**
     * Factory for tests or manual wiring without using the global singleton.
     */
    public static BookService create(BookDAO bookDAO) {
        return new BookService(bookDAO);
    }


    public void addBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book must not be null");
        }
        if (book.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity must be non-negative");
        }
        if (book.getPublishYear() < 0) {
            throw new IllegalArgumentException("Publish year must be non-negative");
        }
        bookDAO.addBook(book);
    }

    public void deleteBook(String bookId) {
        if (bookId == null || bookId.isBlank()) {
            throw new IllegalArgumentException("Book ID must not be blank");
        }
        if (bookDAO.findBookById(bookId) == null) {
            throw new IllegalArgumentException("Book ID not found: " + bookId);
        }
        bookDAO.deleteBook(bookId);
    }

    public void updateBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book must not be null");
        }
        if (book.getBookID() == null || book.getBookID().isBlank()) {
            throw new IllegalArgumentException("Book ID must not be blank");
        }
        if (bookDAO.findBookById(book.getBookID()) == null) {
            throw new IllegalArgumentException("Book ID not found: " + book.getBookID());
        }
        if (book.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity must be non-negative");
        }
        if (book.getPublishYear() < 0) {
            throw new IllegalArgumentException("Publish year must be non-negative");
        }
        bookDAO.updateBook(book);
    }

    public List<Book> searchBooksValue(String value) {
        List<Book> books = bookDAO.searchBooksValue(value);
        return books == null ? Collections.emptyList() : books;
    }

    public Book getBookById(String bookId) {
        return bookDAO.findBookById(bookId);
    }

    public Book getBookByIsbn(String isbn) {
        return bookDAO.findBookByIsbn(isbn);
    }

    public void updateQuantity(String bookId, int delta) {
        if (bookId == null || bookId.isBlank()) {
            throw new IllegalArgumentException("Book ID must not be blank");
        }
        Book existing = bookDAO.findBookById(bookId);
        if (existing == null) {
            throw new IllegalArgumentException("Book ID not found: " + bookId);
        }
        int newQty = existing.getQuantity() + delta;
        if (newQty < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        bookDAO.updateQuantity(bookId, delta);
    }

    public Book fetchBookInfoFromApi(String isbn) {
        return bookDAO.fetchBookInfoFromAPI(isbn);
    }

    public String fetchBookDescription(Book book) {
        return bookDAO.fetchBookDescriptionFromAPI(book);
    }

    public String generateQrCodeForBook(Book book) {
        if (book == null || book.getIsbn() == null || book.getIsbn().isBlank()) {
            return null;
        }
        return bookDAO.generateQrCodeForBook(book.getIsbn());
    }
}
