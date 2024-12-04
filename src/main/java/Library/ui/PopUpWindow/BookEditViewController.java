package Library.ui.PopUpWindow;

import Library.backend.bookModel.Book;
import Library.ui.Admin.AdminMainController;
import Library.ui.Utils.Notification;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import static Library.ui.MainController.DEFAULT_COVER;

public class BookEditViewController extends PopUpController {

    @FXML
    private Button CancelButton;

    @FXML
    private Button SaveButton;

    @FXML
    private TextField authorInput;

    @FXML
    private TextField categoryInput;

    @FXML
    private ImageView cover;

    @FXML
    private TextField isbnCodeInput;

    @FXML
    private TextField publishYearInput;

    @FXML
    private TextField quantityInput;

    @FXML
    private TextField titleInput;

    /**
     * Hàm lưu thông tin sách sau khi chỉnh sửa
     * @param event sự kiện nhấn nút
     */
    @FXML
    void Save(ActionEvent event) {
        boolean success = validateInputs();

        if (success) {
            // Lấy thông tin sách từ ISBN, sau đó cập nhật
            Book b = Book.getBookByIsbn(isbnCodeInput.getText());
            if (b == null) {
                showNotification("Lỗi!", "Không tìm thấy sách với ISBN này.");
                return;  // Dừng lại nếu không tìm thấy sách
            }

            b.updateBook(
                    titleInput.getText(),
                    authorInput.getText(),
                    Integer.parseInt(publishYearInput.getText()),
                    categoryInput.getText(),
                    b.getIsbn(),
                    b.getCoverCode(),
                    Integer.parseInt(quantityInput.getText())
            );
            // Cập nhật giao diện
            AdminMainController mainController = (AdminMainController) getPopUpWindow().getMainController();
            mainController.libraryManageController.updateBookInList(b);
            getPopUpWindow().close();  // Đóng cửa sổ popup

            // Hiển thị thông báo thành công
            Notification notification = new Notification("Chúc mừng!", "Bạn đã chỉnh sửa sách thành công");
            notification.display();
        } else {
            // Hiển thị thông báo lỗi nếu không hợp lệ
            Notification notification = new Notification("Lỗi!", "Vui lòng thử lại");
            notification.display();
        }
    }

    /**
     * Hàm quay lại màn hình thông tin sách
     * @param event sự kiện nhấn nút quay lại
     */
    @FXML
    void goBack(ActionEvent event) {
        getPopUpWindow().backtoInfo();  // Quay lại màn hình trước
    }

    /**
     * Hàm hiển thị thông tin sách lên giao diện
     * @param selectedBook sách được chọn
     */
    public void setData(Book selectedBook) {
        isbnCodeInput.setText(selectedBook.getIsbn());
        titleInput.setText(selectedBook.getTitle());
        publishYearInput.setText(String.valueOf(selectedBook.getPublishYear()));
        categoryInput.setText(selectedBook.getCategory());
        quantityInput.setText(String.valueOf(selectedBook.getQuantity()));
        authorInput.setText(selectedBook.getAuthor());

        // Hiển thị hình ảnh bìa sách
        try {
            Image image = new Image(selectedBook.getCoverCode());
            cover.setImage(image);
        } catch (Exception e) {
            cover.setImage(DEFAULT_COVER);
        }
    }

    /**
     * Kiểm tra tính hợp lệ của các trường nhập vào
     * @return true nếu hợp lệ, false nếu không hợp lệ
     */
    private boolean validateInputs() {
        // Kiểm tra ISBN
        String isbn = isbnCodeInput.getText();
        if (isbn == null || isbn.trim().isEmpty()) {
            showNotification("Lỗi!", "Vui lòng nhập ISBN.");
            return false;
        }

        // Kiểm tra năm xuất bản
        String publishYearText = publishYearInput.getText();
        if (publishYearText == null || publishYearText.trim().isEmpty()) {
            showNotification("Lỗi!", "Vui lòng nhập năm xuất bản.");
            return false;
        }
        try {
            int publishYear = Integer.parseInt(publishYearText.trim());
            if (publishYear < 0) {
                showNotification("Lỗi!", "Năm xuất bản phải là số không âm.");
                return false;
            }
        } catch (NumberFormatException e) {
            showNotification("Lỗi!", "Năm xuất bản phải là số.");
            return false;
        }

        // Kiểm tra số lượng
        String quantityText = quantityInput.getText();
        if (quantityText == null || quantityText.trim().isEmpty()) {
            showNotification("Lỗi!", "Vui lòng nhập số lượng.");
            return false;
        }
        try {
            int quantity = Integer.parseInt(quantityText.trim());
            if (quantity < 0) {
                showNotification("Lỗi!", "Số lượng sách phải là số không âm.");
                return false;
            }
        } catch (NumberFormatException e) {
            showNotification("Lỗi!", "Số lượng sách phải là số.");
            return false;
        }

        return true;
    }


    /**
     * Hiển thị thông báo lỗi hoặc thành công
     * @param title tiêu đề thông báo
     * @param message nội dung thông báo
     */
    private void showNotification(String title, String message) {
        Notification notification = new Notification(title, message);
        notification.display();
    }
}
