// src/main/java/Library/ui/PopUpWindow/UserViewController.java
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
    void Save(ActionEvent event) throws InstantiationException, IllegalAccessException {
        if (tabTitle.getText().equals("THÊM USER MỚI")) {
            if (username.getText().isEmpty() || password.getText().isEmpty() || verifypassword.getText().isEmpty() || email.getText().isEmpty() || phone.getText().isEmpty()) {
                Notification notification = new Notification("Lỗi", "Vui lòng điền đầy đủ thông tin");
                notification.display();
                return;
            }
            if (!password.getText().equals(verifypassword.getText())) {
                Notification notification = new Notification("Lỗi", "Mật khẩu không khớp");
                notification.display();
                return;
            }
            MemberDAO memberDAO = MemberDAOImpl.getInstance();
            Member member = new Member();
            member.setUserName(username.getText());
            member.setPassword(password.getText());
            member.setEmail(email.getText());
            member.setPhone(phone.getText());
            memberDAO.createMember(member);

            ((AdminMainController) getPopUpWindow().getMainController()).userManageController.updateUSerList();
        } else {
            MemberDAO memberDAO = MemberDAOImpl.getInstance();
            Member member = new Member();
            member.setMemberID(memberDAO.getMemberByEmail(email.getText()).getMemberID());
            member.setUserName(username.getText());
            member.setPassword(password.getText());
            member.setEmail(email.getText());
            member.setPhone(phone.getText());
            memberDAO.updateMember(member);
            System.out.println("Updated user: " + member);
            SessionManager.getInstance().setLoggedInMember(member);
        }
        getPopUpWindow().close();
        Notification notification = new Notification("Cập nhật thông tin người dùng", "Đã cập nhật thông tin người dùng thành công");
        notification.display();
        // Refresh the data in UserManageController
        ;
        
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