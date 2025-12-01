package Library.backend.Review.Dao;

import Library.backend.Review.Model.Review;

import java.util.List;

public interface ReviewDAO {
    boolean addReview(Review review); // Add a new review
    boolean updateReview(Review review); // Update an existing review
    Review getReviewByBookAndMember(String bookID, int memberID); // Get a review by bookID and memberID
    double getAverageRatingForBook(String bookID); // Get average rating for a book
    int getRatingCountForBook(String bookID); // Get total number of ratings for a book
}
