package Library.backend.Request.Test;

import Library.backend.Request.DAO.RequestDAOImpl;
import Library.backend.Request.Model.Request;
import Library.backend.database.JDBCUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Sử dụng Phân hoạch tương đương kiểm thử đơn vị cho các unit trong RequestDAO.
public class RequestDAOTest {

    private static final String H2_URL = "jdbc:h2:mem:library_request;MODE=MySQL;DB_CLOSE_DELAY=-1";
    private static final String CREATE_MEMBERS_TABLE = """
            CREATE TABLE IF NOT EXISTS Members (
                memberID INT PRIMARY KEY
            )
            """;
    private static final String CREATE_REQUESTS_TABLE = """
            CREATE TABLE IF NOT EXISTS Requests (
                requestID INT AUTO_INCREMENT PRIMARY KEY,
                memberID INT,
                bookID VARCHAR(255),
                issueDate TIMESTAMP,
                dueDate TIMESTAMP,
                returnDate TIMESTAMP,
                status VARCHAR(255),
                overdue BOOLEAN,
                CONSTRAINT fk_member FOREIGN KEY (memberID) REFERENCES Members(memberID)
            )
            """;

    private static final String STATUS_HOLD = "Đang giữ";
    private static final String STATUS_BORROW = "Đang mượn";
    private static final String STATUS_RETURNED = "Đã trả";

    private RequestDAOImpl dao;

