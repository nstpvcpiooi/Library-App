package Library.backend.Recommendation.Model;

public class Recommendation {
    private int memberID; // ID của thành viên
    private String preferenceCategory; // Thể loại yêu thích của người dùng

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
