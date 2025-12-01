package Library.ui.PopUpWindow;

import Library.backend.Book.Model.Book;
import Library.backend.Book.Service.BookService;
import Library.backend.Request.service.RequestService;
import Library.backend.Request.Model.Request;
import Library.backend.Review.Model.Review;
import Library.backend.Review.service.ReviewService;
import Library.backend.Session.SessionManager;
import Library.ui.Admin.AdminMainController;
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
    private Request currentRequest;

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
    private final BookService bookService = BookService.getInstance();
    @FXML
    void Remove(ActionEvent event) {
        if (getPopUpWindow().getMainController() instanceof AdminMainController) {
            bookService.deleteBook(selectedBook.getBookID());
            ((AdminMainController) getPopUpWindow().getMainController()).libraryManageController.removeBook(selectedBook);
            getPopUpWindow().close();
            Notification notification = new Notification("Chúc mừng!", "Bạn đã xóa sách thành công");
            notification.display();

        } else if (getPopUpWindow().getMainController() instanceof UserMainController) {
            if (currentRequest != null && "Đang giữ".equals(currentRequest.getStatus())) {
                SessionManager sessionManager = SessionManager.getInstance();
                RequestService.getInstance().cancelHold(sessionManager.getLoggedInMember().getMemberID(), selectedBook.getBookID());
                ActionButton.setText("ĐẶT GIỮ");
                ActionButton.getStyleClass().remove("BorrowedButton");
                ActionButton.setDisable(false);
                RemoveButton.setVisible(false);
                hideOverdue();
                Notification notification = new Notification("Đã hủy", "Bạn đã hủy giữ chỗ. Sách đã được trả về kho.");
                notification.display();
            } else {
                Notification notification = new Notification("Thông báo", "Vui lòng mang sách đến thư viện để trả. Nhân viên sẽ cập nhật trạng thái giúp bạn.");
                notification.display();
            }
        }
    }
    @FXML
    void Rating(MouseEvent event)
    {
       // Review review = new Review()
        System.out.println("Rating: " + YourRating.getRating());
        SessionManager sessionManager = SessionManager.getInstance();
        ReviewService.getInstance().submitReview(selectedBook.getBookID(), sessionManager.getLoggedInMember().getMemberID(),
                (int) YourRating.getRating(), "");
    }

    @FXML
    void Action(ActionEvent event)  {
        if (getPopUpWindow().getMainController() instanceof AdminMainController) {
            getPopUpWindow().displayEdit(selectedBook);
        } else if (getPopUpWindow().getMainController() instanceof UserMainController) {

            // TODO: MƯỢN SÁCH
            SessionManager sessionManager = SessionManager.getInstance();
            if (selectedBook.getQuantity()>0) {
                if (RequestService.getInstance().hasOverdueBorrow(sessionManager.getLoggedInMember().getMemberID()))
                {
                    Notification notification = new Notification("Lỗi!", "Bạn đang mượn sách quá hạn. Vui lòng trả sách trước khi mượn sách mới");
                    notification.display();
                    return;
                }
                if (RequestService.getInstance().hasReachedBorrowLimit(sessionManager.getLoggedInMember().getMemberID())) {
                    Notification notification = new Notification("Lỗi!", "Bạn chỉ được mượn tối đa 5 cuốn. Vui lòng trả bớt trước khi mượn thêm.");
                    notification.display();
                    return;
                }
                try {
                    RequestService.getInstance().placeHold(sessionManager.getLoggedInMember().getMemberID(), selectedBook.getBookID());
                } catch (IllegalStateException ex) {
                    Notification notification = new Notification("Lỗi!", ex.getMessage());
                    notification.display();
                    return;
                }
                selectedBook = bookService.getBookById(selectedBook.getBookID());
                System.out.println("Số lượng sách còn lại: " + selectedBook.getQuantity());
                setData(selectedBook);
                ActionButton.setText("ĐANG GIỮ");
                ActionButton.getStyleClass().add("BorrowedButton");
                ActionButton.setDisable(true);
                RemoveButton.setVisible(false);
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
        CompletableFuture<Void> qrCodeTask = CompletableFuture.runAsync(() -> new QRCodeHandler().handleQRCode(book, ImageQR), executorService);
        CompletableFuture<Request> requestTask = CompletableFuture.supplyAsync(() -> {
            if (getPopUpWindow().getMainController() instanceof UserMainController) {
                SessionManager sessionManager = SessionManager.getInstance();
                return RequestService.getInstance()
                        .getLatestRequest(sessionManager.getLoggedInMember().getMemberID(), book.getBookID());
            }
            return null;
        }, executorService);


        return CompletableFuture.allOf(coverTask, qrCodeTask, requestTask).thenRun(() -> {
            Request request = requestTask.join();
            currentRequest = request;

            // Cập nhật giao diện trên UI Thread
            Platform.runLater(() -> {
                title.setText(book.getTitle());
                author.setText(book.getAuthor());
                isbn.setText(book.getIsbn());
                category.setText(book.getCategory());
                publishyear.setText(String.valueOf(book.getPublishYear()));
                quantity.setText(String.valueOf(book.getQuantity()));
                description.setText(bookService.fetchBookDescription(book));
                ReviewService reviewService = ReviewService.getInstance();
                Rating.setRating(reviewService.getAverageRating(book.getBookID()));
                Review existingReview = reviewService.getReview(book.getBookID(),
                        SessionManager.getInstance().getLoggedInMember().getMemberID());
                YourRating.setRating(existingReview != null ? existingReview.getRating() : 0);
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
            ActionButton.setText("ĐẶT GIỮ");
            ActionButton.getStyleClass().remove("BorrowedButton");
            ActionButton.setDisable(false);
            RemoveButton.setVisible(false);
            hideOverdue();
        } else {
            switch (request.getStatus()) {
                case "":
                    ActionButton.setText("ĐẶT GIỮ");
                    ActionButton.getStyleClass().remove("BorrowedButton");
                    ActionButton.setDisable(false);
                    RemoveButton.setVisible(false);
                    hideOverdue();
                    break;
                case "Đang mượn":
                    ActionButton.setText("ĐANG MƯỢN");
                    ActionButton.getStyleClass().add("BorrowedButton");
                    ActionButton.setDisable(true);
                    RemoveButton.setVisible(false);
                    showOverdue("Hạn trả: " + normalizeDate(formatDate(request.getDueDate())));
                    break;
                case "Đang giữ":
                    ActionButton.setText("ĐANG GIỮ");
                    ActionButton.getStyleClass().add("BorrowedButton");
                    ActionButton.setDisable(true);
                    RemoveButton.setVisible(true);
                    RemoveButton.setText("HỦY GIỮ");
                    RemoveButton.setDisable(false);
                    RemoveButton.getStyleClass().remove("BorrowedButton");
                    showOverdue("Hạn giữ đến: " + normalizeDate(formatDate(request.getDueDate())));
                    break;
                case "Đã trả":
                    ActionButton.setText("ĐẶT GIỮ");
                    ActionButton.getStyleClass().remove("BorrowedButton");
                    ActionButton.setDisable(false);
                    RemoveButton.setVisible(false);
                    hideOverdue();
                    break;
                default:
                    ActionButton.setText("ĐẶT GIỮ");
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
        public void handleQRCode(Book book, ImageView imageView) {
            String qrCodePath = "src/main/resources/Library/" + book.getBookID() + "_qr.png";
            File qrFile = new File(qrCodePath);

            CompletableFuture.runAsync(() -> {
                try {
                    String qrPath = qrFile.exists() ? qrCodePath : bookService.generateQrCodeForBook(book);
                    if (qrPath != null) {
                    loadImageToImageView(qrPath, imageView);
                    }
                } catch (Exception e) {
                    System.err.println("Lỗi khi xử lý QR Code: " + e.getMessage());
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
        CompletableFuture<Void> qrCodeTask = CompletableFuture.runAsync(() -> new QRCodeHandler().handleQRCode(book, ImageQR), executorService);
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
