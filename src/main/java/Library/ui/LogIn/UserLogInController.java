package Library.ui.LogIn;

import Library.MainApplication;
import Library.backend.Session.SessionManager;
import Library.ui.Utils.Notification;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class UserLogInController extends LogInTabController {
    @FXML
    private TextField username;

    @FXML
    private Label forgotPasswordButton;

    protected ForgotPasswordController forgotPasswordController;

    @FXML
    void submit(ActionEvent event) {
        String username = this.username.getText();
        String password = this.password.getText();
        if(memberDAO.login(username, password) != null) {
            // TODO: YES, set the logged in member to user
            SessionManager.getInstance().setLoggedInMember(memberDAO.login(username, password));
            logInViewController.setReturnType(LogInViewController.LogInType.USER);
            Stage current = ((Stage) submitButton.getScene().getWindow());
            current.close();
        }
        else {
            // TODO: Notify the user that they have failed to log in
            Notification notification = new Notification("Lỗi!", "Đăng nhập thất bại!");
            notification.display();
        }
    }

    @FXML
    void tab(KeyEvent event) {
        if (event.getCode().toString().equals("TAB")) {
            password.requestFocus();
        }
    }

    @Override
    void goBack(ActionEvent event) {
        username.clear();
        super.goBack(event);
    }

    @FXML
    void forgotPassword(MouseEvent event) {
        Stage forgotPasswordStage = new Stage();
        Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.
                    getResource("fxml/LogInTab/ForgotPassword.fxml"));
            root = fxmlLoader.load();
            forgotPasswordStage.setTitle("Quên mật khẩu");
            forgotPasswordStage.setScene(new Scene(root));
            forgotPasswordStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}
