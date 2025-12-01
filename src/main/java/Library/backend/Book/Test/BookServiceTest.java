package Library.backend.Book.Test;

import Library.backend.Book.DAO.BookDAO;
import Library.backend.Book.Model.Book;
import Library.backend.Book.Service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

// Sử dụng phân hoạch tương đương hộp đen cho các unit trong BookService. Mỗi TC-xx ghi rõ đầu vào/kỳ vọng.
public class BookServiceTest {

    private static class RecordingDaoStub implements BookDAO {
        AtomicReference<String> lastId = new AtomicReference<>();
        AtomicReference<Book> lastBook = new AtomicReference<>();
        AtomicReference<Integer> lastDelta = new AtomicReference<>();
        AtomicReference<String> lastIsbn = new AtomicReference<>();
        String qrResult = "qr-path";
        List<Book> searchResult = List.of();
        Book bookResult;
        Book fetchBookResult;
        String descriptionResult = "desc";
        String previewResult = "preview";
        String duplicateIsbnForAdd;
        String duplicateIsbnForUpdate;
        String duplicateIdForUpdate;

        @Override
        public void addBook(Book book) {

            lastBook.set(book);
        }

        @Override
        public void deleteBook(String bookID) {
            lastId.set(bookID);
        }

        @Override
        public void updateBook(Book book) {

            lastBook.set(book);
        }

        @Override
        public List<Book> searchBooksValue(String value) {
            lastId.set(value);
            return searchResult;
        }

        @Override
        public void updateQuantity(String bookID, int delta) {
            lastId.set(bookID);
            lastDelta.set(delta);
        }

        @Override
        public Book findBookById(String bookID) {
            lastId.set(bookID);
            return bookResult;
        }

        @Override
        public Book findBookByIsbn(String isbn) {
            lastIsbn.set(isbn);
            return bookResult;
        }

        @Override
        public Book fetchBookInfoFromAPI(String isbn) {
            lastIsbn.set(isbn);
            return fetchBookResult;
        }

        @Override
        public String fetchBookDescriptionFromAPI(Book book) {
            lastBook.set(book);
            return descriptionResult;
        }

        @Override
        public String generateQrCodeForBook(String isbn) {
            lastIsbn.set(isbn);
            return qrResult;
        }




    }

    @Test
    // TC-01: book null => Expected output: generateQrCodeForBook trả null, không gọi DAO.
    @DisplayName("generateQrCodeForBook: null book -> returns null without calling DAO")
    void generateQrCodeForBook_nullBook_returnsNull() {
        RecordingDaoStub dao = new RecordingDaoStub();
        BookService service = BookService.create(dao);

        assertNull(service.generateQrCodeForBook(null));
        assertNull(dao.lastIsbn.get());
    }

    @Test
    // TC-02: ISBN blank => Expected output: generateQrCodeForBook trả null, không gọi DAO.
    @DisplayName("generateQrCodeForBook: blank ISBN -> returns null without calling DAO")
    void generateQrCodeForBook_blankIsbn_returnsNull() {
        RecordingDaoStub dao = new RecordingDaoStub();
        BookService service = BookService.create(dao);
        Book book = new Book("B1", "T", "A", 2020, "C", "   ", "", 1);

        assertNull(service.generateQrCodeForBook(book));
        assertNull(dao.lastIsbn.get());
    }

    @Test
    // TC-03: ISBN hợp lệ => Expected output: generateQrCodeForBook gọi DAO, trả đúng qrResult từ DAO.
    @DisplayName("generateQrCodeForBook: valid ISBN -> delegates to DAO and returns qrResult")
    void generateQrCodeForBook_valid_callsDao() {
        RecordingDaoStub dao = new RecordingDaoStub();
        dao.qrResult = "qr-path";
        BookService service = BookService.create(dao);
        Book book = new Book("B1", "T", "A", 2020, "C", "9780132350884", "", 1);

        String result = service.generateQrCodeForBook(book);

        assertEquals("qr-path", result);
        assertEquals("9780132350884", dao.lastIsbn.get());
    }

