package Library.backend.Request.Test;

import Library.backend.Book.DAO.BookDAO;
import Library.backend.Book.Model.Book;
import Library.backend.Book.Service.BookService;
import Library.backend.Request.DAO.RequestDAO;
import Library.backend.Request.Model.Request;
import Library.backend.Request.service.RequestService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

// Sử dụng Phân hoạch tương đương kiểm thử đơn vị cho các unit trong RequestService.
public class RequestServiceTest {

    private static final String STATUS_HOLD = "Đang giữ";
    private static final String STATUS_BORROW = "Đang mượn";
    private static final String STATUS_CANCELLED = "Đã hủy";
    private static final String STATUS_RETURNED = "Đã trả";

    private static class RecordingBookDao implements BookDAO {
        AtomicReference<String> lastBookId = new AtomicReference<>();
        AtomicInteger quantityDelta = new AtomicInteger();
        Book defaultBook = new Book("DEFAULT", "T", "A", 2020, "C", "isbn", "", 1);
        java.util.Map<String, Book> booksById = new java.util.HashMap<>();

        @Override public void addBook(Book book) { throw new UnsupportedOperationException(); }
        @Override public void deleteBook(String bookID) { throw new UnsupportedOperationException(); }
        @Override public void updateBook(Book book) { throw new UnsupportedOperationException(); }
        @Override public List<Book> searchBooksValue(String value) { throw new UnsupportedOperationException(); }
        @Override public void updateQuantity(String bookID, int delta) { lastBookId.set(bookID); quantityDelta.set(delta); }
        @Override
        public Book findBookById(String bookID) {
            lastBookId.set(bookID);
            if (booksById.containsKey(bookID)) return booksById.get(bookID);
            return defaultBook;
        }
        @Override public Book findBookByIsbn(String isbn) { throw new UnsupportedOperationException(); }
        @Override public Book fetchBookInfoFromAPI(String isbn) { throw new UnsupportedOperationException(); }
        @Override public String fetchBookDescriptionFromAPI(Book book) { throw new UnsupportedOperationException(); }
        @Override public String generateQrCodeForBook(String isbn) { throw new UnsupportedOperationException(); }

    }

    private static class RecordingRequestDao implements RequestDAO {
        AtomicReference<Request> lastInserted = new AtomicReference<>();
        AtomicReference<Request> lastUpdated = new AtomicReference<>();
        AtomicReference<Integer> lastId = new AtomicReference<>();
        AtomicReference<String> lastBookId = new AtomicReference<>();
        AtomicReference<Integer> lastMemberId = new AtomicReference<>();
        List<Request> activeRequests = List.of();
        List<Request> borrowHistory = List.of();
        List<Request> overdueCandidates = List.of();
        List<Request> allRequests = List.of();
        List<String> distinctBookIds = List.of();
        Request findByIdResult;
        Request latestResult;
        boolean hasActiveRequest;

        @Override public void insert(Request request) { lastInserted.set(request); }
        @Override public void update(Request request) { lastUpdated.set(request); }
        @Override public Request findById(int requestID) { lastId.set(requestID); return findByIdResult; }
        @Override public Request findLatestByMemberAndBook(int memberID, String bookID) { lastMemberId.set(memberID); lastBookId.set(bookID); return latestResult; }
        @Override public List<Request> findAll() { return allRequests; }
        @Override public List<Request> findBorrowHistory(int memberID) { lastMemberId.set(memberID); return borrowHistory; }
        @Override public boolean existsActiveRequest(int memberID, String bookID) { lastMemberId.set(memberID); lastBookId.set(bookID); return hasActiveRequest; }
        @Override public List<Request> findOverdueCandidates(LocalDateTime timestamp) { return overdueCandidates; }
        @Override public List<Request> findActiveRequestsByMember(int memberID) { lastMemberId.set(memberID); return activeRequests; }
        @Override public List<String> findDistinctBookIdsByMember(int memberID) { lastMemberId.set(memberID); return distinctBookIds; }
    }

