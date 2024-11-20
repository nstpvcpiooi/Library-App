package Library.ui;

import Library.MainApplication;
import Library.backend.bookModel.Book;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class BookCardSmallController {

    @FXML
    private HBox container;

    @FXML
    private ImageView cover;

    @FXML
    private Label title;

    @FXML
    private Label author;

    public void setData(Book book) {
        try {
            Image image = new Image(book.getCoverCode());
            cover.setImage(image);
            /**????????*/
        } catch (Exception e) {
            System.out.println("Error loading image from " + book.getCoverCode());
            cover.setImage (new Image("D:/My Code/lib2024-1117/src/main/resources/Library/image/default-cover.png"));
//            cover.setImage (new Image("https://marketplace.canva.com/EAFaQMYuZbo/1/0/1003w/canva-brown-rusty-mystery-novel-book-cover-hG1QhA7BiBU.jpg"));
        }

        title.setText(book.getTitle());
        author.setText(book.getAuthor());
    }

//    @Override
//    public void initialize(URL location, ResourceBundle resources) {
//        Book book = new Book("001", "Lập trình Java", "Nguyễn Văn A", 2022,
//                "CNTT", "978-3-16-148410-0", "link-to-cover-image", 1);
//
//        setData(book);
//    }
}

