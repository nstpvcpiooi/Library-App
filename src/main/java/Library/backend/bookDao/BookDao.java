package Library.backend.bookDao;

import java.util.List;

import Library.backend.bookModel.Book;

public interface BookDao {
    void addBook(Book book);

    void deleteBook(String bookID);

    void updateBook(Book book);

    List<Book> searchBooks(String criteria, String value);

    Book fetchBookInfoFromAPI(String isbn);

    void updateBookStatus(String bookID, int newStatus);

    void generateQrCodeForBook(String bookID);

    String fetchBookDescriptionFromAPI(Book book);
}
