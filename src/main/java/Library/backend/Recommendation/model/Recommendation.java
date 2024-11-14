package Library.backend.Recommendation.model;

import java.util.List;
import Library.backend.Recommendation.Dao.MysqlRecommendationDao;
import Library.backend.Recommendation.Dao.RecommendationDao;

public class Recommendation {
    private String recommendationID;
    private String memberID;
    private String bookID;
    private String preferenceCategory; // Thể loại yêu thích của người dùng
    private int popularityScore; // Điểm số phổ biến của cuốn sách
    private String generatedTimestamp; // Thời gian tạo đề xuất

    // Private Constructor for Builder
    private Recommendation(Builder builder) {
        this.recommendationID = builder.recommendationID;
        this.memberID = builder.memberID;
        this.bookID = builder.bookID;
        this.preferenceCategory = builder.preferenceCategory;
        this.popularityScore = builder.popularityScore;
        this.generatedTimestamp = builder.generatedTimestamp;
    }

    // Getters
    public String getRecommendationID() {
        return recommendationID;
    }

    public String getMemberID() {
        return memberID;
    }

    public String getBookID() {
        return bookID;
    }

    public String getPreferenceCategory() {
        return preferenceCategory;
    }

    public int getPopularityScore() {
        return popularityScore;
    }

    public String getGeneratedTimestamp() {
        return generatedTimestamp;
    }

    // Setters
    public void setRecommendationID(String recommendationID) {
        this.recommendationID = recommendationID;
    }

    public void setMemberID(String memberID) {
        this.memberID = memberID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public void setPreferenceCategory(String preferenceCategory) {
        this.preferenceCategory = preferenceCategory;
    }

    public void setPopularityScore(int popularityScore) {
        this.popularityScore = popularityScore;
    }

    public void setGeneratedTimestamp(String generatedTimestamp) {
        this.generatedTimestamp = generatedTimestamp;
    }

    // Convenience methods to interact with MysqlRecommendationDao
    public boolean save() {
        return MysqlRecommendationDao.getInstance().addRecommendation(this);
    }

    public boolean update() {
        return MysqlRecommendationDao.getInstance().updateRecommendation(this);
    }

    public boolean delete() {
        return MysqlRecommendationDao.getInstance().deleteRecommendation(this.recommendationID);
    }

    public static Recommendation findById(String recommendationID) {
        return MysqlRecommendationDao.getInstance().getRecommendationById(recommendationID);
    }

    public static List<Recommendation> findByMemberId(String memberID) {
        return MysqlRecommendationDao.getInstance().getRecommendationsForMember(memberID);
    }

    public static List<Recommendation> findRecommendationsBasedOnPreferencesAndRequests(String memberID) {
        return MysqlRecommendationDao.getInstance().getRecommendationsBasedOnPreferencesAndRequests(memberID);
    }

    public static List<Recommendation> findPopularRecommendations() {
        return MysqlRecommendationDao.getInstance().getPopularRecommendations();
    }

    @Override
    public String toString() {
        return "Recommendation{" +
                "recommendationID='" + recommendationID + '\'' +
                ", memberID='" + memberID + '\'' +
                ", bookID='" + bookID + '\'' +
                ", preferenceCategory='" + preferenceCategory + '\'' +
                ", popularityScore=" + popularityScore +
                ", generatedTimestamp='" + generatedTimestamp + '\'' +
                '}';
    }

    // Builder Class
    public static class Builder {
        private String recommendationID;
        private String memberID;
        private String bookID;
        private String preferenceCategory;
        private int popularityScore;
        private String generatedTimestamp;

        public Builder recommendationID(String recommendationID) {
            this.recommendationID = recommendationID;
            return this;
        }

        public Builder memberID(String memberID) {
            this.memberID = memberID;
            return this;
        }

        public Builder bookID(String bookID) {
            this.bookID = bookID;
            return this;
        }

        public Builder preferenceCategory(String preferenceCategory) {
            this.preferenceCategory = preferenceCategory;
            return this;
        }

        public Builder popularityScore(int popularityScore) {
            this.popularityScore = popularityScore;
            return this;
        }

        public Builder generatedTimestamp(String generatedTimestamp) {
            this.generatedTimestamp = generatedTimestamp;
            return this;
        }

        public Recommendation build() {
            return new Recommendation(this);
        }
    }
}