    private static RequestService newService(RecordingRequestDao dao, RecordingBookDao bookDao) {
        return RequestService.create(dao, BookService.create(bookDao));
    }

    private static Request requestWithStatus(String status) {
        Request r = new Request();
        r.setStatus(status);
        r.setBookID("B1");
        r.setMemberID(1);
        return r;
    }

    @Test
    // TC-01: member đã có request đang active cho cùng bookID => Expected output: placeHold ném IllegalStateException, không tạo thêm request mới và không thay đổi tồn kho.
    @DisplayName("placeHold: da co active request -> IllegalStateException")
    void placeHold_existingActive_throws() {
        RecordingRequestDao dao = new RecordingRequestDao();
        dao.hasActiveRequest = true;
        RequestService service = newService(dao, new RecordingBookDao());

        assertThrows(IllegalStateException.class, () -> service.placeHold(1, "B1"));
    }

    @Test
    // TC-02: số request đang active của member đã đạt giới hạn tối đa (ví dụ 5 cuốn) => Expected output: placeHold ném IllegalStateException, không gọi insert và không trừ tồn kho.
    @DisplayName("placeHold: vuot gioi han muon -> IllegalStateException")
    void placeHold_reachBorrowLimit_throws() {
        RecordingRequestDao dao = new RecordingRequestDao();
        List<Request> actives = new ArrayList<>();
        for (int i = 0; i < 5; i++) actives.add(new Request());
        dao.activeRequests = actives;
        RequestService service = newService(dao, new RecordingBookDao());

        assertThrows(IllegalStateException.class, () -> service.placeHold(1, "B1"));
    }

    @Test
    // TC-03: bookID không tìm thấy (BookDAO trả về null) khi đặt giữ sách => Expected output: placeHold ném IllegalStateException, không tạo request và không thay đổi tồn kho.
    @DisplayName("placeHold: book khong ton tai -> IllegalStateException")
    void placeHold_missingBook_throws() {
        RecordingRequestDao dao = new RecordingRequestDao();
        RecordingBookDao bookDao = new RecordingBookDao(); // bookResult null -> defaultBook
        bookDao.defaultBook = null; // để mô phỏng không tìm thấy
        RequestService service = newService(dao, bookDao);

        assertThrows(IllegalStateException.class, () -> service.placeHold(1, "B1"));
    }

    @Test
    // TC-04: tìm được book nhưng quantity = 0 (hết sách) => Expected output: placeHold ném IllegalStateException, không tạo request và không thay đổi tồn kho.
    @DisplayName("placeHold: book het hang -> IllegalStateException")
    void placeHold_outOfStock_throws() {
        RecordingRequestDao dao = new RecordingRequestDao();
        RecordingBookDao bookDao = new RecordingBookDao();
        bookDao.defaultBook = new Book("B1", "T", "A", 2020, "C", "isbn", "", 0);
        RequestService service = newService(dao, bookDao);

        assertThrows(IllegalStateException.class, () -> service.placeHold(1, "B1"));
    }

    @Test
    // TC-05: member chưa có request active cho book, số lượng mượn chưa đạt giới hạn, book còn hàng => Expected output: placeHold tạo 1 request trạng thái 'Đang giữ', gọi DAO.insert và trừ tồn kho (delta = -1).
    @DisplayName("placeHold: hop le -> insert va giam ton kho")
    void placeHold_valid_insertsAndDecrements() {
        RecordingRequestDao dao = new RecordingRequestDao();
        RecordingBookDao bookDao = new RecordingBookDao();
        bookDao.defaultBook = new Book("B1", "T", "A", 2020, "C", "isbn", "", 2);
        RequestService service = newService(dao, bookDao);

        Request req = service.placeHold(1, "B1");

        assertNotNull(dao.lastInserted.get());
        assertEquals(STATUS_HOLD, dao.lastInserted.get().getStatus());
        assertEquals("B1", dao.lastInserted.get().getBookID());
        assertEquals(-1, bookDao.quantityDelta.get());
        assertEquals(req, dao.lastInserted.get());
    }

