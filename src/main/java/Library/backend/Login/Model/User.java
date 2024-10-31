package Library.backend.Login.Model;
import Library.backend.Request.Controller.RequestController;

public class User extends Member {

    RequestController requestController = new RequestController();
    public User(Member member) {
        this.setMemberID(member.getMemberID());
        this.setUserName(member.getUserName());
        this.setPassword(member.getPassword());
        this.setEmail(member.getEmail());
        this.setPhone(member.getPhone());
        this.setOtp(member.getOtp());
        this.setDuty(0);
    }
    public void borrowBook(int bookId) {
        // Implementation for borrowing a book
        requestController.BorrowRequest(this.getMemberID(), bookId);
    }
    public void returnBook(int bookId) {
        // Implementation for returning a book
        requestController.returnBook(this.getMemberID(), bookId);
    }

}