    @Test
    // TC-04: book null => Expected output: addBook ném IllegalArgumentException.
    @DisplayName("addBook: null book -> throws IllegalArgumentException")
    void addBook_null_throwsException() {
        BookService service = BookService.create(new RecordingDaoStub());
        assertThrows(IllegalArgumentException.class, () -> service.addBook(null));
    }

    @Test
    // TC-05: quantity âm => Expected output: addBook ném IllegalArgumentException.
    @DisplayName("addBook: negative quantity -> throws IllegalArgumentException")
    void addBook_negativeQuantity_throwsException() {
        BookService service = BookService.create(new RecordingDaoStub());
        Book invalid = new Book("B1", "T", "A", 2020, "C", "978", "", -1);
        assertThrows(IllegalArgumentException.class, () -> service.addBook(invalid));
    }

    @Test
    // TC-06: publishYear âm => Expected output: addBook ném IllegalArgumentException.
    @DisplayName("addBook: negative publishYear -> throws IllegalArgumentException")
    void addBook_negativePublishYear_throwsException() {
        BookService service = BookService.create(new RecordingDaoStub());
        Book invalid = new Book("B1", "T", "A", -1990, "C", "978", "", 1);
        assertThrows(IllegalArgumentException.class, () -> service.addBook(invalid));
    }

    @Test
    // TC-07: ISBN không hợp lệ => Expected output: addBook ném IllegalArgumentException.
    @DisplayName("addBook: invalid ISBN -> throws IllegalArgumentException")
    void addBook_invalidIsbn_throwsException() {
        BookService service = BookService.create(new RecordingDaoStub());
        Book invalid = new Book("B1", "T", "A", 2020, "C", "12345", "", 1);
        assertThrows(IllegalArgumentException.class, () -> service.addBook(invalid));
    }

    @Test
    // TC-08: dữ liệu hợp lệ => Expected output: addBook gọi DAO với đúng Book truyền vào.
    @DisplayName("addBook: valid data -> calls DAO with given Book")
    void addBook_valid_callsDao() {
        RecordingDaoStub dao = new RecordingDaoStub();
        BookService service = BookService.create(dao);
        Book valid = new Book("B1", "T", "A", 2020, "C", "9780132350884", "", 2);

        service.addBook(valid);

        assertSame(valid, dao.lastBook.get());
    }

    @Test
    // TC-09: ISBN trùng với sách đã có => Expected output: addBook ném IllegalArgumentException.
    @DisplayName("addBook: duplicate ISBN -> throws IllegalArgumentException")
    void addBook_duplicateIsbn_throwsException() {
        RecordingDaoStub dao = new RecordingDaoStub();
        dao.duplicateIsbnForAdd = "9780132350884";
        BookService service = BookService.create(dao);
        Book duplicate = new Book("B2", "T", "A", 2021, "C", "9780132350884", "", 1);

        assertThrows(IllegalArgumentException.class, () -> service.addBook(duplicate));
    }


    @Test
    // TC-10: ID blank => Expected output: deleteBook ném IllegalArgumentException.
    @DisplayName("deleteBook: blank ID -> throws IllegalArgumentException")
    void deleteBook_blank_throwsException() {
        BookService service = BookService.create(new RecordingDaoStub());
        assertThrows(IllegalArgumentException.class, () -> service.deleteBook("   "));
    }

    @Test
    // TC-11: ID không tồn tại => Expected output: deleteBook ném IllegalArgumentException.
    @DisplayName("deleteBook: non-existing ID -> throws IllegalArgumentException")
    void deleteBook_missing_throwsException() {
        BookService service = BookService.create(new RecordingDaoStub());
        assertThrows(IllegalArgumentException.class, () -> service.deleteBook("missing"));
    }

