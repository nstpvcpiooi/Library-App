package Library.backend.Recommendation.Dao;

import Library.backend.Recommendation.model.Recommendation;
import Library.backend.bookModel.Book;

import java.util.List;

public interface RecommendationDao {
    boolean addRecommendation(Recommendation recommendation);
    List<Recommendation> getRecommendationsForMember(int memberID);
    List<Book> getPopularRecommendations();
    List<Book> getRecommendationsBasedOnBorrowHistory(int memberID);
    List<Book> getRecommendationsFromSimilarUsers(int memberID);
    List<Book> getCombinedRecommendations(int memberID);
}
