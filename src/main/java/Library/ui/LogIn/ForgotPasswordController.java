package Library.ui.LogIn;

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

    @FXML
    void sendMail(ActionEvent event) {
        String email = this.email.getText();

        // TODO: GỌI HÀM BACKEND GỬI MẬT KHẨU ĐẾN EMAIL

        // close window
        Stage stage = (Stage) sendMailButton.getScene().getWindow();
        stage.close();

        Notification notification = new Notification("Thông báo", "Mật khẩu đã được gửi đến email của bạn!");
        notification.display();
    }

    @FXML
    void enter(KeyEvent event) {
        if (event.getCode().toString().equals("ENTER")) {
            sendMail(new ActionEvent());
        }
    }

}
