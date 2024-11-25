package Library.ui.User;

import Library.MainApplication;
import Library.backend.bookModel.Book;
import Library.ui.BookCard.BookCardSmallController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller cho home tab.
 */
public class HomeTabController implements Initializable {

    /** Nút tìm kiếm. Khi bấm vào sẽ chuyển sang Search tab */
    @FXML
    private Pane searchBar;

    /** LIST SÁCH ĐỀ XUẤT */
    @FXML
    private ListView<Book> RecommendationList;

    /** Box chào mừng khi mở app */
    @FXML
    private VBox welcomeBox;

    private UserMainController userMainController;

    /** Khi click vào nút tìm kiếm, chuyển sang tab tìm kiếm */
    @FXML
    void SearchButtonClicked(MouseEvent event) throws Exception {
        System.out.println("Search Button Clicked");
        userMainController.setContentPane(userMainController.searchTab);
        userMainController.setCurrentTab(userMainController.getSearchButton());

        // CLick/hover search tab button? chưa có
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("HomeTabController initialized");

        RecommendationList.setCellFactory(lv -> new HomeTabController.BookListCell());
        RecommendationList.getItems().addAll(getRecommendations());
    }


    // TODO Lấy reccomendation từ back-end
    private List<Book> getRecommendations() {
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
        ls.add(new Book("", "RICH DAD & POOR DAD", "Robert T.Kiyosaki",
                1997, "Business", "978-3-16-148410-0",
                "image/img.png", 1));
        ls.add(new Book("", "I LOVE YOU", "Robert T.Kiyosaki",
                1997, "Business", "978-3-16-148410-0",
                "image/img.png", 1));

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
                FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("fxml/BookCard/BookCard_small.fxml"));
                try {
                    HBox bookCard = loader.load();
                    BookCardSmallController controller = loader.getController();
                    controller.setData(book);
                    setGraphic(bookCard);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public UserMainController getMainController() {
        return userMainController;
    }

    public void setMainController(UserMainController userMainController) {
        this.userMainController = userMainController;
    }

}
