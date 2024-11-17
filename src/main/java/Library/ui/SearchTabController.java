package Library.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class SearchTabController {

    @FXML
    private Button backButton;

    private MainController mainController;

    @FXML
    void BackToHome(ActionEvent event) {
        System.out.println("Back to Home Button Clicked");
        mainController.setContentPane(mainController.homeTab);
    }

    public MainController getMainController() {
        return mainController;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
