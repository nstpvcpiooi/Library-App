package Library.ui;

import Library.MainApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private Pane homeButton;

    @FXML
    private Pane historyButton;

    @FXML
    private Pane searchButton;

    @FXML
    private Pane profileButton;


    private Pane currentTab;

    @FXML
    private AnchorPane ContentPane;

    /** Home Tab */
    public HomeTabController homeTabController;
    public AnchorPane homeTab;

    /** Search Tab */
    public SearchTabController searchTabController;
    public AnchorPane searchTab;

    /** History Tab */
    public HistoryTabController historyTabController;
    public AnchorPane historyTab;

    /** Profile Tab */
    public ProfileTabController profileTabController;
    public AnchorPane profileTab;

    @FXML
    void ButtonClick(MouseEvent event) {
        setCurrentTab((Pane) event.getSource());

        // NOTE: CHƯA CÓ HIỂN THỊ NỘI DUNG TAB?
        if (currentTab.equals(homeButton)) {
            System.out.println("Home Button Clicked");
            setContentPane(homeTab);
        } else if (currentTab.equals(historyButton)) {
            System.out.println("History Button Clicked");
            setContentPane(historyTab);
        } else if (currentTab.equals(searchButton)) {
            System.out.println("Search Button Clicked");
            setContentPane(searchTab);
        } else if (currentTab.equals(profileButton)) {
            System.out.println("Profile Button Clicked");
            setContentPane(profileTab);
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

        // KHỞI TẠO HISTORY TAB
        System.out.println("History Tab Initialized");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("fxml/HistoryTabView.fxml"));
            historyTab = fxmlLoader.load();
            historyTabController = fxmlLoader.getController();
            historyTabController.setMainController(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // KHỞI TẠO PROFILE TAB
        System.out.println("Profile Tab Initialized");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("fxml/ProfileTabView.fxml"));
            profileTab = fxmlLoader.load();
            profileTabController = fxmlLoader.getController();
            profileTabController.setMainController(this);
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

    // Set currentTab
    public void setCurrentTab(Pane b) {
        if (!b.equals(currentTab)) {
            b.getStyleClass().clear();
            b.getStyleClass().add("MenuButtonPressed");

            if (currentTab != null) {
                currentTab.getStyleClass().clear();
                currentTab.getStyleClass().add("MenuButton");
            }
            currentTab = b;
        }
    }


    public Pane getHomeButton() {
        return homeButton;
    }

    public Pane getHistoryButton() {
        return historyButton;
    }

    public Pane getSearchButton() {
        return searchButton;
    }

    public Pane getProfileButton() {
        return profileButton;
    }
}