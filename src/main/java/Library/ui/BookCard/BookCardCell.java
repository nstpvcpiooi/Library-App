package Library.ui.BookCard;

import Library.MainApplication;
import Library.backend.bookModel.Book;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import static Library.ui.MainController.DEFAULT_COVER;

public class BookCardCell extends ListCell<Book> {
    public enum BookCardType {SMALL, LARGE}

    protected BookCardType type;
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final int MAX_CONCURRENT_LOADS = 5; // Giới hạn số lượng load đồng thời
    private static final Semaphore loadSemaphore = new Semaphore(MAX_CONCURRENT_LOADS);

    public BookCardCell(BookCardType type) {
        this.type = type;
        // Tắt style mặc định khi selected
        setStyle("-fx-background-color: transparent;");
    }

    @Override
    protected void updateItem(Book book, boolean empty) {
        super.updateItem(book, empty);
        if (empty || book == null) {
            setText(null);
            setGraphic(null);
        } else {
            // Tạo container cho loading indicator
            HBox loadingContainer = new HBox();
            loadingContainer.setAlignment(javafx.geometry.Pos.CENTER);
            loadingContainer.getStyleClass().add("container");
            
            // Đặt kích thước container dựa vào loại card
            switch (type) {
                case SMALL:
                    loadingContainer.setPrefSize(205, 110);
                    break;
                case LARGE:
                    loadingContainer.setPrefSize(830, 150);
                    break;
            }

            // Tạo và thêm loading indicator vào container
            ProgressIndicator loadingIndicator = new ProgressIndicator();
            loadingIndicator.setMaxSize(30, 30);
            loadingContainer.getChildren().add(loadingIndicator);

            setGraphic(loadingContainer);

            // Run the update task in a separate thread
            executor.submit(() -> {
                try {
                    loadSemaphore.acquire(); // Giới hạn số lượng load đồng thời
                    FXMLLoader loader = new FXMLLoader();
                    BookCardController controller;
                    HBox bookCard;

                    switch (type) {
                        case SMALL:
                            loader.setLocation(MainApplication.class.getResource("fxml/BookCard/BookCard_small.fxml"));
                            break;
                        case LARGE:
                            loader.setLocation(MainApplication.class.getResource("fxml/BookCard/BookCard_large.fxml"));
                            break;
                    }

                    try {
                        bookCard = loader.load();
                        controller = loader.getController();
                        bookCard.getStyleClass().add("container");

                        // Load book data
                        controller.setData(book);

                        Image image;
                        // Load book cover image
                        try {
                            image = new Image(book.getCoverCode());
                        } catch (Exception e) {
                            System.err.println("Error loading image: " + e.getMessage());
                            image = DEFAULT_COVER;
                        }

                        // Update the UI on the JavaFX Application Thread
                        Image finalImage = image;
                        Platform.runLater(() -> {
                            if (getItem() == book) {
                                controller.cover.setImage(finalImage);
                                setGraphic(bookCard);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Platform.runLater(() -> {
                            if (getItem() == book) {
                                Label errorLabel = new Label("Error loading book card");
                                errorLabel.setAlignment(javafx.geometry.Pos.CENTER);
                                errorLabel.setPrefSize(loadingContainer.getPrefWidth(), loadingContainer.getPrefHeight());
                                setGraphic(errorLabel);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    loadSemaphore.release();
                }
            });
        }
    }
}