    @Test
    // TC-12: ID hợp lệ => Expected output: deleteBook gọi DAO với đúng ID.
    @DisplayName("deleteBook: valid ID -> calls DAO with that ID")
    void deleteBook_valid_callsDao() {
        RecordingDaoStub dao = new RecordingDaoStub();
        dao.bookResult = new Book("B1", "T", "A", 2020, "C", "978", "", 1);
        BookService service = BookService.create(dao);

        service.deleteBook("B1");

        assertEquals("B1", dao.lastId.get());
    }


    @Test
    // TC-13: book null => Expected output: updateBook ném IllegalArgumentException.
    @DisplayName("updateBook: null book -> throws IllegalArgumentException")
    void updateBook_null_throwsException() {
        BookService service = BookService.create(new RecordingDaoStub());
        assertThrows(IllegalArgumentException.class, () -> service.updateBook(null));
    }

    @Test
    // TC-14: ID blank => Expected output: updateBook ném IllegalArgumentException.
    @DisplayName("updateBook: blank ID -> throws IllegalArgumentException")
    void updateBook_blankId_throwsException() {
        RecordingDaoStub dao = new RecordingDaoStub();
        BookService service = BookService.create(dao);
        Book invalid = new Book("   ", "T", "A", 2020, "C", "978", "", 1);
        assertThrows(IllegalArgumentException.class, () -> service.updateBook(invalid));
    }

    @Test
    // TC-15: ID không tồn tại => Expected output: updateBook ném IllegalArgumentException.
    @DisplayName("updateBook: non-existing ID -> throws IllegalArgumentException")
    void updateBook_missingId_throwsException() {
        RecordingDaoStub dao = new RecordingDaoStub(); // not found
        BookService service = BookService.create(dao);
        Book invalid = new Book("missing", "T", "A", 2020, "C", "978", "", 1);
        assertThrows(IllegalArgumentException.class, () -> service.updateBook(invalid));
    }

    @Test
    // TC-16: quantity âm => Expected output: updateBook ném IllegalArgumentException.
    @DisplayName("updateBook: negative quantity -> throws IllegalArgumentException")
    void updateBook_negativeQuantity_throwsException() {
        RecordingDaoStub dao = new RecordingDaoStub();
        dao.bookResult = new Book("B1", "T", "A", 2020, "C", "978", "", 1); // existing
        BookService service = BookService.create(dao);
        Book invalid = new Book("B1", "T", "A", 2020, "C", "978", "", -1);
        assertThrows(IllegalArgumentException.class, () -> service.updateBook(invalid));
    }

    @Test
    // TC-17: publishYear âm => Expected output: updateBook ném IllegalArgumentException.
    @DisplayName("updateBook: negative publishYear -> throws IllegalArgumentException")
    void updateBook_negativePublishYear_throwsException() {
        RecordingDaoStub dao = new RecordingDaoStub();
        dao.bookResult = new Book("B1", "T", "A", 2020, "C", "978", "", 1); // existing
        BookService service = BookService.create(dao);
        Book invalid = new Book("B1", "T", "A", -1990, "C", "978", "", 1);
        assertThrows(IllegalArgumentException.class, () -> service.updateBook(invalid));
    }

    @Test
    // TC-18: dữ liệu hợp lệ => Expected output: updateBook gọi DAO với đúng Book truyền vào.
    @DisplayName("updateBook: valid data -> calls DAO with given Book")
    void updateBook_valid_callsDao() {
        RecordingDaoStub dao = new RecordingDaoStub();
        dao.bookResult = new Book("B1", "T", "A", 2020, "C", "978", "", 1); // existing
        BookService service = BookService.create(dao);
        Book valid = new Book("B1", "T2", "A2", 2021, "C2", "978", "", 3);

        service.updateBook(valid);

        assertSame(valid, dao.lastBook.get());
    }