    @Test
    // TC-06: request mới nhất cho member+book không ở trạng thái 'Đang giữ' (ví dụ 'Đang mượn') => Expected output: cancelHold không gọi DAO.update, không thay đổi tồn kho (noop).
    @DisplayName("cancelHold: khong co request dang giu -> bo qua")
    void cancelHold_noActiveRequest_noop() {
        RecordingRequestDao dao = new RecordingRequestDao();
        dao.latestResult = requestWithStatus(STATUS_BORROW); // không phải giữ
        RecordingBookDao bookDao = new RecordingBookDao();
        RequestService service = newService(dao, bookDao);

        service.cancelHold(1, "B1");

        assertNull(dao.lastUpdated.get());
        assertEquals(0, bookDao.quantityDelta.get());
    }

    @Test
    // TC-07: request mới nhất cho member+book đang ở trạng thái 'Đang giữ' => Expected output: cancelHold cập nhật status thành 'Đã hủy', set returnDate và cộng lại 1 đơn vị vào tồn kho.
    @DisplayName("cancelHold: dang giu -> huy va tra ton kho")
    void cancelHold_hold_updatesAndRestoresStock() {
        RecordingRequestDao dao = new RecordingRequestDao();
        RecordingBookDao bookDao = new RecordingBookDao();
        // Tạo request đang giữ bằng chính service để dùng đúng chuỗi trạng thái.
        RequestService service = newService(dao, bookDao);
        Request hold = service.placeHold(1, "B1");
        dao.latestResult = hold;
        dao.lastUpdated.set(null);
        bookDao.quantityDelta.set(0);

        service.cancelHold(1, "B1");

        Request updated = dao.lastUpdated.get();
        assertNotNull(updated, "DAO update should be called");
        assertEquals(STATUS_CANCELLED, updated.getStatus());
        assertNotNull(updated.getReturnDate());
        assertEquals(1, bookDao.quantityDelta.get());
    }
    @Test
    // TC-08: requestDAO.findLatestByMemberAndBook trả null => Expected output: cancelHold không cập nhật gì và không thay đổi tồn kho.
    @DisplayName("cancelHold: latest request null -> no action")
    void cancelHold_noRequestFound_noop() {
        RecordingRequestDao dao = new RecordingRequestDao();
        dao.latestResult = null;
        RecordingBookDao bookDao = new RecordingBookDao();
        RequestService service = newService(dao, bookDao);

        service.cancelHold(1, "B1");

        assertNull(dao.lastUpdated.get());
        assertEquals(0, bookDao.quantityDelta.get());
    }
    @Test
    // TC-09: approveIssue với requestID không tồn tại (DAO.findById trả null) => Expected output: approveIssue ném IllegalArgumentException, không gọi update/không đổi tồn kho.
    @DisplayName("approveIssue: khong tim thay -> IllegalArgumentException")
    void approveIssue_missing_throws() {
        RecordingRequestDao dao = new RecordingRequestDao(); // findByIdResult null
        RecordingBookDao bookDao = new RecordingBookDao();
        RequestService service = newService(dao, bookDao);

        assertThrows(IllegalArgumentException.class, () -> service.approveIssue(1));
    }

    @Test
    // TC-10: request tìm được nhưng trạng thái không phải 'Đang giữ' (ví dụ 'Đang mượn') => Expected output: approveIssue không thay đổi gì, không gọi update lần 2, không đổi tồn kho.
    @DisplayName("approveIssue: trang thai khong phai dang giu -> bo qua")
    void approveIssue_wrongStatus_noop() {
        RecordingRequestDao dao = new RecordingRequestDao();
        Request r = requestWithStatus(STATUS_BORROW);
        r.setRequestID(1);
        dao.findByIdResult = r;
        RecordingBookDao bookDao = new RecordingBookDao();
        RequestService service = newService(dao, bookDao);

        service.approveIssue(1);

        assertNull(dao.lastUpdated.get());
        assertNull(dao.lastId.get()); // không gọi findById lần 2
    }

