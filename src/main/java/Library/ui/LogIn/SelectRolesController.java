package Library.ui.LogIn;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SelectRolesController {
    @FXML
    private Button AdminButton;

    @FXML
    private Button UserButton;

    protected LogInViewController logInViewController;

    public LogInViewController getLogInViewController() {
        return logInViewController;
    }

    public void setLogInViewController(LogInViewController logInViewController) {
        this.logInViewController = logInViewController;
    }

    @FXML
    void ButtonClicked(ActionEvent event) {
        if (event.getSource() == UserButton) {
            logInViewController.setContainer(logInViewController.userLogInView);
        } else if (event.getSource() == AdminButton) {
            logInViewController.setContainer(logInViewController.adminLogInView);
        }
    }

}
