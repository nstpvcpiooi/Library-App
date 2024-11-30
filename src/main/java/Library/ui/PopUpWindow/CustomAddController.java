package Library.ui.PopUpWindow;

import Library.backend.bookModel.Book;
import Library.ui.Notification.Notification;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CustomAddController extends PopUpController {

    @FXML
    private TextField authorInput;

    @FXML
    private TextField categoryInput;

    @FXML
    private TextField publishYearInput;

    @FXML
    private TextField titleInput;

    @FXML
    private TextField quantityInput;

    @FXML
    private TextField isbnCodeInput;

    @FXML
    private ImageView cover;

    @FXML
    private Button saveButton;

    @FXML
    private Button backButton;


    @FXML
    void Save(ActionEvent event) {
        boolean success = true;

        if (success) {
            // TODO: Add the new item to the database

            getPopUpWindow().close();

            Notification notification = new Notification("Chúc mừng!", "Bạn đã thêm sách vào thư viện thành công");
            notification.display();
        } else {
            Notification notification = new Notification("Lỗi!", "Không thể thêm sách vào thư viện");
            notification.display();
        }
    }

    @FXML
    void goBack(ActionEvent event) {
        getPopUpWindow().backtoAdd();
    }


    public void setData(Book selectedBook) {

        if (selectedBook != null) {
            isbnCodeInput.setText(selectedBook.getIsbn());

            try {
                Image image = new Image(selectedBook.getCoverCode());
                cover.setImage(image);
            } catch (Exception e) {
                cover.setImage(new Image("D:/My Code/lib2024-1117/src/main/resources/Library/image/default-cover.png"));
            }

        } else {
            isbnCodeInput.clear();
            cover.setImage(new Image("D:/My Code/lib2024-1117/src/main/resources/Library/image/default-cover.png"));
        }
    }
}