    @Test
    // TC-11: request ở trạng thái 'Đang giữ' => Expected output: approveIssue cập nhật status sang 'Đang mượn', set issueDate/dueDate, đảm bảo isOverdue = false, không đổi tồn kho (đã trừ khi giữ).
    @DisplayName("approveIssue: dang giu -> cap nhat sang dang muon")
    void approveIssue_hold_updates() {
        RecordingRequestDao dao = new RecordingRequestDao();
        RecordingBookDao bookDao = new RecordingBookDao();
        RequestService service = newService(dao, bookDao);

        Request hold = service.placeHold(1, "B1"); // sinh request với status đúng
        hold.setRequestID(1);
        dao.findByIdResult = hold;
        dao.lastUpdated.set(null);

        service.approveIssue(1);

        Request updated = dao.lastUpdated.get();
        assertNotNull(updated, "DAO update should be called");
        assertNotNull(updated.getIssueDate());
        assertNotNull(updated.getDueDate());
        assertEquals(STATUS_BORROW, updated.getStatus());
        assertFalse(updated.isOverdue());
    }

    @Test
    // TC-12: approveReturn với requestID không tồn tại (DAO.findById trả null) => Expected output: approveReturn ném IllegalArgumentException, không gọi update/không đổi tồn kho.
    @DisplayName("approveReturn: khong tim thay -> IllegalArgumentException")
    void approveReturn_missing_throws() {
        RecordingRequestDao dao = new RecordingRequestDao();
        RecordingBookDao bookDao = new RecordingBookDao();
        RequestService service = newService(dao, bookDao);

        assertThrows(IllegalArgumentException.class, () -> service.approveReturn(1));
    }

    @Test
    // TC-13: request tìm được nhưng trạng thái không phải 'Đang mượn' (ví dụ 'Đang giữ') => Expected output: approveReturn không cập nhật request và không thay đổi tồn kho.
    @DisplayName("approveReturn: khong phai dang muon -> bo qua")
    void approveReturn_wrongStatus_noop() {
        RecordingRequestDao dao = new RecordingRequestDao();
        Request r = requestWithStatus(STATUS_HOLD);
        r.setRequestID(1);
        dao.findByIdResult = r;
        RecordingBookDao bookDao = new RecordingBookDao();
        RequestService service = newService(dao, bookDao);

        service.approveReturn(1);

        assertNull(dao.lastUpdated.get());
        assertEquals(0, bookDao.quantityDelta.get());
        assertNull(dao.lastId.get());
    }

    @Test
    // TC-14: request ở trạng thái 'Đang mượn' => Expected output: approveReturn cập nhật status thành 'Đã trả', set returnDate, đặt isOverdue=false và tăng tồn kho thêm 1.
    @DisplayName("approveReturn: dang muon -> cap nhat va tra ton kho")
    void approveReturn_borrow_updates() {
        RecordingRequestDao dao = new RecordingRequestDao();
        Request r = requestWithStatus(STATUS_BORROW);
        r.setRequestID(1);
        dao.findByIdResult = r;
        RecordingBookDao bookDao = new RecordingBookDao();
        RequestService service = newService(dao, bookDao);

        service.approveReturn(1);

        Request updated = dao.lastUpdated.get();
        assertNotNull(updated, "DAO update should be called");
        assertEquals(STATUS_RETURNED, updated.getStatus());
        assertNotNull(updated.getReturnDate());
        assertFalse(updated.isOverdue());
        assertEquals(1, bookDao.quantityDelta.get());
    }

