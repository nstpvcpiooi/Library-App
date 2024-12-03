// src/main/java/Library/ui/Admin/UserManageController.java
package Library.ui.Admin;

import Library.backend.Login.Model.User;
import Library.backend.Login.DAO.MemberDAOImpl;
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

public class UserManageController implements Initializable {
    @FXML
    private TableView<User> table;

    @FXML
    private TableColumn<User, String> Email;

    @FXML
    private TableColumn<User, String> Phone;

    @FXML
    private TableColumn<User, Integer> ID;

    @FXML
    private TableColumn<User, String> UserName;

    @FXML
    private Button addButton;

    @FXML
    private Button editButton;

    @FXML
    private Button removeButton;

    private ObservableList<User> UserList;

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
        getMainController().getPopUpWindow().getUserViewController().setTabTitle("THÊM USER MỚI");
        getMainController().getPopUpWindow().displayUser(null);
    }

    @FXML
    void edit(ActionEvent event) {
        User selectedItem = table.getSelectionModel().getSelectedItem();
        getMainController().getPopUpWindow().getUserViewController().setTabTitle("CHỈNH SỬA USER");
        getMainController().getPopUpWindow().displayUser(selectedItem);
    }

    @FXML
    void remove(ActionEvent event) {
        User selectedItem = table.getSelectionModel().getSelectedItem();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận");
        alert.setHeaderText("Bạn có chắc chắn muốn xóa người dùng này?");

        ButtonType yes = new ButtonType("Có");
        ButtonType no = new ButtonType("Không");
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(yes, no);
        Optional<ButtonType> opt = alert.showAndWait();

        if (opt.get() == yes) {
            MemberDAOImpl.getInstance().deleteMemberById(selectedItem.getMemberID());
            UserList.remove(selectedItem);
            Notification notification = new Notification("Thành công!", "Đã xóa thành công " + selectedItem.getUserName());
            notification.display();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hideButtons();
        List<User> users = MemberDAOImpl.getInstance().DisplayMembers();
        UserList = FXCollections.observableArrayList(users);
        UserName.setCellValueFactory(new PropertyValueFactory<User, String>("userName"));
        Email.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
        Phone.setCellValueFactory(new PropertyValueFactory<User, String>("phone"));
        ID.setCellValueFactory(new PropertyValueFactory<User, Integer>("memberID"));
        table.setItems(UserList);
    }

    public void refreshData() {
        List<User> users = MemberDAOImpl.getInstance().DisplayMembers();
        UserList = FXCollections.observableArrayList(users);
        UserName.setCellValueFactory(new PropertyValueFactory<User, String>("userName"));
        Email.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
        Phone.setCellValueFactory(new PropertyValueFactory<User, String>("phone"));
        ID.setCellValueFactory(new PropertyValueFactory<User, Integer>("memberID"));
        table.setItems(UserList);
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

        User selectedItem = table.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            showButtons();
        } else {
            hideButtons();
        }
    }
}