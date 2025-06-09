import Library.backend.Login.DAO.MemberDAO;
import Library.backend.Login.DAO.MemberDAOImpl;
import Library.backend.Login.Model.Member;
import Library.backend.Recommendation.Dao.MysqlRecommendationDao;
import Library.backend.Recommendation.Dao.RecommendationDao;
import Library.backend.bookDao.GoogleBookDao;
import Library.backend.bookModel.Book;
import Library.backend.database.JDBCUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class MainTest {
    public static void main(String[] args) {
        // Test database connection
        try (Connection conn = JDBCUtil.getConnection()) {
            System.out.println("Database connection successful!");
            
            // Check if tables exist and have data
            try (Statement stmt = conn.createStatement()) {
                // Check Books table
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM Books");
                if (rs.next()) {
                    System.out.println("Number of books in database: " + rs.getInt("count"));
                }
                
                // Check Requests table
                rs = stmt.executeQuery("SELECT COUNT(*) as count FROM Requests");
                if (rs.next()) {
                    System.out.println("Number of requests in database: " + rs.getInt("count"));
                }
                
                // Check Reviews table
                rs = stmt.executeQuery("SELECT COUNT(*) as count FROM Reviews");
                if (rs.next()) {
                    System.out.println("Number of reviews in database: " + rs.getInt("count"));
                }
            }
        } catch (Exception e) {
            System.out.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // Test member retrieval
        MemberDAOImpl memberDAO = MemberDAOImpl.getInstance();
        Member member = memberDAO.getMemberByUsername("ppmelon2004");
        if (member == null) {
            System.out.println("Member not found!");
            return;
        }
        System.out.println("Found member with ID: " + member.getMemberID());

        // Test each recommendation type separately
        MysqlRecommendationDao mysqlRecommendationDao = MysqlRecommendationDao.getInstance();
        
        System.out.println("\nTesting Popular Recommendations:");
        List<Book> popularBooks = mysqlRecommendationDao.getPopularRecommendations();
        System.out.println("Popular books count: " + popularBooks.size());
        for (Book book : popularBooks) {
            System.out.println("- " + book.getTitle());
        }

        System.out.println("\nTesting Recommendations Based on Borrow History:");
        List<Book> historyBooks = mysqlRecommendationDao.getRecommendationsBasedOnBorrowHistory(member.getMemberID());
        System.out.println("History-based recommendations count: " + historyBooks.size());
        for (Book book : historyBooks) {
            System.out.println("- " + book.getTitle());
        }

        System.out.println("\nTesting Recommendations From Similar Users:");
        List<Book> similarUserBooks = mysqlRecommendationDao.getRecommendationsFromSimilarUsers(member.getMemberID());
        System.out.println("Similar user recommendations count: " + similarUserBooks.size());
        for (Book book : similarUserBooks) {
            System.out.println("- " + book.getTitle());
        }

        System.out.println("\nTesting Combined Recommendations:");
        List<Book> combinedBooks = mysqlRecommendationDao.getCombinedRecommendations(member.getMemberID());
        System.out.println("Combined recommendations count: " + combinedBooks.size());
        for (Book book : combinedBooks) {
            System.out.println("Title: " + book.getTitle());
            System.out.println("Author: " + book.getAuthor());
            System.out.println("Category: " + book.getCategory());
            System.out.println("-------------------");
        }
    }
}
