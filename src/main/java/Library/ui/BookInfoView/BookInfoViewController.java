package Library.ui.BookInfoView;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controller cho cửa sổ hiển thị thông tin sách chi tiết
 */
public class BookInfoViewController {

    @FXML
    private Button closeButton;

    public BookInfoView getBookInfoView() {
        return bookInfoView;
    }

    public void setBookInfoView(BookInfoView bookInfoView) {
        this.bookInfoView = bookInfoView;
    }

    private BookInfoView bookInfoView;

    @FXML
    void close(ActionEvent event) {
        bookInfoView.close();
    }

}
