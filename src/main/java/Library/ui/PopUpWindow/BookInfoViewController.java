package Library.ui.PopUpWindow;

import Library.backend.Login.Model.User;
import Library.backend.Request.DAO.RequestDAOImpl;
import Library.backend.Request.Model.Request;
import Library.backend.Review.Dao.MysqlReviewDao;
import Library.backend.Review.model.Review;
import Library.backend.Session.SessionManager;
import Library.backend.bookDao.BookDao;
import Library.backend.bookDao.MysqlBookDao;
import Library.backend.bookModel.Book;
import Library.ui.Admin.AdminMainController;
import Library.ui.BookCard.BookCardController;
import Library.ui.Utils.Notification;
import Library.ui.User.UserMainController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.controlsfx.control.Rating;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static Library.ui.MainController.DEFAULT_COVER;
import static java.lang.String.valueOf;

/**
 * Controller cho cửa sổ hiển thị thông tin sách chi tiết
 */
public class BookInfoViewController extends PopUpController {

    @FXML
    private Label author;

    @FXML
    private AnchorPane container;

    @FXML
    private Button closeButton;

    @FXML
    private ImageView cover;

    @FXML
    private Label title;

    @FXML
    private Label isbn;

    @FXML
    private Label category;

    @FXML
    private Label publishyear;

    @FXML
    private Label description;

    @FXML
    private Label quantity;

    @FXML
    private ImageView ImageQR;

    @FXML
    private Button ActionButton;

    private Book selectedBook;

    @FXML
    private Button RemoveButton;

    @FXML
    private Label overdue;

