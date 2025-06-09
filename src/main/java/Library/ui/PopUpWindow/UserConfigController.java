package Library.ui.PopUpWindow;

import Library.backend.Login.DAO.MemberDAO;
import Library.backend.Login.DAO.MemberDAOImpl;
import Library.backend.Login.Model.Member;
import Library.backend.Session.SessionManager;
import Library.ui.Admin.AdminMainController;
import Library.ui.Utils.Notification;
import Library.ui.Utils.VisiblePasswordFieldSkin;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class UserConfigController extends PopUpController implements Initializable {
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
    void Save(ActionEvent event) throws InstantiationException, IllegalAccessException {
        MemberDAO memberDAO = MemberDAOImpl.getInstance();

        if (tabTitle.getText().equals("THÊM USER MỚI") && validateInputs()) {
            Member member = new Member();
            member.setUserName(username.getText());
            member.setPassword(password.getText());
            member.setEmail(email.getText());
            member.setPhone(phone.getText());
            memberDAO.createMember(member);

            ((AdminMainController) getPopUpWindow().getMainController()).userManageController.updateUSerList();

            System.out.println("Created user: " + member);
            getPopUpWindow().close();
            Notification notification = new Notification("Thêm người dùng", "Đã thêm người dùng thành công");
            notification.display();

        } else if (tabTitle.getText().equals("CHỈNH SỬA USER") && validateInputs()) {
            Member member = new Member();
            member.setMemberID(memberDAO.getMemberByEmail(email.getText()).getMemberID());
            member.setUserName(username.getText());
            member.setPassword(password.getText());
            member.setEmail(email.getText());
            member.setPhone(phone.getText());
            memberDAO.updateMember(member);

            ((AdminMainController) getPopUpWindow().getMainController()).userManageController.updateUSerList();

            System.out.println("Updated user: " + member);
            SessionManager.getInstance().setLoggedInMember(member);

            getPopUpWindow().close();
            Notification notification = new Notification("Cập nhật thông tin người dùng", "Đã cập nhật thông tin người dùng thành công");
            notification.display();
        }

    }

    public void setData(Member user) {
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

    private boolean validateInputs() {
        String passwordText = password.getText();
        String verifyPasswordText = verifypassword.getText();
        String emailText = email.getText();
        String phoneText = phone.getText();
        String usernameText = username.getText();

        if (usernameText.isEmpty() || passwordText.isEmpty() ||
                verifyPasswordText.isEmpty() || emailText.isEmpty() || phoneText.isEmpty()) {
            Notification notification = new Notification("Lỗi!", "Vui lòng điền đầy đủ thông tin");
            notification.display();
            return false;
        }

        if (!passwordText.equals(verifyPasswordText)) {
            Notification notification = new Notification("Lỗi!", "Mật khẩu không khớp");
            notification.display();
            return false;
        }

        return true;
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