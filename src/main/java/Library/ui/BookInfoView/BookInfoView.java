package Library.ui.BookInfoView;

import Library.MainApplication;
import Library.backend.bookModel.Book;
import Library.ui.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.*;

import java.io.IOException;

public class BookInfoView {
    protected Stage bookInfostage;
    protected Scene bookInfoScene;

    private MainController mainController;

    private BookInfoViewController bookInfoViewController;

    /**
     * Constructor
     */
    public BookInfoView() {

        Parent root;
        bookInfostage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("fxml/BookInfoView.fxml"));
            root = loader.load();
            bookInfoViewController = loader.getController();
            bookInfoViewController.setBookInfoView(this);
            bookInfoScene = new Scene(root);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        bookInfoScene.setFill(Color.TRANSPARENT);

        bookInfostage.initStyle(StageStyle.TRANSPARENT);
        bookInfostage.initModality(Modality.APPLICATION_MODAL);

        bookInfostage.setScene(bookInfoScene);

        bookInfostage.setX(282);
        bookInfostage.setY(110);
    }

    public void display(Book selectedBook) {
        bookInfostage.setTitle(selectedBook.getTitle());
        bookInfostage.close();
        mainController.setBackgroundEffect();
        bookInfostage.show();
    }

    public void close() {
        mainController.removeBackgroundEffect();
        bookInfostage.close();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
