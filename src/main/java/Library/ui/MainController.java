package Library.ui;

import Library.ui.BookInfoView.BookInfoView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class MainController implements Initializable {

    protected BookInfoView bookInfoView;

    /**
     * Nút hiện tại đang được chọn.
     */
    protected Pane currentTab;

    /**
     * Phần chứa nội dung của các tab. (Home, Search, History, Profile...)
     */
    @FXML
    protected AnchorPane ContentPane;


    public void initialize(URL location, ResourceBundle resources) {
        // KHỞI TẠO BOOK INFO VIEW
        bookInfoView = new BookInfoView();
    }

    /**
     * Thay đổi nội dung của ContentPane.
     */
    public void setContentPane(AnchorPane contentPane) {
        ContentPane.getChildren().clear();
        ContentPane.getChildren().add(contentPane);
    }

    /**
     * Đặt nút hiện tại đang được chọn.
     */
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

    public BookInfoView getBookInfoView() {
        return bookInfoView;
    }
}
