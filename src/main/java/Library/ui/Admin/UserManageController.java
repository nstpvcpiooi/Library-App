package Library.ui.Admin;

import Library.backend.Login.Model.Member;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

public class UserManageController implements Initializable {
    @FXML
    private TableView<Member> table;

    @FXML
    private TableColumn<Member, Integer> Duty;

    @FXML
    private TableColumn<Member, String> Email;

    @FXML
    private TableColumn<Member, String> Password;

    @FXML
    private TableColumn<Member, String> Phone;

    @FXML
    private TableColumn<Member, String> Preference;

    @FXML
    private TableColumn<Member, String> UserName;

    private ObservableList<Member> MemberList;

    private AdminMainController MainController;

    public void setMainController(AdminMainController adminMainController) {
        this.MainController = adminMainController;
    }

    public AdminMainController getMainController() {
        return MainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
