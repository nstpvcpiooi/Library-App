package Library.ui.PopUpWindow;

import Library.backend.bookModel.Book;
import Library.ui.Admin.AdminMainController;
import Library.ui.Notification.Notification;
import Library.ui.User.UserMainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

/**
 * Controller cho cửa sổ hiển thị thông tin sách chi tiết
 */
public class BookInfoViewController extends PopUpController {

    @FXML
    private Label author;

    @FXML
    private AnchorPane container;

    @FXML
    private Button closeButton;

    @FXML
    private ImageView cover;

    @FXML
    private Label title;

    @FXML
    private Label isbn;

    @FXML
    private Label category;

    @FXML
    private Label publishyear;

    @FXML
    private Button ActionButton;

    private Book selectedBook;

    @FXML
    private Button RemoveButton;

    @FXML
    void Remove(ActionEvent event) {
        if (getPopUpWindow().getMainController() instanceof AdminMainController) {
            //  TODO: XÓA SÁCH

            getPopUpWindow().close();
            Notification notification = new Notification("Chúc mừng!", "Bạn đã xóa sách thành công");
            notification.display();

        } else if (getPopUpWindow().getMainController() instanceof UserMainController) {
            //  TODO: TRẢ SÁCH

            ActionButton.setText("MƯỢN SÁCH");
            ActionButton.getStyleClass().remove("BorrowedButton");
            ActionButton.setDisable(false);
            RemoveButton.setVisible(false);

            Notification notification = new Notification("Chúc mừng!", "Bạn đã trả sách thành công");
            notification.display();
        }
    }


    @FXML
    void Action(ActionEvent event) {
        if (getPopUpWindow().getMainController() instanceof AdminMainController) {
            getPopUpWindow().displayEdit(selectedBook);
        } else if (getPopUpWindow().getMainController() instanceof UserMainController) {

            // TODO: MƯỢN SÁCH

            ActionButton.setText("ĐANG MƯỢN");
            ActionButton.getStyleClass().add("BorrowedButton");
            ActionButton.setDisable(true);

            RemoveButton.setText("TRẢ SÁCH");
            RemoveButton.setVisible(true);
        }
    }

    public void setData(Book book) {
        selectedBook = book;

        // 1. LẤY ẢNH BÌA SÁCH
        try {
            // TODO KIỂM TRA ĐỊA CHỈ ẢNH BỊ LỖI?
            Image image = new Image(book.getCoverCode());
            cover.setImage(image);

        } catch (Exception e) {
            System.out.println("Error loading image from " + book.getCoverCode());
            cover.setImage(new Image("D:/My Code/lib2024-1117/src/main/resources/Library/image/default-cover.png"));

            // demo với link ảnh trên web
//            cover.setImage (new Image("https://marketplace.canva.com/EAFaQMYuZbo/1/0/1003w/canva-brown-rusty-mystery-novel-book-cover-hG1QhA7BiBU.jpg"));
        }

        // 2. LẤY THONG TIN SÁCH
        title.setText(book.getTitle());
        author.setText(book.getAuthor());
        isbn.setText(book.getIsbn());
        category.setText(book.getCategory());
        publishyear.setText(String.valueOf(book.getPublishYear()));

        if (getPopUpWindow().getMainController() instanceof UserMainController) {
            // nếu sách đã được mượn thì hiển thị nút trả sách
            // hàm kiểm tra sách đã mượn?

            // if (!book.isBorrowed()) { ????????
            ActionButton.setText("MƯỢN SÁCH");

            ActionButton.getStyleClass().remove("BorrowedButton");
            ActionButton.setDisable(false);
            RemoveButton.setVisible(false);

            // } else {
            //     ActionButton.setText("ĐANG MƯỢN");
            //     ActionButton.getStyleClass().add("BorrowedButton");
            //     ActionButton.setDisable(true);
            //     RemoveButton.setText("TRẢ SÁCH");
            //     RemoveButton.setVisible(true);
            // }

        } else {
            ActionButton.setText("CHỈNH SỬA");
            RemoveButton.setText("XÓA SÁCH");
            RemoveButton.setVisible(true);
        }

    }

}
