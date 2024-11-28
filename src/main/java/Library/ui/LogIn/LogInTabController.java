package Library.ui.LogIn;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

public abstract class LogInTabController {
    @FXML
    private Button backButton;

    @FXML
    private Button submitButton;

    @FXML
    private PasswordField password;

    abstract void submit(ActionEvent event);

    protected LogInViewController logInViewController;

    public LogInViewController getLogInViewController() {
        return logInViewController;
    }

    public void setLogInViewController(LogInViewController logInViewController) {
        this.logInViewController = logInViewController;
    }

    @FXML
    void goBack(ActionEvent event) {
        logInViewController.setContainer(logInViewController.selectRolesView);
//        logInViewController.setReturnType(LogInViewController.LogInType.GUEST);
    }
}
