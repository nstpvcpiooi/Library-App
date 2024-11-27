package Library.ui.Admin;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class LibraryManageController {

    @FXML
    private HBox AddButton;

    @FXML
    private HBox SearchBar;

    @FXML
    private ListView<?> SearchResult;

    @FXML
    private TextField SearchText;

    private AdminMainController MainController;


    @FXML
    void AddBook(MouseEvent event) {

    }

    @FXML
    void search(KeyEvent event) {

    }

    public void setMainController(AdminMainController adminMainController) {
        this.MainController = adminMainController;
    }

    public AdminMainController getMainController() {
        return MainController;
    }
}
