package Library.ui.LogIn;

import Library.MainApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class LogInViewController implements Initializable {

    public enum LogInType {GUEST, USER, ADMIN}

    private LogInType returnType = LogInType.GUEST;

    @FXML
    private AnchorPane Container;

    private UserLogInController userLogInController;
    private AnchorPane userLogInView;

    public void setContainer(AnchorPane logInView) {
        Container.getChildren().clear();
        Container.getChildren().add(logInView);
    }

    public LogInType getReturnType() {
        return returnType;
    }

    public void setReturnType(LogInType returnType) {
        this.returnType = returnType;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("fxml/LogInTab/UserLogIn.fxml"));
            userLogInView = fxmlLoader.load();
            userLogInController = fxmlLoader.getController();
            userLogInController.setLogInViewController(this);
            setContainer(userLogInView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
