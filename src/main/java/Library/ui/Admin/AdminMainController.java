package Library.ui.Admin;

import Library.MainApplication;
import Library.ui.MainController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminMainController extends MainController {

    /**
     * Các nút điều hướng giữa các tab.
     */
    @FXML
    private Pane LibraryManageButton;

    @FXML
    private Pane RequestManageButton;

    @FXML
    private Pane UserManageButton;

    /** LibraryManage Tab */
    public LibraryManageController libraryManageController;
    public AnchorPane libraryManageTab;


    /** UserManage Tab */
    public UserManageController userManageController;
    public AnchorPane userManageTab;

    /** RequestManage Tab */
    public RequestManageController requestManageController;
    public AnchorPane requestManageTab;


    /**
     * Xử lý sự kiện khi click vào các nút điều hướng -> hiển thị tab tương ứng (setContentPane).
     */
    @FXML
    void ButtonClick(MouseEvent event) {
        setCurrentTab((Pane) event.getSource());

        if (currentTab.equals(LibraryManageButton)) {
            setContentPane(libraryManageTab);
        } else if (currentTab.equals(UserManageButton)) {
            setContentPane(userManageTab);
            userManageController.hideButtons();
        } else if (currentTab.equals(RequestManageButton)) {
            setContentPane(requestManageTab);
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // KHỞI TẠO LIBRARYMANAGE TAB
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("fxml/AdminTab/LibraryManageTabView.fxml"));
            libraryManageTab = loader.load();
            libraryManageController = loader.getController();
            libraryManageController.setMainController(this);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // KHỞI TẠO USERMANAGE TAB
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("fxml/AdminTab/UserManageTabView.fxml"));
            userManageTab = loader.load();
            userManageController = loader.getController();
            userManageController.setMainController(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // KHỞI TẠO REQUESTMANAGE TAB
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("fxml/AdminTab/RequestManageTabView.fxml"));
            requestManageTab = loader.load();
            requestManageController = loader.getController();
            requestManageController.setMainController(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ĐẶT LIBRARYMANAGE LÀ TAB MẶC ĐỊNH
        currentTab = LibraryManageButton;
        currentTab.getStyleClass().clear();
        currentTab.getStyleClass().add("MenuButtonPressed");

        ContentPane.getChildren().add(libraryManageTab);

        super.initialize(location, resources);
    }

    public Pane getLibraryManageButton() {
        return LibraryManageButton;
    }

    public Pane getUserManageButton() {
        return UserManageButton;
    }
}
