package Library.backend.Recommendation.service;

import Library.backend.Book.Model.Book;
import Library.backend.Recommendation.DAO.MysqlRecommendationDAOImpl;
import Library.backend.Recommendation.DAO.RecommendationDAO;
import Library.backend.Recommendation.Model.Recommendation;

import java.util.List;
import java.util.Objects;

/**
 * Handles recommendation workflows while keeping controllers decoupled from DAO implementations.
 */
public class RecommendationService {

    private static volatile RecommendationService instance;

    private final RecommendationDAO recommendationDAO;

    private RecommendationService(RecommendationDAO recommendationDAO) {
        this.recommendationDAO = Objects.requireNonNull(recommendationDAO);
    }

    public static RecommendationService getInstance() {
        if (instance == null) {
            synchronized (RecommendationService.class) {
                if (instance == null) {
                    instance = new RecommendationService(MysqlRecommendationDAOImpl.getInstance());
                }
            }
        }
        return instance;
    }

    /**
     * Factory for tests or manual dependency injection.
     */
    public List<Book> getCombinedRecommendations(int memberId) {
        return recommendationDAO.getCombinedRecommendations(memberId);
    }
}
