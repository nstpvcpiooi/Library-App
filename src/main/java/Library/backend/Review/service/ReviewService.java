package Library.backend.Review.service;

import Library.backend.Review.DAO.MysqlReviewDAOImpl;
import Library.backend.Review.DAO.ReviewDAO;
import Library.backend.Review.Model.Review;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Encapsulates review workflows such as creating/updating ratings,
 * computing aggregates, etc. Keeps controllers/models away from DAO singletons.
 */
public class ReviewService {

    private static volatile ReviewService instance;

    private final ReviewDAO reviewDAO;

    private ReviewService(ReviewDAO reviewDAO) {
        this.reviewDAO = Objects.requireNonNull(reviewDAO);
    }

    public static ReviewService getInstance() {
        if (instance == null) {
            synchronized (ReviewService.class) {
                if (instance == null) {
                    instance = new ReviewService(MysqlReviewDAOImpl.getInstance());
                }
            }
        }
        return instance;
    }

    /**
     * Factory for tests or manual dependency injection without touching the singleton.
     */
    public static ReviewService create(ReviewDAO reviewDAO) {
        return new ReviewService(reviewDAO);
    }


    public void submitReview(String bookId, int memberId, int rating, String comment) {
        Review existing = reviewDAO.getReviewByBookAndMember(bookId, memberId);
        if (existing != null) {
            existing.setRating(rating);
            existing.setComment(comment);
            existing.setReviewTimestamp(LocalDate.now().toString());
            reviewDAO.updateReview(existing);
        } else {
            Review review = new Review.Builder()
                    .bookID(bookId)
                    .memberID(memberId)
                    .rating(rating)
                    .reviewTimestamp(LocalDate.now().toString())
                    .comment(comment)
                    .build();
            reviewDAO.addReview(review);
        }
    }



    public Review getReview(String bookId, int memberId) {
        return reviewDAO.getReviewByBookAndMember(bookId, memberId);
    }
    public double getAverageRating(String bookId) {
        return reviewDAO.getAverageRatingForBook(bookId);
    }

    public int getRatingCount(String bookId) {
        return reviewDAO.getRatingCountForBook(bookId);
    }
}

