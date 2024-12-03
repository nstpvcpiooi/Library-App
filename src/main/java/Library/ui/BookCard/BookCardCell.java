package Library.ui.BookCard;

import Library.MainApplication;
import Library.backend.bookModel.Book;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

                    // Load book cover image
                    Image image = new Image(book.getCoverCode());

                    // Update the UI on the JavaFX Application Thread
                    Platform.runLater(() -> {
                        controller.cover.setImage(image);
                        setGraphic(bookCard);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}