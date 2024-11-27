package Library.ui.BookInfoView;

import Library.MainApplication;
import Library.backend.bookModel.Book;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class BookInfoView {
    protected Stage bookInfostage;

    /**
     * Constructor
     */
    public BookInfoView() {
        Parent root;
        bookInfostage = new Stage();
        try {
            root = FXMLLoader.load(MainApplication.class.getResource("fxml/BookInfoView.fxml"));
            bookInfostage.setScene(new Scene(root));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void display(Book selectedBook) {
        bookInfostage.setTitle(selectedBook.getTitle());
        bookInfostage.close();
        bookInfostage.show();
    }


    //    protected Stage bookInfoStage;
//
//    @Override
//    public void initialize(URL location, ResourceBundle resources) {
//        // Khởi tạo các thành phần của cửa sổ
//        bookInfoStage = new Stage();
//        bookInfoStage.initStyle(StageStyle.TRANSPARENT);
//        bookInfoStage.initModality(Modality.APPLICATION_MODAL);
//
//        // Load giao diện từ file fxml
//        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("fxml/BookInfoView.fxml"));
//        try {
//            bookInfoStage.setScene(new javafx.scene.Scene(fxmlLoader.load()));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void display() {
//        bookInfoStage.showAndWait();
//    }
}
