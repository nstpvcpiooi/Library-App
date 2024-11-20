package Library.backend.Request.DAO;

import Library.backend.Request.Model.Request;
import Library.backend.bookModel.Book;
import Library.backend.database.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RequestDAOImpl implements RequestDAO {
    private static RequestDAOImpl instance;

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
    public void updateBorrowTime(int requestID, LocalDateTime borrowTime) {
        String query = "UPDATE Requests SET borrowDate = ? WHERE requestID = ?";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setObject(1, borrowTime);
            preparedStatement.setInt(2, requestID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateReturnTime(int requestID, LocalDateTime returnTime) {
        String query = "UPDATE Requests SET returnDate = ? WHERE requestID = ?";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setObject(1, returnTime);
            preparedStatement.setInt(2, requestID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createBorrowRequest(Request request) {
        String checkQuery = "SELECT COUNT(*) FROM Requests WHERE memberID = ? AND bookID = ?";
        String insertQuery = "INSERT INTO Requests (memberID, bookID, borrowDate, returnDate) VALUES (?, ?, ?, ?)";

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
            insertStatement.setObject(3, request.getBorrowDate());
            insertStatement.setObject(4, request.getReturnDate());

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
                request.setBorrowDate(resultSet.getObject("borrowDate", LocalDateTime.class));
                request.setReturnDate(resultSet.getObject("returnDate", LocalDateTime.class));
                borrowHistory.add(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return borrowHistory;
    }

    @Override
    public void borrowRequest(int memberID, Book book) {
        if (book.getQuantity() > 0) {
            LocalDateTime borrowDate = LocalDateTime.now();
            LocalDateTime returnDate = borrowDate.plusDays(7);
            createBorrowRequest(new Request(memberID, book.getBookID(), borrowDate, returnDate));
            book.updateQuantity(book.getQuantity()-1);
        } else {
            throw new IllegalStateException("Cannot borrow book: Quantity is 0.");
        }
    }

    @Override
    public void returnBook(int memberID, Book book) {
        List<Request> requests = getMemberBorrowHistory(memberID);
        for (Request request : requests) {
            if (request.getBookID().equals(book.getBookID()) && request.getReturnDate() == null) {
                updateReturnTime(request.getRequestID(), LocalDateTime.now());
                book.updateQuantity(book.getQuantity()+1); // Increase the quantity by 1
            }
        }
    }
}