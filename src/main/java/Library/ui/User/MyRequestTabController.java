package Library.ui.User;

import Library.backend.Request.DAO.RequestDAOImpl;
import Library.backend.Session.SessionManager;
import Library.backend.bookModel.Book;
import Library.ui.BookCard.BookCardCell;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static Library.ui.BookCard.BookCardCell.BookCardType.LARGE;

public class MyRequestTabController implements Initializable {

    @FXML
    private ListView<Book> BorrowedBooks;

    private UserMainController userMainController;

    @FXML
    void SelectBook(MouseEvent event) {
        Book selectedBook = BorrowedBooks.getSelectionModel().getSelectedItem();
        getMainController().getPopUpWindow().displayInfo(selectedBook);
    }

    private List<Book> getBorrowedBooks() {


        return RequestDAOImpl.getInstance().getBooksByMemberID(SessionManager.getInstance().getLoggedInMember().getMemberID());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        BorrowedBooks.setCellFactory(lv -> new BookCardCell(LARGE));
        BorrowedBooks.getItems().addAll(getBorrowedBooks());
    }

    public UserMainController getMainController() {
        return userMainController;
    }

    public void setMainController(UserMainController userMainController) {
        this.userMainController = userMainController;
    }
}
