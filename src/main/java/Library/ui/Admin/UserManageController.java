package Library.ui.Admin;

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
import java.util.Optional;
import java.util.ResourceBundle;

public class UserManageController implements Initializable {
    @FXML
    private TableView<demoUser> table;

    @FXML
    private TableColumn<demoUser, String> Email;

    @FXML
    private TableColumn<demoUser, String> Phone;

    @FXML
    private TableColumn<demoUser, String> ID;

    @FXML
    private TableColumn<demoUser, String> UserName;

    @FXML
    private Button addButton;

    @FXML
    private Button editButton;

    @FXML
    private Button removeButton;

    private ObservableList<demoUser> UserList;

    private AdminMainController MainController;

    public void setMainController(AdminMainController adminMainController) {
        this.MainController = adminMainController;
    }

    public AdminMainController getMainController() {
        return MainController;
    }

    @FXML
    void add(ActionEvent event) {
        getMainController().getPopUpWindow().getUserViewController().setTabTitle("THÊM USER MỚI");
        getMainController().getPopUpWindow().displayUser(null);
    }

    @FXML
    void edit(ActionEvent event) {
        demoUser selectedItem = table.getSelectionModel().getSelectedItem();
        getMainController().getPopUpWindow().getUserViewController().setTabTitle("CHỈNH SỬA USER");
        getMainController().getPopUpWindow().displayUser(selectedItem);
    }

    @FXML
    void remove(ActionEvent event) {
        demoUser selectedItem = table.getSelectionModel().getSelectedItem();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận");
        alert.setHeaderText("Bạn có chắc chắn muốn xóa người dùng này?");

        ButtonType yes = new ButtonType("Có");
        ButtonType no = new ButtonType("Không");
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(yes, no);
        Optional<ButtonType> opt = alert.showAndWait();

        if (opt.get() == yes) {

            // TODO GỌI HÀM XÓA NGƯỜI DÙNG TẠI ĐÂY

            UserList.remove(selectedItem);
            Notification notification = new Notification("Thành công!", "Đã xóa thành công " + selectedItem.getUserName());
            notification.display();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hideButtons();
        UserList = FXCollections.observableArrayList(
                new demoUser("usernam1", "password1", "email1@gmail.com", "0912345678", "id1", "preference1"),
                new demoUser("usernam2", "password2", "email2@gmail.com", "0912345678", "id1", "preference2"),
                new demoUser("usernam3", "password3", "email3@gmail.com", "0912345678", "id1", "preference3"),
                new demoUser("usernam4", "password4", "email4@gmail.com", "0912345678","id1", "preference4"),
                new demoUser("usernam5", "password5", "email5@gmail.com", "0912345678", "id1", "preference5"),
                new demoUser("usernam6", "password6", "email6@gmail.com", "0912345678", "id1", "preference6"),
                new demoUser("usernam7", "password7", "email7@gmail.com", "0912345678", "id1", "preference7"),
                new demoUser("usernam8", "password8", "email8@gmail.com", "0912345678", "id1", "preference8")
        );
        UserName.setCellValueFactory(new PropertyValueFactory<demoUser, String>("userName"));
        Email.setCellValueFactory(new PropertyValueFactory<demoUser, String>("email"));
        Phone.setCellValueFactory(new PropertyValueFactory<demoUser, String>("phone"));
        ID.setCellValueFactory(new PropertyValueFactory<demoUser, String>("id"));
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
        demoUser selectedItem = table.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            showButtons();
        } else {
            hideButtons();
        }
    }
}
