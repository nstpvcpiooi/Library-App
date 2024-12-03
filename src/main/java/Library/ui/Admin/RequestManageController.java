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

/**
 * Controller cho giao diện quản lý yêu cầu của admin
 */
public class RequestManageController implements Initializable {

    /**
     * Bảng hiển thị danh sách yêu cầu
     */
    @FXML
    private TableView<Request> table;

    /**
     * Cột hiển thị mã sách, mã người mượn, ngày mượn, ngày hết hạn, ngày trả, mã yêu cầu, trạng thái, quá hạn...
     */
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

    /**
     * Nút duyệt yêu cầu
     */
    @FXML
    private Button approveButton;

    /**
     * Controller chính của admin
     */
    private AdminMainController MainController;

    /**
     * Duyệt yêu cầu (hàm xử lý sự kiện khi nhấn nút duyệt yêu cầu approveButton)
     * @param event
     */
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

    /**
     * Chọn một yêu cầu (hàm xử lý sự kiện khi click vào một yêu cầu trong bảng)
     * @param event
     */
    @FXML
    void selectItem(MouseEvent event) {

        // TODO: CHECK XEM CÓ LỖI KHÔNG
        // Nếu yêu cầu đang chờ duyệt thì hiển thị nút duyệt yêu cầu
        Request selectedRequest = table.getSelectionModel().getSelectedItem();
        if (selectedRequest != null) {
            if (selectedRequest.getStatus().equals("pending issue") || selectedRequest.getStatus().equals("pending return")) {
                showButtons();
            } else {
                hideButtons();
            }
        }

//        showButtons();
    }

    /**
     * Ẩn nút duyệt yêu cầu
     */
    public void hideButtons() {
        approveButton.setVisible(false);
        table.getSelectionModel().clearSelection();
    }

    /**
     * Hiển thị nút duyệt yêu cầu
     */
    public void showButtons() {
        approveButton.setVisible(true);

    }

    /**
     * Khởi tạo giao diện quản lý yêu cầu
     */
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

    /**
     * Cập nhật dữ liệu
     */
    void refreshData() {
        ObservableList<Request> requests = FXCollections.observableArrayList(RequestDAOImpl.getInstance().getAllRequests());
        table.setItems(requests);
    }

    public void setMainController(AdminMainController adminMainController) {
        this.MainController = adminMainController;
        refreshData();
    }

    public AdminMainController getMainController() {
        return MainController;
    }
}