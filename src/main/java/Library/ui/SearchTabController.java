package Library.ui;

import Library.MainApplication;
import Library.backend.bookModel.Book;
import Library.ui.BookCard.BookCardLargeController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;

public class SearchTabController {

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
     */
    @FXML
    private TextField SearchText;

    @FXML
    private ListView<Book> SearchResult;

    private MainController mainController;

    @FXML
    void BackToHome(ActionEvent event) {
        System.out.println("Back to Home Button Clicked");
        mainController.setContentPane(mainController.homeTab);
        mainController.setCurrentTab(mainController.getHomeButton());
    }

    public MainController getMainController() {
        return mainController;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        List<Book> ls = new ArrayList<>();

        // TODO HERE
        ls.add(new Book("", "RICH DAD & POOR DAD", "Robert T.Kiyosaki",
                1997, "Business", "978-3-16-148410-0",
                "image/img.png", 1));
        ls.add(new Book("", "THE RICHEST MAN IN BABYLON", "George Samuel Clason",
                1926, "Business", "978-3-16-148410-0",
                "image/img.png", 1));
        ls.add(new Book("", "THE 7 HABITS OF HIGHLY EFFECTIVE PEOPLE", "Stephen R.Covey",
                1989, "Business", "978-3-16-148410-0",
                "image/img.png", 1));
        ls.add(new Book("", "THINK AND GROW RICH", "Napoleon Hill",
                1937, "Business", "978-3-16-148410-0",
                "image/img.png", 1));

        SearchResult.setCellFactory(lv -> new BookListCell());
        SearchResult.getItems().addAll(ls);
    }

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
}
