package Library.backend.Recommendation.Dao;

import Library.backend.Recommendation.model.Recommendation;
import java.util.List;

public interface RecommendationDao {
    // Add a new recommendation to the database
    boolean addRecommendation(Recommendation recommendation);

    // Update an existing recommendation
    boolean updateRecommendation(Recommendation recommendation);

    // Delete a recommendation by its ID
    boolean deleteRecommendation(String recommendationID);

    // Get a recommendation by its ID
    Recommendation getRecommendationById(String recommendationID);

    // Get all recommendations for a specific member
    List<Recommendation> getRecommendationsForMember(String memberID);

    // Get recommendations based on member preferences and borrowing requests
    List<Recommendation> getRecommendationsBasedOnPreferencesAndRequests(String memberID);

    // Get the most popular recommendations across the system
    List<Recommendation> getPopularRecommendations();
}
