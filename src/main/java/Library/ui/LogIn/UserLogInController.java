package Library.ui.LogIn;

import Library.backend.Session.SessionManager;
import Library.ui.Notification.Notification;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class UserLogInController extends LogInTabController {
    @FXML
    private TextField username;

    @FXML
    void submit(ActionEvent event) {

        String username = this.username.getText();
        String password = this.password.getText();
        if(memberDAO.login(username, password) != null) {
            SessionManager.getInstance().setLoggedInMember(memberDAO.login(username, password));
            logInViewController.setReturnType(LogInViewController.LogInType.USER);

            Stage current = ((Stage) submitButton.getScene().getWindow());
            current.close();
        }
        else {
            // Notify the user that they have failed to log in
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
}
