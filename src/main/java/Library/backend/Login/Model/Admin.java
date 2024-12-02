package Library.backend.Login.Model;

import Library.backend.Login.DAO.MemberDAO;
import Library.backend.Login.DAO.MemberDAOImpl;
import Library.backend.Request.DAO.RequestDAO;
import Library.backend.Request.DAO.RequestDAOImpl;
import Library.backend.Request.Model.Request;
import Library.backend.Review.Dao.MysqlReviewDao;
import Library.backend.Review.Dao.ReviewDao;
import Library.backend.bookDao.BookDao;
import Library.backend.bookDao.MysqlBookDao;
import Library.backend.bookModel.Book;

import java.time.LocalDateTime;
import java.util.List;

public class Admin extends Member {
    private RequestDAO requestDAO = RequestDAOImpl.getInstance();
    private BookDao bookDao = MysqlBookDao.getInstance();
    private MemberDAO memberDao = MemberDAOImpl.getInstance();
    private ReviewDao ReviewDao = MysqlReviewDao.getInstance();
    public Admin(Member member) {
        this.setMemberID(member.getMemberID());
        this.setUserName(member.getUserName());
        this.setPassword(member.getPassword());
        this.setEmail(member.getEmail());
        this.setPhone(member.getPhone());
        this.setOtp(member.getOtp());
        this.setDuty(1);// Initialize the BookDao
    }
    public List<Request> getAllRequests() {
        return requestDAO.getAllRequests();
    }

    public Request searchRequestsByRequestID(int requestID) {
        return requestDAO.getRequestById(requestID);
    }

    public List<Request> searchRequestsByMemberID(int memberID) {
        return requestDAO.getRequestsByMemberID(memberID);
    }

    public void addBook(Book book) {
        // Implementation for adding a book
        bookDao.addBook(book);
    }

    public void removeBook(String bookId) {
        // Implementation for removing a book
        bookDao.deleteBook(bookId);
    }
    public List<Member> searchMembers(String criteria, String value) {
        // Implementation for searching members
        return memberDao.searchMembers(criteria, value);
    }
    public List<User> DisplayMembers() {
        // Implementation for displaying all members
        return memberDao.DisplayMembers();
    }
    public void banMember(int memberID) {
        // Implementation for banning a member
        memberDao.deleteMemberById(memberID);
    }
    public void updateBook(Book book) {
        // Implementation for updating a book
        bookDao.updateBook(book);
    }
    public void deleteReview(int memberID, String bookID) {
        // Implementation for deleting a review
        ReviewDao.deleteReview(bookID, memberID);
    }
    public void approveIssueRequest(int requestID) {
        Request request = requestDAO.getRequestById(requestID);
        if (request != null && "pending issue".equals(request.getStatus())) {
            request.setStatus("approved issue");
            requestDAO.updateRequest(request);
        }
    }

    public void approveReturnRequest(int requestID) {
        Request request = requestDAO.getRequestById(requestID);
        if (request != null && "pending return".equals(request.getStatus())) {
            request.setStatus("approved return");
            request.setReturnDate(LocalDateTime.now());
            request.setOverdue(false);
            requestDAO.updateRequest(request);

            // Increment the book quantity
            bookDao.updateQuantity(request.getBookID(), 1);
        }
    }
}