package Library.backend.Review.model;

import java.util.List;

public class Test {
    public static void main(String[] args) {
        // Test case: Create a new review using the Builder pattern
        Review review = new Review.Builder()
                .memberID(1) // memberID is now an integer
                .bookID("B001")
                .rating(5)
                .comment("Great book, very insightful!")
                .reviewTimestamp("2024-11-15") // Only date is required
                .build();

        System.out.println("Created Review: " + review);

        // Test case: Save the review to the database
        if (review.save()) {
            System.out.println("Review saved successfully.");
        } else {
            System.out.println("Failed to save the review.");
        }

        // Test case: Update the review
        review.setRating(4);
        review.setComment("Good book, but some parts were difficult to understand.");
        if (review.update()) {
            System.out.println("Review updated successfully.");
        } else {
            System.out.println("Failed to update the review.");
        }

        // Test case: Find the review by bookID and memberID
        Review foundReview = Review.findByBookAndMember("B001", 1);
        if (foundReview != null) {
            System.out.println("Found Review: " + foundReview);
        } else {
            System.out.println("Review not found.");
        }

        // Test case: Find reviews by book ID
        List<Review> reviewsForBook = Review.findByBookId("B001");
        System.out.println("Reviews for book B001:");
        for (Review r : reviewsForBook) {
            System.out.println(r);
        }

        // Test case: Find reviews by member ID
        List<Review> reviewsByMember = Review.findByMemberId(1);
        System.out.println("Reviews by member 1:");
        for (Review r : reviewsByMember) {
            System.out.println(r);
        }

        // Test case: Get average rating for a book
        double averageRating = Review.getAverageRating("B001");
        System.out.println("Average rating for book B001: " + averageRating);

        // Test case: Delete the review
        if (review.delete()) {
            System.out.println("Review deleted successfully.");
        } else {
            System.out.println("Failed to delete the review.");
        }
    }
}