    @BeforeAll
    static void initDatabase() throws SQLException {
        try (Connection con = DriverManager.getConnection(H2_URL);
             Statement st = con.createStatement()) {
            st.execute(CREATE_MEMBERS_TABLE);
            st.execute(CREATE_REQUESTS_TABLE);
        }
        JDBCUtil.setConnectionSupplier(() -> {
            try {
                return DriverManager.getConnection(H2_URL);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @AfterAll
    static void resetConnection() {
        JDBCUtil.resetConnectionSupplier();
    }

    @BeforeEach
    void resetState() throws SQLException {
        dao = RequestDAOImpl.getInstance();
        try (Connection con = DriverManager.getConnection(H2_URL);
             Statement st = con.createStatement()) {
            st.execute("DELETE FROM Requests");
            st.execute("DELETE FROM Members");
        }
    }

    @Test
    // TC-01: insert request với memberID hợp lệ và dữ liệu đầy đủ => Expected output: lưu request thành công, gán requestID tự tăng và findById đọc lại được đúng bản ghi.
    @DisplayName("insert: hop le -> luu va gan requestID")
    void insert_persistsAndAssignsId() {
        Request req = buildRequest(1, "B1", STATUS_HOLD);
        ensureMemberExists(1);

        dao.insert(req);
        Request found = dao.findById(req.getRequestID());

        assertTrue(req.getRequestID() > 0);
        assertNotNull(found);
        assertEquals("B1", found.getBookID());
        assertEquals(STATUS_HOLD, found.getStatus());
    }

    @Test
    // TC-02: truyền request = null vào insert => Expected output: insert ném NullPointerException, không ghi dữ liệu xuống DB.
    @DisplayName("insert: request null -> NullPointerException")
    void insert_null_throwsNullPointer() {
        assertThrows(NullPointerException.class, () -> dao.insert(null));
    }

    @Test
    // TC-03: insert request với memberID không tồn tại (vi phạm khóa ngoại) => Expected output: insert không gán requestID, không thêm bản ghi mới vào bảng Requests.
    @DisplayName("insert: memberID khong ton tai -> khong luu")
    void insert_missingMember_notPersisted() throws SQLException {
        Request req = buildRequest(999, "B99", STATUS_HOLD);

        dao.insert(req); // SQLException bị nuốt; không gán ID

        assertEquals(0, req.getRequestID());
        try (Connection con = DriverManager.getConnection(H2_URL);
             Statement st = con.createStatement();
             var rs = st.executeQuery("SELECT COUNT(*) FROM Requests")) {
            rs.next();
            assertEquals(0, rs.getInt(1));
        }
    }

    @Test
    // TC-04: update một request đã tồn tại trong DB => Expected output: các trường status/overdue/issueDate/dueDate của bản ghi tương ứng được cập nhật đúng.
    @DisplayName("update: request ton tai -> cap nhat truong")
    void update_updatesExistingRow() {
        Request req = insertDirect(1, "B1", STATUS_HOLD);
        req.setStatus(STATUS_BORROW);
        req.setOverdue(true);
        LocalDateTime now = LocalDateTime.now();
        req.setIssueDate(now);
        req.setDueDate(now.plusDays(7));

        dao.update(req);
        Request stored = dao.findById(req.getRequestID());

        assertEquals(STATUS_BORROW, stored.getStatus());
        assertTrue(stored.isOverdue());
        assertEquals(req.getIssueDate().withNano(0), stored.getIssueDate().withNano(0));
        assertEquals(req.getDueDate().withNano(0), stored.getDueDate().withNano(0));
    }

    @Test
    // TC-05: update với requestID không tồn tại trong DB => Expected output: không tạo thêm bản ghi mới, số lượng bản ghi trong bảng Requests không đổi.
    @DisplayName("update: requestID khong ton tai -> khong tao moi")
    void update_missingDoesNothing() throws SQLException {
        Request ghost = buildRequest(2, "B2", STATUS_BORROW);
        ghost.setRequestID(999);
        ensureMemberExists(2);

        dao.update(ghost);

        try (Connection con = DriverManager.getConnection(H2_URL);
             Statement st = con.createStatement();
             var rs = st.executeQuery("SELECT COUNT(*) FROM Requests")) {
            rs.next();
            assertEquals(0, rs.getInt(1));
        }
    }
    @Test
    // TC-06: truyền request = null vào update => Expected output: update ném NullPointerException.
    @DisplayName("update: request null -> NullPointerException")
    void update_nullRequest_throwsException() {
        assertThrows(NullPointerException.class, () -> dao.update(null));
    }
    @Test
    // TC-07: findById với requestID tồn tại => Expected output: trả về đối tượng Request có cùng requestID và dữ liệu khớp với bản ghi trong DB.
    @DisplayName("findById: ton tai -> tra ve Request dung")
    void findById_existing_returnsRequest() {
        Request req = insertDirect(1, "B1", STATUS_BORROW);

        Request found = dao.findById(req.getRequestID());

        assertNotNull(found);
        assertEquals(req.getRequestID(), found.getRequestID());
    }

    @Test
    // TC-08: findById với requestID không tồn tại => Expected output: trả null.
    @DisplayName("findById: khong ton tai -> tra null")
    void findById_missing_returnsNull() {
        assertNull(dao.findById(12345));
    }
    @Test
    // TC-09: findById với requestID âm => Expected output: trả null.
    @DisplayName("findById: negative ID -> returns null")
    void findById_negative_returnsNull() {
        assertNull(dao.findById(-10));
    }
    @Test
    // TC-10: có nhiều request cho cùng (memberID, bookID) với các requestID khác nhau => Expected output: findLatestByMemberAndBook trả về request có requestID lớn nhất.
    @DisplayName("findLatestByMemberAndBook: tra requestID lon nhat")
    void findLatestByMemberAndBook_returnsLatest() {
        insertDirect(1, "B1", STATUS_HOLD);
        Request latest = insertDirect(1, "B1", STATUS_BORROW); // ID lớn hơn

        Request found = dao.findLatestByMemberAndBook(1, "B1");

        assertNotNull(found);
        assertEquals(latest.getRequestID(), found.getRequestID());
    }

    @Test
    // TC-11: không có request nào cho (memberID, bookID) truyền vào => Expected output: findLatestByMemberAndBook trả null.
    @DisplayName("findLatestByMemberAndBook: khong co du lieu -> null")
    void findLatestByMemberAndBook_noData_returnsNull() {
        assertNull(dao.findLatestByMemberAndBook(5, "B1"));
    }
    @Test
    // TC-12: member có request nhưng bookID khác tham số => Expected output: findLatestByMemberAndBook trả null.
    @DisplayName("findLatestByMemberAndBook: member hop le nhung khong co bookID -> null")
    void findLatestByMemberAndBook_differentBook_returnsNull() {
        insertDirect(4, "B10", STATUS_HOLD);

        assertNull(dao.findLatestByMemberAndBook(4, "B11"));
    }
    @Test
    // TC-13: trong bảng có nhiều request => Expected output: findAll trả về danh sách chứa đầy đủ các bản ghi (đúng số lượng).
    @DisplayName("findAll: tra ve day du request")
    void findAll_returnsAll() {
        insertDirect(1, "B1", STATUS_HOLD);
        insertDirect(2, "B2", STATUS_RETURNED);

        List<Request> all = dao.findAll();

        assertEquals(2, all.size());
    }
    @Test
    // TC-14: bảng Requests rỗng => Expected output: findAll trả list rỗng.
    @DisplayName("findAll: empty table -> returns empty list")
    void findAll_empty_returnsEmptyList() {
        assertTrue(dao.findAll().isEmpty());
    }

    @Test
    // TC-15: chỉ có một bản ghi => Expected output: findAll trả list chứa đúng một Request với requestID đã lưu.
    @DisplayName("findAll: one row -> returns single element list")
    void findAll_singleRow_returnsSingle() {
        Request req = insertDirect(5, "B20", STATUS_BORROW);

        List<Request> all = dao.findAll();

        assertEquals(1, all.size());
        assertEquals(req.getRequestID(), all.get(0).getRequestID());
    }
    @Test
    // TC-16: bảng có request của nhiều member khác nhau => Expected output: findBorrowHistory chỉ trả về các request có memberID khớp với tham số truyền vào.
    @DisplayName("findBorrowHistory: loc theo memberID")
    void findBorrowHistory_filtersByMember() {
        insertDirect(1, "B1", STATUS_HOLD);
        insertDirect(2, "B2", STATUS_BORROW);

        List<Request> history = dao.findBorrowHistory(1);

        assertEquals(1, history.size());
        assertEquals(1, history.get(0).getMemberID());
    }

    @Test
    // TC-17: không có request nào của memberID truyền vào => Expected output: findBorrowHistory trả về danh sách rỗng.
    @DisplayName("findBorrowHistory: khong co du lieu -> list rong")
    void findBorrowHistory_empty_returnsEmpty() {
        assertTrue(dao.findBorrowHistory(99).isEmpty());
    }
    @Test
    // TC-18: memberID âm khi lấy lịch sử => Expected output: findBorrowHistory trả list rỗng.
    @DisplayName("findBorrowHistory: negative memberID -> returns empty list")
    void findBorrowHistory_negativeMember_returnsEmpty() {
        assertTrue(dao.findBorrowHistory(-1).isEmpty());
    }
    @Test
    // TC-19: tồn tại request với trạng thái 'Đang giữ' hoặc 'Đang mượn' cho (memberID, bookID) => Expected output: existsActiveRequest trả true; với trạng thái khác (ví dụ 'Đã trả') trả false.
    @DisplayName("existsActiveRequest: status giu/muon -> true, khac -> false")
    void existsActiveRequest_checksActiveStatuses() {
        insertDirect(1, "B1", STATUS_HOLD);
        assertTrue(dao.existsActiveRequest(1, "B1"));

        insertDirect(1, "B2", STATUS_RETURNED);
        assertFalse(dao.existsActiveRequest(1, "B2"));
    }
    @Test
    // TC-20: tồn tại request nhưng bảng không có status active => Expected output: existsActiveRequest trả false.
    @DisplayName("existsActiveRequest: chi co status khac giu/muon -> false")
    void existsActiveRequest_onlyInactiveStatuses_returnsFalse() {
        insertDirect(6, "B30", STATUS_RETURNED);

        assertFalse(dao.existsActiveRequest(6, "B30"));
    }

    @Test
    // TC-21: không có bản ghi nào cho member/book => Expected output: existsActiveRequest trả false.
    @DisplayName("existsActiveRequest: khong co request -> false")
    void existsActiveRequest_noRows_returnsFalse() {
        assertFalse(dao.existsActiveRequest(100, "NONE"));
    }
    @Test
    // TC-22: có request quá hạn (dueDate < thời điểm truyền vào) và còn ở trạng thái active => Expected output: findOverdueCandidates trả về các request đó, bỏ qua request không active.
    @DisplayName("findOverdueCandidates: dueDate < now va active -> duoc tra ve")
    void findOverdueCandidates_filtersByDueDateAndStatus() {
        LocalDateTime past = LocalDateTime.now().minusDays(2);
        insertDirect(1, "B1", STATUS_BORROW, past, past.minusDays(1));
        insertDirect(2, "B2", STATUS_RETURNED, past, past.minusDays(1)); // không active

        List<Request> candidates = dao.findOverdueCandidates(LocalDateTime.now());

        assertEquals(1, candidates.size());
        assertEquals("B1", candidates.get(0).getBookID());
    }
    @Test
    // TC-23: request ở trạng thái 'Đang giữ' quá hạn => Expected output: findOverdueCandidates vẫn trả về request này.
    @DisplayName("findOverdueCandidates: status 'Đang giữ' qua han -> duoc tra ve")
    void findOverdueCandidates_holdStatusStillReturned() {
        LocalDateTime past = LocalDateTime.now().minusDays(3);
        insertDirect(7, "B40", STATUS_HOLD, past, past.minusDays(1));

        List<Request> candidates = dao.findOverdueCandidates(LocalDateTime.now());

        assertEquals(1, candidates.size());
        assertEquals("B40", candidates.get(0).getBookID());
    }

    @Test
    // TC-24: dueDate vẫn còn hạn => Expected output: findOverdueCandidates trả list rỗng.
    @DisplayName("findOverdueCandidates: dueDate >= now -> list rong")
    void findOverdueCandidates_notYetDue_returnsEmpty() {
        LocalDateTime future = LocalDateTime.now().plusDays(1);
        insertDirect(8, "B41", STATUS_BORROW, LocalDateTime.now(), future);

        assertTrue(dao.findOverdueCandidates(LocalDateTime.now()).isEmpty());
    }
    @Test
    // TC-25: bảng có nhiều request với các trạng thái khác nhau và nhiều member => Expected output: findActiveRequestsByMember chỉ trả về các request của memberID tương ứng với status 'Đang giữ' hoặc 'Đang mượn'.
    @DisplayName("findActiveRequestsByMember: chi tra status giu/muon cua member")
    void findActiveRequestsByMember_filters() {
        insertDirect(1, "B1", STATUS_HOLD);
        insertDirect(1, "B2", STATUS_BORROW);
        insertDirect(1, "B3", STATUS_RETURNED);
        insertDirect(2, "B4", STATUS_BORROW); // member khác

        List<Request> actives = dao.findActiveRequestsByMember(1);

        assertEquals(2, actives.size());
        assertTrue(actives.stream().allMatch(r -> r.getMemberID() == 1));
        assertTrue(actives.stream().allMatch(r -> STATUS_HOLD.equals(r.getStatus()) || STATUS_BORROW.equals(r.getStatus())));
    }
    @Test
    // TC-26: member không có request nào => Expected output: findActiveRequestsByMember trả list rỗng.
    @DisplayName("findActiveRequestsByMember: member khong co request -> list rong")
    void findActiveRequestsByMember_noRequests_returnsEmpty() {
        assertTrue(dao.findActiveRequestsByMember(200).isEmpty());
    }

    @Test
    // TC-27: member chỉ có request ở trạng thái không active => Expected output: findActiveRequestsByMember trả list rỗng.
    @DisplayName("findActiveRequestsByMember: chi co request khong active -> list rong")
    void findActiveRequestsByMember_onlyInactive_returnsEmpty() {
        insertDirect(9, "B50", STATUS_RETURNED);

        assertTrue(dao.findActiveRequestsByMember(9).isEmpty());
    }
    @Test
    // TC-28: một member có nhiều request cho cùng một bookID và cho các bookID khác nhau => Expected output: findDistinctBookIdsByMember trả về danh sách các bookID không trùng lặp của member đó.
    @DisplayName("findDistinctBookIdsByMember: loai bo trung lap bookID")
    void findDistinctBookIdsByMember_dedup() {
        insertDirect(3, "B1", STATUS_HOLD);
        insertDirect(3, "B1", STATUS_BORROW);
        insertDirect(3, "B2", STATUS_RETURNED);

        List<String> ids = dao.findDistinctBookIdsByMember(3);

        assertEquals(2, ids.size());
        assertTrue(ids.contains("B1"));
        assertTrue(ids.contains("B2"));
    }
    @Test
    // TC-29: member không có request nào => Expected output: findDistinctBookIdsByMember trả list rỗng.
    @DisplayName("findDistinctBookIdsByMember: member khong co request -> list rong")
    void findDistinctBookIdsByMember_noRequests_returnsEmpty() {
        assertTrue(dao.findDistinctBookIdsByMember(300).isEmpty());
    }

    @Test
    // TC-30: member chỉ có nhiều request cho đúng 1 bookID => Expected output: findDistinctBookIdsByMember trả list gồm một phần tử duy nhất.
    @DisplayName("findDistinctBookIdsByMember: nhieu request cung bookID -> chi 1 ID")
    void findDistinctBookIdsByMember_singleBookMultipleRequests_returnsSingleId() {
        insertDirect(4, "ONLY", STATUS_HOLD);
        insertDirect(4, "ONLY", STATUS_BORROW);

        List<String> ids = dao.findDistinctBookIdsByMember(4);

        assertEquals(1, ids.size());
        assertEquals("ONLY", ids.get(0));
    }


















    private static Request buildRequest(int memberId, String bookId, String status) {
        LocalDateTime now = LocalDateTime.now();
        return new Request(memberId, bookId, now, now.plusDays(1), null, status, false);
    }

    private static void ensureMemberExists(int memberId) {
        String sql = "MERGE INTO Members (memberID) KEY(memberID) VALUES (?)";
        try (Connection con = DriverManager.getConnection(H2_URL);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ensure member failed", e);
        }
    }

    private static Request insertDirect(int memberId, String bookId, String status) {
        return insertDirect(memberId, bookId, status, null, null);
    }

    private static Request insertDirect(int memberId, String bookId, String status, LocalDateTime issueDate, LocalDateTime dueDate) {
        ensureMemberExists(memberId);
        String sql = "INSERT INTO Requests (memberID, bookID, issueDate, dueDate, returnDate, status, overdue) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Request req = new Request();
        try (Connection con = DriverManager.getConnection(H2_URL);
             PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime issue = issueDate != null ? issueDate : now;
            LocalDateTime due = dueDate != null ? dueDate : now.plusDays(1);
            ps.setInt(1, memberId);
            ps.setString(2, bookId);
            ps.setObject(3, issue);
            ps.setObject(4, due);
            ps.setObject(5, due.plusDays(1));
            ps.setString(6, status);
            ps.setBoolean(7, false);
            ps.executeUpdate();
            try (var keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    req.setRequestID(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Insert request failed", e);
        }
        req.setMemberID(memberId);
        req.setBookID(bookId);
        req.setIssueDate(issueDate);
        req.setDueDate(dueDate);
        req.setStatus(status);
        return req;
    }
}
