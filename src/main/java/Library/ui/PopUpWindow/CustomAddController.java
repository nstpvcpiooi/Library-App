package Library.ui.PopUpWindow;

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

    /**
     * Ô nhập tên tác giả, thể loại, năm xuất bản, tiêu đề, số lượng, mã ISBN...v.v
     */
    @FXML
    private TextField authorInput;

    @FXML
    private TextField categoryInput;

    @FXML
    private TextField publishYearInput;

    @FXML
    private TextField titleInput;

    @FXML
    private TextField quantityInput;

    @FXML
    private TextField isbnCodeInput;

    /**
     * Ảnh bìa sách
     */
    @FXML
    private ImageView cover;

    /**
     * Nút lưu thông tin sách
     */
    @FXML
    private Button saveButton;

    /**
     * Nút quay lại
     */
    @FXML
    private Button backButton;

    /**
     * Hàm xử lý sự kiện khi nhấn vào nút lưu thông tin sách
     * @param event sự kiện chuột
     *
     * Khi nhấn vào nút lưu thông tin sách, kiểm tra thông tin nhập vào có hợp lệ không
     *
     * Nếu thông tin nhập vào hợp lệ, thêm sách vào database
     *
     * Nếu thông tin nhập vào không hợp lệ, hiển thị thông báo lỗi
     */
    @FXML
    void Save(ActionEvent event) {
        // Biến để kiểm tra trạng thái đầu vào
        boolean success = true;
        Book b = null;

        try {
            // Kiểm tra và lấy dữ liệu đầu vào
            int publishYear = Integer.parseInt(publishYearInput.getText());
            if (publishYear < 0) {
                success = false;
                showNotification("Lỗi!", "Năm xuất bản phải là số không âm.");
            }

            int quantity = Integer.parseInt(quantityInput.getText());
            if (quantity < 0) {
                success = false;
                showNotification("Lỗi!", "Số lượng phải là số không âm.");
            }

            // Nếu thành công, tạo đối tượng sách
            if (success) {
                b = new Book(
                        "",
                        titleInput.getText(),
                        authorInput.getText(),
                        publishYear,
                        categoryInput.getText(),
                        "",
                        "",
                        quantity
                );
            }

        } catch (NumberFormatException e) {
            success = false;
            showNotification("Lỗi!", "Vui lòng đảm bảo các trường số phải nhập đúng định dạng số nguyên.");
        }

        // Xử lý lưu sách vào database hoặc thông báo lỗi
        if (success && b != null) {
            // TODO: Thêm sách vào cơ sở dữ liệu
            b.addBook();
            getPopUpWindow().close();
            showNotification("Chúc mừng!", "Bạn đã thêm sách vào thư viện thành công.");
        } else {
            showNotification("Lỗi!", "Không thể thêm sách vào thư viện. Vui lòng kiểm tra lại thông tin.");
        }
    }

    // Hàm hiển thị thông báo
    private void showNotification(String title, String message) {
        Notification notification = new Notification(title, message);
        notification.display();
    }

    /**
     * Hàm xử lý sự kiện khi nhấn vào nút quay lại
     * @param event sự kiện chuột
     *
     * Khi nhấn vào nút quay lại, quay lại cửa sổ thêm sách
     * (chọn giữa thêm sách tùy chỉnh và thêm sách bằng mã ISBN)
     */
    @FXML
    void goBack(ActionEvent event) {
        getPopUpWindow().backtoAdd();
    }


    /**
     * Hiển thị thông tin sách (trong trường hợp thêm sách tùy chỉnh thì không cần hiển thị thông tin sách)
     *
     * Trong trường hợp thêm sách bằng mã ISBN, hiển thị thông tin sách từ API
     */
    public void setData(Book selectedBook) {

        // TODO: THÊM SÁCH BẰNG MÃ ISBN
        // Nếu selectedBook != null thì hiển thị thông tin sách từ API
        if (selectedBook != null) {
            isbnCodeInput.setText(selectedBook.getIsbn());

            try {
                Image image = new Image(selectedBook.getCoverCode());
                cover.setImage(image);
            } catch (Exception e) {
                cover.setImage(DEFAULT_COVER);
            }

        }
        // TODO: THÊM SÁCH TÙY CHỌN
        // Nếu selectedBook == null thì không hiển thị thông tin sách
        else {
            isbnCodeInput.clear();
            cover.setImage(DEFAULT_COVER);
        }
    }
}
