package Library;

import Library.backend.Request.OverdueRequestHandler;
import Library.ui.LogIn.LogInViewController;
import Library.ui.Utils.SearchUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainApplication extends Application {
    public static final String LOGO_PATH = "icon/APP-LOGO.png";
    public LogInViewController.LogInType logInType;
    private OverdueRequestHandler overdueRequestHandler;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        /**
         * 1. Hiển thị cửa sổ đăng nhập
         * 2. Dựa vào loại đăng nhập để hiển thị cửa sổ chính (ADMIN hoặc USER)
         */
        ShowLogInWindow();

        stage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream(LOGO_PATH))));
        stage.setResizable(false); // không cho phóng to, thu nhỏ cửa sổ

        /**
         * Nếu là USER thì hiển thị cửa sổ UserMainView
         * Nếu là ADMIN thì hiển thị cửa sổ AdminMainView
         */
        if (logInType == LogInViewController.LogInType.USER) {
            ShowUserWindow(stage);
        } else if (logInType == LogInViewController.LogInType.ADMIN) {
            ShowAdminWindow(stage);
        }

        // Add a shutdown hook to stop the scheduler gracefully.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (overdueRequestHandler != null) {
                overdueRequestHandler.stop();
            }
        }));
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if (overdueRequestHandler != null) {
            overdueRequestHandler.stop();
        }
        Platform.exit();
        System.exit(0);
        SearchUtils.shutdownSearch();
    }

    private void ShowLogInWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("fxml/LogInView.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        LogInViewController controller = fxmlLoader.getController();

        Stage Firststage = new Stage();
        Firststage.setTitle("UETLibz - LOG IN");
        Firststage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream(LOGO_PATH))));
        Firststage.setResizable(false); // không cho phóng to, thu nhỏ cửa sổ
        Firststage.setScene(scene);
        Firststage.showAndWait();

        logInType = controller.getReturnType();
    }

    private void ShowUserWindow(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("fxml/UserMainView.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);

        stage.setTitle("UETLibz - USER");
        stage.setScene(scene);

        // Start the overdue handler thread
        overdueRequestHandler = new OverdueRequestHandler();
        overdueRequestHandler.start();

        // KHI ĐÓNG CỬA SỔ
        stage.setOnCloseRequest(windowEvent -> {
            Platform.exit();
            System.exit(0);
        });

        stage.show();
    }

    private void ShowAdminWindow(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("fxml/AdminMainView.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);

        stage.setTitle("UETLibz - ADMIN");
        stage.setScene(scene);

        // Start the overdue handler thread
        overdueRequestHandler = new OverdueRequestHandler();
        overdueRequestHandler.start();

        // KHI ĐÓNG CỬA SỔ
        stage.setOnCloseRequest(windowEvent -> {
            Platform.exit();
            System.exit(0);
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}