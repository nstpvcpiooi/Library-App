package Library.backend.Review.model;

import Library.backend.Review.Dao.MysqlReviewDao;

import java.util.List;

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

    // Convenience methods to interact with MysqlReviewDao
    public boolean save() {
        return MysqlReviewDao.getInstance().addReview(this);
    }

    public boolean update() {
        return MysqlReviewDao.getInstance().updateReview(this);
    }

    public boolean delete() {
        return MysqlReviewDao.getInstance().deleteReview(this.bookID, this.memberID);
    }

    public static Review findByBookAndMember(String bookID, int memberID) {
        return MysqlReviewDao.getInstance().getReviewByBookAndMember(bookID, memberID);
    }

    public static List<Review> findByBookId(String bookID) {
        return MysqlReviewDao.getInstance().getReviewsForBook(bookID);
    }

    public static List<Review> findByMemberId(int memberID) {
        return MysqlReviewDao.getInstance().getReviewsByMember(String.valueOf(memberID));
    }

    public static double getAverageRating(String bookID) {
        return MysqlReviewDao.getInstance().getAverageRatingForBook(bookID);
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
