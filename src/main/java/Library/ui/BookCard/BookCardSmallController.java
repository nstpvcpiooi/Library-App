package Library.ui.BookCard;

import Library.backend.bookModel.Book;
import javafx.scene.image.Image;

import static Library.ui.MainController.DEFAULT_COVER;

/**
 * Controller cho một card sách nhỏ (hiển thị ảnh bìa, tiêu đề, tác giả).
 */
public class BookCardSmallController extends BookCardController {

    @Override
    public void setData(Book book) {

        // 1. LẤY ẢNH BÌA SÁCH
        try {
            // TODO KIỂM TRA ĐỊA CHỈ ẢNH BỊ LỖI?
            Image image = new Image(book.getCoverCode());
            cover.setImage(image);

        } catch (Exception e) {
            System.out.println("Error loading image from " + book.getCoverCode());
            cover.setImage(DEFAULT_COVER);
        }

        // 2. LẤY TIÊU ĐỀ
        title.setText(book.getTitle());

        // 3. LẤY TÊN TÁC GIẢ
        author.setText(book.getAuthor());
    }
}

