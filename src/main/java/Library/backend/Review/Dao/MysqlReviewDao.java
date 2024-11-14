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

    // Singleton pattern for Dao instance
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
        String query = "INSERT INTO Review (reviewID, memberID, bookID, rating, comment, reviewTimestamp) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, review.getReviewID());
            statement.setString(2, review.getMemberID());
            statement.setString(3, review.getBookID());
            statement.setInt(4, review.getRating());
            statement.setString(5, review.getComment());
            statement.setString(6, review.getReviewTimestamp());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateReview(Review review) {
        String query = "UPDATE Review SET memberID = ?, bookID = ?, rating = ?, comment = ?, reviewTimestamp = ? WHERE reviewID = ?";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, review.getMemberID());
            statement.setString(2, review.getBookID());
            statement.setInt(3, review.getRating());
            statement.setString(4, review.getComment());
            statement.setString(5, review.getReviewTimestamp());
            statement.setString(6, review.getReviewID());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteReview(String reviewID) {
        String query = "DELETE FROM Review WHERE reviewID = ?";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, reviewID);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Review getReviewById(String reviewID) {
        String query = "SELECT * FROM Review WHERE reviewID = ?";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, reviewID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Review.Builder()
                        .reviewID(resultSet.getString("reviewID"))
                        .memberID(resultSet.getString("memberID"))
                        .bookID(resultSet.getString("bookID"))
                        .rating(resultSet.getInt("rating"))
                        .comment(resultSet.getString("comment"))
                        .reviewTimestamp(resultSet.getString("reviewTimestamp"))
                        .build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Review> getReviewsForBook(String bookID) {
        List<Review> reviewsList = new ArrayList<>();
        String query = "SELECT * FROM Review WHERE bookID = ?";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, bookID);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Review review = new Review.Builder()
                        .reviewID(resultSet.getString("reviewID"))
                        .memberID(resultSet.getString("memberID"))
                        .bookID(resultSet.getString("bookID"))
                        .rating(resultSet.getInt("rating"))
                        .comment(resultSet.getString("comment"))
                        .reviewTimestamp(resultSet.getString("reviewTimestamp"))
                        .build();
                reviewsList.add(review);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviewsList;
    }

    @Override
    public List<Review> getReviewsByMember(String memberID) {
        List<Review> reviewsList = new ArrayList<>();
        String query = "SELECT * FROM Review WHERE memberID = ?";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, memberID);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Review review = new Review.Builder()
                        .reviewID(resultSet.getString("reviewID"))
                        .memberID(resultSet.getString("memberID"))
                        .bookID(resultSet.getString("bookID"))
                        .rating(resultSet.getInt("rating"))
                        .comment(resultSet.getString("comment"))
                        .reviewTimestamp(resultSet.getString("reviewTimestamp"))
                        .build();
                reviewsList.add(review);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviewsList;
    }

    @Override
    public double getAverageRatingForBook(String bookID) {
        String query = "SELECT AVG(rating) AS averageRating FROM Review WHERE bookID = ?";
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
