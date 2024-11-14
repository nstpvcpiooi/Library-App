package Library.backend.Review.Dao;

import Library.backend.Review.model.Review;
import java.util.List;

public interface ReviewDao {
    // Add a new review to the database
    boolean addReview(Review review);

    // Update an existing review
    boolean updateReview(Review review);

    // Delete a review by its ID
    boolean deleteReview(String reviewID);

    // Get a review by its ID
    Review getReviewById(String reviewID);

    // Get all reviews for a specific book
    List<Review> getReviewsForBook(String bookID);

    // Get all reviews written by a specific member
    List<Review> getReviewsByMember(String memberID);

    // Get average rating for a specific book
    double getAverageRatingForBook(String bookID);
}
