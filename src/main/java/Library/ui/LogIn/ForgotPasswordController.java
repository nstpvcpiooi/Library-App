package Library.ui.LogIn;

import Library.backend.Member.Model.Member;
import Library.backend.Member.Service.MemberService;
import Library.backend.database.DatabaseConnectionException;
import Library.backend.util.EmailUtil;
import Library.ui.Utils.Notification;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class ForgotPasswordController {

    @FXML
    private TextField email;

    @FXML
    private Button sendMailButton;

    private final MemberService memberService = MemberService.getInstance();

    @FXML
    void sendMail(ActionEvent event) {
        String targetEmail = email.getText() == null ? "" : email.getText().trim();
        if (targetEmail.isEmpty()) {
            showNotification("Lỗi!", "Vui lòng nhập email đã đăng ký.");
            return;
        }

        try {
            Member member = memberService.getMemberByEmail(targetEmail);
            if (member == null) {
                showNotification("Thông báo", "Không tìm thấy tài khoản với email này.");
                return;
            }
            EmailUtil.sendEmail(member.getEmail(), "Quên mật khẩu",
                    "Mật khẩu của bạn là: " + member.getPassword());
            showNotification("Thông báo", "Mật khẩu đã được gửi đến email của bạn!");
            closeWindow();
        } catch (DatabaseConnectionException ex) {
            showNotification("Lỗi!", ex.getMessage());
        }
    }

    @FXML
    void enter(KeyEvent event) {
        if ("ENTER".equals(event.getCode().toString())) {
            sendMail(new ActionEvent());
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) sendMailButton.getScene().getWindow();
        stage.close();
    }

    private void showNotification(String title, String message) {
        Notification notification = new Notification(title, message);
        notification.display();
    }
}