    @Test
    // TC-19: ISBN mới trùng với sách khác => Expected output: updateBook ném IllegalArgumentException.
    @DisplayName("updateBook: new ISBN duplicates another book -> throws IllegalArgumentException")
    void updateBook_duplicateIsbn_throwsException() {
        RecordingDaoStub dao = new RecordingDaoStub();
        dao.bookResult = new Book("B1", "T", "A", 2020, "C", "111", "", 1); // current
        dao.duplicateIsbnForUpdate = "222";
        dao.duplicateIdForUpdate = "B2";
        BookService service = BookService.create(dao);
        Book update = new Book("B1", "T", "A", 2020, "C", "222", "", 1);

        assertThrows(IllegalArgumentException.class, () -> service.updateBook(update));
    }

    @Test
    // TC-20: ISBN không hợp lệ => Expected output: updateBook ném IllegalArgumentException.
    @DisplayName("updateBook: invalid ISBN -> throws IllegalArgumentException")
    void updateBook_invalidIsbn_throwsException() {
        RecordingDaoStub dao = new RecordingDaoStub();
        dao.bookResult = new Book("B1", "T", "A", 2020, "C", "978", "", 1); // existing
        BookService service = BookService.create(dao);
        Book invalidIsbn = new Book("B1", "T", "A", 2020, "C", "12345", "", 1);

        assertThrows(IllegalArgumentException.class, () -> service.updateBook(invalidIsbn));
    }


    @Test
    // TC-21: ID blank => Expected output: updateQuantity ném IllegalArgumentException.
    @DisplayName("updateQuantity: blank ID -> throws IllegalArgumentException")
    void updateQuantity_blankId_throwsException() {
        BookService service = BookService.create(new RecordingDaoStub());
        assertThrows(IllegalArgumentException.class, () -> service.updateQuantity("   ", 1));
    }

    @Test
    // TC-22: ID không tồn tại => Expected output: updateQuantity ném IllegalArgumentException.
    @DisplayName("updateQuantity: non-existing ID -> throws IllegalArgumentException")
    void updateQuantity_missingId_throwsException() {
        BookService service = BookService.create(new RecordingDaoStub());
        assertThrows(IllegalArgumentException.class, () -> service.updateQuantity("missing", 1));
    }

    @Test
    // TC-23: Tồn kho mới < 0 => Expected output: updateQuantity ném IllegalArgumentException.
    @DisplayName("updateQuantity: resulting stock < 0 -> throws IllegalArgumentException")
    void updateQuantity_negativeResult_throwsException() {
        RecordingDaoStub dao = new RecordingDaoStub();
        dao.bookResult = new Book("B1", "T", "A", 2020, "C", "978", "", 1);
        BookService service = BookService.create(dao);
        assertThrows(IllegalArgumentException.class, () -> service.updateQuantity("B1", -2));
    }

    @Test
    // TC-24: dữ liệu hợp lệ => Expected output: updateQuantity gọi DAO với đúng ID và delta.
    @DisplayName("updateQuantity: valid change -> calls DAO with correct parameters")
    void updateQuantity_valid_callsDao() {
        RecordingDaoStub dao = new RecordingDaoStub();
        dao.bookResult = new Book("B1", "T", "A", 2020, "C", "978", "", 2);
        BookService service = BookService.create(dao);

        service.updateQuantity("B1", -1);

        assertEquals("B1", dao.lastId.get());
        assertEquals(-1, dao.lastDelta.get());
    }


    @Test
    // TC-25: DAO trả null => Expected output: searchBooksValue trả list rỗng (không null).
    @DisplayName("searchBooksValue: DAO returns null -> returns empty list")
    void searchBooksValue_null_returnsEmptyList() {
        RecordingDaoStub dao = new RecordingDaoStub();
        dao.searchResult = null;
        BookService service = BookService.create(dao);

        List<Book> result = service.searchBooksValue("java");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals("java", dao.lastId.get());
    }

    @Test
    // TC-26: keyword hợp lệ => Expected output: searchBooksValue trả lại đúng list từ DAO.
    @DisplayName("searchBooksValue: valid keyword -> returns list from DAO")
    void searchBooksValue_valid_returnsDaoList() {
        RecordingDaoStub dao = new RecordingDaoStub();
        Book b = new Book("B1", "T", "A", 2020, "C", "978", "", 1);
        dao.searchResult = List.of(b);
        BookService service = BookService.create(dao);

        List<Book> result = service.searchBooksValue("java");

        assertEquals(1, result.size());
        assertSame(b, result.get(0));
        assertEquals("java", dao.lastId.get());
    }

