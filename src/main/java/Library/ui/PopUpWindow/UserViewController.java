package Library.ui.PopUpWindow;

import Library.ui.Admin.demoUser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class UserViewController extends PopUpController {
    @FXML
    private Button cancelButton;

    @FXML
    private TextField email;

    @FXML
    private Button okButton;

    @FXML
    private PasswordField password;

    @FXML
    private TextField phone;

    @FXML
    private TextField username;

    @FXML
    private PasswordField verifypassword;

    @FXML
    private Label tabTitle;

    @FXML
    void Save(ActionEvent event) {

    }

    public void setData(demoUser user) {
        if (user == null) {
            return;
        }
        username.setText(user.getUserName());
        password.setText(user.getPassword());
        verifypassword.setText(user.getPassword());
        email.setText(user.getEmail());
        phone.setText(user.getPhone());
    }

    public void setTabTitle(String title) {
        tabTitle.setText(title);
    }
}
