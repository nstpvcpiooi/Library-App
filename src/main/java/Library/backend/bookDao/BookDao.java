package Library.backend.bookDao;

import Library.backend.bookModel.Book;

import java.util.List;

public interface BookDao {
    void addBook(Book book);

    void deleteBook(String bookID);

    void updateBook(Book book);

    List<Book> searchBooks(String criteria,String value);

    List<Book> searchBooksValue(String value);

    List<Book> advancedSearchBooks(String value, int limit, int offset);

    Book fetchBookInfoFromAPI(String isbn);

    void updateQuantity(String bookID, int n);

    void generateQrCodeForBook(String bookID);

    String fetchBookDescriptionFromAPI(Book book);

}
