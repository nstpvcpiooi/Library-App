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
import javafx.beans.property.SimpleIntegerProperty;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller cho giao diện quản lý người dùng của admin
 */
public class UserManageController extends AdminTabController implements Initializable {

    /**
     * Bảng hiển thị danh sách người dùng
     */
    @FXML
    private TableView<User> table;

    /**
     * Cột hiển thị mã người dùng, tên người dùng, email, số điện thoại
     */
    @FXML
    private TableColumn<User, String> Email;

    @FXML
    private TableColumn<User, String> Phone;

    @FXML
    private TableColumn<User, Integer> ID;

    @FXML
    private TableColumn<User, String> UserName;

    /**
     * Nút thêm, sửa, xóa người dùng
     */
    @FXML
    private Button addButton;

    @FXML
    private Button editButton;

    @FXML
    private Button removeButton;

    /**
     * Danh sách người dùng
     */
    private ObservableList<User> UserList;

    private static UserManageController instance;

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
            table.getSelectionModel().clearSelection();
            Notification notification = new Notification("Thành công!", "Đã xóa thành công " + selectedItem.getUserName());
            notification.display();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hideButtons();
        refreshData();
    }

    public void refreshData() {
        List<User> users = MemberDAOImpl.getInstance().DisplayMembers();
        UserList = FXCollections.observableArrayList(users);

        UserName.setCellValueFactory(new PropertyValueFactory<User, String>("userName"));
        Email.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
        Phone.setCellValueFactory(new PropertyValueFactory<User, String>("phone"));
        ID.setCellValueFactory(cellData -> {
            int index = UserList.indexOf(cellData.getValue());
            return new SimpleIntegerProperty(index + 1).asObject();
        });
        table.setItems(UserList);

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

        User selectedItem = table.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            showButtons();
        } else {
            hideButtons();
        }
    }

    public void updateUSerList() {
        UserList = FXCollections.observableArrayList(MemberDAOImpl.getInstance().DisplayMembers());
        table.setItems(UserList);
    }
}