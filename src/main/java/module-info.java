module libraryapp.libraryapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.json;
    requires mysql.connector.j;
    requires com.google.zxing;
    requires com.google.zxing.javase;


    opens Library to javafx.fxml;
    exports Library;
}