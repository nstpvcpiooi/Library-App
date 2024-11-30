package Library.ui.LogIn;

import Library.backend.Session.SessionManager;
import Library.ui.Notification.Notification;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class AdminLogInController extends LogInTabController {

    @FXML
    void submit(ActionEvent event) {

        String password = this.password.getText();
        if(memberDAO.login("admin", password) != null) {
            SessionManager.getInstance().setLoggedInMember(memberDAO.login("admin", password));
            logInViewController.setReturnType(LogInViewController.LogInType.ADMIN);

            Stage current = ((Stage) (submitButton.getScene().getWindow()));
            current.close();
        } else {
            // Notify the user that they have failed to log in
            Notification notification = new Notification("Lỗi!", "Đăng nhập thất bại!");
            notification.display();
        }
    }

}
