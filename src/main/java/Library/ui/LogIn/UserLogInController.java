package Library.ui.LogIn;


import Library.backend.Login.DAO.MemberDAO;
import Library.backend.Login.DAO.MemberDAOImpl;
import Library.backend.Login.Model.User;
import Library.backend.Session.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UserLogInController extends LogInTabController {
    @FXML
    private TextField username;
    @FXML
    private TextField password;

    public MemberDAO memberDAO = MemberDAOImpl.getInstance();


    @FXML
    void submit(ActionEvent event) {

        String username = this.username.getText();
        String password = this.password.getText();
        if(memberDAO.login(username, password) != null) {
            SessionManager.getInstance().setLoggedInMember(memberDAO.login(username, password));
            logInViewController.setReturnType(LogInViewController.LogInType.USER);
            ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
        }
        else {
            //SHOW ALERT LOGIN FAILED
        }
    }
}
