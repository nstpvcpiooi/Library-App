package Library.ui.User;

import Library.backend.Request.DAO.RequestDAOImpl;
import Library.backend.Request.Model.Request;
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
import java.util.stream.Collectors;

import static Library.ui.BookCard.BookCardCell.BookCardType.LARGE;

public class MyRequestTabController extends UserTabController implements Initializable {

    @FXML
    private ListView<Book> BorrowedBooks;

    @FXML
    void SelectBook(MouseEvent event) {
        Book selectedBook = BorrowedBooks.getSelectionModel().getSelectedItem();
        getMainController().getPopUpWindow().displayInfo(selectedBook);
    }

    private List<Book> getBorrowedBooks() {
        List<Book> allBooks = RequestDAOImpl.getInstance().getBooksByMemberID(SessionManager.getInstance().getLoggedInMember().getMemberID());
        return allBooks.stream()
            .filter(book -> {
                Request request = RequestDAOImpl.getInstance().getRequestByMemberIDAndBookID(
                    SessionManager.getInstance().getLoggedInMember().getMemberID(),
                    book.getBookID()
                );
                return request != null && request.getStatus().equals("approved issue");
            })
            .collect(Collectors.toList());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        BorrowedBooks.setCellFactory(lv -> new BookCardCell(LARGE));
        BorrowedBooks.getItems().addAll(getBorrowedBooks());
    }

}
