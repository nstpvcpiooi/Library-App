package Library.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

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