    @Test
    // TC-15: trong danh sách overdueCandidates có request trạng thái 'Đang mượn' => Expected output: processOverdueRequests đánh dấu isOverdue=true cho các request đó và gọi update, không đổi tồn kho.
    @DisplayName("processOverdueRequests: danh dau overdue cho dang muon")
    void processOverdueRequests_marksBorrowOverdue() {
        RecordingRequestDao dao = new RecordingRequestDao();
        Request borrow = requestWithStatus(STATUS_BORROW);
        dao.overdueCandidates = List.of(borrow);
        RecordingBookDao bookDao = new RecordingBookDao();
        RequestService service = newService(dao, bookDao);

        service.processOverdueRequests();

        Request updated = dao.lastUpdated.get();
        assertNotNull(updated, "DAO update should be called");
        assertTrue(updated.isOverdue());
        assertEquals(borrow, updated);
        assertEquals(0, bookDao.quantityDelta.get());
    }

    @Test
    // TC-16: trong danh sách overdueCandidates có request trạng thái 'Đang giữ' => Expected output: processOverdueRequests cập nhật status thành 'Đã hủy', set returnDate và cộng lại 1 đơn vị tồn kho.
    @DisplayName("processOverdueRequests: huy request dang giu")
    void processOverdueRequests_cancelsHold() {
        RecordingRequestDao dao = new RecordingRequestDao();
        Request hold = requestWithStatus(STATUS_HOLD);
        dao.overdueCandidates = List.of(hold);
        RecordingBookDao bookDao = new RecordingBookDao();
        RequestService service = newService(dao, bookDao);

        service.processOverdueRequests();

        Request updated = dao.lastUpdated.get();
        assertNotNull(updated, "DAO update should be called");
        assertEquals(STATUS_CANCELLED, updated.getStatus());
        assertNotNull(updated.getReturnDate());
        assertEquals(1, bookDao.quantityDelta.get());
    }

    @Test
    // TC-17: danh sách overdueCandidates rỗng => Expected output: processOverdueRequests không gọi DAO.update, không thay đổi tồn kho (noop).
    @DisplayName("processOverdueRequests: danh sach rong -> khong update")
    void processOverdueRequests_empty_noop() {
        RecordingRequestDao dao = new RecordingRequestDao(); // overdueCandidates rỗng
        RecordingBookDao bookDao = new RecordingBookDao();
        RequestService service = newService(dao, bookDao);

        service.processOverdueRequests();

        assertNull(dao.lastUpdated.get());
        assertEquals(0, bookDao.quantityDelta.get());
    }

    @Test
    // TC-18: borrowHistory của member có ít nhất một request trạng thái 'Đang mượn' và isOverdue=true => Expected output: hasOverdueBorrow trả true.
    @DisplayName("hasOverdueBorrow: co qua han dang muon -> true")
    void hasOverdueBorrow_trueWhenOverdueBorrow() {
        RecordingRequestDao dao = new RecordingRequestDao();
        Request overdueBorrow = requestWithStatus(STATUS_BORROW);
        overdueBorrow.setOverdue(true);
        dao.borrowHistory = List.of(overdueBorrow);
        RequestService service = newService(dao, new RecordingBookDao());

        assertTrue(service.hasOverdueBorrow(1));
    }

