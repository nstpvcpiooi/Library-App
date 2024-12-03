package Library.ui;

import Library.MainApplication;
import Library.ui.PopUpWindow.PopUpWindow;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * MainController là lớp cơ sở cho các AdminMainController và UserMainController.
 * MainController là controller chính cho giao diện người dùng hoặc admin.
 */
public abstract class MainController implements Initializable {

    public static final Image DEFAULT_COVER = new Image(MainApplication.class.getResource("image/default-cover.png").toString());

    protected PopUpWindow popUpWindow;

    /**
     * Nút hiện tại đang được chọn.
     */
    protected Pane currentTab;

    /**
     * Phần chứa nội dung của các tab. (Home, Search, History, Profile...)
     */
    @FXML
    protected AnchorPane ContentPane;

    public AnchorPane getRoot() {
        return root;
    }

    @FXML
    protected AnchorPane root;


    public void initialize(URL location, ResourceBundle resources) {
        // KHỞI TẠO BOOK INFO VIEW
        popUpWindow = new PopUpWindow();
        popUpWindow.setMainController(this);
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

    public PopUpWindow getPopUpWindow() {
        return popUpWindow;
    }

    /** set background effect for root */
    public void setBackgroundEffect() {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(-0.3);
        root.setEffect(colorAdjust);
    }

    public void removeBackgroundEffect() {
        root.setEffect(null);
    }
}
