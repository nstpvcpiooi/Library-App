package Library.ui.Admin;

import Library.backend.Login.DAO.MemberDAOImpl;
import Library.backend.Login.Model.Member;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
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
        List<Member> members = MemberDAOImpl.getInstance().DisplayMembers();
        MemberList = FXCollections.observableArrayList(members);
        UserName.setCellValueFactory(new PropertyValueFactory<Member, String>("userName"));
        Password.setCellValueFactory(new PropertyValueFactory<Member, String>("password"));
        Email.setCellValueFactory(new PropertyValueFactory<Member, String>("email"));
        Phone.setCellValueFactory(new PropertyValueFactory<Member, String>("phone"));
        Duty.setCellValueFactory(new PropertyValueFactory<Member, Integer>("duty"));
        table.setItems(MemberList);
    }
}
