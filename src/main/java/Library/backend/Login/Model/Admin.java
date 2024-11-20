package Library.backend.Login.Model;

import Library.backend.Login.DAO.MemberDAO;
import Library.backend.Login.DAO.MemberDAOImpl;
import Library.backend.Review.Dao.MysqlReviewDao;
import Library.backend.Review.Dao.ReviewDao;
import Library.backend.bookDao.BookDao;
import Library.backend.bookDao.MysqlBookDao;
import Library.backend.bookModel.Book;

import java.util.List;

public class Admin extends Member {
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
    public List<Member> DisplayMembers() {
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
}