    @Test
    // TC-27: searchBooksValue với keyword blank => Expected output: service vẫn gọi DAO, khi DAO trả list rỗng thì kết quả rỗng.
    @DisplayName("searchBooksValue: blank keyword -> still delegates and returns empty list")
    void searchBooksValue_blankKeyword_returnsEmptyList() {
        RecordingDaoStub dao = new RecordingDaoStub();
        dao.searchResult = List.of();
        BookService service = BookService.create(dao);

        List<Book> result = service.searchBooksValue("   ");

        assertTrue(result.isEmpty());
        assertEquals("   ", dao.lastId.get());
    }
    @Test
    // TC-28: fetchBookInfoFromApi hợp lệ => Expected output: fetchBookInfoFromApi trả về đúng Book từ DAO.
    @DisplayName("fetchBookInfoFromApi: valid call -> returns same Book as DAO")
    void fetchBookInfoFromApi_delegates() {
        RecordingDaoStub dao = new RecordingDaoStub();
        Book expected = new Book("B1", "T", "A", 2020, "C", "978", "", 1);
        dao.fetchBookResult = expected;
        BookService service = BookService.create(dao);

        Book result = service.fetchBookInfoFromApi("978");

        assertSame(expected, result);
        assertEquals("978", dao.lastIsbn.get());
    }

    @Test
    // TC-29: fetchBookInfoFromApi DAO trả null => Expected output: fetchBookInfoFromApi trả null.
    @DisplayName("fetchBookInfoFromApi: DAO returns null -> returns null")
    void fetchBookInfoFromApi_nullFromDao_returnsNull() {
        RecordingDaoStub dao = new RecordingDaoStub(); // fetchBookResult mac dinh null
        BookService service = BookService.create(dao);

        Book result = service.fetchBookInfoFromApi("invalid");

        assertNull(result);
        assertEquals("invalid", dao.lastIsbn.get());
    }
    @Test
    // TC-30: fetchBookInfoFromApi với isbn = null => Expected output: service vẫn gọi DAO (isbn null) và trả về kết quả DAO.
    @DisplayName("fetchBookInfoFromApi: null isbn -> delegates and returns DAO result")
    void fetchBookInfoFromApi_nullIsbn_delegates() {
        RecordingDaoStub dao = new RecordingDaoStub();
        Book expected = new Book("B2", "Null ISBN", "Author", 2020, "Tech", "000", "", 1);
        dao.fetchBookResult = expected;
        BookService service = BookService.create(dao);

        Book result = service.fetchBookInfoFromApi(null);

        assertSame(expected, result);
        assertNull(dao.lastIsbn.get());
    }
    @Test
    // TC-31: getBookById hợp lệ => Expected output: getBookById trả về đúng Book từ DAO.
    @DisplayName("getBookById: valid ID -> returns same Book as DAO")
    void getBookById_delegates() {
        RecordingDaoStub dao = new RecordingDaoStub();
        Book expected = new Book("B1", "T", "A", 2020, "C", "978", "", 1);
        dao.bookResult = expected;
        BookService service = BookService.create(dao);

        Book result = service.getBookById("B1");

        assertSame(expected, result);
        assertEquals("B1", dao.lastId.get());
    }

