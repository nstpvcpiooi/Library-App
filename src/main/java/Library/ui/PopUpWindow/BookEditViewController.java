package Library.ui.PopUpWindow;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class BookEditViewController extends PopUpController {
    @FXML
    private Button CancelButton;

    @FXML
    void goBack(ActionEvent event) {
        getPopUpWindow().backtoInfo();
    }

}
