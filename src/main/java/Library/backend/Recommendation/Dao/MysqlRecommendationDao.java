package Library.backend.Recommendation.Dao;

import Library.backend.Recommendation.model.Recommendation;
import Library.backend.database.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MysqlRecommendationDao implements RecommendationDao {
    private static MysqlRecommendationDao instance;

    // Singleton pattern for Dao instance
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
        String query = "INSERT INTO Recommendation (recommendationID, memberID, bookID, preferenceCategory, popularityScore, generatedTimestamp) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, recommendation.getRecommendationID());
            statement.setString(2, recommendation.getMemberID());
            statement.setString(3, recommendation.getBookID());
            statement.setString(4, recommendation.getPreferenceCategory());
            statement.setInt(5, recommendation.getPopularityScore());
            statement.setString(6, recommendation.getGeneratedTimestamp());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateRecommendation(Recommendation recommendation) {
        String query = "UPDATE Recommendation SET memberID = ?, bookID = ?, preferenceCategory = ?, popularityScore = ?, generatedTimestamp = ? WHERE recommendationID = ?";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, recommendation.getMemberID());
            statement.setString(2, recommendation.getBookID());
            statement.setString(3, recommendation.getPreferenceCategory());
            statement.setInt(4, recommendation.getPopularityScore());
            statement.setString(5, recommendation.getGeneratedTimestamp());
            statement.setString(6, recommendation.getRecommendationID());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteRecommendation(String recommendationID) {
        String query = "DELETE FROM Recommendation WHERE recommendationID = ?";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, recommendationID);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Recommendation getRecommendationById(String recommendationID) {
        String query = "SELECT * FROM Recommendation WHERE recommendationID = ?";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, recommendationID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Recommendation.Builder()
                        .recommendationID(resultSet.getString("recommendationID"))
                        .memberID(resultSet.getString("memberID"))
                        .bookID(resultSet.getString("bookID"))
                        .preferenceCategory(resultSet.getString("preferenceCategory"))
                        .popularityScore(resultSet.getInt("popularityScore"))
                        .generatedTimestamp(resultSet.getString("generatedTimestamp"))
                        .build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Recommendation> getRecommendationsForMember(String memberID) {
        List<Recommendation> recommendationsList = new ArrayList<>();
        String query = "SELECT * FROM Recommendation WHERE memberID = ?";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, memberID);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Recommendation recommendation = new Recommendation.Builder()
                        .recommendationID(resultSet.getString("recommendationID"))
                        .memberID(resultSet.getString("memberID"))
                        .bookID(resultSet.getString("bookID"))
                        .preferenceCategory(resultSet.getString("preferenceCategory"))
                        .popularityScore(resultSet.getInt("popularityScore"))
                        .generatedTimestamp(resultSet.getString("generatedTimestamp"))
                        .build();
                recommendationsList.add(recommendation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recommendationsList;
    }

    @Override
    public List<Recommendation> getRecommendationsBasedOnPreferencesAndRequests(String memberID) {
        List<Recommendation> recommendationsList = new ArrayList<>();
        String query = "SELECT * FROM Books WHERE category IN (SELECT DISTINCT category FROM Requests WHERE memberID = ?) " +
                "OR category = (SELECT preferenceCategory FROM Members WHERE memberID = ?) " +
                "OR bookID IN (SELECT bookID FROM Reviews WHERE rating >= 4) ORDER BY RAND() LIMIT 10";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, memberID);
            statement.setString(2, memberID);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Recommendation recommendation = new Recommendation.Builder()
                        .recommendationID(resultSet.getString("bookID") + "_rec")
                        .memberID(memberID)
                        .bookID(resultSet.getString("bookID"))
                        .preferenceCategory(resultSet.getString("category"))
                        .popularityScore(0) // Popularity score can be computed as needed
                        .generatedTimestamp(String.valueOf(System.currentTimeMillis()))
                        .build();
                recommendationsList.add(recommendation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recommendationsList;
    }

    @Override
    public List<Recommendation> getPopularRecommendations() {
        List<Recommendation> recommendationsList = new ArrayList<>();
        String query = "SELECT * FROM Books ORDER BY (SELECT COUNT(*) FROM Reviews WHERE Reviews.bookID = Books.bookID AND Reviews.rating >= 4) DESC LIMIT 10";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Recommendation recommendation = new Recommendation.Builder()
                        .recommendationID(resultSet.getString("bookID") + "_popular")
                        .memberID(null)
                        .bookID(resultSet.getString("bookID"))
                        .preferenceCategory(resultSet.getString("category"))
                        .popularityScore(0) // Popularity score can be computed as needed
                        .generatedTimestamp(String.valueOf(System.currentTimeMillis()))
                        .build();
                recommendationsList.add(recommendation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recommendationsList;
    }
}
