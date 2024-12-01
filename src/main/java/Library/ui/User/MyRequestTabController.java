package Library.ui.User;

import Library.backend.bookModel.Book;
import Library.ui.BookCard.BookCardCell;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ArrayList;
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
        List<Book> ls = new ArrayList<>();

        ls.add(new Book("", "STEVE JOBS", "Walter Isaacson",
                2011, "Technology", "978-3-16-148410-3",
                "image/img.png", 1));
        ls.add(new Book("", "SAPIENS", "Yuval Noah Harari",
                2011, "History", "978-3-16-148410-4",
                "image/img.png", 1));

        return ls;
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
