package Library.ui.Utils;

import javafx.util.Duration;
import org.controlsfx.control.Notifications;

/**
 * Class hiển thị thông báo cho người dùng.
 */
public class Notification {

    /**
     * Nội dung thông báo.
     */
    private final String text;

    /**
     * Tiêu đề thông báo.
     */
    private final String title;

    /**
     * Constructor.
     * @param title Tiêu đề thông báo.
     * @param text Nội dung thông báo.
     */
    public Notification(String title, String text) {
        this.text = text;
        this.title = title;
    }

    /**
     * Hiển thị thông báo.
     */
    public void display() {
        Notifications.create()
                .title(title)
                .text(text)
                .styleClass("notification")
                .hideAfter(Duration.seconds(3)) // Chỉnh thời gian hiển thị thông báo
                .show();
    }
}
