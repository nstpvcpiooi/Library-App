package Library.backend.Recommendation.DAO;

import Library.backend.Recommendation.Model.Recommendation;
import Library.backend.Book.Model.Book;

import java.util.List;

public interface RecommendationDAO {
    List<Book> getCombinedRecommendations(int memberID);
}
