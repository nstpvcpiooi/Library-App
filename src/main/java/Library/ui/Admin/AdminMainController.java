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

    /** RequestManage Tab */
    public RequestManageController requestManageController;
    public AnchorPane requestManageTab;

    /** UserManage Tab */
    public UserManageController userManageController;
    public AnchorPane userManageTab;

    /** Settings Tab */
    // TODO: Khai báo controller và tab tương ứng cho SettingsTab

    /**
     * Xử lý sự kiện khi click vào các nút điều hướng -> hiển thị tab tương ứng (setContentPane).
     */
    @FXML
    void ButtonClick(MouseEvent event) {
        setCurrentTab((Pane) event.getSource());

        if (currentTab.equals(LibraryManageButton)) {
            setContentPane(libraryManageTab);
        } else if (currentTab.equals(RequestManageButton)) {
            setContentPane(requestManageTab);
        } else if (currentTab.equals(UserManageButton)) {
            setContentPane(userManageTab);
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

        // KHỞI TẠO REQUESTMANAGE TAB
        try {

            } catch (Exception e) {
                e.printStackTrace();
        }

        // KHỞI TẠO USERMANAGE TAB
        try {

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

    public Pane getRequestManageButton() {
        return RequestManageButton;
    }

    public Pane getLibraryManageButton() {
        return LibraryManageButton;
    }

    public Pane getUserManageButton() {
        return UserManageButton;
    }
}
