package Library.ui.BookCard;

import Library.backend.Request.service.RequestService;
import Library.backend.Request.Model.Request;
import Library.backend.Session.SessionManager;
import Library.backend.Book.Model.Book;
import Library.backend.Review.service.ReviewService;
import javafx.scene.image.Image;

import static Library.ui.MainController.DEFAULT_COVER;

/**
 * Controller cho một card sách lớn (hiển thị ảnh bìa, tiêu đề, tác giả).
 */
public class BookCardLargeController extends BookCardController {

    @Override
    public void setData(Book book) {
        // 1. LẤY ẢNH BÌA SÁCH


        // 2. LẤY TIÊU ĐỀ
        title.setText(book.getTitle());
        // Hiển thị rating trung bình và số lượt đánh giá (chỉ xem, không cho tương tác)
        ReviewService reviewService = ReviewService.getInstance();
        double rating = reviewService.getAverageRating(book.getBookID());
        int ratingTotal = reviewService.getRatingCount(book.getBookID());
        averageRating.setRating(rating);
        averageRating.setDisable(true);
        averageRating.setMouseTransparent(true);
        ratingCount.setText("(" + ratingTotal + " đánh giá)");

        if (SessionManager.getInstance().getLoggedInMember().getDuty()==0) {
            Request request = RequestService.getInstance()
                    .getLatestRequest(SessionManager.getInstance().getLoggedInMember().getMemberID(), book.getBookID());
            if (request != null) {
                if (request.isOverdue()) {
                    OverdueTag.setText("Quá hạn");
                } else {
                    OverdueTag.setVisible(false);
                }
            }
        }
        // 3. LẤY TÊN TÁC GIẢ
        author.setText(book.getAuthor());

        if (SessionManager.getInstance().getLoggedInMember().getDuty()==1)   {
            OverdueTag.setVisible(false);

            quantity.setText("Số lượng: " + book.getQuantity());
            quantity.setVisible(true);
        } else {
            quantity.setVisible(false);
        }
        // if request exists and returnDate is null, show due date
        // (because it is not returned yet so we can show due date)
        // add highlight to the card if overdue
        Request request = RequestService.getInstance()
                .getLatestRequest(SessionManager.getInstance().getLoggedInMember().getMemberID(), book.getBookID());
        if (request != null && request.getReturnDate() == null) {
            System.out.println(request.getDueDate());
        }


    }
}

