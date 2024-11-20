package Library.backend.Login.Model;

import Library.backend.Recommendation.Dao.MysqlRecommendationDao;
import Library.backend.Recommendation.Dao.RecommendationDao;
import Library.backend.Recommendation.model.Recommendation;
import Library.backend.Request.DAO.RequestDAO;
import Library.backend.Request.DAO.RequestDAOImpl;
import Library.backend.Request.Model.Request;
import Library.backend.Review.Dao.MysqlReviewDao;
import Library.backend.Review.Dao.ReviewDao;
import Library.backend.Review.model.Review;
import Library.backend.bookModel.Book;

import java.util.List;

public class User extends Member {
    private List<Recommendation> recommendations;
    private RecommendationDao recommendationDAO = MysqlRecommendationDao.getInstance();
    private RequestDAO requestDAO = RequestDAOImpl.getInstance();
    private ReviewDao reviewDao = MysqlReviewDao.getInstance();

    public User(Member member) {
        this.setMemberID(member.getMemberID());
        this.setUserName(member.getUserName());
        this.setPassword(member.getPassword());
        this.setEmail(member.getEmail());
        this.setPhone(member.getPhone());
        this.setOtp(member.getOtp());
        this.setDuty(0);
    }

    public void borrowBook(Book book) {
        requestDAO.borrowRequest(this.getMemberID(), book);
    }

    public void returnBook(Book book) {
        requestDAO.returnBook(this.getMemberID(), book);
    }

    public List<Request> getRequests() {
        return requestDAO.getMemberBorrowHistory(this.getMemberID());
    }

    public List<Recommendation> getRecommendations() {
        return recommendationDAO.getRecommendationsForMember(this.getMemberID());
    }

    public void addPreferenceCategory(String preferenceCategory) {
        recommendationDAO.addRecommendation(new Recommendation.Builder()
                .memberID(this.getMemberID())
                .preferenceCategory(preferenceCategory)
                .build());
    }

    public void reviewBook(Book book, int rating, String comment) {
        Review existingReview = reviewDao.getReviewByBookAndMember(book.getBookID(), this.getMemberID());
        if (existingReview != null) {
            existingReview.setRating(rating);
            existingReview.setComment(comment);
            existingReview.setReviewTimestamp(java.time.LocalDate.now().toString());
            reviewDao.updateReview(existingReview);
        } else {
            Review newReview = new Review.Builder()
                    .bookID(book.getBookID())
                    .memberID(this.getMemberID())
                    .rating(rating)
                    .reviewTimestamp(java.time.LocalDate.now().toString())
                    .comment(comment)
                    .build();
            reviewDao.addReview(newReview);
        }
    }

    public List<Review> getAllReview() {
        return reviewDao.getReviewsByMember(this.getMemberID());
    }
}