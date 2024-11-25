package Library.ui.LogIn;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public abstract class LogInTabController {
    @FXML
    private Button backButton;

    @FXML
    private Button submitButton;

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
