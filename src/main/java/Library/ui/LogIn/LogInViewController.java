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

    LogInType returnType;

    @FXML
    private AnchorPane Container;

    /** ADMIN LOG IN*/
    public AdminLogInController adminLogInController;
    public AnchorPane adminLogInView;

    /** USER LOG IN*/
    public UserLogInController userLogInController;
    public AnchorPane userLogInView;

    /** SELECT ROLES*/
    public SelectRolesController selectRolesController;
    public AnchorPane selectRolesView;


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

        // KHỞI TẠO USER LOG IN VIEW
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("fxml/LogInTab/UserLogIn.fxml"));
            userLogInView = fxmlLoader.load();
            userLogInController = fxmlLoader.getController();
            userLogInController.setLogInViewController(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // KHỞI TẠO ADMIN LOG IN VIEW
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("fxml/LogInTab/AdminLogIn.fxml"));
            adminLogInView = fxmlLoader.load();
            adminLogInController = fxmlLoader.getController();
            adminLogInController.setLogInViewController(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // KHỞI TẠO SELECT ROLES VIEW
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("fxml/LogInTab/SelectRoles.fxml"));
            selectRolesView = fxmlLoader.load();
            selectRolesController = fxmlLoader.getController();
            selectRolesController.setLogInViewController(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setContainer(selectRolesView);

    }

}
