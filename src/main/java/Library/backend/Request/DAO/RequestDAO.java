package Library.backend.Request.DAO;


import Library.backend.Request.Model.Request;
import Library.backend.bookModel.Book;

import java.time.LocalDateTime;
import java.util.List;

public interface RequestDAO {

    void createBorrowRequest(Request request);
    List<Request> getMemberBorrowHistory(int memberID);
    void updateReturnTime(int requestID, LocalDateTime returnTime);
    void updateBorrowTime(int requestID, LocalDateTime borrowTime);
    void borrowRequest(int memberID, Book book);
    void returnBook(int memberID, Book book);
}