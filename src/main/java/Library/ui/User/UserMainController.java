package Library.ui.User;

import Library.MainApplication;
import Library.backend.Login.Model.Member;
import Library.backend.Session.SessionManager;
import Library.ui.MainController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class UserMainController extends MainController {

    @FXML
    private AnchorPane ContentPane;

    @FXML
    private Pane MyRequestButton;

    @FXML
    private Pane homeButton;

    @FXML
    private HBox profileButton;

    @FXML
    private AnchorPane root;

    @FXML
    private Pane searchButton;

    @FXML
    private Label userName;

    /** Home Tab */
    public HomeTabController homeTabController;
    public AnchorPane homeTab;

    /** Search Tab */
    public SearchTabController searchTabController;
    public AnchorPane searchTab;

    /** MyRequest Tab */
    public MyRequestTabController myRequestTabController;
    public AnchorPane myRequestTab;

    private Member currentUser;

    public Pane getSearchButton() {
        return searchButton;
    }

    public Pane getHomeButton() {
        return homeButton;
    }

    /**
     * Khởi tạo giao diện chính của ứng dụng.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setCurrentUser(SessionManager.getInstance().getLoggedInMember());
        System.out.println("Logged-in Member: " + getCurrentUser());

        // KHỞI TẠO HOME TAB
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("fxml/UserTab/HomeTabView.fxml"));
            homeTab = fxmlLoader.load();
            homeTabController = fxmlLoader.getController();
            homeTabController.setMainController(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // KHỞI TẠO SEARCH TAB
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("fxml/UserTab/SearchTabView.fxml"));
            searchTab = fxmlLoader.load();
            searchTabController = fxmlLoader.getController();
            searchTabController.setMainController(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // KHỞI TẠO MYREQUEST TAB
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("fxml/UserTab/MyRequestTabView.fxml"));
            myRequestTab = fxmlLoader.load();
            myRequestTabController = fxmlLoader.getController();
            myRequestTabController.setMainController(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ĐẶT HOME TAB LÀ TAB MẶC ĐỊNH KHI KHỞI ĐỘNG ỨNG DỤNG
        currentTab = homeButton;
        currentTab.getStyleClass().clear();
        currentTab.getStyleClass().add("MenuButtonPressed");

        ContentPane.getChildren().add(homeTab);

        // TODO: Hiển thị thông tin người dùng
        if (getCurrentUser() != null) {
            userName.setText("Xin chào, " + getCurrentUser().getUserName());
        } else {
            userName.setText("Xin chào, khách!");
        }

        super.initialize(location, resources);
    }

    /**
     * Xử lý sự kiện khi click vào các nút điều hướng -> hiển thị tab tương ứng (setContentPane).
     */
    @FXML
    void ButtonClick(MouseEvent event) {
        if (event.getSource().equals(profileButton)) {

            currentUser = SessionManager.getInstance().getLoggedInMember();
            System.out.println("Logged-in Member: " + currentUser);
            // TODO: Hiển thị NGƯỜI DÙNG VỪA ĐĂNG NHẬP
            if (currentUser != null) {
                getPopUpWindow().displayUser(currentUser);
                getPopUpWindow().getUserViewController().setTabTitle("THÔNG TIN CỦA BẠN");
            } else {
                System.err.println("Không tìm thấy thông tin người dùng!");
            }
            return;

        }
        setCurrentTab((Pane) event.getSource());

        if (currentTab.equals(homeButton)) {
            setContentPane(homeTab);
        } else if (currentTab.equals(searchButton)) {
            setContentPane(searchTab);
        } else if (currentTab.equals(MyRequestButton)) {
            setContentPane(myRequestTab);
        }
    }

}
