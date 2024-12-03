package Library.ui.Admin;

import Library.backend.Login.Model.Admin;
import Library.backend.Request.DAO.RequestDAOImpl;
import Library.backend.Request.Model.Request;
import Library.ui.Utils.Notification;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class RequestManageController implements Initializable {
    @FXML
    private TableView<Request> table;

    @FXML
    private TableColumn<Request, Integer> BookID;

    @FXML
    private TableColumn<Request, String> DueDate;

    @FXML
    private TableColumn<Request, String> IssueDate;

    @FXML
    private TableColumn<Request, String> ReturnDate;

    @FXML
    private TableColumn<Request, Integer> memberID;

    @FXML
    private TableColumn<Request, Integer> requestID;

    @FXML
    private TableColumn<Request, String> Status;

    @FXML
    private TableColumn<Request, Boolean> Overdue;

    @FXML
    private Button approveButton;



    private AdminMainController MainController;

    @FXML
    void approve(ActionEvent event) {
        Request selectedRequest = table.getSelectionModel().getSelectedItem();
        if (selectedRequest != null) {

            if (selectedRequest.getStatus().equals("approved issue")) {
                Notification notification = new Notification("Error", "Request already approved");
                notification.display();
                return;
            }
            else if (selectedRequest.getStatus().equals("pending return")) {
                Admin admin = new Admin();
                admin.approveReturnRequest(selectedRequest.getRequestID());
            } else if (selectedRequest.getStatus().equals("pending issue")) {
                Admin admin = new Admin();
                admin.approveIssueRequest(selectedRequest.getRequestID());
            }
            else {
                Notification notification = new Notification("Error", "Request already approved");
                notification.display();
                return;
            }
            Notification notification = new Notification("Success", "Request approved successfully");
            notification.display();
            refreshData();
        }
    }



    @FXML
    void selectItem(MouseEvent event) {
        showButtons();
    }

    public void setMainController(AdminMainController adminMainController) {
        this.MainController = adminMainController;
        refreshData();
    }

    public AdminMainController getMainController() {
        return MainController;
    }
    public void hideButtons() {
        approveButton.setVisible(false);

    }
    public void showButtons() {
        approveButton.setVisible(true);

    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hideButtons();
        refreshData();
        requestID.setCellValueFactory(new PropertyValueFactory<Request, Integer>("requestID"));
        memberID.setCellValueFactory(new PropertyValueFactory<Request, Integer>("memberID"));
        BookID.setCellValueFactory(new PropertyValueFactory<Request, Integer>("bookID"));
        IssueDate.setCellValueFactory(new PropertyValueFactory<Request, String>("issueDate"));
        DueDate.setCellValueFactory(new PropertyValueFactory<Request, String>("dueDate"));
        ReturnDate.setCellValueFactory(new PropertyValueFactory<Request, String>("returnDate"));
        Status.setCellValueFactory(new PropertyValueFactory<Request, String>("status"));
        Overdue.setCellValueFactory(new PropertyValueFactory<Request, Boolean>("overdue"));
    }
    void refreshData() {
        ObservableList<Request> requests = FXCollections.observableArrayList(RequestDAOImpl.getInstance().getAllRequests());
        table.setItems(requests);
    }
}