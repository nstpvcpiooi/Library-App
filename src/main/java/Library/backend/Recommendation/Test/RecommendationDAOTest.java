package Library.backend.Recommendation.Test;

import Library.backend.Book.Model.Book;
import Library.backend.Recommendation.DAO.MysqlRecommendationDAOImpl;
import Library.backend.database.JDBCUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

// Sử dụng Phân hoạch tương đương kiểm thử đơn vị cho các unit trong RecommendationDAO.
public class RecommendationDAOTest {

    private static final String H2_URL = "jdbc:h2:mem:library_recommendation;MODE=MySQL;DB_CLOSE_DELAY=-1";
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
    private static final String CREATE_REVIEWS_TABLE = """
            CREATE TABLE IF NOT EXISTS Reviews (
                reviewID INT AUTO_INCREMENT PRIMARY KEY,
                bookID VARCHAR(255),
                memberID INT,
                rating INT
            )
            """;
    private static final String CREATE_REQUESTS_TABLE = """
            CREATE TABLE IF NOT EXISTS Requests (
                requestID INT AUTO_INCREMENT PRIMARY KEY,
                memberID INT,
                bookID VARCHAR(255)
            )
            """;

    private MysqlRecommendationDAOImpl dao;

    @BeforeAll
    static void initDatabase() throws SQLException {
        try (Connection con = DriverManager.getConnection(H2_URL);
             Statement st = con.createStatement()) {
            st.execute(CREATE_BOOKS_TABLE);
            st.execute(CREATE_REVIEWS_TABLE);
            st.execute(CREATE_REQUESTS_TABLE);
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
    static void resetConnection() {
        JDBCUtil.resetConnectionSupplier();
    }

    @BeforeEach
    void resetState() throws SQLException {
        dao = MysqlRecommendationDAOImpl.getInstance();
        try (Connection con = DriverManager.getConnection(H2_URL);
             Statement st = con.createStatement()) {
            st.execute("TRUNCATE TABLE Reviews");
            st.execute("TRUNCATE TABLE Requests");
            st.execute("TRUNCATE TABLE Books");
        }
    }

    @Test
    // TC-01: có dữ liệu lịch sử mượn, độc giả tương tự và sách đánh giá cao => Expected output: getCombinedRecommendations hợp nhất cả 3 nguồn gợi ý và loại bỏ trùng lặp, trả về đủ các bookID kỳ vọng.
    @DisplayName("getCombinedRecommendations hop nhat 3 nguon va khong trung lap")
    void getCombinedRecommendations_mergeAllSources() {
        insertBook(buildBook("B1", "Tech"));
        insertBook(buildBook("B2", "Tech"));
        insertBook(buildBook("B3", "History"));
        insertBook(buildBook("B4", "Drama"));

        insertRequest(1, "B1"); // lịch sử -> B1, B2 (cùng category)
        insertRequest(2, "B1"); // người tương tự
        insertRequest(2, "B3"); // người tương tự gợi ý B3
        insertReview("B4", 5);  // popular

        List<Book> result = dao.getCombinedRecommendations(1);

        Set<String> ids = new HashSet<>(bookIds(result));
        assertEquals(Set.of("B1", "B2", "B3", "B4"), ids);
    }

    @Test
    // TC-02: chỉ có dữ liệu sách được đánh giá cao (popular), không có lịch sử mượn hay độc giả tương tự => Expected output: getCombinedRecommendations chỉ trả về danh sách các sách popular đó.
    @DisplayName("getCombinedRecommendations chi co popular")
    void getCombinedRecommendations_onlyPopular() {
        insertBook(buildBook("B10", "Tech"));
        insertReview("B10", 5);

        List<Book> result = dao.getCombinedRecommendations(99);

        assertEquals(1, result.size());
        assertEquals("B10", result.get(0).getBookID());
    }

    @Test
    // TC-03: không có bản ghi nào trong các bảng phục vụ recommend => Expected output: getCombinedRecommendations trả về list rỗng.
    @DisplayName("getCombinedRecommendations khong co du lieu")
    void getCombinedRecommendations_noData() {
        List<Book> result = dao.getCombinedRecommendations(5);

        assertTrue(result.isEmpty());
    }

    private static Book buildBook(String id, String category) {
        return new Book(id, "Title " + id, "Author", 2020, category, "9780000000000", "cover", 1);
    }

    private static void insertBook(Book book) {
        String sql = "INSERT INTO Books (bookID, title, author, publishYear, category, isbn, coverCode, quantity) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(H2_URL);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, book.getBookID());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getAuthor());
            ps.setInt(4, book.getPublishYear());
            ps.setString(5, book.getCategory());
            ps.setString(6, book.getIsbn());
            ps.setString(7, book.getCoverCode());
            ps.setInt(8, book.getQuantity());
            ps.executeUpdate();
        } catch (SQLException e) {
            fail("Insert book failed: " + e.getMessage());
        }
    }

    private static void insertReview(String bookId, int rating) {
        String sql = "INSERT INTO Reviews (bookID, memberID, rating) VALUES (?, 1, ?)";
        try (Connection con = DriverManager.getConnection(H2_URL);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, bookId);
            ps.setInt(2, rating);
            ps.executeUpdate();
        } catch (SQLException e) {
            fail("Insert review failed: " + e.getMessage());
        }
    }

    private static void insertRequest(int memberId, String bookId) {
        String sql = "INSERT INTO Requests (memberID, bookID) VALUES (?, ?)";
        try (Connection con = DriverManager.getConnection(H2_URL);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ps.setString(2, bookId);
            ps.executeUpdate();
        } catch (SQLException e) {
            fail("Insert request failed: " + e.getMessage());
        }
    }

    private static List<String> bookIds(List<Book> books) {
        return books.stream()
                .map(Book::getBookID)
                .collect(Collectors.toList());
    }
}
