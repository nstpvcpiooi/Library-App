package Library.ui.Utils;

import Library.backend.bookModel.Book;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.util.Duration;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public interface SearchUtils {

    /* ---------- TÀI NGUYÊN DÙNG CHUNG ---------- */
    ExecutorService EXEC = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "search-worker");
        t.setDaemon(true);
        return t;
    });

    Map<ListView<?>, Future<?>> JOBS = new ConcurrentHashMap<>();

    default PauseTransition makeDebounce() {
        return new PauseTransition(Duration.millis(400));
    }

    static void shutdownSearch() { EXEC.shutdownNow(); }


    /* ---------- TRIGGER CHÍNH ---------- */
    default void triggerSearch(String q, ListView<Book> lv) {

        /* 1. Hủy job cũ nếu đang chạy */
        Future<?> prev = JOBS.remove(lv);
        if (prev != null) prev.cancel(true);

        /* 2. Hiển thị spinner ở placeholder */
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setMaxSize(40, 40);
        lv.getItems().clear();            // danh sách rỗng ➜ placeholder hiện
        lv.setPlaceholder(spinner);

        /* 3. Background task */
        Task<ObservableList<Book>> task = new Task<>() {
            @Override protected ObservableList<Book> call() {
                return FXCollections.observableArrayList(getSearchList(q));
            }
        };

        task.setOnSucceeded(e -> {
            ObservableList<Book> books = task.getValue();
            
            // Tạo một task mới để load từng cell
            Task<Void> loadCellsTask = new Task<>() {
                @Override
                protected Void call() {
                    // Đợi một chút để đảm bảo ListView đã sẵn sàng
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                    return null;
                }
            };

            loadCellsTask.setOnSucceeded(event -> {
                // Sau khi load xong, cập nhật UI trên JavaFX Application Thread
                javafx.application.Platform.runLater(() -> {
                    lv.setItems(books);
                    lv.setPlaceholder(new Label(
                            books.isEmpty() ? "Không có kết quả" : ""));
                });
            });

            // Chạy task load cells
            EXEC.submit(loadCellsTask);
            JOBS.remove(lv);
        });

        task.setOnFailed(e -> {
            lv.setPlaceholder(new Label("Đã xảy ra lỗi!"));
            task.getException().printStackTrace();
            JOBS.remove(lv);
        });

        JOBS.put(lv, EXEC.submit(task));
    }


    /* ---------- HÀM TÌM KIẾM ---------- */
    private List<Book> getSearchList(String q) {
//        if (q == null || q.trim().isEmpty()) return Collections.emptyList();
        return Book.searchBooksValue(q);
    }
}


