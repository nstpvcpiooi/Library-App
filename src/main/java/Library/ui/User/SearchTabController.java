package Library.ui.User;

import Library.backend.bookModel.Book;
import Library.ui.BookCard.BookCardCell;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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

    private UserMainController userMainController;

    /**
     * Khi click vào nút quay về trang chủ
     */
    @FXML
    void BackToHome(ActionEvent event) {
        System.out.println("Back to Home Button Clicked");
        userMainController.setContentPane(userMainController.homeTab);
        userMainController.setCurrentTab(userMainController.getHomeButton());
    }

    @FXML
    void search(KeyEvent event) {
        String query = SearchText.getText();
        SearchResult.getItems().clear();
        SearchResult.getItems().addAll(getSearchList(query));
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

    /**
     * Lấy danh sách kết quả tìm kiếm từ query
     *
     * @param query từ khóa tìm kiếm
     * @return danh sách kết quả tìm kiếm
     */
    private List<Book> getSearchList(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Book.searchBooksValue("");
        }
        return Book.searchBooksValue(query);
    }

    public UserMainController getMainController() {
        return userMainController;
    }

    public void setMainController(UserMainController userMainController) {
        this.userMainController = userMainController;
    }
}
