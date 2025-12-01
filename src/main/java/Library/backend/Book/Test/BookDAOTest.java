package Library.backend.Book.Test;

import Library.backend.Book.DAO.BookDAOImpl;
import Library.backend.Book.Model.Book;
import Library.backend.database.JDBCUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

// Kiểm thử đơn vị cho BookDAOImpl (phân hoạch tương đương).
public class BookDAOTest {

    private static final String H2_URL = "jdbc:h2:mem:library;MODE=MySQL;DB_CLOSE_DELAY=-1";
    private static final String CREATE_BOOKS_TABLE = """
            CREATE TABLE IF NOT EXISTS Books (
                bookID VARCHAR(255) PRIMARY KEY,
                title VARCHAR(255),
                author VARCHAR(255),
                publishYear INT,
                category VARCHAR(255),
                isbn VARCHAR(255),
                coverCode VARCHAR(255),
                quantity INT
            )
            """;

    private BookDAOImpl dao;

    @BeforeAll
    static void initDatabase() throws SQLException {
        try (Connection con = DriverManager.getConnection(H2_URL);
             Statement st = con.createStatement()) {
            st.execute(CREATE_BOOKS_TABLE);
        }
        JDBCUtil.setConnectionSupplier(() -> {
            try {
                return DriverManager.getConnection(H2_URL);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @AfterAll
    static void resetHooks() {
        JDBCUtil.resetConnectionSupplier();
        BookDAOImpl.resetTestHooks();
    }

    @BeforeEach
    void resetState() throws SQLException {
        dao = BookDAOImpl.getInstance();
        try (Connection con = DriverManager.getConnection(H2_URL);
             Statement st = con.createStatement()) {
            st.execute("TRUNCATE TABLE Books");
        }
        BookDAOImpl.resetTestHooks();
    }

    @ParameterizedTest
    // TC-01: ISBN null/rỗng/độ dài khác 10/13 => Expected output: fetchBookInfoFromAPI trả null (bỏ qua input không hợp lệ).
    @NullSource
    @ValueSource(strings = {"", "123456789", "123456789012"})
    @DisplayName("fetchBookInfoFromAPI: invalid ISBN -> returns null")
    void fetchBookInfoFromAPI_invalidIsbn_returnsNull(String isbn) {
        assertNull(dao.fetchBookInfoFromAPI(isbn));
    }

    @Test
    // TC-02: ISBN hợp lệ nhưng HTTP status từ API khác 200 => Expected output: fetchBookInfoFromAPI trả null (xem là lỗi HTTP).
    @DisplayName("fetchBookInfoFromAPI: HTTP error -> returns null")
    void fetchBookInfoFromAPI_httpError_returnsNull() throws Exception {
        BookDAOImpl.setHttpConnectionFactory(singleResponseFactory(
                httpResponse(HttpURLConnection.HTTP_BAD_REQUEST, "{}")
        ));

        assertNull(dao.fetchBookInfoFromAPI("9780132350884"));
    }

    @Test
    // TC-03: ISBN hợp lệ, HTTP 200 nhưng totalItems = 0 (không có sách nào) => Expected output: fetchBookInfoFromAPI trả null.
    @DisplayName("fetchBookInfoFromAPI: totalItems = 0 -> returns null")
    void fetchBookInfoFromAPI_noItems_returnsNull() throws Exception {
        BookDAOImpl.setHttpConnectionFactory(singleResponseFactory(
                httpOk("""
                        {"totalItems":0}
                        """)
        ));

        assertNull(dao.fetchBookInfoFromAPI("9780132350884"));
    }

    @Test
    // TC-04: ISBN hợp lệ, HTTP 200 và có ít nhất 1 item trong kết quả => Expected output: fetchBookInfoFromAPI trả về Book với thông tin map đúng từ JSON.
    @DisplayName("fetchBookInfoFromAPI: valid ISBN -> returns mapped Book")
    void fetchBookInfoFromAPI_validIsbn_returnsBook() throws Exception {
        BookDAOImpl.setHttpConnectionFactory(singleResponseFactory(
                httpOk(fakeBookPayload("book-01", "Clean Code", "Robert Martin",
                        2008, "Software", "9780132350884", "https://img/cc.jpg", null))
        ));

        Book book = dao.fetchBookInfoFromAPI("9780132350884");

        assertNotNull(book);
        assertEquals("book-01", book.getBookID());
        assertEquals("Clean Code", book.getTitle());
        assertEquals("Robert Martin", book.getAuthor());
        assertEquals("9780132350884", book.getIsbn());
    }

    @Test
    // TC-05: book = null => Expected output: fetchBookDescriptionFromAPI không gọi API, trả về chuỗi "No book provided.".
    @DisplayName("fetchBookDescriptionFromAPI: null book -> returns message")
    void fetchBookDescriptionFromAPI_nullBook_returnsMessage() {
        assertEquals("No book provided.", dao.fetchBookDescriptionFromAPI(null));
    }

    @Test
    // TC-06: book hợp lệ nhưng API trả HTTP lỗi (status != 200) => Expected output: fetchBookDescriptionFromAPI trả về "API connection error.".
    @DisplayName("fetchBookDescriptionFromAPI: HTTP error -> returns error message")
    void fetchBookDescriptionFromAPI_httpError_returnsMessage() throws Exception {
        Book book = new Book("B1", "T", "A", 2020, "C", "9780132350884", "", 1);
        BookDAOImpl.setHttpConnectionFactory(singleResponseFactory(
                httpResponse(HttpURLConnection.HTTP_BAD_REQUEST, "{}")
        ));

        assertEquals("API connection error.", dao.fetchBookDescriptionFromAPI(book));
    }

    @Test
    // TC-07: book hợp lệ, HTTP 200 và JSON có trường description => Expected output: fetchBookDescriptionFromAPI trả về đúng nội dung description.
    @DisplayName("fetchBookDescriptionFromAPI: has description -> returns description")
    void fetchBookDescriptionFromAPI_hasDescription_returnsDescription() throws Exception {
        Book book = new Book("B1", "T", "A", 2020, "C", "9780132350884", "", 1);
        BookDAOImpl.setHttpConnectionFactory(singleResponseFactory(
                httpOk("""
                        {
                          "items":[{"volumeInfo":{"description":"Sample description"}}]
                        }
                        """)
        ));

        assertEquals("Sample description", dao.fetchBookDescriptionFromAPI(book));
    }

    @Test
    // TC-08: book hợp lệ, HTTP 200 nhưng mảng items rỗng => Expected output: fetchBookDescriptionFromAPI trả về "No book found.".
    @DisplayName("fetchBookDescriptionFromAPI: no items -> returns 'No book found.'")
    void fetchBookDescriptionFromAPI_noItem_returnsNoBookFound() throws Exception {
        Book book = new Book("B1", "T", "A", 2020, "C", "9780132350884", "", 1);
        BookDAOImpl.setHttpConnectionFactory(singleResponseFactory(
                httpOk("""
                        {"items":[]}
                        """)
        ));

        assertEquals("No book found.", dao.fetchBookDescriptionFromAPI(book));
    }

    @Test
    // TC-09: book hợp lệ, JSON không có trường description => Expected output: fetchBookDescriptionFromAPI trả về "No description available.".
    @DisplayName("fetchBookDescriptionFromAPI: missing description -> returns fallback")
    void fetchBookDescriptionFromAPI_missingDescription_returnsFallback() throws Exception {
        Book book = new Book("B1", "T", "A", 2020, "C", "9780132350884", "", 1);
        BookDAOImpl.setHttpConnectionFactory(singleResponseFactory(
                httpOk("""
                        {
                          "items":[{"volumeInfo":{}}]
                        }
                        """)
        ));

        assertEquals("No description available.", dao.fetchBookDescriptionFromAPI(book));
    }

    @Test
    // TC-10: book hợp lệ nhưng khi đọc dữ liệu xảy ra IOException/JSONException => Expected output: fetchBookDescriptionFromAPI trả về "Error occurred.".
    @DisplayName("fetchBookDescriptionFromAPI: IO/JSON error -> returns 'Error occurred.'")
    void fetchBookDescriptionFromAPI_exception_returnsError() throws Exception {
        Book book = new Book("B1", "T", "A", 2020, "C", "9780132350884", "", 1);
        BookDAOImpl.setHttpConnectionFactory(ThrowingHttpURLConnection::new);

        assertEquals("Error occurred.", dao.fetchBookDescriptionFromAPI(book));
    }
    @Test
    // TC-11: addBook với Book hợp lệ => Expected output: bản ghi được lưu và có thể đọc lại bằng findBookById với title đúng.
    @DisplayName("addBook: valid data -> persists record")
    void addBook_valid_savesRecord() {
        Book book = new Book("B1", "Java 101", "Alice", 2022, "Tech", "1111111111", "", 3);

        dao.addBook(book);

        Book stored = dao.findBookById("B1");
        assertNotNull(stored);
        assertEquals("Java 101", stored.getTitle());
    }

    @Test
    // TC-12: addBook được gọi 2 lần với cùng khóa chính bookID => Expected output: Bảng chỉ có 1 bản ghi cho ID đó.
    @DisplayName("addBook: duplicate ID -> does not insert second record")
    void addBook_duplicateId_noSecondInsert() {
        Book book = new Book("B1", "Java 101", "Alice", 2022, "Tech", "1111111111", "", 3);
        dao.addBook(book);

        dao.addBook(book); // duplicate PK

        assertEquals(1, dao.searchBooksValue("Java").size());
    }
    @Test
    // TC-13: addBook với nhiều bookID khác nhau => Expected output: mỗi bản ghi được lưu riêng biệt và đọc lại đúng title tương ứng.
    @DisplayName("addBook: distinct IDs -> each persists independently")
    void addBook_multipleDistinctIds_persistIndependently() {
        Book first = new Book("B100", "Java 301", "Carol", 2023, "Tech", "3333333333", "", 2);
        Book second = new Book("B200", "Rust 101", "Dave", 2024, "Tech", "4444444444", "", 1);

        dao.addBook(first);
        dao.addBook(second);

        assertEquals("Java 301", dao.findBookById("B100").getTitle());
        assertEquals("Rust 101", dao.findBookById("B200").getTitle());
    }
    @Test
    // TC-14: deleteBook với bookID tồn tại => Expected output: bản ghi bị xóa và findBookById cho ID đó trả null.
    @DisplayName("deleteBook: existing ID -> deletes record")
    void deleteBook_existing_removesRecord() {
        insertBook(new Book("B1", "Java 101", "Alice", 2022, "Tech", "1111111111", "", 3));

        dao.deleteBook("B1");

        assertNull(dao.findBookById("B1"));
    }

    @Test
    // TC-15: deleteBook với bookID không tồn tại => Expected output: không xóa bản ghi nào khác và các sách còn lại vẫn tồn tại.
    @DisplayName("deleteBook: missing ID -> keeps existing rows")
    void deleteBook_missing_noChanges() {
        insertBook(new Book("B1", "Java 101", "Alice", 2022, "Tech", "1111111111", "", 3));

        dao.deleteBook("B2");

        assertNotNull(dao.findBookById("B1"));
    }
    @Test
    // TC-16: deleteBook chỉ xóa bản ghi trùng ID, không ảnh hưởng các book khác => Expected output: bookID khác vẫn tồn tại sau khi xóa.
    @DisplayName("deleteBook: removes target ID but keeps others")
    void deleteBook_removeOne_keepsOthers() {
        insertBook(new Book("B101", "Clean Code", "Martin", 2008, "Tech", "5555555555", "", 2));
        insertBook(new Book("B102", "Clean Architecture", "Martin", 2017, "Tech", "6666666666", "", 1));

        dao.deleteBook("B101");

        assertNull(dao.findBookById("B101"));
        assertNotNull(dao.findBookById("B102"));
    }
    @Test
    // TC-17: updateBook với bookID đã tồn tại => Expected output: title/category/quantity trong DB được cập nhật theo Book truyền vào.
    @DisplayName("updateBook: existing ID -> updates fields")
    void updateBook_existing_updatesFields() {
        insertBook(new Book("B1", "Java 101", "Alice", 2022, "Tech", "1111111111", "", 3));
        Book updated = new Book("B1", "Java 201", "Alice B", 2023, "Tech2", "1111111111", "COV", 5);

        dao.updateBook(updated);

        Book stored = dao.findBookById("B1");
        assertEquals("Java 201", stored.getTitle());
        assertEquals("Tech2", stored.getCategory());
        assertEquals(5, stored.getQuantity());
    }

    @Test
    // TC-18: updateBook với bookID chưa tồn tại => Expected output: không tạo bản ghi mới, findBookById với ID đó vẫn trả null.
    @DisplayName("updateBook: missing ID -> does not insert new record")
    void updateBook_missing_noInsert() {
        Book updated = new Book("B2", "Java 201", "Alice B", 2023, "Tech2", "1111111111", "COV", 5);

        dao.updateBook(updated);

        assertNull(dao.findBookById("B2"));
    }
    @Test
    // TC-19: updateBook chỉ thay đổi coverCode => Expected output: coverCode mới được lưu nhưng title/category giữ nguyên.
    @DisplayName("updateBook: update coverCode partition")
    void updateBook_coverCodeOnly_updatesCover() {
        Book original = new Book("B103", "Kotlin Basics", "Eve", 2021, "Tech", "7777777777", "OLD", 4);
        insertBook(original);
        Book update = new Book("B103", "Kotlin Basics", "Eve", 2021, "Tech", "7777777777", "NEW", 4);

        dao.updateBook(update);

        Book stored = dao.findBookById("B103");
        assertEquals("NEW", stored.getCoverCode());
        assertEquals("Kotlin Basics", stored.getTitle());
    }

    @Test
    // TC-20: searchBooksValue với keyword khớp một phần tiêu đề của một số sách => Expected output: trả về danh sách chỉ chứa các Book khớp (ví dụ "Java 101").
    @DisplayName("searchBooksValue: matching keyword -> returns result list")
    void searchBooksValue_matching_returnsResults() {
        insertBook(new Book("B1", "Java 101", "Alice", 2022, "Tech", "1111111111", "", 3));
        insertBook(new Book("B2", "Python", "Bob", 2021, "Tech", "2222222222", "", 4));

        List<Book> results = dao.searchBooksValue("Java");

        assertEquals(1, results.size());
        assertEquals("B1", results.get(0).getBookID());
    }

    @Test
    // TC-21: searchBooksValue với keyword không khớp tiêu đề nào => Expected output: trả về danh sách rỗng.
    @DisplayName("searchBooksValue: non-matching keyword -> returns empty list")
    void searchBooksValue_nonMatching_returnsEmpty() {
        insertBook(new Book("B1", "Java 101", "Alice", 2022, "Tech", "1111111111", "", 3));

        List<Book> results = dao.searchBooksValue("History");

        assertTrue(results.isEmpty());
    }
    @Test
    // TC-22: searchBooksValue khớp theo author => Expected output: trả về đúng sách có author chứa keyword.
    @DisplayName("searchBooksValue: matches author partition")
    void searchBooksValue_authorMatch_returnsResults() {
        insertBook(new Book("B104", "Mathematics", "Helen Brown", 2018, "Education", "8888888888", "", 2));
        insertBook(new Book("B105", "Physics", "Ian Green", 2019, "Education", "9999999999", "", 2));

        List<Book> results = dao.searchBooksValue("Helen");

        assertEquals(1, results.size());
        assertEquals("B104", results.get(0).getBookID());
    }
    @Test
    // TC-23: updateQuantity được gọi với delta dương rồi âm trên cùng một sách => Expected output: quantity tăng 2 rồi giảm 1, phản ánh đúng tổng delta.
    @DisplayName("updateQuantity: positive then negative delta -> stock increases then decreases")
    void updateQuantity_positiveAndNegativeDelta_updateStock() {
        insertBook(new Book("B1", "Java 101", "Alice", 2022, "Tech", "1111111111", "", 3));

        dao.updateQuantity("B1", 2);
        assertEquals(5, dao.findBookById("B1").getQuantity());

        dao.updateQuantity("B1", -1);
        assertEquals(4, dao.findBookById("B1").getQuantity());
    }
    @Test
    // TC-24: updateQuantity với delta = 0 => Expected output: quantity giữ nguyên, không cập nhật DB.
    @DisplayName("updateQuantity: delta = 0 -> quantity unchanged")
    void updateQuantity_zeroDelta_keepsQuantity() {
        insertBook(new Book("B106", "Algorithms", "Kim", 2017, "Tech", "1212121212", "", 5));

        dao.updateQuantity("B106", 0);

        assertEquals(5, dao.findBookById("B106").getQuantity());
    }

    @Test
    // TC-25: updateQuantity với bookID không tồn tại => Expected output: không có bản ghi mới nào được tạo, hàm chạy xong không ném lỗi.
    @DisplayName("updateQuantity: missing bookID -> no changes")
    void updateQuantity_missingId_noChange() {
        dao.updateQuantity("UNKNOWN", 3);

        assertNull(dao.findBookById("UNKNOWN"));
    }
    @Test
    // TC-26: findBookById với ID tồn tại => Expected output: trả về Book tương ứng (không null).
    @DisplayName("findBookById: existing ID -> returns Book")
    void findBookById_existing_returnsBook() {
        insertBook(new Book("B1", "Java 101", "Alice", 2022, "Tech", "1111111111", "", 3));

        assertNotNull(dao.findBookById("B1"));
    }

    @Test
    // TC-27: findBookById với ID không tồn tại => Expected output: trả về null.
    @DisplayName("findBookById: missing ID -> returns null")
    void findBookById_missing_returnsNull() {
        assertNull(dao.findBookById("B1"));
    }
    @Test
    // TC-28: findBookById khi bảng có nhiều bản ghi => Expected output: trả về đúng Book ứng với ID được hỏi (không nhầm bản ghi khác).
    @DisplayName("findBookById: multiple rows -> returns matching record")
    void findBookById_multipleRows_returnsCorrectBook() {
        insertBook(new Book("B107", "Scala Basics", "Frank", 2020, "Tech", "2323232323", "", 2));
        insertBook(new Book("B108", "Scala Advanced", "Frank", 2021, "Tech", "3434343434", "", 1));

        Book found = dao.findBookById("B108");

        assertNotNull(found);
        assertEquals("Scala Advanced", found.getTitle());
    }
    @Test
    // TC-29: findBookByIsbn với ISBN tồn tại => Expected output: trả về Book tương ứng.
    @DisplayName("findBookByIsbn: existing ISBN -> returns Book")
    void findBookByIsbn_existing_returnsBook() {
        insertBook(new Book("B1", "Java 101", "Alice", 2022, "Tech", "1111111111", "", 3));

        assertNotNull(dao.findBookByIsbn("1111111111"));
    }

    @Test
    // TC-30: findBookByIsbn với ISBN không tồn tại => Expected output: trả về null.
    @DisplayName("findBookByIsbn: missing ISBN -> returns null")
    void findBookByIsbn_missing_returnsNull() {
        assertNull(dao.findBookByIsbn("1111111111"));
    }
    @Test
    // TC-31: findBookByIsbn với nhiều ISBN trong bảng => Expected output: trả về đúng Book khớp ISBN mà không phụ thuộc thứ tự insert.
    @DisplayName("findBookByIsbn: multiple rows -> returns exact ISBN match")
    void findBookByIsbn_multipleRows_returnsExactMatch() {
        insertBook(new Book("B109", "Swift Basics", "Gina", 2019, "Tech", "4545454545", "", 1));
        insertBook(new Book("B110", "Swift Advanced", "Gina", 2020, "Tech", "5656565656", "", 2));

        Book found = dao.findBookByIsbn("5656565656");

        assertNotNull(found);
        assertEquals("B110", found.getBookID());
    }
    @Test
    // TC-32: generateQrCodeForBook với ISBN rỗng/blank => Expected output: trả null và không cố gắng tạo QR.
    @DisplayName("generateQrCodeForBook: blank ISBN -> returns null")
    void generateQrCodeForBook_invalidIsbn_returnsNull() {
        assertNull(dao.generateQrCodeForBook("  "));
    }

    @Test
    // TC-33: file QR cho ISBN tương ứng đã tồn tại trên đĩa => Expected output: generateQrCodeForBook trả về path hiện có và không ghi đè file.
    @DisplayName("generateQrCodeForBook: file already exists -> returns existing path")
    void generateQrCodeForBook_existingFile_returnsPath() throws Exception {
        String isbn = "9780134494166";
        Path tempDir = Files.createTempDirectory("qr-output");
        Path existing = tempDir.resolve(isbn + "_qr.png");
        Files.createFile(existing);
        BookDAOImpl.setQrOutputDir(tempDir.toString());

        String qrPath = dao.generateQrCodeForBook(isbn);

        assertEquals(existing.toString(), qrPath);
        assertTrue(Files.exists(existing));
    }

    @Test
    // TC-34: ISBN hợp lệ, thư mục output đã cấu hình và chưa có file QR => Expected output: generateQrCodeForBook tạo file QR mới và trả về path của file.
    @DisplayName("generateQrCodeForBook: valid -> creates QR file and returns path")
    void generateQrCodeForBook_valid_createsFile() throws Exception {
        String isbn = "9780134494166";
        Path tempDir = Files.createTempDirectory("qr-output");
        BookDAOImpl.setQrOutputDir(tempDir.toString());

        String qrPath = dao.generateQrCodeForBook(isbn);

        assertNotNull(qrPath);
        assertTrue(Files.exists(Path.of(qrPath)));
    }

    private void insertBook(Book book) {
        dao.addBook(book);
    }

    private static StubConnectionFactory singleResponseFactory(StubHttpURLConnection connection) {
        return new StubConnectionFactory(new StubHttpURLConnection[]{connection});
    }

    private static StubHttpURLConnection httpOk(String body) throws IOException {
        return httpResponse(HttpURLConnection.HTTP_OK, body);
    }

    private static StubHttpURLConnection httpResponse(int status, String body) throws IOException {
        return new StubHttpURLConnection(new URL("http://fake"), status, body);
    }

    private static String fakeBookPayload(String bookId,
                                          String title,
                                          String author,
                                          int publishYear,
                                          String category,
                                          String isbn,
                                          String thumbnail,
                                          String previewLink) {
        String previewBlock = previewLink == null ? "" : """
                ,"previewLink":"%s"
                """.formatted(previewLink);
        return """
                {
                  "totalItems": 1,
                  "items": [
                    {
                      "id": "%s",
                      "volumeInfo": {
                        "title": "%s",
                        "authors": ["%s"],
                        "publishedDate": %d,
                        "categories": ["%s"],
                        "industryIdentifiers": [{"identifier": "%s"}],
                        "imageLinks": {"thumbnail": "%s"}%s
                      }
                    }
                  ]
                }
                """.formatted(bookId, title, author, publishYear, category, isbn, thumbnail, previewBlock);
    }

    private static class StubHttpURLConnection extends HttpURLConnection {
        private final int responseCode;
        private final byte[] payload;

        protected StubHttpURLConnection(URL url, int responseCode, String body) {
            super(url);
            this.responseCode = responseCode;
            this.payload = Objects.requireNonNull(body).getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public int getResponseCode() {
            return responseCode;
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(payload);
        }

        @Override
        public void disconnect() {}

        @Override
        public boolean usingProxy() {
            return false;
        }

        @Override
        public void connect() {}
    }

    private static class ThrowingHttpURLConnection extends HttpURLConnection {
        protected ThrowingHttpURLConnection(URL url) {
            super(url);
        }

        @Override
        public int getResponseCode() {
            return HttpURLConnection.HTTP_OK;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            throw new IOException("Simulated IO failure");
        }

        @Override
        public void disconnect() {}

        @Override
        public boolean usingProxy() {
            return false;
        }

        @Override
        public void connect() {}
    }

    private static class StubConnectionFactory implements BookDAOImpl.HttpConnectionFactory {
        private final Queue<HttpURLConnection> responses = new ArrayDeque<>();

        StubConnectionFactory(HttpURLConnection[] conns) {
            for (HttpURLConnection conn : conns) {
                responses.add(conn);
            }
        }

        @Override
        public HttpURLConnection open(URL url) {
            HttpURLConnection conn = responses.poll();
            if (conn == null) {
                throw new IllegalStateException("Missing stub for " + url);
            }
            return conn;
        }
    }
}
