package Library.ui.User;

import Library.MainApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller cho giao diện chính của ứng dụng.
 * (gồm các nút điều hướng và phần chứa nội dung chính của các tab)
 */
public class UserMainController implements Initializable {

    /**
     * Các nút điều hướng giữa các tab.
     */
    @FXML
    private Pane homeButton;

    @FXML
    private Pane categoryButton;

    @FXML
    private Pane settingsButton;

    @FXML
    private Pane searchButton;

    @FXML
    private Pane profileButton;

    /**
     * Nút hiện tại đang được chọn.
     */
    private Pane currentTab;

    /**
     * Phần chứa nội dung của các tab. (Home, Search, History, Profile...)
     */
    @FXML
    private AnchorPane ContentPane;

    /** Home Tab */
    public HomeTabController homeTabController;
    public AnchorPane homeTab;

    /** Search Tab */
    public SearchTabController searchTabController;
    public AnchorPane searchTab;

    /** Category Tab */
    public CategoryTabController categoryTabController;
    public AnchorPane categoryTab;

    /** Profile Tab */
    public ProfileTabController profileTabController;
    public AnchorPane profileTab;

    /** Settings Tab */
    // TODO: Khai báo controller và tab tương ứng cho SettingsTab

    /**
     * Xử lý sự kiện khi click vào các nút điều hướng -> hiển thị tab tương ứng (setContentPane).
     */
    @FXML
    void ButtonClick(MouseEvent event) {
        setCurrentTab((Pane) event.getSource());

        if (currentTab.equals(homeButton)) {
            System.out.println("Home Button Clicked");
            setContentPane(homeTab);
        } else if (currentTab.equals(categoryButton)) {
            System.out.println("History Button Clicked");
            setContentPane(categoryTab);
        } else if (currentTab.equals(searchButton)) {
            System.out.println("Search Button Clicked");
            setContentPane(searchTab);
        } else if (currentTab.equals(profileButton)) {
            System.out.println("Profile Button Clicked");
            setContentPane(profileTab);
        }
    }

    /**
     * Thay đổi nội dung của ContentPane.
     */
    public void setContentPane(AnchorPane contentPane) {
        ContentPane.getChildren().clear();
        ContentPane.getChildren().add(contentPane);
    }

    /**
     * Khởi tạo giao diện chính của ứng dụng.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // ĐẶT HOME TAB LÀ TAB MẶC ĐỊNH KHI KHỞI ĐỘNG ỨNG DỤNG
        currentTab = homeButton;
        currentTab.getStyleClass().clear();
        currentTab.getStyleClass().add("MenuButtonPressed");

        // KHỞI TẠO HOME TAB
        System.out.println("Home Tab Initialized");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("fxml/UserTab/HomeTabView.fxml"));
            homeTab = fxmlLoader.load();
            homeTabController = fxmlLoader.getController();
            homeTabController.setMainController(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // KHỞI TẠO SEARCH TAB
        System.out.println("Search Tab Initialized");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("fxml/UserTab/SearchTabView.fxml"));
            searchTab = fxmlLoader.load();
            searchTabController = fxmlLoader.getController();
            searchTabController.setMainController(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // KHỞI TẠO CATEGORY TAB
        System.out.println("Category Tab Initialized");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("fxml/UserTab/CategoryTabView.fxml"));
            categoryTab = fxmlLoader.load();
            categoryTabController = fxmlLoader.getController();
            categoryTabController.setMainController(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // KHỞI TẠO PROFILE TAB
        System.out.println("Profile Tab Initialized");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("fxml/UserTab/ProfileTabView.fxml"));
            profileTab = fxmlLoader.load();
            profileTabController = fxmlLoader.getController();
            profileTabController.setMainController(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ContentPane.getChildren().add(homeTab);
    }

    /**
     * Đặt nút hiện tại đang được chọn.
     */
    public void setCurrentTab(Pane b) {
        if (!b.equals(currentTab)) {
            b.getStyleClass().clear();
            b.getStyleClass().add("MenuButtonPressed");

            if (currentTab != null) {
                currentTab.getStyleClass().clear();
                currentTab.getStyleClass().add("MenuButton");
            }
            currentTab = b;
        }
    }

    public Pane getHomeButton() {
        return homeButton;
    }

    public Pane getCategoryButton() {
        return categoryButton;
    }

    public Pane getSearchButton() {
        return searchButton;
    }

    public Pane getProfileButton() {
        return profileButton;
    }
}