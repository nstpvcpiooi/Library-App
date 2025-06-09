package Library.ui;

import Library.MainApplication;
import Library.backend.Login.Model.Member;
import Library.ui.LogIn.LogInViewController;
import Library.ui.PopUpWindow.PopUpWindow;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.io.IOException;

import static Library.MainApplication.LOGO_PATH;


/**
 * MainController là lớp cơ sở cho các AdminMainController và UserMainController.
 * MainController là controller chính cho giao diện người dùng hoặc admin.
 */
public abstract class MainController implements Initializable {

    public static final Image DEFAULT_COVER = new Image(MainApplication.class.getResource("image/default-cover.png").toString());

    protected PopUpWindow popUpWindow;

    private Member currentUser;

    /**
     * Nút hiện tại đang được chọn.
     */
    protected Pane currentTab;

    /**
     * Phần chứa nội dung của các tab. (Home, Search, History, Profile...)
     */
    @FXML
    protected AnchorPane ContentPane;

    @FXML
    protected AnchorPane root;

    @FXML
    private Button SignOutButton;

    public void setCurrentUser(Member currentUser) {
        this.currentUser = currentUser;
    }

    public Member getCurrentUser() {
        return currentUser;
    }

    public PopUpWindow getPopUpWindow() {
        return popUpWindow;
    }

    public AnchorPane getRoot() {
        return root;
    }

    public void initialize(URL location, ResourceBundle resources) {
        // KHỞI TẠO BOOK INFO VIEW
        popUpWindow = new PopUpWindow();
        popUpWindow.setMainController(this);
    }

    @FXML
    void SignOut(ActionEvent event) {
        // Clear the current user session
        Library.backend.Session.SessionManager.getInstance().setLoggedInMember(null);
        
        // Close the current window
        Stage currentStage = (Stage) SignOutButton.getScene().getWindow();
        currentStage.close();
        
        // Show the login window
        Platform.runLater(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("fxml/LogInView.fxml"));
                Parent root = fxmlLoader.load();
                Scene scene = new Scene(root);
                LogInViewController controller = fxmlLoader.getController();

                Stage loginStage = new Stage();
                loginStage.setTitle("UETLibz - LOG IN");
                loginStage.getIcons().add(new Image(Objects.requireNonNull(
                        MainApplication.class.getResourceAsStream(LOGO_PATH))));
                loginStage.setResizable(false);
                loginStage.setScene(scene);
                loginStage.showAndWait(); // Wait for login result

                // Handle login result
                LogInViewController.LogInType loginType = controller.getReturnType();
                if (loginType == LogInViewController.LogInType.USER) {
                    // Show user window
                    FXMLLoader userLoader = new FXMLLoader(MainApplication.class.getResource("fxml/UserMainView.fxml"));
                    Parent userRoot = userLoader.load();
                    Scene userScene = new Scene(userRoot);
                    Stage userStage = new Stage();
                    userStage.setTitle("UETLibz - USER");
                    userStage.getIcons().add(new Image(Objects.requireNonNull(
                            MainApplication.class.getResourceAsStream(LOGO_PATH))));
                    userStage.setScene(userScene);
                    userStage.show();
                } else if (loginType == LogInViewController.LogInType.ADMIN) {
                    // Show admin window
                    FXMLLoader adminLoader = new FXMLLoader(MainApplication.class.getResource("fxml/AdminMainView.fxml"));
                    Parent adminRoot = adminLoader.load();
                    Scene adminScene = new Scene(adminRoot);
                    Stage adminStage = new Stage();
                    adminStage.setTitle("UETLibz - ADMIN");
                    adminStage.getIcons().add(new Image(Objects.requireNonNull(
                            MainApplication.class.getResourceAsStream(LOGO_PATH))));
                    adminStage.setScene(adminScene);
                    adminStage.show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
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
