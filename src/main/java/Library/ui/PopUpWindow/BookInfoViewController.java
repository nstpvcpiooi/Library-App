package Library.ui.PopUpWindow;

import Library.backend.Login.Model.User;
import Library.backend.Request.DAO.RequestDAOImpl;
import Library.backend.Request.Model.Request;
import Library.backend.Session.SessionManager;
import Library.backend.bookDao.BookDao;
import Library.backend.bookDao.MysqlBookDao;
import Library.backend.bookModel.Book;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.io.File;
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

    private static final ExecutorService executorService = Executors.newFixedThreadPool(4); // Giới hạn 4 luồng

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

    public void setData(Book book) {
        selectedBook = book;

        // 1. LẤY ẢNH BÌA SÁCH
        /*
        try {
            // TODO KIỂM TRA ĐỊA CHỈ ẢNH BỊ LỖI?
            Image image = new Image(book.getCoverCode());
            cover.setImage(image);

        } catch (Exception e) {
            System.out.println("Error loading image from " + book.getCoverCode());
            cover.setImage(DEFAULT_COVER);

            // demo với link ảnh trên web
//            cover.setImage (new Image("https://marketplace.canva.com/EAFaQMYuZbo/1/0/1003w/canva-brown-rusty-mystery-novel-book-cover-hG1QhA7BiBU.jpg"));
        }
        */
        loadBookDetails(book);
        Platform.runLater(() -> {
            title.setText(book.getTitle());
            author.setText(book.getAuthor());
            isbn.setText(book.getIsbn());
            category.setText(book.getCategory());
            publishyear.setText(String.valueOf(book.getPublishYear()));
            description.setText(book.fetchBookDescriptionFromAPI());
            quantity.setText(String.valueOf(book.getQuantity()));
        });

        QRCodeHandler.handleQRCode(book, ImageQR);
        if (getPopUpWindow().getMainController() instanceof UserMainController) {
            // Xử lý trong luồng nền
            CompletableFuture.runAsync(() -> {
                SessionManager sessionManager = SessionManager.getInstance();
                User user = new User(sessionManager.getLoggedInMember());
                Request request = RequestDAOImpl.getInstance().getRequestByMemberIDAndBookID(user.getMemberID(), book.getBookID());

                Platform.runLater(() -> { // Cập nhật giao diện trên UI Thread
                    if (request == null) {
                        ActionButton.setText("MƯỢN SÁCH");
                        ActionButton.getStyleClass().remove("BorrowedButton");
                        ActionButton.setDisable(false);
                        RemoveButton.setVisible(false);
                    } else if ("approved issue".equals(request.getStatus())) {
                        ActionButton.setText("ĐÃ MƯỢN");
                        ActionButton.getStyleClass().add("BorrowedButton");
                        ActionButton.setDisable(true);
                        RemoveButton.setText("TRẢ SÁCH");
                        RemoveButton.getStyleClass().remove("BorrowedButton");
                        RemoveButton.setVisible(true);
                        RemoveButton.setDisable(false);
                        showOverdue("Hạn trả: " + request.getDueDate());
                    } else if ("pending issue".equals(request.getStatus())) {
                        ActionButton.setText("ĐANG DUYỆT");
                        ActionButton.getStyleClass().add("BorrowedButton");
                        ActionButton.setDisable(true);
                        RemoveButton.setText("TRẢ SÁCH");
                        RemoveButton.setVisible(true);
                    } else if ("pending return".equals(request.getStatus())) {
                        ActionButton.setText("ĐÃ MƯỢN");
                        ActionButton.getStyleClass().add("BorrowedButton");
                        ActionButton.setDisable(true);
                        RemoveButton.setText("ĐANG TRẢ");
                        RemoveButton.setVisible(true);
                        RemoveButton.setDisable(true);
                        RemoveButton.getStyleClass().add("BorrowedButton");
                    } else {
                        ActionButton.setText("MƯỢN SÁCH");
                        ActionButton.getStyleClass().remove("BorrowedButton");
                        ActionButton.setDisable(false);
                        RemoveButton.setVisible(false);
                    }
                });
            });
        } else {
            Platform.runLater(() -> { // Cập nhật giao diện trên UI Thread
                ActionButton.setText("CHỈNH SỬA");
                RemoveButton.setText("XÓA SÁCH");
                RemoveButton.setVisible(true);
            });
        }
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
        CompletableFuture<Void> descriptionTask = CompletableFuture.supplyAsync(book::fetchBookDescriptionFromAPI, executorService)
                .thenAccept(descriptionText ->
                        Platform.runLater(() -> description.setText(descriptionText))
                );

        CompletableFuture.allOf(coverTask, qrCodeTask).thenRun(() ->
                Platform.runLater(() -> {
                    System.out.println("Load cover và QR Code");
                })
        );
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
