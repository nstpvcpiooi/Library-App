package Library.backend.Request.DAO;

import Library.backend.Request.Model.Request;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DAO responsible purely for persisting and retrieving request records.
 */
public interface RequestDAO {
    void insert(Request request);

    void update(Request request);

    Request findById(int requestID);

    Request findLatestByMemberAndBook(int memberID, String bookID);


    List<Request> findAll();

    List<Request> findBorrowHistory(int memberID);

    boolean existsActiveRequest(int memberID, String bookID);

    List<Request> findOverdueCandidates(LocalDateTime timestamp);

    List<Request> findActiveRequestsByMember(int memberID);

    List<String> findDistinctBookIdsByMember(int memberID);
}
