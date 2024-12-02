package Library.ui.User;

import Library.backend.Login.Model.Member;
import Library.backend.Recommendation.Dao.MysqlRecommendationDao;
import Library.backend.Recommendation.Dao.RecommendationDao;
import Library.backend.Session.SessionManager;
import Library.backend.bookModel.Book;
import Library.ui.BookCard.BookCardCell;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static Library.ui.BookCard.BookCardCell.BookCardType.SMALL;

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

    RecommendationDao recommendationDao = MysqlRecommendationDao.getInstance();

    private UserMainController userMainController;

    /** Khi click vào nút tìm kiếm, chuyển sang tab tìm kiếm */
    @FXML
    void SearchButtonClicked(MouseEvent event) throws Exception {
        System.out.println("Search Button Clicked");
        userMainController.setContentPane(userMainController.searchTab);
        userMainController.setCurrentTab(userMainController.getSearchButton());

        // CLick/hover search tab button? chưa có
    }

    @FXML
    void SelectBook(MouseEvent event) {
        Book selectedBook = RecommendationList.getSelectionModel().getSelectedItem();
        getMainController().getPopUpWindow().displayInfo(selectedBook);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("HomeTabController initialized");
        RecommendationList.setCellFactory(lv -> new BookCardCell(SMALL));
        RecommendationList.getItems().addAll(getRecommendations());
    }


    // TODO Lấy reccomendation từ back-end
    private List<Book> getRecommendations() {

        SessionManager sessionManager = SessionManager.getInstance();
        Member member = sessionManager.getLoggedInMember();
        return recommendationDao.getCombinedRecommendations(member.getMemberID());

    }

    public UserMainController getMainController() {
        return userMainController;
    }

    public void setMainController(UserMainController userMainController) {
        this.userMainController = userMainController;
    }

}
