package Library.ui.PopUpWindow;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public abstract class PopUpController {
    @FXML
    private Button closeButton;

    @FXML
    private AnchorPane container;

    @FXML
    void close(ActionEvent event) {
        popUpWindow.close();
    }

    private PopUpWindow popUpWindow;

    public PopUpWindow getPopUpWindow() {
        return popUpWindow;
    }

    public void setPopUpWindow(PopUpWindow popUpWindow) {
        this.popUpWindow = popUpWindow;
    }
}
