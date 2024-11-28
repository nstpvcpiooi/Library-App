package Library.ui.PopUpWindow;

import Library.MainApplication;
import Library.backend.bookModel.Book;
import Library.ui.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.*;

import java.io.IOException;

public class PopUpWindow {
    protected Stage PopUpStage;

    private MainController mainController;

    protected Scene bookInfoScene;
    private BookInfoViewController bookInfoViewController;

    protected Scene bookAddScene;
    private BookAddViewController bookAddViewController;

    protected Scene bookEditScene;
    private BookEditViewController bookEditViewController;

    /**
     * Constructor
     */
    public PopUpWindow() {

        PopUpStage = new Stage();
        PopUpStage.initStyle(StageStyle.TRANSPARENT);
        PopUpStage.initModality(Modality.APPLICATION_MODAL);
        PopUpStage.setX(282);
        PopUpStage.setY(110);

        // BookInfoView
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("fxml/BookInfoView.fxml"));
            Parent root = loader.load();
            bookInfoViewController = loader.getController();
            bookInfoViewController.setPopUpWindow(this);
            bookInfoScene = new Scene(root);
            bookInfoScene.setFill(Color.TRANSPARENT);

        }
        catch (IOException e) {
            e.printStackTrace();
        }

        // BookAddView
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("fxml/BookAddView.fxml"));
            Parent root = loader.load();
            bookAddViewController = loader.getController();
            bookAddViewController.setPopUpWindow(this);
            bookAddScene = new Scene(root);
            bookAddScene.setFill(Color.TRANSPARENT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // BookEditView
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("fxml/BookEditView.fxml"));
            Parent root = loader.load();
            bookEditViewController = loader.getController();
            bookEditViewController.setPopUpWindow(this);
            bookEditScene = new Scene(root);
            bookEditScene.setFill(Color.TRANSPARENT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void displayInfo(Book selectedBook) {
        PopUpStage.setScene(bookInfoScene);
        mainController.setBackgroundEffect();
        bookInfoViewController.setData(selectedBook);
        PopUpStage.close();
        PopUpStage.show();
    }

    public void displayAdd() {
        PopUpStage.setScene(bookAddScene);
        mainController.setBackgroundEffect();
        PopUpStage.close();
        PopUpStage.show();
    }

    public void displayEdit(Book selectedBook) {
        PopUpStage.setScene(bookEditScene);
        mainController.setBackgroundEffect();
//        bookEditViewController.setData(selectedBook);
//        PopUpStage.close();
        PopUpStage.show();
    }

    public void backtoInfo() {
        PopUpStage.setScene(bookInfoScene);
        mainController.setBackgroundEffect();
        PopUpStage.show();
    }

    public void close() {
        mainController.removeBackgroundEffect();
        PopUpStage.close();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public MainController getMainController() {
        return mainController;
    }
}
