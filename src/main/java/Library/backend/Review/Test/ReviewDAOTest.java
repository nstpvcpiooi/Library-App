package Library.backend.Review.Test;

import Library.backend.Review.DAO.MysqlReviewDAOImpl;
import Library.backend.Review.Model.Review;
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
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

// Sử dụng Phân hoạch tương đương kiểm thử đơn vị cho các unit trong ReviewDAO.
public class ReviewDAOTest {

    private static final String H2_URL = "jdbc:h2:mem:library_review;MODE=MySQL;DB_CLOSE_DELAY=-1";
    private static final String CREATE_REVIEWS_TABLE = """
            CREATE TABLE IF NOT EXISTS Reviews (
                reviewID INT AUTO_INCREMENT PRIMARY KEY,
                bookID VARCHAR(255),
                memberID INT,
                rating INT,
                reviewTimestamp DATE,
                comment VARCHAR(255)
            )
            """;

    private MysqlReviewDAOImpl dao;

    @BeforeAll
    static void initDatabase() throws SQLException {
        try (Connection con = DriverManager.getConnection(H2_URL);
             Statement st = con.createStatement()) {
            st.execute(CREATE_REVIEWS_TABLE);
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
        dao = MysqlReviewDAOImpl.getInstance();
        try (Connection con = DriverManager.getConnection(H2_URL);
             Statement st = con.createStatement()) {
            st.execute("TRUNCATE TABLE Reviews");
        }
    }

    @Test
    // TC-01: thêm một review hợp lệ (bookID/memberID/rating/comment đầy đủ) => Expected output: addReview trả true và có thể đọc lại đúng review đó từ DB bằng getReviewByBookAndMember.
    @DisplayName("addReview: valid review -> returns true and can be read back")
    void addReview_insertsRow() {
        Review review = new Review.Builder()
                .bookID("B1")
                .memberID(10)
                .rating(5)
                .reviewTimestamp(LocalDate.now().toString())
                .comment("Great")
                .build();

        boolean ok = dao.addReview(review);

        assertTrue(ok);
        Review stored = dao.getReviewByBookAndMember("B1", 10);
        assertNotNull(stored);
        assertEquals(5, stored.getRating());
        assertEquals("Great", stored.getComment());
    }
    @Test
    // TC-02: addReview lần thứ hai với bookID giống nhưng memberID khác => Expected output: addReview trả true và tổng số review cho book đó tăng lên 2.
    @DisplayName("addReview: second reviewer for same book -> persists second row")
    void addReview_secondReviewer_persists() {
        insertReview("B5", 1, 4, LocalDate.now().toString(), "First");
        Review second = new Review.Builder()
                .bookID("B5")
                .memberID(2)
                .rating(5)
                .reviewTimestamp(LocalDate.now().toString())
                .comment("Second")
                .build();

        assertTrue(dao.addReview(second));
        assertEquals(2, dao.getRatingCountForBook("B5"));
    }

    @Test
    // TC-03: truyền review = null vào addReview => Expected output: addReview ném NullPointerException.
    @DisplayName("addReview: null review -> NullPointerException")
    void addReview_nullReview_throwsException() {
        assertThrows(NullPointerException.class, () -> dao.addReview(null));
    }

    @Test
    // TC-04: cập nhật một review đã tồn tại trong DB => Expected output: updateReview trả true và khi đọc lại thì rating/comment đã được thay bằng giá trị mới.
    @DisplayName("updateReview: existing review -> updates rating and comment")
    void updateReview_updatesExisting() {
        insertReview("B1", 10, 3, LocalDate.now().minusDays(1).toString(), "Old");
        Review updated = new Review.Builder()
                .bookID("B1")
                .memberID(10)
                .rating(4)
                .reviewTimestamp(LocalDate.now().toString())
                .comment("New")
                .build();

        boolean ok = dao.updateReview(updated);

        assertTrue(ok);
        Review stored = dao.getReviewByBookAndMember("B1", 10);
        assertEquals(4, stored.getRating());
        assertEquals("New", stored.getComment());
    }

    @Test
    // TC-05: gọi updateReview với bookID/memberID không tồn tại trong DB => Expected output: updateReview trả false (không có bản ghi nào được cập nhật).
    @DisplayName("updateReview: missing review -> returns false")
    void updateReview_missing_returnsFalse() {
        Review updated = new Review.Builder()
                .bookID("B_missing")
                .memberID(99)
                .rating(2)
                .reviewTimestamp(LocalDate.now().toString())
                .comment("none")
                .build();

        assertFalse(dao.updateReview(updated));
    }
    @Test
    // TC-06: truyền review = null vào updateReview => Expected output: updateReview ném NullPointerException.
    @DisplayName("updateReview: null review -> NullPointerException")
    void updateReview_nullReview_throwsException() {
        assertThrows(NullPointerException.class, () -> dao.updateReview(null));
    }
    @Test
    // TC-07: không có review nào cho cặp bookID/memberID trong DB => Expected output: getReviewByBookAndMember trả null.
    @DisplayName("getReviewByBookAndMember: missing review -> returns null")
    void getReviewByBookAndMember_missing_returnsNull() {
        assertNull(dao.getReviewByBookAndMember("missing", 1));
    }
    @Test
    // TC-08: tồn tại review cho cặp bookID/memberID => Expected output: getReviewByBookAndMember trả về đúng Review và khớp rating/comment.
    @DisplayName("getReviewByBookAndMember: existing review -> returns Review")
    void getReviewByBookAndMember_existing_returnsReview() {
        insertReview("B6", 3, 4, LocalDate.now().toString(), "Nice book");

        Review found = dao.getReviewByBookAndMember("B6", 3);

        assertNotNull(found);
        assertEquals(4, found.getRating());
        assertEquals("Nice book", found.getComment());
    }

    @Test
    // TC-09: gọi getReviewByBookAndMember với bookID = null => Expected output: trả null (không tìm thấy).
    @DisplayName("getReviewByBookAndMember: bookID null -> returns null")
    void getReviewByBookAndMember_nullBookId_returnsNull() {
        assertNull(dao.getReviewByBookAndMember(null, 1));
    }
    @Test
    // TC-10: một book có nhiều review với rating khác nhau => Expected output: getAverageRatingForBook trả đúng giá trị trung bình cộng của các rating đó.
    @DisplayName("getAverageRatingForBook: computes correct average")
    void getAverageRatingForBook_computesAverage() {
        insertReview("B1", 1, 5, LocalDate.now().toString(), "A");
        insertReview("B1", 2, 3, LocalDate.now().toString(), "B");

        double avg = dao.getAverageRatingForBook("B1");

        assertEquals(4.0, avg);
    }

    @Test
    // TC-11: DB có review cho nhiều book khác nhau => Expected output: getAverageRatingForBook chỉ tính rating của book truyền vào, bỏ qua review của sách khác.
    @DisplayName("getAverageRatingForBook: counts only target book")
    void getAverageRatingForBook_filtersByBook() {
        insertReview("B1", 1, 5, LocalDate.now().toString(), "A");
        insertReview("B2", 1, 1, LocalDate.now().toString(), "B");

        double avg = dao.getAverageRatingForBook("B1");

        assertEquals(5.0, avg);
    }

    @Test
    // TC-12: book chưa có review nào trong DB => Expected output: getAverageRatingForBook trả 0.0.
    @DisplayName("getAverageRatingForBook: no reviews -> returns 0.0")
    void getAverageRatingForBook_noReviews_returnsZero() {
        assertEquals(0.0, dao.getAverageRatingForBook("B2"));
    }

    @Test
    // TC-13: một book có nhiều review trong DB => Expected output: getRatingCountForBook trả đúng số lượng review (số dòng) của book đó.
    @DisplayName("getRatingCountForBook: returns correct count")
    void getRatingCountForBook_countsRows() {
        insertReview("B1", 1, 5, LocalDate.now().toString(), "A");
        insertReview("B1", 2, 4, LocalDate.now().toString(), "B");

        assertEquals(2, dao.getRatingCountForBook("B1"));
    }

    @Test
    // TC-14: DB có review cho nhiều book khác nhau => Expected output: getRatingCountForBook chỉ đếm số review của book truyền vào, không đếm review sách khác.
    @DisplayName("getRatingCountForBook: counts only target book")
    void getRatingCountForBook_filtersByBook() {
        insertReview("B1", 1, 5, LocalDate.now().toString(), "A");
        insertReview("B2", 2, 4, LocalDate.now().toString(), "B");

        assertEquals(1, dao.getRatingCountForBook("B1"));
    }

    @Test
    // TC-15: book không có review nào trong DB => Expected output: getRatingCountForBook trả 0.
    @DisplayName("getRatingCountForBook: no data -> returns 0")
    void getRatingCountForBook_noRows_returnsZero() {
        assertEquals(0, dao.getRatingCountForBook("B3"));
    }






    private static void insertReview(String bookId, int memberId, int rating, String ts, String comment) {
        String sql = "INSERT INTO Reviews (bookID, memberID, rating, reviewTimestamp, comment) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(H2_URL);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, bookId);
            ps.setInt(2, memberId);
            ps.setInt(3, rating);
            ps.setDate(4, java.sql.Date.valueOf(ts));
            ps.setString(5, comment);
            ps.executeUpdate();
        } catch (SQLException e) {
            fail("Insert review failed: " + e.getMessage());
        }
    }
}
