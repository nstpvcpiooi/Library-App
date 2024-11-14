package Library.backend.Review.model;

import Library.backend.Review.Dao.MysqlReviewDao;
import java.util.List;

public class Review {
    private String reviewID;
    private String memberID;
    private String bookID;
    private int rating; // Rating for the book, value from 1 to 5
    private String comment; // Optional comment by the user
    private String reviewTimestamp; // Time when the review was created

    // Private constructor for the builder
    private Review(Builder builder) {
        this.reviewID = builder.reviewID;
        this.memberID = builder.memberID;
        this.bookID = builder.bookID;
        this.rating = builder.rating;
        this.comment = builder.comment;
        this.reviewTimestamp = builder.reviewTimestamp;
    }

    // Getters
    public String getReviewID() {
        return reviewID;
    }

    public String getMemberID() {
        return memberID;
    }

    public String getBookID() {
        return bookID;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public String getReviewTimestamp() {
        return reviewTimestamp;
    }

    // Setters
    public void setReviewID(String reviewID) {
        this.reviewID = reviewID;
    }

    public void setMemberID(String memberID) {
        this.memberID = memberID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setReviewTimestamp(String reviewTimestamp) {
        this.reviewTimestamp = reviewTimestamp;
    }

    // Convenience methods to interact with MysqlReviewDao
    public boolean save() {
        return MysqlReviewDao.getInstance().addReview(this);
    }

    public boolean update() {
        return MysqlReviewDao.getInstance().updateReview(this);
    }

    public boolean delete() {
        return MysqlReviewDao.getInstance().deleteReview(this.reviewID);
    }

    public static Review findById(String reviewID) {
        return MysqlReviewDao.getInstance().getReviewById(reviewID);
    }

    public static List<Review> findByBookId(String bookID) {
        return MysqlReviewDao.getInstance().getReviewsForBook(bookID);
    }

    public static List<Review> findByMemberId(String memberID) {
        return MysqlReviewDao.getInstance().getReviewsByMember(memberID);
    }

    public static double getAverageRating(String bookID) {
        return MysqlReviewDao.getInstance().getAverageRatingForBook(bookID);
    }

    @Override
    public String toString() {
        return "Review{" +
                "reviewID='" + reviewID + '\'' +
                ", memberID='" + memberID + '\'' +
                ", bookID='" + bookID + '\'' +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", reviewTimestamp='" + reviewTimestamp + '\'' +
                '}';
    }

    // Builder class
    public static class Builder {
        private String reviewID;
        private String memberID;
        private String bookID;
        private int rating;
        private String comment;
        private String reviewTimestamp;

        public Builder reviewID(String reviewID) {
            this.reviewID = reviewID;
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

        public Builder rating(int rating) {
            if (rating < 1 || rating > 5) {
                throw new IllegalArgumentException("Rating must be between 1 and 5");
            }
            this.rating = rating;
            return this;
        }

        public Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public Builder reviewTimestamp(String reviewTimestamp) {
            this.reviewTimestamp = reviewTimestamp;
            return this;
        }

        public Review build() {
            return new Review(this);
        }
    }
}