    @FXML
    private HBox overdueBox;
    @FXML
    private Rating Rating;
    @FXML
    private Rating YourRating;

    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);
    @FXML
    void Remove(ActionEvent event) {
        if (getPopUpWindow().getMainController() instanceof AdminMainController) {
            //  TODO: XÓA SÁCH
            selectedBook.deleteBook();
            ((AdminMainController) getPopUpWindow().getMainController()).libraryManageController.removeBook(selectedBook);
            getPopUpWindow().close();
            Notification notification = new Notification("Chúc mừng!", "Bạn đã xóa sách thành công");
            notification.display();

        } else if (getPopUpWindow().getMainController() instanceof UserMainController) {
            //  TODO: TRẢ SÁCH
            SessionManager sessionManager = SessionManager.getInstance();
            User user = new User(sessionManager.getLoggedInMember());
            user.createReturnRequest(selectedBook.getBookID());
            Request request = RequestDAOImpl.getInstance().getRequestByMemberIDAndBookID(user.getMemberID(), selectedBook.getBookID());
            RequestDAOImpl.getInstance().updateRequest(request);
            //request = RequestDAOImpl.getInstance().getRequestByMemberIDAndBookID(user.getMemberID(), selectedBook.getBookID());
            if (request.getStatus().equals("approved return")) {
                ActionButton.setText("MƯỢN SÁCH");

                ActionButton.getStyleClass().remove("BorrowedButton");
                ActionButton.setDisable(false);
                RemoveButton.setVisible(false);
                Notification notification = new Notification("Chúc mừng!", "Bạn đã trả sách thành công");
                notification.display();
            }
            else {
                ActionButton.setText("ĐÃ MƯỢN");
                ActionButton.getStyleClass().add("BorrowedButton");
                ActionButton.setDisable(true);
                RemoveButton.setText("ĐANG TRẢ");
                RemoveButton.setVisible(true);
                RemoveButton.setDisable(true);
                RemoveButton.getStyleClass().add("BorrowedButton");
            }
        }
    }
    @FXML
    void Rating(MouseEvent event)
    {
       // Review review = new Review()
        System.out.println("Rating: " + YourRating.getRating());
        //MysqlReviewDao.getInstance().addReview(selectedBook.getBookID(), YourRating.getRating());
        SessionManager sessionManager = SessionManager.getInstance();
        User user = new User(sessionManager.getLoggedInMember());
        user.reviewBook(selectedBook.getBookID(), (int) YourRating.getRating(),"");
    }

    @FXML
    void Action(ActionEvent event)  {
        if (getPopUpWindow().getMainController() instanceof AdminMainController) {
            getPopUpWindow().displayEdit(selectedBook);
        } else if (getPopUpWindow().getMainController() instanceof UserMainController) {

            // TODO: MƯỢN SÁCH
            SessionManager sessionManager = SessionManager.getInstance();
            User user = new User(sessionManager.getLoggedInMember());
            if (selectedBook.getQuantity()>0) {
                if (user.hasOverdueBook())
                {
                    Notification notification = new Notification("Lỗi!", "Bạn đang mượn sách quá hạn. Vui lòng trả sách trước khi mượn sách mới");
                    notification.display();
                    return;
                }
                user.createIssueRequest(selectedBook.getBookID());
                selectedBook = Book.getBookById(selectedBook.getBookID());
                System.out.println("Số lượng sách còn lại: " + selectedBook.getQuantity());
                setData(selectedBook);
                ActionButton.setText("ĐANG DUYỆT");
                ActionButton.getStyleClass().add("BorrowedButton");
                ActionButton.setDisable(true);
                RemoveButton.setText("TRẢ SÁCH");
                RemoveButton.setVisible(true);
            }
            else {
                ActionButton.setText("HẾT SÁCH");
                ActionButton.setDisable(true);
            }
        }
    }

    public CompletableFuture<Void> setData(Book book) {
        selectedBook = book;

        // Tải dữ liệu đồng thời
        CompletableFuture<Void> coverTask = CompletableFuture.runAsync(() -> loadBookCover(book.getCoverCode()), executorService);
        CompletableFuture<Void> qrCodeTask = CompletableFuture.runAsync(() -> QRCodeHandler.handleQRCode(book, ImageQR), executorService);
        CompletableFuture<Request> requestTask = CompletableFuture.supplyAsync(() -> {
            if (getPopUpWindow().getMainController() instanceof UserMainController) {
                SessionManager sessionManager = SessionManager.getInstance();
                User user = new User(sessionManager.getLoggedInMember());
                return RequestDAOImpl.getInstance().getRequestByMemberIDAndBookID(user.getMemberID(), book.getBookID());
            }
            return null; // Không cần tải request nếu là Admin
        }, executorService);


        return CompletableFuture.allOf(coverTask, qrCodeTask, requestTask).thenRun(() -> {
            Request request = requestTask.join();

            // Cập nhật giao diện trên UI Thread
            Platform.runLater(() -> {
                title.setText(book.getTitle());
                author.setText(book.getAuthor());
                isbn.setText(book.getIsbn());
                category.setText(book.getCategory());
                publishyear.setText(String.valueOf(book.getPublishYear()));
                quantity.setText(String.valueOf(book.getQuantity()));
                description.setText(book.fetchBookDescriptionFromAPI());
                Rating.setRating(MysqlReviewDao.getInstance().getAverageRatingForBook(book.getBookID()));
                if (SessionManager.getInstance().getLoggedInMember().getDuty()==1) {
                    YourRating.setDisable(true);
                } else {
                    YourRating.setDisable(false);
                }
                if (getPopUpWindow().getMainController() instanceof UserMainController) {
                    updateUserControls(request);
                } else {
                    updateAdminControls();
                }
            });
        });
    }

    // Cập nhật giao diện cho User
    private void updateUserControls(Request request) {
        if (request == null) {
            ActionButton.setText("MƯỢN SÁCH");
            ActionButton.getStyleClass().remove("BorrowedButton");
            ActionButton.setDisable(false);
            RemoveButton.setVisible(false);
            hideOverdue();
        } else {
            switch (request.getStatus()) {
                case "":
                    ActionButton.setText("MƯỢN SÁCH");
                    ActionButton.getStyleClass().remove("BorrowedButton");
                    ActionButton.setDisable(false);
                    RemoveButton.setVisible(false);
                    hideOverdue();
                    break;
                case "approved issue":
                    ActionButton.setText("ĐÃ MƯỢN");
                    ActionButton.getStyleClass().add("BorrowedButton");
                    ActionButton.setDisable(true);
                    RemoveButton.setText("TRẢ SÁCH");
                    RemoveButton.getStyleClass().remove("BorrowedButton");
                    RemoveButton.setVisible(true);
                    RemoveButton.setDisable(false);
                    showOverdue("Hạn trả: " + normalizeDate(formatDate(request.getDueDate())));
                    break;
                case "pending issue":
                    ActionButton.setText("ĐANG DUYỆT");
                    ActionButton.getStyleClass().add("BorrowedButton");
                    ActionButton.setDisable(true);
                    RemoveButton.setText("TRẢ SÁCH");
                    RemoveButton.setVisible(true);
                    showOverdue("Hạn trả: " + normalizeDate(formatDate(request.getDueDate())));
                    break;
                case "pending return":
                    ActionButton.setText("ĐÃ MƯỢN");
                    ActionButton.getStyleClass().add("BorrowedButton");
                    ActionButton.setDisable(true);
                    RemoveButton.setText("ĐANG TRẢ");
                    RemoveButton.setVisible(true);
                    RemoveButton.setDisable(true);
                    RemoveButton.getStyleClass().add("BorrowedButton");
                    showOverdue("Hạn trả: " + normalizeDate(formatDate(request.getDueDate())));
                    break;
                default:
                    ActionButton.setText("MƯỢN SÁCH");
                    ActionButton.getStyleClass().remove("BorrowedButton");
                    ActionButton.setDisable(false);
                    RemoveButton.setVisible(false);
                    System.out.println("Request status: " + request.getStatus());
                    hideOverdue();
                    System.out.println("Request status: " + request.getStatus());
            }
        }
    }

    // Cập nhật giao diện cho Admin
    private void updateAdminControls() {
        ActionButton.setText("CHỈNH SỬA");
        RemoveButton.setText("XÓA SÁCH");
        RemoveButton.setVisible(true);
    }

    public class QRCodeHandler {
        public static void handleQRCode(Book book, ImageView imageView) {
            String qrCodePath = "src/main/resources/Library/" + book.getBookID() + "_qr.png";
            File qrFile = new File(qrCodePath);

            CompletableFuture.runAsync(() -> {
                if (qrFile.exists()) {
                    System.out.println("QR Code đã tồn tại: " + qrCodePath);
                    loadImageToImageView(qrCodePath, imageView);
                } else {
                    System.out.println("Đang tạo QR Code mới cho sách: " + book.getTitle());
                    try {
                        String generatedQrCodePath = book.generateQrCodeForBook();
                        loadImageToImageView(generatedQrCodePath, imageView);
                    } catch (Exception e) {
                        System.out.println("Lỗi khi tạo QR Code: " + e.getMessage());
                    }
                }
            }, executorService);
        }

        private static void loadImageToImageView(String imagePath, ImageView imageView) {
            Platform.runLater(() -> {
                try {
                    Image qrImage = new Image("file:" + imagePath);
                    imageView.setImage(qrImage);
                } catch (Exception e) {
                    System.out.println("Lỗi khi tải ảnh QR Code: " + e.getMessage());
                }
            });
        }
    }

    public void loadBookCover(String coverCode) {
        executorService.submit(() -> {
            try {
                Image image = new Image(coverCode, true);
                Platform.runLater(() -> cover.setImage(image));
            } catch (Exception e) {
                System.err.println("Lỗi khi tải ảnh bìa: " + coverCode);
                Platform.runLater(() -> cover.setImage(DEFAULT_COVER));
            }
        });
    }

    public void loadBookDetails(Book book) {
        CompletableFuture<Void> coverTask = CompletableFuture.runAsync(() -> loadBookCover(book.getCoverCode()), executorService);
        CompletableFuture<Void> qrCodeTask = CompletableFuture.runAsync(() -> QRCodeHandler.handleQRCode(book, ImageQR), executorService);
        CompletableFuture.allOf(coverTask, qrCodeTask).thenRun(() ->
                Platform.runLater(() -> {
                    System.out.println("Load cover và QR Code");
                })
        );
    }

    private String normalizeDate(String dateString) {
        if (dateString == null) {
            return null;
        }
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd"); // Adjust this format based on your input date format
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yy");

        try {
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return dateTime.format(formatter);
    }

    protected void showOverdue(String... text) {
        overdueBox.setVisible(true);

        // thêm thông tin hạn trả sách
        if (text.length > 0) {
            overdue.setText(String.join("\n", text));
        } else {
            overdue.setText("Chưa có thông tin hạn trả");
        }
    }

    protected void hideOverdue() {
        overdueBox.setVisible(false);
    }
}
