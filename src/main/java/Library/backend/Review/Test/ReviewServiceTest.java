package Library.backend.Review.Test;

import Library.backend.Review.DAO.ReviewDAO;
import Library.backend.Review.Model.Review;
import Library.backend.Review.service.ReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

// Sử dụng Phân hoạch tương đương kiểm thử đơn vị cho các unit trong ReviewService.
public class ReviewServiceTest {

    private static class RecordingReviewDao implements ReviewDAO {
        AtomicReference<Review> lastAdded = new AtomicReference<>();
        AtomicReference<Review> lastUpdated = new AtomicReference<>();
        AtomicReference<String> lastBookId = new AtomicReference<>();
        AtomicInteger lastMemberId = new AtomicInteger(-1);
        Review reviewResult;
        double avgResult = 0.0;
        int countResult = 0;

        @Override
        public boolean addReview(Review review) {
            lastAdded.set(review);
            return true;
        }

        @Override
        public boolean updateReview(Review review) {
            lastUpdated.set(review);
            return true;
        }

        @Override
        public Review getReviewByBookAndMember(String bookID, int memberID) {
            lastBookId.set(bookID);
            lastMemberId.set(memberID);
            return reviewResult;
        }

        @Override
        public double getAverageRatingForBook(String bookID) {
            lastBookId.set(bookID);
            return avgResult;
        }

        @Override
        public int getRatingCountForBook(String bookID) {
            lastBookId.set(bookID);
            return countResult;
        }
    }


    @Test
    // TC-01: User submit Review lần đầu => Expected output: Review mới được thêm vào thành công.
    @DisplayName("submitReview: no existing review -> calls addReview")
    void submitReview_new_callsAdd() {
        RecordingReviewDao dao = new RecordingReviewDao();
        ReviewService service = ReviewService.create(dao);

        service.submitReview("B1", 10, 5, "Great");

        Review added = dao.lastAdded.get();
        assertNotNull(added);
        assertEquals("B1", added.getBookID());
        assertEquals(10, added.getMemberID());
        assertEquals(5, added.getRating());
        assertEquals("Great", added.getComment());
        assertEquals(LocalDate.now().toString(), added.getReviewTimestamp());
        assertNull(dao.lastUpdated.get());
    }

    @Test
    // TC-02: User đã từng submit Review => Expected output: Review mới được update thành công.
    @DisplayName("submitReview: existing review -> calls updateReview")
    void submitReview_existing_callsUpdate() {
        RecordingReviewDao dao = new RecordingReviewDao();
        Review existing = new Review.Builder()
                .bookID("B1")
                .memberID(10)
                .rating(3)
                .reviewTimestamp(LocalDate.now().minusDays(1).toString())
                .comment("Old")
                .build();
        dao.reviewResult = existing;
        ReviewService service = ReviewService.create(dao);

        service.submitReview("B1", 10, 4, "Better");

        assertNull(dao.lastAdded.get());
        Review updated = dao.lastUpdated.get();
        assertNotNull(updated);
        assertSame(existing, updated);
        assertEquals("B1", updated.getBookID());
        assertEquals(10, updated.getMemberID());
        assertEquals(4, updated.getRating());
        assertEquals("Better", updated.getComment());
        assertEquals(LocalDate.now().toString(), updated.getReviewTimestamp());
    }
    @Test
    // TC-03: người dùng review mới nhưng comment = null => Expected output: submitReview vẫn tạo Review mới, gọi addReview và lưu comment null.
    @DisplayName("submitReview: null comment -> still calls addReview with null comment")
    void submitReview_nullComment_callsAdd() {
        RecordingReviewDao dao = new RecordingReviewDao();
        ReviewService service = ReviewService.create(dao);

        service.submitReview("B5", 11, 4, null);

        Review added = dao.lastAdded.get();
        assertNotNull(added);
        assertNull(added.getComment());
        assertEquals("B5", added.getBookID());
        assertEquals(11, added.getMemberID());
    }
    @Test
    // TC-04: DAO lấy được Review thành công => Expected output: Trả về thành công Review đã thêm.
    @DisplayName("getReview: existing review -> returns DAO result")
    void getReview_delegates() {
        RecordingReviewDao dao = new RecordingReviewDao();
        Review expected = new Review.Builder()
                .bookID("B2")
                .memberID(9)
                .rating(4)
                .reviewTimestamp(LocalDate.now().toString())
                .comment("Ok")
                .build();
        dao.reviewResult = expected;
        ReviewService service = ReviewService.create(dao);

        Review result = service.getReview("B2", 9);

        assertSame(expected, result);
        assertEquals("B2", dao.lastBookId.get());
        assertEquals(9, dao.lastMemberId.get());
    }
    @Test
    // TC-05: DAO trả về null cho getReview => Expected output: service trả null và truyền đúng tham số xuống DAO.
    @DisplayName("getReview: DAO returns null -> service returns null")
    void getReview_nullFromDao_returnsNull() {
        RecordingReviewDao dao = new RecordingReviewDao();
        ReviewService service = ReviewService.create(dao);

        assertNull(service.getReview("B6", 12));
        assertEquals("B6", dao.lastBookId.get());
        assertEquals(12, dao.lastMemberId.get());
    }