    @Test
    // TC-19: borrowHistory không có request quá hạn => Expected output: hasOverdueBorrow trả false.
    @DisplayName("hasOverdueBorrow khong co qua han -> false")
    void hasOverdueBorrow_falseWhenNoOverdue() {
        RecordingRequestDao dao = new RecordingRequestDao();
        Request okBorrow = requestWithStatus(STATUS_BORROW);
        okBorrow.setOverdue(false);
        dao.borrowHistory = List.of(okBorrow);
        RequestService service = newService(dao, new RecordingBookDao());

        assertFalse(service.hasOverdueBorrow(1));
    }
    @Test
    // TC-20: borrowHistory rỗng => Expected output: hasOverdueBorrow trả false.
    @DisplayName("hasOverdueBorrow: empty history -> false")
    void hasOverdueBorrow_emptyHistory_returnsFalse() {
        RecordingRequestDao dao = new RecordingRequestDao();
        dao.borrowHistory = List.of();
        RequestService service = newService(dao, new RecordingBookDao());

        assertFalse(service.hasOverdueBorrow(1));
    }
    @Test
    // TC-21: số request active >= giới hạn (5) => Expected output: hasReachedBorrowLimit trả true; nếu ít hơn giới hạn thì trả false.
    @DisplayName("hasReachedBorrowLimit: kiem tra so active request")
    void hasReachedBorrowLimit_checksSize() {
        RecordingRequestDao dao = new RecordingRequestDao();
        dao.activeRequests = List.of(new Request(), new Request(), new Request(), new Request(), new Request());
        RequestService service = newService(dao, new RecordingBookDao());
        assertTrue(service.hasReachedBorrowLimit(1));

        dao.activeRequests = List.of(new Request(), new Request());
        assertFalse(service.hasReachedBorrowLimit(1));
    }
    @Test
    // TC-22: không có request active nào => Expected output: hasReachedBorrowLimit trả false.
    @DisplayName("hasReachedBorrowLimit: no active requests -> false")
    void hasReachedBorrowLimit_noRequests_returnsFalse() {
        RecordingRequestDao dao = new RecordingRequestDao();
        dao.activeRequests = List.of();
        RequestService service = newService(dao, new RecordingBookDao());

        assertFalse(service.hasReachedBorrowLimit(1));
    }

    @Test
    // TC-23: số request active đúng bằng giới hạn (5) => Expected output: hasReachedBorrowLimit trả true.
    @DisplayName("hasReachedBorrowLimit: exactly 5 active requests -> true")
    void hasReachedBorrowLimit_exactLimit_returnsTrue() {
        RecordingRequestDao dao = new RecordingRequestDao();
        dao.activeRequests = List.of(new Request(), new Request(), new Request(), new Request(), new Request());
        RequestService service = newService(dao, new RecordingBookDao());

        assertTrue(service.hasReachedBorrowLimit(1));
    }
    @Test
    // TC-24: Lịch sử mượn của member chứa book không tồn tại hoặc đã bị xoá => Expected output: getBooksByMember chỉ trả về các Book khác null (lọc bỏ những book không tồn tại).
    @DisplayName("getBooksByMember: gom chi book non-null")
    void getBooksByMember_filtersNull() {
        RecordingRequestDao dao = new RecordingRequestDao();
        dao.distinctBookIds = List.of("B1", "B2"); // B2 trả null -> phải lọc bỏ
        RecordingBookDao bookDao = new RecordingBookDao();
        Book b1 = new Book("B1", "T1", "A", 2020, "C", "isbn", "", 1);
        bookDao.booksById.put("B1", b1);
        RequestService service = newService(dao, bookDao);

        List<Book> books = service.getBooksByMember(1);

        assertEquals(1, books.size());
        assertSame(b1, books.get(0));
    }

    @Test
    // TC-25: DAO trả về danh sách bookID rỗng cho member => Expected output: getBooksByMember trả list rỗng.
    @DisplayName("getBooksByMember: danh sach bookID rong -> tra list rong")
    void getBooksByMember_emptyIds_returnsEmpty() {
        RecordingRequestDao dao = new RecordingRequestDao();
        dao.distinctBookIds = List.of();
        RecordingBookDao bookDao = new RecordingBookDao();
        RequestService service = newService(dao, bookDao);

        List<Book> books = service.getBooksByMember(1);

        assertTrue(books.isEmpty());
    }
    @Test
    // TC-26: tất cả bookID trả về đều map được sang Book => Expected output: getBooksByMember trả lại list chứa đầy đủ số lượng bookID.
    @DisplayName("getBooksByMember: all IDs valid -> returns all books")
    void getBooksByMember_allValidBooks_returnsAll() {
        RecordingRequestDao dao = new RecordingRequestDao();
        dao.distinctBookIds = List.of("B10", "B20");
        RecordingBookDao bookDao = new RecordingBookDao();
        Book b10 = new Book("B10", "T10", "A", 2020, "C", "isbn10", "", 1);
        Book b20 = new Book("B20", "T20", "A", 2020, "C", "isbn20", "", 1);
        bookDao.booksById.put("B10", b10);
        bookDao.booksById.put("B20", b20);
        RequestService service = newService(dao, bookDao);

        List<Book> books = service.getBooksByMember(2);

        assertEquals(2, books.size());
        assertTrue(books.contains(b10));
        assertTrue(books.contains(b20));
    }
    @Test
    // TC-27: DAO trả về một Request mới nhất cho member+book => Expected output: getLatestRequest trả đúng Request đó và truyền đúng memberID/bookID xuống DAO.
    @DisplayName("getLatestRequest: uy quyen DAO, truyen dung tham so")
    void getLatestRequest_delegates() {
        RecordingRequestDao dao = new RecordingRequestDao();
        Request latest = requestWithStatus(STATUS_HOLD);
        dao.latestResult = latest;
        RecordingBookDao bookDao = new RecordingBookDao();
        RequestService service = newService(dao, bookDao);

        assertSame(latest, service.getLatestRequest(2, "B9"));
        assertEquals(2, dao.lastMemberId.get());
        assertEquals("B9", dao.lastBookId.get());
    }

