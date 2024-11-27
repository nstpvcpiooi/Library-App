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
    exports Library.ui.User;
    opens Library.ui.User to javafx.fxml;
    exports Library.ui.LogIn;
    opens Library.ui.LogIn to javafx.fxml;
    exports Library.ui.Admin;
    opens Library.ui.Admin to javafx.fxml;
    exports Library.ui.BookInfoView;
    opens Library.ui.BookInfoView to javafx.fxml;
}