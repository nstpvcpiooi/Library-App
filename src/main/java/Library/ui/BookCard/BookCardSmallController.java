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

            // demo với link ảnh trên web
//            cover.setImage (new Image("https://marketplace.canva.com/EAFaQMYuZbo/1/0/1003w/canva-brown-rusty-mystery-novel-book-cover-hG1QhA7BiBU.jpg"));
        }

        // 2. LẤY TIÊU ĐỀ
        title.setText(book.getTitle());

        // 3. LẤY TÊN TÁC GIẢ
        author.setText(book.getAuthor());
    }
}

