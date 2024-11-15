package Library.backend.Request.DAO;


import Library.backend.Request.Model.Request;
import java.time.LocalDateTime;
import java.util.List;

public interface RequestDAO {

    void createBorrowRequest(Request request);
    List<Request> getMemberBorrowHistory(int memberID);
    void updateReturnTime(int requestID, LocalDateTime returnTime);
    void borrowRequest(int memberID, int bookID);
    void returnBook(int memberID, int bookID);
    void updateBorrowTime(int requestID, LocalDateTime borrowTime);
}