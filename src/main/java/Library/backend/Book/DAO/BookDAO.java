package Library.backend.Book.DAO;

import Library.backend.Book.Model.Book;
import com.google.zxing.WriterException;

import java.io.IOException;
import java.util.List;

/**
 * Unified DAO for both local persistence and external book data/providers.
 */
public interface BookDAO {
    void addBook(Book book);

    void deleteBook(String bookID);

    void updateBook(Book book);

    List<Book> searchBooksValue(String value);

    void updateQuantity(String bookID, int delta);

    Book findBookById(String bookID);

    Book findBookByIsbn(String isbn);

    Book fetchBookInfoFromAPI(String isbn);

    String fetchBookDescriptionFromAPI(Book book);

    String generateQrCodeForBook(String isbn);

}
