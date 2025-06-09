package Library.backend.Recommendation.Dao;

import Library.backend.Recommendation.model.Recommendation;
import Library.backend.bookModel.Book;
import Library.backend.database.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
public class MysqlRecommendationDao implements RecommendationDao {
    private static MysqlRecommendationDao instance;

    // Singleton pattern
    public static MysqlRecommendationDao getInstance() {
        if (instance == null) {
            synchronized (MysqlRecommendationDao.class) {
                if (instance == null) {
                    instance = new MysqlRecommendationDao();
                }
            }
        }
        return instance;
    }

    @Override
    public boolean addRecommendation(Recommendation recommendation) {
        String query = "INSERT INTO Recommendations (memberID, preferenceCategory) VALUES (?, ?)";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, recommendation.getMemberID());
            statement.setString(2, recommendation.getPreferenceCategory());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Recommendation> getRecommendationsForMember(int memberID) {
        List<Recommendation> recommendationsList = new ArrayList<>();
        String query = "SELECT * FROM Recommendations WHERE memberID = ?";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, memberID);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Recommendation recommendation = new Recommendation.Builder()
                        .memberID(resultSet.getInt("memberID"))
                        .preferenceCategory(resultSet.getString("preferenceCategory"))
                        .build();
                recommendationsList.add(recommendation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recommendationsList;
    }

    @Override
    public List<Book> getPopularRecommendations() {
        List<Book> booksList = new ArrayList<>();
        String query = "SELECT b.*, AVG(r.rating) AS avgRating " +
                "FROM Books b " +
                "INNER JOIN Reviews r ON b.bookID = r.bookID " +
                "GROUP BY b.bookID " +
                "HAVING avgRating >= 4 " +
                "ORDER BY avgRating DESC, COUNT(r.rating) DESC LIMIT 10";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Book book = new Book(
                        resultSet.getString("bookID"),
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getInt("publishYear"),
                        resultSet.getString("category"),
                        resultSet.getString("isbn"),
                        resultSet.getString("coverCode"),
                      //  resultSet.getInt("status"),
                        resultSet.getInt("quantity")
                );
                booksList.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return booksList;
    }


    @Override
    public List<Book> getRecommendationsBasedOnBorrowHistory(int memberID) {
        List<Book> booksList = new ArrayList<>();
        String query = "SELECT DISTINCT b.* FROM Books b " +
                "WHERE b.category IN (" +
                "    SELECT DISTINCT b2.category " +
                "    FROM Requests r " +
                "    JOIN Books b2 ON r.bookID = b2.bookID " +
                "    WHERE r.memberID = ?" +
                ") ORDER BY RAND() LIMIT 10";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, memberID);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Book book = new Book(
                        resultSet.getString("bookID"),
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getInt("publishYear"),
                        resultSet.getString("category"),
                        resultSet.getString("isbn"),
                        resultSet.getString("coverCode"),
                 //       resultSet.getInt("status"),
                        resultSet.getInt("quantity")

                );
                booksList.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return booksList;
    }

    @Override
    public List<Book> getRecommendationsFromSimilarUsers(int memberID) {
        List<Book> booksList = new ArrayList<>();
        String query = "SELECT DISTINCT b.* FROM Books b " +
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
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, memberID);
            statement.setInt(2, memberID);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Book book = new Book(
                        resultSet.getString("bookID"),
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getInt("publishYear"),
                        resultSet.getString("category"),
                        resultSet.getString("isbn"),
                        resultSet.getString("coverCode"),
                    //    resultSet.getInt("status"),
                        resultSet.getInt("quantity")

                );
                booksList.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return booksList;
    }

    @Override
    public List<Book> getCombinedRecommendations(int memberID) {
        // Lấy danh sách từ các phương pháp khác nhau
        List<Book> basedOnBorrowHistory = getRecommendationsBasedOnBorrowHistory(memberID);
        List<Book> basedOnSimilarUsers = getRecommendationsFromSimilarUsers(memberID);
        List<Book> popularBooks = getPopularRecommendations();

        LinkedHashSet<Book> combinedRecommendations = new LinkedHashSet<>();
        combinedRecommendations.addAll(basedOnBorrowHistory);
        combinedRecommendations.addAll(basedOnSimilarUsers);
        combinedRecommendations.addAll(popularBooks);

        return new ArrayList<>(combinedRecommendations);
    }

}
