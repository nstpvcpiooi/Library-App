package Library.ui.LogIn;


import Library.backend.Login.DAO.MemberDAO;
import Library.backend.Login.DAO.MemberDAOImpl;
import Library.backend.Login.Model.Admin;
import Library.backend.Login.Model.User;
import Library.backend.Session.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AdminLogInController extends LogInTabController {

    @FXML
    TextField password;

    MemberDAO memberDAO = MemberDAOImpl.getInstance();

    @FXML
    void submit(ActionEvent event) {

        String password = this.password.getText();
        if(memberDAO.login("admin", password) != null) {
            SessionManager.getInstance().setLoggedInMember(memberDAO.login("admin", password));
            logInViewController.setReturnType(LogInViewController.LogInType.ADMIN);
            ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
        }
        else {
            //SHOW ALERT LOGIN FAILED
        }
    }
}
