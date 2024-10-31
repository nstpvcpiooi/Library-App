package Library.backend.Request.DAO;



import Library.backend.Request.Model.Request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface RequestDAO {
    List<Request> getMemberBorrowHistory(int memberID);
    void updateBorrowTime(int requestID, LocalDateTime borrowTime);
    void updateReturnTime(int requestID, LocalDateTime returnTime);
    void createBorrowRequest(Request request);
}