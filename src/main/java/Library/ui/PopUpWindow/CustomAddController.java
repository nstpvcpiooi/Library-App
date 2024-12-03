package Library.ui.PopUpWindow;

import Library.backend.bookDao.MysqlBookDao;
import Library.backend.bookModel.Book;
import Library.ui.Utils.Notification;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import static Library.ui.MainController.DEFAULT_COVER;

public class CustomAddController extends PopUpController {

    @FXML
    private TextField authorInput, categoryInput, publishYearInput, titleInput, quantityInput, isbnCodeInput;

    @FXML
    private ImageView cover;

    @FXML
    private Button saveButton, backButton;

    /**
     * Hàm xử lý sự kiện khi nhấn vào nút lưu thông tin sách
     * @param event sự kiện chuột
     */
    @FXML
    void Save(ActionEvent event) {
        // Kiểm tra tính hợp lệ của dữ liệu nhập vào
        boolean success = validateInputs();

        if (!success) {
            showNotification("Lỗi!", "Thông tin nhập không hợp lệ. Vui lòng kiểm tra lại.");
            return;
        }

        try {
            // Tạo đối tượng sách từ dữ liệu nhập vào
            Book newBook = new Book(
                    isbnCodeInput.getText(),
                    titleInput.getText(),
                    authorInput.getText(),
                    Integer.parseInt(publishYearInput.getText()),
                    categoryInput.getText(),
                    "",
                    "",
                    Integer.parseInt(quantityInput.getText())
            );

            // Lưu sách vào cơ sở dữ liệu
            MysqlBookDao bookDao = MysqlBookDao.getInstance();
            bookDao.addBook(newBook);

            // Đóng popup và hiển thị thông báo thành công
            getPopUpWindow().close();
            showNotification("Chúc mừng!", "Bạn đã thêm sách vào thư viện thành công.");

        } catch (Exception e) {
            e.printStackTrace();
            showNotification("Lỗi!", "Không thể thêm sách vào thư viện. Vui lòng thử lại.");
        }
    }

    @FXML
    void goBack(ActionEvent event) {
        getPopUpWindow().backtoAdd();
    }

    /**
     * Hàm xử lý hiển thị dữ liệu sách lên giao diện
     * @param selectedBook đối tượng Book được chọn
     */
    public void setData(Book selectedBook) {
        if (selectedBook != null) {
            displayBookDetails(selectedBook);
            selectedBook.setCoverCode(selectedBook.getCoverCode() != null ? selectedBook.getCoverCode() : "");
        } else {
            resetForm();
        }
    }

    /**
     * Hiển thị chi tiết sách lên giao diện
     * @param book đối tượng Book chứa thông tin
     */
    private void displayBookDetails(Book book) {
        isbnCodeInput.setText(book.getIsbn());

        try {
            Image image = new Image(book.getCoverCode());
            cover.setImage(image);
        } catch (Exception e) {
            cover.setImage(DEFAULT_COVER);
        }

        titleInput.setText(book.getTitle());
        authorInput.setText(book.getAuthor());
        categoryInput.setText(book.getCategory());
        publishYearInput.setText(Integer.toString(book.getPublishYear()));
        quantityInput.setText(Integer.toString(book.getQuantity()));
    }

    /**
     * Đặt lại giao diện về trạng thái ban đầu
     */
    private void resetForm() {
        isbnCodeInput.clear();
        titleInput.clear();
        authorInput.clear();
        categoryInput.clear();
        publishYearInput.clear();
        quantityInput.clear();
        cover.setImage(DEFAULT_COVER);
    }

    /**
     * Kiểm tra tính hợp lệ của thông tin nhập vào
     * @return true nếu hợp lệ, false nếu không hợp lệ
     */
    private boolean validateInputs() {
        try {
            int publishYear = Integer.parseInt(publishYearInput.getText());
            if (publishYear < 0) {
                showNotification("Lỗi!", "Năm xuất bản phải là số không âm.");
                return false;
            }

            int quantity = Integer.parseInt(quantityInput.getText());
            if (quantity < 0) {
                showNotification("Lỗi!", "Số lượng phải là số không âm.");
                return false;
            }
        } catch (NumberFormatException e) {
            showNotification("Lỗi!", "Vui lòng đảm bảo các trường số phải nhập đúng định dạng số nguyên.");
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
