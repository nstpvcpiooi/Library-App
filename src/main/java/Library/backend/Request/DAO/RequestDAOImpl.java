
package Library.backend.Request.DAO;

import Library.backend.Request.Model.Request;
import Library.backend.bookDao.BookDao;
import Library.backend.bookDao.MysqlBookDao;
import Library.backend.bookModel.Book;
import Library.backend.database.JDBCUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RequestDAOImpl implements RequestDAO {
    private static RequestDAOImpl instance;
    private BookDao bookDao = MysqlBookDao.getInstance();
    private RequestDAOImpl() {
        // Private constructor to prevent instantiation
    }

    public static RequestDAOImpl getInstance() {
        if (instance == null) {
            synchronized (RequestDAOImpl.class) {
                if (instance == null) {
                    instance = new RequestDAOImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public void createBorrowRequest(Request request) {
        String checkQuery = "SELECT COUNT(*) FROM Requests WHERE memberID = ? AND bookID = ? AND (status = 'approved issue' OR status = 'pending issue' OR status = 'pending return')";
        String insertQuery = "INSERT INTO Requests (memberID, bookID, issueDate, dueDate, returnDate, status, overdue) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
             PreparedStatement insertStatement = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {

            // Check for duplicate requests
            checkStatement.setInt(1, request.getMemberID());
            checkStatement.setString(2, request.getBookID());
            ResultSet resultSet = checkStatement.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                throw new SQLException("Duplicate borrow request: memberID and bookID combination already exists.");
            }

            // No duplicates, proceed with insert
            insertStatement.setInt(1, request.getMemberID());
            insertStatement.setString(2, request.getBookID());
            insertStatement.setObject(3, request.getIssueDate());
            insertStatement.setObject(4, request.getDueDate());
            insertStatement.setObject(5, request.getReturnDate());
            insertStatement.setString(6, request.getStatus());
            insertStatement.setBoolean(7, request.isOverdue());

            int rowsInserted = insertStatement.executeUpdate();
            if (rowsInserted > 0) {
                resultSet = insertStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    request.setRequestID(resultSet.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public List<Request> getMemberBorrowHistory(int memberID) {
        List<Request> borrowHistory = new ArrayList<>();
        String query = "SELECT * FROM Requests WHERE memberID = ?";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, memberID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Request request = new Request();
                request.setRequestID(resultSet.getInt("requestID"));
                request.setMemberID(resultSet.getInt("memberID"));
                request.setBookID(resultSet.getString("bookID"));
                request.setIssueDate(resultSet.getObject("issueDate", LocalDateTime.class));
                request.setDueDate(resultSet.getObject("dueDate", LocalDateTime.class));
                request.setReturnDate(resultSet.getObject("returnDate", LocalDateTime.class));
                request.setStatus(resultSet.getString("status"));
                request.setOverdue(resultSet.getBoolean("overdue"));
                borrowHistory.add(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return borrowHistory;
    }

    @Override
    public void updateRequest(Request request) {
        System.out.println(request.getRequestID());
        String query = "UPDATE Requests SET issueDate = ?, dueDate = ?, returnDate = ?, status = ?, overdue = ? WHERE requestID = ?";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setObject(1, request.getIssueDate());
            preparedStatement.setObject(2, request.getDueDate());
            preparedStatement.setObject(3, request.getReturnDate());
            preparedStatement.setString(4, request.getStatus());
            preparedStatement.setBoolean(5, request.isOverdue());
            preparedStatement.setInt(6, request.getRequestID());
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Request getRequestById(int requestID) {
        String query = "SELECT * FROM Requests WHERE requestID = ?";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, requestID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Request request = new Request();
                request.setRequestID(resultSet.getInt("requestID"));
                request.setMemberID(resultSet.getInt("memberID"));
                request.setBookID(resultSet.getString("bookID"));
                request.setIssueDate(resultSet.getObject("issueDate", LocalDateTime.class));
                request.setDueDate(resultSet.getObject("dueDate", LocalDateTime.class));
                request.setReturnDate(resultSet.getObject("returnDate", LocalDateTime.class));
                request.setStatus(resultSet.getString("status"));
                request.setOverdue(resultSet.getBoolean("overdue"));
                return request;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void handleOverdueRequests() {
        String query = "SELECT * FROM Requests WHERE dueDate < ? AND (status = 'pending issue' OR status = 'pending return' OR status = 'approved issue')";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setObject(1, LocalDateTime.now());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Request request = new Request();
                request.setRequestID(resultSet.getInt("requestID"));
                request.setMemberID(resultSet.getInt("memberID"));
                request.setBookID(resultSet.getString("bookID"));
                request.setIssueDate(resultSet.getObject("issueDate", LocalDateTime.class));
                request.setDueDate(resultSet.getObject("dueDate", LocalDateTime.class));
                request.setReturnDate(resultSet.getObject("returnDate", LocalDateTime.class));
                request.setStatus(resultSet.getString("status"));
                request.setOverdue(resultSet.getBoolean("overdue"));

                if ("pending issue".equals(request.getStatus())) {
                    // Delete the request and return the book to the library
                    bookDao.updateQuantity(request.getBookID(), 1);
                    request.setOverdue(false);
                    request.setStatus("approved return");
                    updateRequest(request);
                } else if ("pending return".equals(request.getStatus()) || "approved issue".equals(request.getStatus())) {
                    // Mark the request as overdue
                    request.setOverdue(true);
                    updateRequest(request);
                } else if ("approved return".equals(request.getStatus())) {
                    // Delete the overdue and update the return date
                    request.setOverdue(false);
                    request.setReturnDate(LocalDateTime.now());
                    updateRequest(request);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteRequest(int requestID) {
        String query = "DELETE FROM Requests WHERE requestID = ?";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, requestID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public ObservableList<Request> getAllRequests() {
        ObservableList<Request> requests = FXCollections.observableArrayList();
        String query = "SELECT * FROM Requests ORDER BY CASE WHEN status = 'pending issue' THEN 1 WHEN status = 'pending return' THEN 2 ELSE 3 END, overdue DESC";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                Request request = new Request();
                request.setRequestID(resultSet.getInt("requestID"));
                request.setMemberID(resultSet.getInt("memberID"));
                request.setBookID(resultSet.getString("bookID"));
                request.setIssueDate(resultSet.getObject("issueDate", LocalDateTime.class));
                request.setDueDate(resultSet.getObject("dueDate", LocalDateTime.class));
                request.setReturnDate(resultSet.getObject("returnDate", LocalDateTime.class));
                request.setStatus(resultSet.getString("status"));
                request.setOverdue(resultSet.getBoolean("overdue"));
                requests.add(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    @Override
    public List<Request> getRequestsByMemberID(int memberID) {
        List<Request> requests = new ArrayList<>();
        String query = "SELECT * FROM Requests WHERE memberID = ?";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, memberID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Request request = new Request();
                request.setRequestID(resultSet.getInt("requestID"));
                request.setMemberID(resultSet.getInt("memberID"));
                request.setBookID(resultSet.getString("bookID"));
                request.setIssueDate(resultSet.getObject("issueDate", LocalDateTime.class));
                request.setDueDate(resultSet.getObject("dueDate", LocalDateTime.class));
                request.setReturnDate(resultSet.getObject("returnDate", LocalDateTime.class));
                request.setStatus(resultSet.getString("status"));
                request.setOverdue(resultSet.getBoolean("overdue"));
                requests.add(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }
    @Override
    public Request getRequestByMemberIDAndBookID(int memberID, String bookID) {
        String query = "SELECT * FROM Requests WHERE memberID = ? AND bookID = ? ORDER BY requestID DESC LIMIT 1";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, memberID);
            preparedStatement.setString(2, bookID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Request request = new Request();
                request.setRequestID(resultSet.getInt("requestID"));
                request.setMemberID(resultSet.getInt("memberID"));
                request.setBookID(resultSet.getString("bookID"));
                request.setIssueDate(resultSet.getObject("issueDate", LocalDateTime.class));
                request.setDueDate(resultSet.getObject("dueDate", LocalDateTime.class));
                request.setReturnDate(resultSet.getObject("returnDate", LocalDateTime.class));
                request.setStatus(resultSet.getString("status"));
                request.setOverdue(resultSet.getBoolean("overdue"));
                return request;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    return null;
    }
    @Override
    public boolean handleDuplicateRequest(int memberID, String bookID) {
        String query = "SELECT COUNT(*) FROM Requests WHERE memberID = ? AND bookID = ? AND (status = 'approved issue' OR status = 'pending issue' OR status = 'pending return')";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, memberID);
            preparedStatement.setString(2, bookID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    @Override
    public List<Book> getBooksByMemberID(int memberID) {
        List<Book> books = new ArrayList<>();
        String query = "SELECT bookID FROM Requests WHERE memberID = ? group by bookID";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, memberID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Book book = Book.getBookById(resultSet.getString("bookID"));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

}