package Library.ui;

import Library.MainApplication;
import com.sun.tools.javac.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private Pane homeButton;

    @FXML
    private Pane button2;

    @FXML
    private Pane button3;

    @FXML
    private Pane button4;

    private Pane currentTab;

    @FXML
    private AnchorPane ContentPane;

    /** Home Tab */
    public HomeTabController homeTabController;
    public AnchorPane homeTab;

    /** Search Tab */
    public SearchTabController searchTabController;
    public AnchorPane searchTab;

    @FXML
    void ButtonClick(MouseEvent event) throws Exception {

        Pane b = (Pane) event.getSource();

        if (!b.equals(currentTab)) {
            b.getStyleClass().clear();
            b.getStyleClass().add("MenuButtonPressed");

            if (currentTab != null) {
                currentTab.getStyleClass().clear();
                currentTab.getStyleClass().add("MenuButton");
            }
            currentTab = b;
        }

        // NOTE: CHƯA CÓ HIỂN THỊ NỘI DUNG TAB?
        if (currentTab.equals(homeButton)) {
            System.out.println("Home Button Clicked");
            setContentPane(homeTab);
        } else if (currentTab.equals(button2)) {
            System.out.println("Button 2 Clicked");
        } else if (currentTab.equals(button3)) {
            System.out.println("Button 3 Clicked");
        } else if (currentTab.equals(button4)) {
            System.out.println("Button 4 Clicked");
        }
    }

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentTab = homeButton;
        currentTab.getStyleClass().clear();
        currentTab.getStyleClass().add("MenuButtonPressed");

        // KHỞI TẠO HOME TAB
        System.out.println("Home Tab Initialized");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("fxml/HomeTabView.fxml"));
            homeTab = fxmlLoader.load();
            homeTabController = fxmlLoader.getController();
            homeTabController.setMainController(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // KHỞI TẠO SEARCH TAB
        System.out.println("Search Tab Initialized");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("fxml/SearchTabView.fxml"));
            searchTab = fxmlLoader.load();
            searchTabController = fxmlLoader.getController();
            searchTabController.setMainController(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ContentPane.getChildren().add(homeTab);
    }

    // Set ContentPane
    public void setContentPane(AnchorPane contentPane) {
        ContentPane.getChildren().clear();
        ContentPane.getChildren().add(contentPane);
    }
}