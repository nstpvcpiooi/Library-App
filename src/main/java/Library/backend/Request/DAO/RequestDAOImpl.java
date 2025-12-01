// src/main/java/Library/backend/Request/DAO/RequestDAOImpl.java
package Library.backend.Request.DAO;

import Library.backend.Request.Model.Request;
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
    public void insert(Request request) {
        String sql = "INSERT INTO Requests (memberID, bookID, issueDate, dueDate, returnDate, status, overdue) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, request.getMemberID());
            statement.setString(2, request.getBookID());
            statement.setObject(3, request.getIssueDate());
            statement.setObject(4, request.getDueDate());
            statement.setObject(5, request.getReturnDate());
            statement.setString(6, request.getStatus());
            statement.setBoolean(7, request.isOverdue());

            int affected = statement.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        request.setRequestID(keys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Request request) {
        String sql = "UPDATE Requests SET issueDate = ?, dueDate = ?, returnDate = ?, status = ?, overdue = ? WHERE requestID = ?";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, request.getIssueDate());
            statement.setObject(2, request.getDueDate());
            statement.setObject(3, request.getReturnDate());
            statement.setString(4, request.getStatus());
            statement.setBoolean(5, request.isOverdue());
            statement.setInt(6, request.getRequestID());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Request findById(int requestID) {
        String sql = "SELECT * FROM Requests WHERE requestID = ?";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, requestID);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRowToRequest(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Request findLatestByMemberAndBook(int memberID, String bookID) {
        String sql = "SELECT * FROM Requests WHERE memberID = ? AND bookID = ? ORDER BY requestID DESC LIMIT 1";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, memberID);
            statement.setString(2, bookID);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRowToRequest(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Request> findAll() {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT * FROM Requests ORDER BY CASE WHEN status = N'Đang giữ' THEN 1 WHEN status = N'Đang mượn' THEN 2 ELSE 3 END, overdue DESC";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                requests.add(mapRowToRequest(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    @Override
    public List<Request> findBorrowHistory(int memberID) {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT * FROM Requests WHERE memberID = ?";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, memberID);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    requests.add(mapRowToRequest(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    @Override
    public boolean existsActiveRequest(int memberID, String bookID) {
        String sql = "SELECT COUNT(*) FROM Requests WHERE memberID = ? AND bookID = ? AND (status = N'Đang mượn' OR status = N'Đang giữ')";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, memberID);
            statement.setString(2, bookID);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Request> findOverdueCandidates(LocalDateTime timestamp) {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT * FROM Requests WHERE dueDate < ? AND (status = N'Đang giữ' OR status = N'Đang mượn')";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, timestamp);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    requests.add(mapRowToRequest(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    @Override
    public List<Request> findActiveRequestsByMember(int memberID) {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT * FROM Requests WHERE memberID = ? AND (status = N'Đang giữ' OR status = N'Đang mượn')";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, memberID);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    requests.add(mapRowToRequest(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    @Override
    public List<String> findDistinctBookIdsByMember(int memberID) {
        List<String> bookIds = new ArrayList<>();
        String sql = "SELECT DISTINCT bookID FROM Requests WHERE memberID = ?";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, memberID);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    bookIds.add(resultSet.getString("bookID"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookIds;
    }

    private Request mapRowToRequest(ResultSet resultSet) throws SQLException {
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
}
