package Library.backend.Review.Model;

public class Review {
    private String bookID; // ID của sách
    private int memberID; // ID của thành viên
    private int rating; // Đánh giá từ 1 đến 5
    private String reviewTimestamp; // Thời gian tạo đánh giá
    private String comment; // Nhận xét tùy chọn

    // Private constructor for the Builder
    private Review(Builder builder) {
        this.bookID = builder.bookID;
        this.memberID = builder.memberID;
        this.rating = builder.rating;
        this.reviewTimestamp = builder.reviewTimestamp;
        this.comment = builder.comment;
    }

    // Getters
    public String getBookID() {
        return bookID;
    }

    public int getMemberID() {
        return memberID;
    }

    public int getRating() {
        return rating;
    }

    public String getReviewTimestamp() {
        return reviewTimestamp;
    }

    public String getComment() {
        return comment;
    }

    // Setters
    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public void setMemberID(int memberID) {
        this.memberID = memberID;
    }

    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
        this.rating = rating;
    }

    public void setReviewTimestamp(String reviewTimestamp) {
        this.reviewTimestamp = reviewTimestamp;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Review{" +
                "bookID='" + bookID + '\'' +
                ", memberID=" + memberID +
                ", rating=" + rating +
                ", reviewTimestamp='" + reviewTimestamp + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }

    // Builder class
    public static class Builder {
        private String bookID;
        private int memberID;
        private int rating;
        private String reviewTimestamp;
        private String comment;

        public Builder bookID(String bookID) {
            this.bookID = bookID;
            return this;
        }

        public Builder memberID(int memberID) {
            this.memberID = memberID;
            return this;
        }

        public Builder rating(int rating) {
            if (rating < 1 || rating > 5) {
                throw new IllegalArgumentException("Rating must be between 1 and 5.");
            }
            this.rating = rating;
            return this;
        }

        public Builder reviewTimestamp(String reviewTimestamp) {
            this.reviewTimestamp = reviewTimestamp;
            return this;
        }

        public Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public Review build() {
            return new Review(this);
        }
    }
}
