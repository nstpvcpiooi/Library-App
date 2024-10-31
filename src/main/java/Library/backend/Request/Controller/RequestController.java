package Library.backend.Request.Controller;

import Library.backend.Request.DAO.RequestDAO;
import Library.backend.Request.DAO.RequestDAOImpl;
import Library.backend.Request.Model.Request;
import java.time.LocalDateTime;
import java.util.List;
public class RequestController {
    private RequestDAO requestDAO;
    public RequestController() {
        this.requestDAO = (RequestDAO) RequestDAOImpl.getInstance();
    }
    public void BorrowRequest(int memberID, int bookID) {
        LocalDateTime borrowDate = LocalDateTime.now();
        LocalDateTime returnDate = borrowDate.plusDays(7);
        requestDAO.createBorrowRequest(new Request(memberID, bookID, borrowDate, returnDate));
    }
    public List<Request> getMemberBorrowHistory(int memberID) {
        return requestDAO.getMemberBorrowHistory(memberID);
    }
    public void returnBook(int memberID, int bookID) {
        List<Request> requests = requestDAO.getMemberBorrowHistory(memberID);
        for (Request request : requests) {
            if (request.getBookID() == bookID && request.getReturnDate() == null) {
                requestDAO.updateReturnTime(request.getRequestID(), LocalDateTime.now());
            }
        }
    }


}
