package Library.ui.LogIn;

import Library.backend.Login.DAO.MemberDAO;
import Library.backend.Login.DAO.MemberDAOImpl;
import Library.ui.Utils.VisiblePasswordFieldSkin;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class LogInTabController implements Initializable {
    @FXML
    private Button backButton;

    @FXML
    protected Button submitButton;

    @FXML
    protected PasswordField password;

    public MemberDAO memberDAO = MemberDAOImpl.getInstance();

    protected LogInViewController logInViewController;

    public LogInViewController getLogInViewController() {
        return logInViewController;
    }

    public void setLogInViewController(LogInViewController logInViewController) {
        this.logInViewController = logInViewController;
    }

    abstract void submit(ActionEvent event);

    @FXML
    void goBack(ActionEvent event) {
        logInViewController.setContainer(logInViewController.selectRolesView);
        logInViewController.setReturnType(LogInViewController.LogInType.GUEST);
        password.clear();
    }

    @FXML
    void enter(KeyEvent event) {
        if (event.getCode().toString().equals("ENTER")) {
            submit(new ActionEvent());
        }
    }

    VisiblePasswordFieldSkin visiblePasswordFieldSkin;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        password.setSkin(new VisiblePasswordFieldSkin(password));
    }
}
