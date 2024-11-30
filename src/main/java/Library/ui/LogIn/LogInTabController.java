package Library.ui.LogIn;

import Library.backend.Login.DAO.MemberDAO;
import Library.backend.Login.DAO.MemberDAOImpl;
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

    public MemberDAO memberDAO = MemberDAOImpl.getInstance();

    protected LogInViewController logInViewController;

    abstract void submit(ActionEvent event);

    @FXML
    void goBack(ActionEvent event) {
        logInViewController.setContainer(logInViewController.selectRolesView);
//        logInViewController.setReturnType(LogInViewController.LogInType.GUEST);
    }

    public LogInViewController getLogInViewController() {
        return logInViewController;
    }

    public void setLogInViewController(LogInViewController logInViewController) {
        this.logInViewController = logInViewController;
    }
}
