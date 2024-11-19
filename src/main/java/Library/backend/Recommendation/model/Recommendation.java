package Library.backend.Recommendation.model;

import Library.backend.Recommendation.Dao.MysqlRecommendationDao;
import Library.backend.bookModel.Book;

import java.util.List;

public class Recommendation {
    private int memberID; // ID của thành viên
    private String preferenceCategory; // Thể loại yêu thích của người dùng

    // Singleton instance of DAO
    private static final MysqlRecommendationDao daoInstance = MysqlRecommendationDao.getInstance();

    // Private Constructor for Builder
    private Recommendation(Builder builder) {
        this.memberID = builder.memberID;
        this.preferenceCategory = builder.preferenceCategory;
    }

    // Getters
    public int getMemberID() {
        return memberID;
    }

    public String getPreferenceCategory() {
        return preferenceCategory;
    }

    // Setters
    public void setMemberID(int memberID) {
        this.memberID = memberID;
    }

    public void setPreferenceCategory(String preferenceCategory) {
        this.preferenceCategory = preferenceCategory;
    }

    // DAO Methods integrated into the model
    public boolean save() {
        return daoInstance.addRecommendation(this);
    }

    public static List<Recommendation> findByMemberId(int memberID) {
        return daoInstance.getRecommendationsForMember(memberID);
    }

    public static List<Book> findRecommendationsBasedOnPreferencesAndRequests(int memberID) {
        return daoInstance.getRecommendationsBasedOnPreferencesAndRequests(memberID);
    }

    public static List<Book> findPopularRecommendations() {
        return daoInstance.getPopularRecommendations();
    }

    public static List<Book> findRecommendationsBasedOnBorrowHistory(int memberID) {
        return daoInstance.getRecommendationsBasedOnBorrowHistory(memberID);
    }

    public static List<Book> findRecommendationsFromSimilarUsers(int memberID) {
        return daoInstance.getRecommendationsFromSimilarUsers(memberID);
    }

    public static List<Book> getCombinedRecommendations(int memberID) {
        return daoInstance.getCombinedRecommendations(memberID);
    }
    @Override
    public String toString() {
        return "Recommendation{" +
                "memberID=" + memberID +
                ", preferenceCategory='" + preferenceCategory + '\'' +
                '}';
    }

    // Builder Class
    public static class Builder {
        private int memberID;
        private String preferenceCategory;

        public Builder memberID(int memberID) {
            this.memberID = memberID;
            return this;
        }

        public Builder preferenceCategory(String preferenceCategory) {
            this.preferenceCategory = preferenceCategory;
            return this;
        }

        public Recommendation build() {
            return new Recommendation(this);
        }
    }
}
