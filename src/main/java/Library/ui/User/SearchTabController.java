package Library.ui.User;

import Library.backend.bookModel.Book;
import Library.ui.BookCard.BookCardCell;
import Library.ui.Utils.SearchUtils;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static Library.ui.BookCard.BookCardCell.BookCardType.LARGE;

public class SearchTabController extends UserTabController implements Initializable, SearchUtils {

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
    void SelectBook(MouseEvent event) {
        Book selectedBook = SearchResult.getSelectionModel().getSelectedItem();
        getMainController().getPopUpWindow().displayInfo(selectedBook);
    }

    private final PauseTransition debounce = makeDebounce();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SearchResult.setCellFactory(lv -> new BookCardCell(LARGE));

//        /* Đăng ký debounce cho ô tìm kiếm */
//        SearchText.textProperty().addListener((obs, oldTxt, newTxt) -> {
//            pause.stop();                        // gõ tiếp → reset timer
//            pause.setOnFinished(e -> triggerSearch(newTxt, SearchResult));
//            pause.playFromStart();               // 400 ms sau nếu không gõ nữa → tìm
//        });
//
//        triggerSearch("", SearchResult);   // tải danh sách ban đầu

        SearchText.textProperty().addListener((obs,o,n) -> {
            debounce.stop();
            debounce.setOnFinished(e -> triggerSearch(n, SearchResult));
            debounce.playFromStart();
        });

        triggerSearch("", SearchResult);          // tải lần đầu
    }

    public UserMainController getMainController() {
        return userMainController;
    }

    public void setMainController(UserMainController userMainController) {
        this.userMainController = userMainController;
    }

}
