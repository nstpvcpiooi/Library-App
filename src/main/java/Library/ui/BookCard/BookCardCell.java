package Library.ui.BookCard;

import Library.MainApplication;
import Library.backend.Book.Model.Book;
import Library.ui.MainController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BookCardCell extends ListCell<Book> {
    public enum BookCardType {SMALL, LARGE}

    protected BookCardType type;
    private static final ExecutorService executor = Executors.newCachedThreadPool(); // Use a cached thread pool

    public BookCardCell(BookCardType type) {
        this.type = type;
    }

    @Override
    protected void updateItem(Book book, boolean empty) {
        super.updateItem(book, empty);
        if (empty || book == null) {
            setText(null);
            setGraphic(null);
        } else {
            // Run the update task in a separate thread
            executor.submit(() -> {
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

                    // Load book data
                    controller.setData(book);
                    Image image = resolveCoverImage(book);

                    // Update the UI on the JavaFX Application Thread
                    Platform.runLater(() -> {
                        if (getItem() == book) {
                            controller.cover.setImage(image);
                            setGraphic(bookCard);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private Image resolveCoverImage(Book book) {
        String coverCode = book.getCoverCode();
        if (coverCode == null || coverCode.isBlank()) {
            return MainController.DEFAULT_COVER;
        }
        try {
            return new Image(coverCode, true);
        } catch (RuntimeException ex) {
            System.err.println("Failed to load cover for book " + book.getBookID() + ": " + ex.getMessage());
            return MainController.DEFAULT_COVER;
        }
    }
}
