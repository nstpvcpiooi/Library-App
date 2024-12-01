package Library.ui.User;

import Library.MainApplication;
import Library.ui.Admin.demoUser;
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

/**
 * Controller cho giao diện chính của ứng dụng.
 * (gồm các nút điều hướng và phần chứa nội dung chính của các tab)
 */
public class UserMainController extends MainController {

    /**
     * Các nút điều hướng giữa các tab.
     */
    @FXML
    private Pane homeButton;

    @FXML
    private Pane searchButton;

    @FXML
    private HBox profileButton;

    @FXML
    private Pane MyRequestButton;

    /** Home Tab */
    public HomeTabController homeTabController;
    public AnchorPane homeTab;

    /** Search Tab */
    public SearchTabController searchTabController;
    public AnchorPane searchTab;

//    /** Profile Tab */
//    public ProfileTabController profileTabController;
//    public AnchorPane profileTab;

    /** MyRequest Tab */
    public MyRequestTabController myRequestTabController;
    public AnchorPane myRequestTab;

    @FXML
    private Label userName;

    /**
     * Xử lý sự kiện khi click vào các nút điều hướng -> hiển thị tab tương ứng (setContentPane).
     */
    @FXML
    void ButtonClick(MouseEvent event) {
        if (event.getSource().equals(profileButton)) {
            // TODO: Hiển thị NGƯỜI DÙNG VỪA ĐĂNG NHẬP
            demoUser user = new demoUser("usernam1", "password1",
                    "email1@gmail.com", "0912345678", 1, "preference1");

            getPopUpWindow().displayUser(user);
            getPopUpWindow().getUserViewController().setTabTitle("THÔNG TIN CỦA BẠN");
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

    /**
     * Khởi tạo giao diện chính của ứng dụng.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

//        // KHỞI TẠO PROFILE TAB
//        try {
//            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("fxml/UserTab/ProfileTabView.fxml"));
//            profileTab = fxmlLoader.load();
//            profileTabController = fxmlLoader.getController();
//            profileTabController.setMainController(this);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

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

        userName.setText("Xin chào, ppmelon2004");

        super.initialize(location, resources);
    }


    public Pane getHomeButton() {
        return homeButton;
    }

    public Pane getSearchButton() {
        return searchButton;
    }

    public Pane getProfileButton() {
        return profileButton;
    }
}