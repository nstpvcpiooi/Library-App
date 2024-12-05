package Library.ui.BookCard;

import Library.backend.bookModel.Book;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public abstract class BookCardController {
    /**
     * Ô chứa thông tin sách (ảnh, tiêu đề, tác giả).
     */
    @FXML
    protected HBox container;

    /**
     * Ảnh bìa sách.
     */
    @FXML
    protected ImageView cover;

    /**
     * Tiêu đề sách.
     */
    @FXML
    protected Label title;

    /**
     * Tác giả sách.
     */
    @FXML
    protected Label author;
    @FXML
    protected Label quantity;
    @FXML
    protected Label OverdueTag;
    /**
     * Hiển thị thông tin sách lên giao diện (lấy link ảnh bìa, tiêu đề, tác giả từ đối tượng sách
     * và hiển thị lên container).
     */
    public abstract void setData(Book book);
}
