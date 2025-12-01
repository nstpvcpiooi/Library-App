package Library.backend.Recommendation.DAO;

import Library.backend.Recommendation.Model.Recommendation;
import Library.backend.Book.Model.Book;
import Library.backend.database.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class MysqlRecommendationDAOImpl implements RecommendationDAO {
    private static MysqlRecommendationDAOImpl instance;

    private MysqlRecommendationDAOImpl() {
    }

    public static MysqlRecommendationDAOImpl getInstance() {
        if (instance == null) {
            synchronized (MysqlRecommendationDAOImpl.class) {
                if (instance == null) {
                    instance = new MysqlRecommendationDAOImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public List<Book> getCombinedRecommendations(int memberID) {
        LinkedHashSet<Book> combinedRecommendations = new LinkedHashSet<>();

        String historyQuery = "SELECT DISTINCT b.* FROM Books b " +
                "WHERE b.category IN (" +
                "    SELECT DISTINCT b2.category " +
                "    FROM Requests r " +
                "    JOIN Books b2 ON r.bookID = b2.bookID " +
                "    WHERE r.memberID = ?" +
                ") ORDER BY RAND() LIMIT 10";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(historyQuery)) {
            statement.setInt(1, memberID);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                combinedRecommendations.add(new Book(
                        resultSet.getString("bookID"),
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getInt("publishYear"),
                        resultSet.getString("category"),
                        resultSet.getString("isbn"),
                        resultSet.getString("coverCode"),
                        resultSet.getInt("quantity")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String similarUserQuery = "SELECT DISTINCT b.* FROM Books b " +
                "JOIN Requests r ON b.bookID = r.bookID " +
                "WHERE r.memberID IN (" +
                "    SELECT DISTINCT r2.memberID " +
                "    FROM Requests r1 " +
                "    JOIN Requests r2 ON r1.bookID = r2.bookID " +
                "    WHERE r1.memberID = ?" +
                ") AND b.bookID NOT IN (" +
                "    SELECT bookID FROM Requests WHERE memberID = ?" +
                ") ORDER BY RAND() LIMIT 10";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(similarUserQuery)) {
            statement.setInt(1, memberID);
            statement.setInt(2, memberID);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                combinedRecommendations.add(new Book(
                        resultSet.getString("bookID"),
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getInt("publishYear"),
                        resultSet.getString("category"),
                        resultSet.getString("isbn"),
                        resultSet.getString("coverCode"),
                        resultSet.getInt("quantity")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String popularQuery = "SELECT b.*, AVG(r.rating) AS avgRating " +
                "FROM Books b " +
                "INNER JOIN Reviews r ON b.bookID = r.bookID " +
                "GROUP BY b.bookID " +
                "HAVING avgRating >= 4 " +
                "ORDER BY avgRating DESC, COUNT(r.rating) DESC LIMIT 10";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(popularQuery)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                combinedRecommendations.add(new Book(
                        resultSet.getString("bookID"),
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getInt("publishYear"),
                        resultSet.getString("category"),
                        resultSet.getString("isbn"),
                        resultSet.getString("coverCode"),
                        resultSet.getInt("quantity")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<>(combinedRecommendations);
    }

}
