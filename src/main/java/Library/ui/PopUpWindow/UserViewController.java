// src/main/java/Library/ui/PopUpWindow/UserViewController.java
package Library.ui.PopUpWindow;

import Library.backend.Member.Model.Member;
import Library.backend.Member.Service.MemberService;
import Library.backend.Session.SessionManager;
import Library.ui.Admin.AdminMainController;
import Library.ui.MainController;
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

    // Biến lưu trữ ID người dùng hiện tại
    private int currentMemberID = -1;


    @FXML
    void Save(ActionEvent event) {
        try {
            MemberService memberService = MemberService.getInstance();

            if (tabTitle.getText().equals("THASM USER MỚI")) {
                // Tạo người dùng mới
                if (!validateInputs()) return; // Kiểm tra dữ liệu hợp lệ

                Member newMember = new Member();
                newMember.setUserName(username.getText());
                newMember.setPassword(password.getText());
                newMember.setEmail(email.getText());
                newMember.setPhone(phone.getText());

                if (memberService.createMember(newMember)) {
                    showNotification("Thành công!", "Người dùng mới đã được thêm vào.");
                } else {
                    showNotification("Lỗi!", "Người dùng hoặc email đã tồn tại.");
                    return;
                }
            } else {
                // Cập nhật người dùng hiện tại
                if (currentMemberID == -1) {
                    showNotification("Lỗi!", "Không thể xác định tài khoản để cập nhật.");
                    return;
                }
                if (!validateInputs()) return; // Kiểm tra dữ liệu hợp lệ

                Member memberToUpdate = new Member();
                memberToUpdate.setMemberID(currentMemberID); // Sử dụng ID đã lưu
                memberToUpdate.setUserName(username.getText());
                memberToUpdate.setPassword(password.getText());
                memberToUpdate.setEmail(email.getText());
                memberToUpdate.setPhone(phone.getText());

                // Kiểm tra trùng lặp username hoặc email
                if (isDuplicateUsernameOrEmail(memberService, memberToUpdate)) return;

                if (memberService.updateMember(memberToUpdate)) {
                    if (SessionManager.getInstance().getLoggedInMember().getMemberID() == currentMemberID) {
                        SessionManager.getInstance().setLoggedInMember(memberToUpdate);
                    }
                    showNotification("Thành công!", "Thông tin người dùng đã được cập nhật.");
                } else {
                    showNotification("Lỗi!", "Không thể cập nhật thông tin người dùng.");
                }
            }

            // Cập nhật danh sách người dùng trên giao diện
            MainController mainController = getPopUpWindow().getMainController();
            if (mainController instanceof AdminMainController) {
                AdminMainController adminMainController = (AdminMainController) mainController;
                adminMainController.userManageController.updateUSerList();

            } else {
                showNotification("Lỗi!", "Không thể cập nhật danh sách và không phải AdminMainController.");
            }

            getPopUpWindow().close();

        } catch (Exception e) {
            e.printStackTrace();
            showNotification("Lỗi!", "Đã xảy ra lỗi trong quá trình xử lý.");
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
        } else {
            currentMemberID = user.getMemberID();
            username.setText(user.getUserName());
            password.setText(user.getPassword());
            verifypassword.setText(user.getPassword());
            email.setText(user.getEmail());
            phone.setText(user.getPhone());
        }
    }

    public void setTabTitle(String title) {
        tabTitle.setText(title);
    }

    private boolean validateInputs() {
        if (username.getText().isEmpty() || password.getText().isEmpty() || verifypassword.getText().isEmpty() ||
                email.getText().isEmpty() || phone.getText().isEmpty()) {
            showNotification("Lỗi!", "Vui lòng điền đầy đủ thông tin.");
            return false;
        }
        if (!password.getText().equals(verifypassword.getText())) {
            showNotification("Lỗi!", "Mật khẩu không khớp.");
            return false;
        }
        return true;
    }

    /**
     * Kiểm tra trùng lặp username hoặc email
     */
    private boolean isDuplicateUsernameOrEmail(MemberService memberService, Member memberToUpdate) {
        Member existingUsername = memberService.getMemberByUsername(memberToUpdate.getUserName());
        Member existingEmail = memberService.getMemberByEmail(memberToUpdate.getEmail());

        if (existingUsername != null && existingUsername.getMemberID() != currentMemberID) {
            showNotification("Lỗi!", "Tên người dùng đã tồn tại.");
            return true;
        }
        if (existingEmail != null && existingEmail.getMemberID() != currentMemberID) {
            showNotification("Lỗi!", "Email đã tồn tại.");
            return true;
        }
        return false;
    }

    /**
     * Hiển thị thông báo
     */
    private void showNotification(String title, String message) {
        Notification notification = new Notification(title, message);
        notification.display();
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