    @Test
    // TC-06: bookId blank khi gọi getReview => Expected output: service vẫn gọi DAO và nhận về kết quả DAO (ở đây null).
    @DisplayName("getReview: blank bookId -> still delegates")
    void getReview_blankBookId_delegates() {
        RecordingReviewDao dao = new RecordingReviewDao();
        ReviewService service = ReviewService.create(dao);

        assertNull(service.getReview("  ", 1));
        assertEquals("  ", dao.lastBookId.get());
    }
    @Test
    // TC-07: Lấy rating trung bình cho book thành công => Expected output: Rating trung bình của book được tính và trả lại chính xác.
    @DisplayName("getAverageRating: valid call -> returns DAO value")
    void getAverageRating_delegates() {
        RecordingReviewDao dao = new RecordingReviewDao();
        dao.avgResult = 4.2;
        ReviewService service = ReviewService.create(dao);

        double result = service.getAverageRating("B3");

        assertEquals(4.2, result);
        assertEquals("B3", dao.lastBookId.get());
    }
    @Test
    // TC-08: DAO trả về 0 cho getAverageRating => Expected output: service trả 0.
    @DisplayName("getAverageRating: DAO returns 0 -> returns 0")
    void getAverageRating_zero_returnsZero() {
        RecordingReviewDao dao = new RecordingReviewDao();
        dao.avgResult = 0.0;
        ReviewService service = ReviewService.create(dao);

        assertEquals(0.0, service.getAverageRating("B7"));
    }

    @Test
    // TC-09: bookId blank khi tính average => Expected output: service vẫn gọi DAO với bookId blank và nhận về giá trị tương ứng.
    @DisplayName("getAverageRating: blank bookId -> still delegates")
    void getAverageRating_blankBookId_delegates() {
        RecordingReviewDao dao = new RecordingReviewDao();
        dao.avgResult = 3.5;
        ReviewService service = ReviewService.create(dao);

        assertEquals(3.5, service.getAverageRating(" "));
        assertEquals(" ", dao.lastBookId.get());
    }
    @Test
    // TC-10: Lấy tổng số lượt rate cho book thành công => Expected output: Tổng số lượt rate cho book được trả lại thành công.
    @DisplayName("getRatingCount: valid call -> returns DAO value")
    void getRatingCount_delegates() {
        RecordingReviewDao dao = new RecordingReviewDao();
        dao.countResult = 7;
        ReviewService service = ReviewService.create(dao);

        int result = service.getRatingCount("B4");

        assertEquals(7, result);
        assertEquals("B4", dao.lastBookId.get());
    }
    @Test
    // TC-11: DAO trả về 0 cho getRatingCount => Expected output: service trả 0.
    @DisplayName("getRatingCount: DAO returns 0 -> returns 0")
    void getRatingCount_zero_returnsZero() {
        RecordingReviewDao dao = new RecordingReviewDao();
        dao.countResult = 0;
        ReviewService service = ReviewService.create(dao);

        assertEquals(0, service.getRatingCount("B8"));
    }

    @Test
    // TC-12: bookId blank khi đếm rating => Expected output: service vẫn gọi DAO và trả về đúng count.
    @DisplayName("getRatingCount: blank bookId -> still delegates")
    void getRatingCount_blankBookId_delegates() {
        RecordingReviewDao dao = new RecordingReviewDao();
        dao.countResult = 2;
        ReviewService service = ReviewService.create(dao);

        assertEquals(2, service.getRatingCount("   "));
        assertEquals("   ", dao.lastBookId.get());
    }







}
