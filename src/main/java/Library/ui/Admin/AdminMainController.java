package Library.ui.Admin;

import Library.MainApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminMainController implements Initializable {

    /**
     * Các nút điều hướng giữa các tab.
     */
    @FXML
    private Pane LibraryManageButton;

    @FXML
    private Pane RequestManageButton;

    @FXML
    private Pane UserManageButton;

    /**
     * Nút hiện tại đang được chọn.
     */
    private Pane currentTab;

    /**
     * Phần chứa nội dung của các tab. (Home, Search, History, Profile...)
     */
    @FXML
    private AnchorPane ContentPane;

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

    /**
     * Thay đổi nội dung của ContentPane.
     */
    public void setContentPane(AnchorPane contentPane) {
        ContentPane.getChildren().clear();
        ContentPane.getChildren().add(contentPane);
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // ĐẶT LIBRARYMANAGE LÀ TAB MẶC ĐỊNH
        currentTab = LibraryManageButton;
        currentTab.getStyleClass().clear();
        currentTab.getStyleClass().add("MenuButtonPressed");

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

        ContentPane.getChildren().add(libraryManageTab);
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
