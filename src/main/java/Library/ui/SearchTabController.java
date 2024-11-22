package Library.ui;

import Library.MainApplication;
import Library.backend.bookModel.Book;
import Library.ui.BookCard.BookCardLargeController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class SearchTabController implements Initializable {

    /**
     * Button quay về trang chủ
     */
    @FXML
    private Button backButton;

    /**
     * Thanh tìm kiếm
     */
    @FXML
    private HBox SearchBar;

    /**
     * Văn bản nội dung tìm kiếm
     *
     * TODO: TÍNH NĂNG TÌM KIẾM
     */
    @FXML
    private TextField SearchText;

    /**
     * Danh sách kết quả tìm kiếm
     */
    @FXML
    private ListView<Book> SearchResult;

    private MainController mainController;

    /**
     * Khi click vào nút quay về trang chủ
     */
    @FXML
    void BackToHome(ActionEvent event) {
        System.out.println("Back to Home Button Clicked");
        mainController.setContentPane(mainController.homeTab);
        mainController.setCurrentTab(mainController.getHomeButton());
    }

    @FXML
    void search(KeyEvent event) {
        String query = SearchText.getText();
        SearchResult.getItems().clear();
        SearchResult.getItems().addAll(getSearchList(query));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SearchResult.setCellFactory(lv -> new BookListCell());
        SearchResult.getItems().addAll(getSearchList(""));
    }

    /**
     * Lấy danh sách kết quả tìm kiếm từ query
     *
     * @param query từ khóa tìm kiếm
     * @return danh sách kết quả tìm kiếm
     */
    private List<Book> getSearchList(String query) {
        List<Book> ls = new ArrayList<>();

        if (query.isEmpty()) {
            return Collections.emptyList();
        }

        // TODO HERE
        if (query.equals("Business")) {
            ls.add(new Book("", "RICH DAD & POOR DAD", "Robert T.Kiyosaki",
                    1997, "Business", "978-3-16-148410-0",
                    "image/img.png", 1));
        } else if (query.equals("Science")) {
            ls.add(new Book("", "A BRIEF HISTORY OF TIME", "Stephen Hawking",
                    1988, "Science", "978-3-16-148410-1",
                    "image/img.png", 1));
        } else if (query.equals("Literature")) {
            ls.add(new Book("", "THE GREAT GATSBY", "F. Scott Fitzgerald",
                    1925, "Literature", "978-3-16-148410-2",
                    "image/img.png", 1));
        } else if (query.equals("Technology")) {
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
        }

        // RETURN
        return ls;
    }

    /**
     * Cell cho ListView kết quả tìm kiếm (BookCardLarge)
     */
    private static class BookListCell extends ListCell<Book> {
        @Override
        protected void updateItem(Book book, boolean empty) {
            super.updateItem(book, empty);
            if (empty || book == null) {
                setText(null);
                setGraphic(null);
            } else {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(MainApplication.class.getResource("fxml/BookCard_large.fxml"));
                try {
                    HBox bookCard = loader.load();
                    BookCardLargeController controller = loader.getController();
                    controller.setData(book);
                    setGraphic(bookCard);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public MainController getMainController() {
        return mainController;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
