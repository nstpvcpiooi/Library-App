package Library.ui.Admin;

import Library.backend.bookModel.Book;
import Library.ui.BookCard.BookCardCell;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import static Library.ui.BookCard.BookCardCell.BookCardType.LARGE;

public class LibraryManageController implements Initializable {

    @FXML
    private HBox AddButton;

    @FXML
    private HBox SearchBar;

    @FXML
    private ListView<Book> SearchResult;

    @FXML
    private TextField SearchText;

    private AdminMainController MainController;


    @FXML
    void AddBook(MouseEvent event) {
        getMainController().getPopUpWindow().displayAdd();
    }

    @FXML
    void search(KeyEvent event) {
        String query = SearchText.getText();
        SearchResult.getItems().clear();
        SearchResult.getItems().addAll(getSearchList(query));
    }

    /**
     * Lấy danh sách kết quả tìm kiếm từ query
     *
     * @param query từ khóa tìm kiếm
     * @return danh sách kết quả tìm kiếm
     */
    private List<Book> getSearchList(String query) {
        List<Book> ls = new ArrayList<>();

        // TODO HERE
            ls.add(new Book("", "RICH DAD & POOR DAD", "Robert T.Kiyosaki",
                    1997, "Business", "978-3-16-148410-0",
                    "image/img.png", 1));
            ls.add(new Book("", "A BRIEF HISTORY OF TIME", "Stephen Hawking",
                    1988, "Science", "978-3-16-148410-1",
                    "image/img.png", 1));
            ls.add(new Book("", "THE GREAT GATSBY", "F. Scott Fitzgerald",
                    1925, "Literature", "978-3-16-148410-2",
                    "image/img.png", 1));
            ls.add(new Book("", "STEVE JOBS", "Walter Isaacson",
                    2011, "Technology", "978-3-16-148410-3",
                    "image/img.png", 1));
            ls.add(new Book("", "SAPIENS", "Yuval Noah Harari",
                    2011, "History", "978-3-16-148410-4",
                    "image/img.png", 1));
            ls.add(new Book("", "THE ALCHEMIST", "Paulo Coelho",
                    1988, "Novel", "978-3-16-148410-5",
                    "image/img.png", 1));
            ls.add(new Book("", "THE POWER OF HABIT", "Charles Duhigg",
                    2012, "Health", "978-3-16-148410-7",
                    "image/img.png", 1));
            ls.add(new Book("", "SALT, FAT, ACID, HEAT", "Samin Nosrat",
                    2017, "Cooking", "978-3-16-148410-8",
                    "image/img.png", 1));

        if (query.equals("Business")) {
            return Collections.singletonList(ls.get(0));
        } else if (query.isEmpty()) {
            return ls;
        } else {
            return Collections.emptyList();
        }
    }

    @FXML
    void SelectBook(MouseEvent event) {
        Book selectedBook = SearchResult.getSelectionModel().getSelectedItem();
        getMainController().getPopUpWindow().displayInfo(selectedBook);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SearchResult.setCellFactory(lv -> new BookCardCell(LARGE));
        SearchResult.getItems().addAll(getSearchList(""));
    }

    public void setMainController(AdminMainController adminMainController) {
        this.MainController = adminMainController;
    }

    public AdminMainController getMainController() {
        return MainController;
    }
}
