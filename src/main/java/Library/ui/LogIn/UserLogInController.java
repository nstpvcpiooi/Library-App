package Library.ui.LogIn;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UserLogInController extends LogInTabController {
    @FXML
    private TextField username;

    @FXML
    void submit(ActionEvent event) {
        logInViewController.setReturnType(LogInViewController.LogInType.USER);
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
    }
}
