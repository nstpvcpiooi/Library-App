package Library.ui.BookCard;

import Library.MainApplication;
import Library.backend.bookModel.Book;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

/**
 * Cell cho ListView kết quả tìm kiếm (BookCardLarge)
 */
public class BookCardCell extends ListCell<Book> {
    public enum BookCardType {SMALL, LARGE}

    protected BookCardType type;

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
                controller.setData(book);
                setGraphic(bookCard);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
