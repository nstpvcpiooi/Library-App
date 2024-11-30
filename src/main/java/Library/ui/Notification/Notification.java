package Library.ui.Notification;

import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class Notification {
//    protected Text Message;
//
//    protected Stage MessageStage;
//
//    protected StackPane root;

//    public Notification(String message) {
//        Message = new Text(message);
//
//        Message.setFont(Font.font("Barlow Medium", 14));
//        Message.setWrappingWidth(150);
//        Message.setTextAlignment(TextAlignment.LEFT);
//        Message.setFill(Color.rgb(25,25,25));
//
//        MessageStage = new Stage();
//        MessageStage.initStyle(StageStyle.TRANSPARENT);
//        MessageStage.setResizable(false);
//
//        root = new StackPane(Message);
//        root.setStyle("-fx-background-radius: 5px; "
//                + "-fx-border-radius: 5px; "
//                + "-fx-border-width: 2px; "
//                + "-fx-border-color: rgb(72,72,72);"
//                + "-fx-background-color: rgb(255,255,255);");
//        root.setPrefWidth(200);
//        root.setPrefHeight(80);
//
//        Scene scene = new Scene(root);
//        scene.setFill(Color.TRANSPARENT);
//
//        MessageStage.setScene(scene);
//
//        MessageStage.setX(155);
//        MessageStage.setY(650);
//
//    }

//    public void display() {
//        MessageStage.show();
//
//        // Close the notification after 4 seconds
//        PauseTransition delay = new PauseTransition(Duration.seconds(4));
//        delay.setOnFinished( event -> MessageStage.close() );
//        delay.play();
//    }

    private final String text;
    private final String title;

    public Notification(String title, String text) {
        this.text = text;
        this.title = title;
    }

    public void display() {
        Notifications.create()
                .title(title)
                .text(text)
                .styleClass("notification")
                .hideAfter(Duration.seconds(3))
                .show();
    }
}
