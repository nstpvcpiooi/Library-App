package Library.backend.Review.Dao;

import Library.backend.Review.model.Review;

import java.util.List;

public interface ReviewDao {
    boolean addReview(Review review); // Add a new review
    boolean updateReview(Review review); // Update an existing review
    boolean deleteReview(String bookID, int memberID); // Delete a review based on bookID and memberID
    Review getReviewByBookAndMember(String bookID, int memberID); // Get a review by bookID and memberID
    List<Review> getReviewsForBook(String bookID); // Get all reviews for a specific book
    List<Review> getReviewsByMember(int memberID); // Get all reviews by a specific member
    double getAverageRatingForBook(String bookID); // Get average rating for a book
}