    @Test
    // TC-32: getBookById không tồn tại => Expected output: getBookById trả null.
    @DisplayName("getBookById: non-existing ID -> returns null")
    void getBookById_notFound_returnsNull() {
        RecordingDaoStub dao = new RecordingDaoStub(); // bookResult mac dinh null
        BookService service = BookService.create(dao);

        Book result = service.getBookById("missing");

        assertNull(result);
        assertEquals("missing", dao.lastId.get());
    }
    @Test
    // TC-33: getBookById với ID chuỗi blank => Expected output: service vẫn chuyển tiếp xuống DAO, DAO trả null.
    @DisplayName("getBookById: blank id -> passes through to DAO")
    void getBookById_blankId_delegates() {
        RecordingDaoStub dao = new RecordingDaoStub();
        BookService service = BookService.create(dao);

        assertNull(service.getBookById("   "));
        assertEquals("   ", dao.lastId.get());
    }
    @Test
    // TC-34: getBookByIsbn hợp lệ => Expected output: getBookByIsbn trả về đúng Book từ DAO.
    @DisplayName("getBookByIsbn: valid ISBN -> returns same Book as DAO")
    void getBookByIsbn_delegates() {
        RecordingDaoStub dao = new RecordingDaoStub();
        Book expected = new Book("B1", "T", "A", 2020, "C", "978", "", 1);
        dao.bookResult = expected;
        BookService service = BookService.create(dao);

        Book result = service.getBookByIsbn("978");

        assertSame(expected, result);
        assertEquals("978", dao.lastIsbn.get());
    }

    @Test
    // TC-35: getBookByIsbn không tồn tại => Expected output: getBookByIsbn trả null.
    @DisplayName("getBookByIsbn: non-existing ISBN -> returns null")
    void getBookByIsbn_notFound_returnsNull() {
        RecordingDaoStub dao = new RecordingDaoStub(); // bookResult mac dinh null
        BookService service = BookService.create(dao);

        Book result = service.getBookByIsbn("missing");

        assertNull(result);
        assertEquals("missing", dao.lastIsbn.get());
    }
    @Test
    // TC-36: getBookByIsbn với ISBN blank => Expected output: service gọi DAO với ISBN blank và trả null khi DAO không có dữ liệu.
    @DisplayName("getBookByIsbn: blank isbn -> delegates blank to DAO")
    void getBookByIsbn_blank_delegates() {
        RecordingDaoStub dao = new RecordingDaoStub();
        BookService service = BookService.create(dao);

        assertNull(service.getBookByIsbn("  "));
        assertEquals("  ", dao.lastIsbn.get());
    }

    @Test
    // TC-37: fetchBookDescription hợp lệ => Expected output: fetchBookDescription trả về đúng description từ DAO.
    @DisplayName("fetchBookDescription: valid book -> returns same description as DAO")
    void fetchBookDescription_delegates() {
        RecordingDaoStub dao = new RecordingDaoStub();
        dao.descriptionResult = "desc";
        BookService service = BookService.create(dao);
        Book book = new Book("B1", "T", "A", 2020, "C", "978", "", 1);

        String result = service.fetchBookDescription(book);

        assertEquals("desc", result);
        assertSame(book, dao.lastBook.get());
    }

    @Test
    // TC-38: fetchBookDescription DAO trả null => Expected output: fetchBookDescription trả null.
    @DisplayName("fetchBookDescription: DAO returns null -> returns null")
    void fetchBookDescription_nullFromDao_returnsNull() {
        RecordingDaoStub dao = new RecordingDaoStub();
        dao.descriptionResult = null;
        BookService service = BookService.create(dao);
        Book book = new Book("B1", "T", "A", 2020, "C", "978", "", 1);

        String result = service.fetchBookDescription(book);

        assertNull(result);
        assertSame(book, dao.lastBook.get());
    }

    @Test
    // TC-39: truyền book = null vào fetchBookDescription => Expected output: service chuyển tiếp giá trị null xuống DAO và nhận về đúng chuỗi mô phỏng.
    @DisplayName("fetchBookDescription: book null -> delegates null to DAO")
    void fetchBookDescription_nullBook_delegatesNull() {
        RecordingDaoStub dao = new RecordingDaoStub();
        dao.descriptionResult = "fallback";
        BookService service = BookService.create(dao);

        String result = service.fetchBookDescription(null);

        assertEquals("fallback", result);
        assertNull(dao.lastBook.get());
    }







}
