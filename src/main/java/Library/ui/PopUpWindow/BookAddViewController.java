package Library.ui.PopUpWindow;

import Library.backend.bookModel.Book;
import Library.ui.Notification.Notification;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class BookAddViewController extends PopUpController {

    @FXML
    private Button CustomAddButton;

    @FXML
    protected TextField isbnCode;

    @FXML
    void displayCustomAdd(ActionEvent event) {
        getPopUpWindow().displayCustomAdd();
        isbnCode.clear();
    }

    @FXML
    void displayAddIsbn(ActionEvent event) {
        Book book = Book.fetchBookInfoFromAPI(isbnCode.getText());
        if (book != null) {
            getPopUpWindow().displayAddIsbn(book);

            isbnCode.clear();
        } else {
            Notification notification = new Notification("Lỗi!", "Vui lòng nhập lại mã ISBN");
            notification.display();
        }
    }

    @Override
    void close(ActionEvent event) {
        isbnCode.clear();
        super.close(event);
    }

    public TextField getIsbnCode() {
        return isbnCode;
    }
}