    @Test
    // TC-28: DAO.getLatestByMemberAndBook trả null => Expected output: getLatestRequest trả null và vẫn truyền đúng memberID/bookID cho DAO.
    @DisplayName("getLatestRequest: DAO tra null -> tra null")
    void getLatestRequest_nullFromDao_returnsNull() {
        RecordingRequestDao dao = new RecordingRequestDao(); // latestResult null
        RequestService service = newService(dao, new RecordingBookDao());

        assertNull(service.getLatestRequest(1, "B1"));
        assertEquals(1, dao.lastMemberId.get());
        assertEquals("B1", dao.lastBookId.get());
    }
    @Test
    // TC-29: memberId âm khi lấy latest request => Expected output: service vẫn truyền nguyên memberId âm xuống DAO và trả về kết quả DAO.
    @DisplayName("getLatestRequest: negative memberId -> still delegates")
    void getLatestRequest_negativeMemberId_delegates() {
        RecordingRequestDao dao = new RecordingRequestDao();
        Request latest = requestWithStatus(STATUS_HOLD);
        dao.latestResult = latest;
        RequestService service = newService(dao, new RecordingBookDao());

        assertSame(latest, service.getLatestRequest(-5, "B1"));
        assertEquals(-5, dao.lastMemberId.get());
    }
    @Test
    // TC-30: DAO.findAll trả về danh sách các Request => Expected output: getAllRequests trả lại chính danh sách (cùng phần tử) từ DAO.
    @DisplayName("getAllRequests: tra ve danh sach DAO")
    void getAllRequests_delegates() {
        RecordingRequestDao dao = new RecordingRequestDao();
        Request r = requestWithStatus(STATUS_HOLD);
        dao.allRequests = List.of(r);
        RequestService service = newService(dao, new RecordingBookDao());

        List<Request> all = service.getAllRequests();

        assertEquals(1, all.size());
        assertSame(r, all.get(0));
    }

    @Test
    // TC-31: DAO trả về danh sách rỗng => Expected output: getAllRequests trả về list rỗng.
    @DisplayName("getAllRequests: DAO returns empty list -> returns empty list")
    void getAllRequests_empty_returnsEmptyList() {
        RecordingRequestDao dao = new RecordingRequestDao();
        dao.allRequests = List.of();
        RequestService service = newService(dao, new RecordingBookDao());

        List<Request> all = service.getAllRequests();

        assertTrue(all.isEmpty());
    }
    @Test
    // TC-32: DAO trả về null khi getAllRequests => Expected output: service cũng trả null và vẫn gọi DAO.
    @DisplayName("getAllRequests: DAO returns null -> service returns null")
    void getAllRequests_nullFromDao_returnsNull() {
        RecordingRequestDao dao = new RecordingRequestDao();
        dao.allRequests = null;
        RequestService service = newService(dao, new RecordingBookDao());

        assertNull(service.getAllRequests());
    }











}
