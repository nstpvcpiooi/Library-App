package Library.backend.Review.Dao;

import Library.backend.Review.model.Review;
import Library.backend.database.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MysqlReviewDao implements ReviewDao {
    private static MysqlReviewDao instance;

    // Singleton Pattern
    public static MysqlReviewDao getInstance() {
        if (instance == null) {
            synchronized (MysqlReviewDao.class) {
                if (instance == null) {
                    instance = new MysqlReviewDao();
                }
            }
        }
        return instance;
    }

    @Override
    public boolean addReview(Review review) {
        String query = "INSERT INTO Reviews (bookID, memberID, rating, reviewTimestamp, comment) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, review.getBookID());
            statement.setInt(2, review.getMemberID());
            statement.setInt(3, review.getRating());
            statement.setDate(4, java.sql.Date.valueOf(review.getReviewTimestamp()));
            statement.setString(5, review.getComment());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateReview(Review review) {
        String query = "UPDATE Reviews SET rating = ?, reviewTimestamp = ?, comment = ? WHERE bookID = ? AND memberID = ?";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, review.getRating());
            statement.setDate(2, java.sql.Date.valueOf(review.getReviewTimestamp()));
            statement.setString(3, review.getComment());
            statement.setString(4, review.getBookID());
            statement.setInt(5, review.getMemberID());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteReview(String bookID, int memberID) {
        String query = "DELETE FROM Reviews WHERE bookID = ? AND memberID = ?";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, bookID);
            statement.setInt(2, memberID);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Review getReviewByBookAndMember(String bookID, int memberID) {
        String query = "SELECT * FROM Reviews WHERE bookID = ? AND memberID = ?";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, bookID);
            statement.setInt(2, memberID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Review.Builder()
                        .bookID(resultSet.getString("bookID"))
                        .memberID(resultSet.getInt("memberID"))
                        .rating(resultSet.getInt("rating"))
                        .reviewTimestamp(resultSet.getDate("reviewTimestamp").toString())
                        .comment(resultSet.getString("comment"))
                        .build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Review> getReviewsForBook(String bookID) {
        List<Review> reviews = new ArrayList<>();
        String query = "SELECT * FROM Reviews WHERE bookID = ?";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, bookID);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Review review = new Review.Builder()
                        .bookID(resultSet.getString("bookID"))
                        .memberID(resultSet.getInt("memberID"))
                        .rating(resultSet.getInt("rating"))
                        .reviewTimestamp(resultSet.getDate("reviewTimestamp").toString())
                        .comment(resultSet.getString("comment"))
                        .build();
                reviews.add(review);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    @Override
    public List<Review> getReviewsByMember(int memberID) {
        List<Review> reviews = new ArrayList<>();
        String query = "SELECT * FROM Reviews WHERE memberID = ?";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, memberID);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Review review = new Review.Builder()
                        .bookID(resultSet.getString("bookID"))
                        .memberID(resultSet.getInt("memberID"))
                        .rating(resultSet.getInt("rating"))
                        .reviewTimestamp(resultSet.getDate("reviewTimestamp").toString())
                        .comment(resultSet.getString("comment"))
                        .build();
                reviews.add(review);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    @Override
    public double getAverageRatingForBook(String bookID) {
        String query = "SELECT AVG(rating) AS averageRating FROM Reviews WHERE bookID = ?";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, bookID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("averageRating");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}
