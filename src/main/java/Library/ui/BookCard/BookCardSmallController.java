package Library.ui.BookCard;

import Library.backend.Book.Model.Book;
import javafx.scene.image.Image;

import static Library.ui.MainController.DEFAULT_COVER;

/**
 * Controller cho một card sách nhỏ (hiển thị ảnh bìa, tiêu đề, tác giả).
 */
public class BookCardSmallController extends BookCardController {

    @Override
    public void setData(Book book) {
        cover.setImage(resolveCover(book));

        // 2. LẤY TIÊU ĐỀ
        title.setText(book.getTitle());

        // 3. LẤY TÊN TÁC GIẢ
        author.setText(book.getAuthor());
    }

    private Image resolveCover(Book book) {
        String coverCode = book.getCoverCode();
        if (coverCode == null || coverCode.isBlank()) {
            return DEFAULT_COVER;
        }
        try {
            return new Image(coverCode, true);
        } catch (RuntimeException e) {
            return DEFAULT_COVER;
        }
    }
}
