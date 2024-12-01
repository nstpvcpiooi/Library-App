package Library.ui.PopUpWindow;

import Library.ui.Admin.demoUser;
import Library.ui.Utils.Notification;
import Library.ui.Utils.VisiblePasswordFieldSkin;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class UserViewController extends PopUpController implements Initializable {
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
        getPopUpWindow().close();
        Notification notification = new Notification("Cập nhật thông tin người dùng", "Đã cập nhật thông tin người dùng thành công");
        notification.display();
    }

    public void setData(demoUser user) {
        passwordFieldSkin.setDefault();
        verifypasswordFieldSkin.setDefault();

        if (user == null) {
            username.setText("");
            password.setText("");
            verifypassword.setText("");
            email.setText("");
            phone.setText("");
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

    VisiblePasswordFieldSkin passwordFieldSkin;
    VisiblePasswordFieldSkin verifypasswordFieldSkin;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        passwordFieldSkin = new VisiblePasswordFieldSkin(password);
        verifypasswordFieldSkin = new VisiblePasswordFieldSkin(verifypassword);
        password.setSkin(passwordFieldSkin);
        verifypassword.setSkin(verifypasswordFieldSkin);
    }
}