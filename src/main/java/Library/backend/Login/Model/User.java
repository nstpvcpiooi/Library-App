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
import Library.backend.bookDao.BookDao;
import Library.backend.bookDao.MysqlBookDao;
import Library.backend.bookModel.Book;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class User extends Member {
    private List<Recommendation> recommendations;
    private BookDao bookDao = MysqlBookDao.getInstance();
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
    public List<Request> getAllRequests() {
        return requestDAO.getRequestsByMemberID(this.getMemberID());
    }
    public List<Book> getBorrowedBooks() {
        List<Request> requests = requestDAO.getMemberBorrowHistory(this.getMemberID());
        List<Book> borrowedBooks = new ArrayList<>();
        for (Request request : requests) {
            if ("approved issue".equals(request.getStatus())) {
                Book book = Book.getBookById(request.getBookID());
                borrowedBooks.add(book);
            }
        }
        return borrowedBooks;
    }
    public void createIssueRequest(String bookID) {

        List<Request> requests = requestDAO.getMemberBorrowHistory(this.getMemberID());
        for (Request request : requests) {
            if (request.isOverdue() && "approved issue".equals(request.getStatus())) {
                System.out.println("You have an overdue book. Please return it before issuing a new one.");
                return;
            }
        }
        Book book = Book.getBookById(bookID);
        if(book.getQuantity()>0) {
            LocalDateTime now = LocalDateTime.now();
            Request request = new Request(this.getMemberID(), bookID, now, now.plusDays(7), null, "pending issue", false);
            requestDAO.createBorrowRequest(request);
            requestDAO.updateRequest(request);

            // Decrement the book quantity
            bookDao.updateQuantity(request.getBookID(), -1);
        }
        else {
            System.out.println("Book is out of stock");
        }
    }

    public void createReturnRequest(String bookID) {
        List<Request> requests = requestDAO.getMemberBorrowHistory(this.getMemberID());
        for (Request request : requests) {
            if (request.getBookID().equals(bookID) && ("approved issue".equals(request.getStatus()) || "pending issue".equals(request.getStatus()))) {
                request.setStatus("pending return");
                requestDAO.updateRequest(request);
                break;
            }
        }
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

    public void reviewBook(String bookID, int rating, String comment) {
        Review existingReview = reviewDao.getReviewByBookAndMember(bookID, this.getMemberID());
        if (existingReview != null) {
            existingReview.setRating(rating);
            existingReview.setComment(comment);
            existingReview.setReviewTimestamp(java.time.LocalDate.now().toString());
            reviewDao.updateReview(existingReview);
        } else {
            Review newReview = new Review.Builder()
                    .bookID(bookID)
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