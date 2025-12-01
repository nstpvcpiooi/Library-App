package Library.backend.Recommendation.Test;

import Library.backend.Book.Model.Book;
import Library.backend.Recommendation.DAO.RecommendationDAO;
import Library.backend.Recommendation.service.RecommendationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

// Sử dụng Phân hoạch tương đương kiểm thử đơn vị cho các unit trong RecommendationService.
public class RecommendationServiceTest {

    private static class RecordingRecommendationDao implements RecommendationDAO {
        AtomicInteger lastMemberId = new AtomicInteger(-1);
        List<Book> combinedResult = List.of();

        @Override
        public List<Book> getCombinedRecommendations(int memberID) {
            lastMemberId.set(memberID);
            return combinedResult;
        }
    }

    private static RecommendationService newServiceWithDao(RecommendationDAO dao) {
        try {
            Constructor<RecommendationService> ctor = RecommendationService.class.getDeclaredConstructor(RecommendationDAO.class);
            ctor.setAccessible(true);
            return ctor.newInstance(dao);
        } catch (Exception e) {
            throw new RuntimeException("Cannot construct RecommendationService for test", e);
        }
    }

    @Test
    // TC-01: memberId hợp lệ, DAO trả về danh sách gợi ý => Expected output: service gọi DAO với đúng memberId và trả lại đúng list Book (cùng phần tử, cùng thứ tự) từ DAO.
    @DisplayName("getCombinedRecommendations: hop le -> goi DAO va tra list DAO")
    void getCombinedRecommendations_delegates() {
        RecordingRecommendationDao dao = new RecordingRecommendationDao();
        Book expected = new Book("B1", "T", "A", 2020, "C", "978", "cover", 1);
        dao.combinedResult = List.of(expected);
        RecommendationService service = newServiceWithDao(dao);

        List<Book> result = service.getCombinedRecommendations(10);

        assertEquals(10, dao.lastMemberId.get());
        assertEquals(1, result.size());
        assertSame(expected, result.get(0));
    }

    @Test
    // TC-02: DAO trả về null khi lấy combined recommendations => Expected output: service cũng trả null và vẫn truyền đúng memberId cho DAO.
    @DisplayName("getCombinedRecommendations: DAO tra null -> tra null")
    void getCombinedRecommendations_nullDaoResult_returnsNull() {
        RecordingRecommendationDao dao = new RecordingRecommendationDao();
        dao.combinedResult = null;
        RecommendationService service = newServiceWithDao(dao);

        assertNull(service.getCombinedRecommendations(5));
        assertEquals(5, dao.lastMemberId.get());
    }

    @Test
    // TC-03: memberId âm => Expected output: service vẫn gọi DAO với memberId âm và trả lại đúng list DAO trả về.
    @DisplayName("getCombinedRecommendations: negative memberId -> still delegates")
    void getCombinedRecommendations_negativeMemberId_delegates() {
        RecordingRecommendationDao dao = new RecordingRecommendationDao();
        Book rec = new Book("B2", "Alt", "Author", 2021, "Cat", "979", "cover", 1);
        dao.combinedResult = List.of(rec);
        RecommendationService service = newServiceWithDao(dao);

        List<Book> result = service.getCombinedRecommendations(-3);

        assertEquals(-3, dao.lastMemberId.get());
        assertEquals(1, result.size());
        assertSame(rec, result.get(0));
    }
}
