package Library;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class MainApplication extends Application {

    // GỌI BACKEND THƯ VIỆN ????

    @Override
    public void start(Stage stage) throws Exception {

        // CỬA SỔ ĐĂNG NHẬP
//        selectDictionary(stage);

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("fxml/MainView.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);

        stage.getIcons().add(new Image(MainApplication.class.getResourceAsStream("icon/icon-512.png")));
        stage.setTitle("Library App");

        stage.setResizable(false); // không cho phóng to, thu nhỏ cửa sổ
        stage.setScene(scene);

        // KHI TẮT APP

//        stage.setOnCloseRequest(windowEvent -> {
//            dict.getHistory().export();
//            dict.getFavorites().export();
//            if (dict instanceof TxtDictionary) {
//                ((TxtDictionary) dict).exportToFiles("src/main/resources/data/demo.txt");
//            }
//            dict.close();
//            Platform.exit();
//            System.exit(0);
//        });

        stage.show();

        // THÔNG BÁO?
//        if (dict instanceof DtbDictionary) {
//            WordOfTheDayWindow wordOfTheDayWindow = new WordOfTheDayWindow();
//            wordOfTheDayWindow.display();
//        }
    }

//    public void selectDictionary(Stage stage) throws IOException {
//    }

    public static void main(String[] args) {
        launch();
    }


}