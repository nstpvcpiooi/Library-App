package Library.ui.LogIn;

import Library.MainApplication;
import Library.backend.Session.SessionManager;
import Library.backend.Member.Service.MemberService;
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
import Library.backend.database.DatabaseConnectionException;

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
        try {
            var member = MemberService.getInstance().getMemberByUserNameAndPassword(username, password);
            if (member != null) {
                SessionManager.getInstance().setLoggedInMember(member);
                logInViewController.setReturnType(
                        member.getDuty() == 1 ? LogInViewController.LogInType.ADMIN : LogInViewController.LogInType.USER);

                Stage current = ((Stage) submitButton.getScene().getWindow());
                current.close();
            } else {
                Notification notification = new Notification("Lỗi!", "Đăng nhập thất bại!");
                notification.display();
            }
        } catch (DatabaseConnectionException ex) {
            Notification notification = new Notification("Lỗi!", ex.getMessage());
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
