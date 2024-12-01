package Library.ui.Admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class RequestManageController implements Initializable {

    @FXML
    private TableColumn<?, ?> BookID;

    @FXML
    private TableColumn<?, ?> DueDate;

    @FXML
    private TableColumn<?, ?> IssueDate;

    @FXML
    private TableColumn<?, ?> ReturnDate;

    @FXML
    private Button approveButton;

    @FXML
    private Button declineButton;

    @FXML
    private TableColumn<?, ?> memberID;

    @FXML
    private TableColumn<?, ?> requestID;

    @FXML
    private TableView<?> table;

    private AdminMainController MainController;

    @FXML
    void approve(ActionEvent event) {

    }

    @FXML
    void decline(ActionEvent event) {

    }

    @FXML
    void selectItem(MouseEvent event) {
        showButtons();
    }

    public void setMainController(AdminMainController adminMainController) {
        this.MainController = adminMainController;
    }

    public AdminMainController getMainController() {
        return MainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void hideButtons() {
        approveButton.setVisible(false);
        declineButton.setVisible(false);
    }

    public void showButtons() {
        approveButton.setVisible(true);
        declineButton.setVisible(true);
    }
}
