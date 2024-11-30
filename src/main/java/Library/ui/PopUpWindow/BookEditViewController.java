package Library.ui.PopUpWindow;

import Library.backend.bookModel.Book;
import Library.ui.Notification.Notification;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class BookEditViewController extends PopUpController {
    @FXML
    private Button CancelButton;

    @FXML
    private Button SaveButton;

    @FXML
    private TextField authorInput;

    @FXML
    private TextField categoryInput;

    @FXML
    private ImageView cover;

    @FXML
    private TextField isbnCodeInput;

    @FXML
    private TextField publishYearInput;

    @FXML
    private TextField quantityInput;

    @FXML
    private TextField titleInput;

    @FXML
    void Save(ActionEvent event) {
        boolean success = true;

        if (success) {
            // TODO: Edit the item in the database

            getPopUpWindow().close();

            Notification notification = new Notification("Chúc mừng!", "Bạn đã chỉnh sửa sách thành công");
            notification.display();
        } else {
            Notification notification = new Notification("Lỗi!", "Vui lòng thử lại");
            notification.display();
        }
    }

    @FXML
    void goBack(ActionEvent event) {
        getPopUpWindow().backtoInfo();
    }

    public void setData(Book selectedBook) {
        isbnCodeInput.setText(selectedBook.getIsbn());

        try {
            Image image = new Image(selectedBook.getCoverCode());
            cover.setImage(image);
        } catch (Exception e) {
            cover.setImage(new Image("D:/My Code/lib2024-1117/src/main/resources/Library/image/default-cover.png"));
        }
    }
}
