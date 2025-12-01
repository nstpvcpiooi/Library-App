package Library.backend.Request.service;

import Library.backend.Book.Model.Book;
import Library.backend.Book.Service.BookService;
import Library.backend.Request.DAO.RequestDAO;
import Library.backend.Request.DAO.RequestDAOImpl;
import Library.backend.Request.Model.Request;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Orchestrates all request-related business logic while delegating persistence to {@link RequestDAO}.
 */
public class RequestService {

    private static volatile RequestService instance;

    private final RequestDAO requestDAO;
    private final BookService bookService;

    private static final int HOLD_DURATION_DAYS = 3;
    private static final int BORROW_DURATION_DAYS = 7;
    private static final int BORROW_LIMIT = 5;
    private ScheduledExecutorService scheduler;
    private static final long OVERDUE_INTERVAL_HOURS = 1;
    private static final long OVERDUE_INITIAL_DELAY_HOURS = 0;

    private RequestService(RequestDAO requestDAO, BookService bookService) {
        this.requestDAO = Objects.requireNonNull(requestDAO);
        this.bookService = Objects.requireNonNull(bookService);
    }

    public static RequestService getInstance() {
        if (instance == null) {
            synchronized (RequestService.class) {
                if (instance == null) {
                    instance = new RequestService(RequestDAOImpl.getInstance(), BookService.getInstance());
                }
            }
        }
        return instance;
    }

    public static RequestService create(RequestDAO requestDAO, BookService bookService) {
        return new RequestService(requestDAO, bookService);
    }

    public Request placeHold(int memberId, String bookId) {
        if (requestDAO.existsActiveRequest(memberId, bookId)) {
            throw new IllegalStateException("Thành viên đã có yêu cầu giữ/mượn cho sách này.");
        }
        if (hasReachedBorrowLimit(memberId)) {
            throw new IllegalStateException("Đã đạt giới hạn mượn.");
        }

        Book book = bookService.getBookById(bookId);
        if (book == null || book.getQuantity() <= 0) {
            throw new IllegalStateException("Sách không khả dụng.");
        }

        LocalDateTime now = LocalDateTime.now();
        Request request = new Request(memberId, bookId, now, now.plusDays(HOLD_DURATION_DAYS), null, "Đang giữ", false);
        requestDAO.insert(request);
        bookService.updateQuantity(bookId, -1);
        return request;
    }

    public void cancelHold(int memberId, String bookId) {
        Request request = requestDAO.findLatestByMemberAndBook(memberId, bookId);
        if (request == null || !"Đang giữ".equals(request.getStatus())) {
            return;
        }
        bookService.updateQuantity(request.getBookID(), 1);
        request.setStatus("Đã hủy");
        request.setReturnDate(LocalDateTime.now());
        request.setOverdue(false);
        requestDAO.update(request);
    }

    public void approveIssue(int requestId) {
        Request request = requestDAO.findById(requestId);
        if (request == null) {
            throw new IllegalArgumentException("Không tìm thấy yêu cầu có id=" + requestId);
        }
        if (!"Đang giữ".equals(request.getStatus())) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        request.setIssueDate(now);
        request.setDueDate(now.plusDays(BORROW_DURATION_DAYS));
        request.setStatus("Đang mượn");
        request.setOverdue(false);
        requestDAO.update(request);
    }

    public void approveReturn(int requestId) {
        Request request = requestDAO.findById(requestId);
        if (request == null) {
            throw new IllegalArgumentException("Không tìm thấy yêu cầu có id=" + requestId);
        }
        if (!"Đang mượn".equals(request.getStatus())) {
            return;
        }
        request.setStatus("Đã trả");
        request.setReturnDate(LocalDateTime.now());
        request.setOverdue(false);
        requestDAO.update(request);
        bookService.updateQuantity(request.getBookID(), 1);
    }

    public void processOverdueRequests() {
        List<Request> candidates = requestDAO.findOverdueCandidates(LocalDateTime.now());
        for (Request request : candidates) {
            if ("Đang mượn".equals(request.getStatus())) {
                request.setOverdue(true);
                requestDAO.update(request);
            } else if ("Đang giữ".equals(request.getStatus())) {
                request.setStatus("Đã hủy");
                request.setOverdue(false);
                request.setReturnDate(LocalDateTime.now());
                requestDAO.update(request);
                bookService.updateQuantity(request.getBookID(), 1);
            }
        }
    }

    /**
     * Start background scheduler to process overdue requests every hour.
     */
    public synchronized void startOverdueScheduler() {
        if (scheduler != null && !scheduler.isShutdown()) {
            return;
        }
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                processOverdueRequests();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, OVERDUE_INITIAL_DELAY_HOURS, OVERDUE_INTERVAL_HOURS, TimeUnit.HOURS);
    }

    public synchronized void stopOverdueScheduler() {
        if (scheduler != null) {
            scheduler.shutdown();
            scheduler = null;
        }
    }


    public Request getLatestRequest(int memberId, String bookId) {
        return requestDAO.findLatestByMemberAndBook(memberId, bookId);
    }

    public List<Request> getAllRequests() {
        return requestDAO.findAll();
    }

    public List<Book> getBooksByMember(int memberId) {
        List<Book> books = new ArrayList<>();
        for (String bookId : requestDAO.findDistinctBookIdsByMember(memberId)) {
            Book book = bookService.getBookById(bookId);
            if (book != null) {
                books.add(book);
            }
        }
        return books;
    }

    public boolean hasOverdueBorrow(int memberId) {
        return requestDAO.findBorrowHistory(memberId).stream()
                .anyMatch(r -> r.isOverdue() && "Đang mượn".equals(r.getStatus()));
    }

    public boolean hasReachedBorrowLimit(int memberId) {
        return requestDAO.findActiveRequestsByMember(memberId).size() >= BORROW_LIMIT;
    }

}

