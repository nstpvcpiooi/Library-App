// src/main/java/Library/ui/Admin/UserManageController.java
package Library.ui.Admin;

import Library.backend.Member.Model.Member;
import Library.backend.Member.Service.MemberService;
import Library.ui.Utils.Notification;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller cho giao diện quản lý người dùng của admin.
 */
public class UserManageController implements Initializable {

    @FXML
    private TableView<Member> table;

    @FXML
    private TableColumn<Member, String> Email;

    @FXML
    private TableColumn<Member, String> Phone;

    @FXML
    private TableColumn<Member, Integer> ID;

    @FXML
    private TableColumn<Member, String> UserName;

    @FXML
    private Button addButton;

    @FXML
    private Button editButton;

    @FXML
    private Button removeButton;

    private ObservableList<Member> UserList;

    private AdminMainController MainController;

    private static UserManageController instance;

    public void setMainController(AdminMainController adminMainController) {
        refreshData();
        this.MainController = adminMainController;
    }

    public AdminMainController getMainController() {
        refreshData();
        return MainController;

    }

    @FXML
    void add(ActionEvent event) {
        getMainController().getPopUpWindow().getUserViewController().setTabTitle("THASM USER MỚI");
        getMainController().getPopUpWindow().displayUser(null);
    }

    @FXML
    void edit(ActionEvent event) {
        Member selectedItem = table.getSelectionModel().getSelectedItem();
        getMainController().getPopUpWindow().getUserViewController().setTabTitle("CHỈNH SỬA USER");
        getMainController().getPopUpWindow().displayUser(selectedItem);
    }

    @FXML
    void remove(ActionEvent event) {
        Member selectedItem = table.getSelectionModel().getSelectedItem();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận");
        alert.setHeaderText("Bạn có chắc chắn muốn xóa người dùng này?");

        ButtonType yes = new ButtonType("Có");
        ButtonType no = new ButtonType("Không");
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(yes, no);
        Optional<ButtonType> opt = alert.showAndWait();

        if (opt.isPresent() && opt.get() == yes) {
            MemberService.getInstance().deleteMemberById(selectedItem.getMemberID());
            UserList.remove(selectedItem);
            Notification notification = new Notification("Thành công!", "Đã xóa thành công " + selectedItem.getUserName());
            notification.display();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hideButtons();
        List<Member> users = MemberService.getInstance().displayMembers();
        UserList = FXCollections.observableArrayList(users);
        UserName.setCellValueFactory(new PropertyValueFactory<Member, String>("userName"));
        Email.setCellValueFactory(new PropertyValueFactory<Member, String>("email"));
        Phone.setCellValueFactory(new PropertyValueFactory<Member, String>("phone"));
        ID.setCellValueFactory(new PropertyValueFactory<Member, Integer>("memberID"));
        table.setItems(UserList);
    }

    public void refreshData() {
        UserName.setCellValueFactory(new PropertyValueFactory<Member, String>("userName"));
        Email.setCellValueFactory(new PropertyValueFactory<Member, String>("email"));
        Phone.setCellValueFactory(new PropertyValueFactory<Member, String>("phone"));
        ID.setCellValueFactory(new PropertyValueFactory<Member, Integer>("memberID"));

        updateUSerList();
    }

    public void hideButtons() {
        editButton.setVisible(false);
        removeButton.setVisible(false);
        table.getSelectionModel().clearSelection();

    }

    public void showButtons() {
        editButton.setVisible(true);
        removeButton.setVisible(true);

    }

    @FXML
    void selectItem(MouseEvent event) {

        Member selectedItem = table.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            showButtons();
        } else {
            hideButtons();
        }
    }

    public void updateUSerList() {
        UserList = FXCollections.observableArrayList(MemberService.getInstance().displayMembers());
        table.setItems(UserList);
    }
}
