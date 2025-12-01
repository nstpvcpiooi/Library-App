package Library;

import Library.backend.Request.service.RequestService;
import Library.backend.database.DatabaseConnectionException;
import Library.ui.LogIn.LogInViewController;
import Library.ui.Utils.Notification;
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

    public LogInViewController.LogInType logInType;
    private RequestService requestService;

    @Override
    public void start(Stage stage) throws Exception {

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            Throwable cause = throwable;
            while (cause != null && !(cause instanceof DatabaseConnectionException)) {
                cause = cause.getCause();
            }
            if (cause instanceof DatabaseConnectionException) {
                DatabaseConnectionException dbEx = (DatabaseConnectionException) cause;
                Platform.runLater(() -> {
                    Notification notification = new Notification("Lỗi!", dbEx.getMessage());
                    notification.display();
                });
            }
        });

        /**
         * 1. Hiển thị cửa sổ đăng nhập
         * 2. Dựa vào loại đăng nhập để hiển thị cửa sổ chính (ADMIN hoặc USER)
         */
        ShowLogInWindow();

        stage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("icon/icon-512.png"))));
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

        // Add a shutdown hook to stop the scheduler gracefully
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (requestService != null) {
                requestService.stopOverdueScheduler();
            }
        }));
    }

    private void ShowLogInWindow() throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("fxml/LogInView.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        LogInViewController controller = fxmlLoader.getController();

        Stage Firststage = new Stage();
        Firststage.setTitle("Library App - Log In");
        Firststage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("icon/icon-512.png"))));
        Firststage.setResizable(false); // không cho phóng to, thu nhỏ cửa sổ
        Firststage.setScene(scene);
        Firststage.showAndWait();

        logInType = controller.getReturnType();
    }

    private void ShowUserWindow(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("fxml/UserMainView.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);

        stage.setTitle("Library App - User");
        stage.setScene(scene);

        // Start the overdue handler thread
        requestService = RequestService.getInstance();
        requestService.startOverdueScheduler();

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

        stage.setTitle("Library App - Admin");
        stage.setScene(scene);

        // Start the overdue handler thread
        requestService = RequestService.getInstance();
        requestService.startOverdueScheduler();

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
