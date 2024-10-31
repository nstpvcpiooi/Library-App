package Library.backend.Request.Model;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Request {
    private int requestID;
    private int memberID;
    private int bookID;
    private LocalDateTime borrowDate;

    public Request(int memberID, int bookID, LocalDateTime borrowDate, LocalDateTime returnDate) {
        this.memberID = memberID;
        this.bookID = bookID;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
    }
    public Request(){

    }
    public int getRequestID() {
        return requestID;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public int getMemberID() {
        return memberID;
    }

    public void setMemberID(int memberID) {
        this.memberID = memberID;
    }

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public LocalDateTime getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDateTime borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    private LocalDateTime returnDate;
}
