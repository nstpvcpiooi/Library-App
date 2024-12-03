package Library.ui.PopUpWindow;

import Library.backend.Login.Model.User;
import Library.backend.Request.DAO.RequestDAOImpl;
import Library.backend.Request.Model.Request;
import Library.backend.Session.SessionManager;
import Library.backend.bookDao.BookDao;
import Library.backend.bookDao.MysqlBookDao;
import Library.backend.bookModel.Book;
import Library.ui.Admin.AdminMainController;
import Library.ui.Utils.Notification;
import Library.ui.User.UserMainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.util.Scanner;

import static Library.ui.MainController.DEFAULT_COVER;

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
    private ImageView ImageQR;

    @FXML
    private Button ActionButton;

    private Book selectedBook;

    @FXML
    private Button RemoveButton;

    @FXML
    void Remove(ActionEvent event) {
        if (getPopUpWindow().getMainController() instanceof AdminMainController) {
            //  TODO: XÓA SÁCH
            selectedBook.deleteBook();
            ((AdminMainController) getPopUpWindow().getMainController()).libraryManageController.removeBook(selectedBook);
            getPopUpWindow().close();
            Notification notification = new Notification("Chúc mừng!", "Bạn đã xóa sách thành công");
            notification.display();

        } else if (getPopUpWindow().getMainController() instanceof UserMainController) {
            //  TODO: TRẢ SÁCH
            SessionManager sessionManager = SessionManager.getInstance();
            User user = new User(sessionManager.getLoggedInMember());
            user.createReturnRequest(selectedBook.getBookID());
            Request request = RequestDAOImpl.getInstance().getRequestByMemberIDAndBookID(user.getMemberID(), selectedBook.getBookID());
            RequestDAOImpl.getInstance().updateRequest(request);
            //request = RequestDAOImpl.getInstance().getRequestByMemberIDAndBookID(user.getMemberID(), selectedBook.getBookID());
            if (request.getStatus().equals("approved return")) {
                ActionButton.setText("MƯỢN SÁCH");

                ActionButton.getStyleClass().remove("BorrowedButton");
                ActionButton.setDisable(false);
                RemoveButton.setVisible(false);
                Notification notification = new Notification("Chúc mừng!", "Bạn đã trả sách thành công");
                notification.display();
            }
            else {
                ActionButton.setText("ĐÃ MƯỢN");
                ActionButton.getStyleClass().add("BorrowedButton");
                ActionButton.setDisable(true);
                RemoveButton.setText("ĐANG TRẢ");
                RemoveButton.setVisible(true);
                RemoveButton.setDisable(true);
                RemoveButton.getStyleClass().add("BorrowedButton");
            }





        }
    }


    @FXML
    void Action(ActionEvent event)  {
        if (getPopUpWindow().getMainController() instanceof AdminMainController) {
            getPopUpWindow().displayEdit(selectedBook);
        } else if (getPopUpWindow().getMainController() instanceof UserMainController) {

            // TODO: MƯỢN SÁCH
            SessionManager sessionManager = SessionManager.getInstance();
            User user = new User(sessionManager.getLoggedInMember());
            if (selectedBook.getQuantity()>0) {
                if (user.hasOverdueBook())
                {
                    Notification notification = new Notification("Lỗi!", "Bạn đang mượn sách quá hạn. Vui lòng trả sách trước khi mượn sách mới");
                    notification.display();
                    return;
                }
                user.createIssueRequest(selectedBook.getBookID());
                ActionButton.setText("ĐANG DUYỆT");
                ActionButton.getStyleClass().add("BorrowedButton");
                ActionButton.setDisable(true);
                RemoveButton.setText("TRẢ SÁCH");
                RemoveButton.setVisible(true);
            }
            else {
                ActionButton.setText("HẾT SÁCH");
                ActionButton.setDisable(true);
            }
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
            cover.setImage(DEFAULT_COVER);

            // demo với link ảnh trên web
//            cover.setImage (new Image("https://marketplace.canva.com/EAFaQMYuZbo/1/0/1003w/canva-brown-rusty-mystery-novel-book-cover-hG1QhA7BiBU.jpg"));
        }

        // 2. LẤY THONG TIN SÁCH
        title.setText(book.getTitle());
        author.setText(book.getAuthor());
        isbn.setText(book.getIsbn());
        category.setText(book.getCategory());
        publishyear.setText(String.valueOf(book.getPublishYear()));

        // TODO: HIỂN THỊ ẢNH QR
        // ImageQR.setImage(?????????????));

        if (getPopUpWindow().getMainController() instanceof UserMainController) {
            // nếu sách đã được mượn thì hiển thị nút trả sách
            // hàm kiểm tra sách đã mượn?

            // if (!book.isBorrowed()) { ????????
            SessionManager sessionManager = SessionManager.getInstance();
            User user = new User(sessionManager.getLoggedInMember());
            Request request = RequestDAOImpl.getInstance().getRequestByMemberIDAndBookID(user.getMemberID(), book.getBookID());
            if (request == null) {
                ActionButton.setText("MƯỢN SÁCH");
                ActionButton.getStyleClass().remove("BorrowedButton");
                ActionButton.setDisable(false);
                RemoveButton.setVisible(false);
            } else if (request.getStatus().equals("approved issue")) {
                ActionButton.setText("ĐÃ MƯỢN");
                ActionButton.getStyleClass().add("BorrowedButton");
                ActionButton.setDisable(true);
                RemoveButton.setText("TRẢ SÁCH");
                RemoveButton.getStyleClass().remove("BorrowedButton");
                RemoveButton.setVisible(true);
                RemoveButton.setDisable(false);
            } else if (request.getStatus().equals("pending issue")) {
                ActionButton.setText("ĐANG DUYỆT");
                ActionButton.getStyleClass().add("BorrowedButton");
                ActionButton.setDisable(true);
                RemoveButton.setText("TRẢ SÁCH");
                RemoveButton.setVisible(true);
            }
            else if (request.getStatus().equals("pending return")) {
                ActionButton.setText("ĐÃ MƯỢN");
                ActionButton.getStyleClass().add("BorrowedButton");
                ActionButton.setDisable(true);
                RemoveButton.setText("ĐANG TRẢ");
                RemoveButton.setVisible(true);
                RemoveButton.setDisable(true);
                RemoveButton.getStyleClass().add("BorrowedButton");
            }
            else {
                ActionButton.setText("MƯỢN SÁCH");
                ActionButton.getStyleClass().remove("BorrowedButton");
                ActionButton.setDisable(false);
                RemoveButton.setVisible(false);
            }


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
