module libraryapp.libraryapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.json;
    requires mysql.connector.j;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires java.mail;
    requires jdk.compiler;


    opens Library to javafx.fxml;
    exports Library;
    exports Library.ui;
    opens Library.ui to javafx.fxml;
    exports Library.ui.BookCard;
    opens Library.ui.BookCard to javafx.fxml;
    exports Library.ui.UserTab;
    opens Library.ui.UserTab to javafx.fxml;
}