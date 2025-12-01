package Library.ui.LogIn;

import Library.backend.Member.Service.MemberService;
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

    public MemberService memberService = MemberService.getInstance();

    protected LogInViewController logInViewController;

    abstract void submit(ActionEvent event);

    @FXML
    void goBack(ActionEvent event) {
        if (logInViewController != null) {
            logInViewController.setReturnType(LogInViewController.LogInType.GUEST);
        }
        password.clear();
        passwordFieldSkin.setDefault();
    }

    @FXML
    void enter(KeyEvent event) {
        if (event.getCode().toString().equals("ENTER")) {
            submit(new ActionEvent());
        }
    }

    public LogInViewController getLogInViewController() {
        return logInViewController;
    }

    public void setLogInViewController(LogInViewController logInViewController) {
        this.logInViewController = logInViewController;
    }

    VisiblePasswordFieldSkin passwordFieldSkin;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        passwordFieldSkin = new VisiblePasswordFieldSkin(password);
        password.setSkin(passwordFieldSkin);
    }
}
