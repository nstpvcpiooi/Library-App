package Library.ui.Admin;

import Library.backend.Book.Model.Book;
import Library.backend.Book.Service.BookService;
import Library.backend.Review.service.ReviewService;
import Library.ui.BookCard.BookCardCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ResourceBundle;

import static Library.ui.BookCard.BookCardCell.BookCardType.LARGE;

/**
 * Controller cho giao diện quản lý thư viện của admin
 */
public class LibraryManageController implements Initializable {

    private final BookService bookService = BookService.getInstance();
    private final ReviewService reviewService = ReviewService.getInstance();

    private ObservableList<Book> bookList = FXCollections.observableArrayList();

    /**
     * Nút thêm sách
     */
    @FXML
    private HBox AddButton;

    /**
     * Ô tìm kiếm
     */
    @FXML
    private HBox SearchBar;

    /**
     * Danh sách kết quả tìm kiếm
     */
    @FXML
    private ListView<Book> SearchResult;


    /**
     * Ô nhập từ khóa tìm kiếm (chứa từ khóa tìm kiếm)
     */
    @FXML
    private TextField SearchText;

    @FXML
    private ComboBox<String> categoryField;

    @FXML
    private TextField authorField;

    @FXML
    private ComboBox<Double> minRatingField;

    /**
     * Controller chính của admin (đã được khởi tạo trong AdminMainController)
     */
    private AdminMainController MainController;

    /**
     * Hàm xử lý sự kiện khi nhấn vào nút thêm sách (AddButton)
     * @param event sự kiện chuột
     *
     * Khi nhấn vào nút thêm sách, hiển thị cửa sổ thêm sách (displayAdd)
     *
     * Có hai cách thêm sách: thêm sách tùy chỉnh và thêm sách bằng mã ISBN
     *
     * TODO: Chức năng thêm sách trong CustomAddController
     */
    @FXML
    void AddBook(MouseEvent event) {
        getMainController().getPopUpWindow().displayAdd();
    }

    /**
     * Hàm xử lý sự kiện khi nhập từ khóa tìm kiếm (SearchText)
     * @param event sự kiện phím
     *
     * Khi nhập từ khóa tìm kiếm, hiển thị danh sách kết quả tìm kiếm (getSearchList)
     */
    @FXML
    void search(KeyEvent event) {
        // kiem tra neu la phim nhap ky tu
        if (event.getCode().isLetterKey() || event.getCode().isDigitKey() ||
                event.getCode().isWhitespaceKey() || event.getCode().equals(KeyCode.ENTER)
                || event.getCode().equals(KeyCode.BACK_SPACE) || event.getCode().equals(KeyCode.DELETE)) {
            updateSearchResults();
        }
    }

    /**
     * Lấy danh sách kết quả tìm kiếm từ query
     *
     * @param query từ khóa tìm kiếm
     * @return danh sách kết quả tìm kiếm
     */
    private void updateSearchResults() {
        String query = SearchText.getText();
        List<Book> base = (query == null || query.trim().isEmpty())
                ? bookService.searchBooksValue("")
                : bookService.searchBooksValue(query.trim());
        List<Book> filtered = applyAdvancedFilters(base);
        bookList.setAll(filtered);
    }

    /**
     * Sau khi xóa sách, xoá sách khỏi danh sách kết quả tìm kiếm
     */
    public void removeBook(Book book) {
        bookList.remove(book);
    }

    /**
     * Hàm xử lý sự kiện khi chọn một sách trong danh sách kết quả tìm kiếm (SearchResult)
     * @param event sự kiện chuột
     *
     * Khi chọn một sách, hiển thị thông tin chi tiết của sách (displayInfo)
     */
    @FXML
    void SelectBook(MouseEvent event) {
        Book selectedBook = SearchResult.getSelectionModel().getSelectedItem();
        getMainController().getPopUpWindow().displayInfo(selectedBook);
    }

    /**
     * Khởi tạo giao diện quản lý thư viện
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Gắn ObservableList vào ListView
        SearchResult.setItems(bookList);

        // Khởi tạo book card cell
        SearchResult.setCellFactory(lv -> new BookCardCell(LARGE));

        // Hiển thị sách trong tab Library Manage khi mới mở ứng dụng
        updateSearchResults();
        loadCategories();
        loadRatingOptions();
        registerAdvancedSearchListeners();
    }

    public void setMainController(AdminMainController adminMainController) {
        this.MainController = adminMainController;
    }

    public AdminMainController getMainController() {
        return MainController;
    }

    public void updateBookInList(Book updatedBook) {
        for (int i = 0; i < bookList.size(); i++) {
            if (bookList.get(i).getIsbn().equals(updatedBook.getIsbn())) {
                bookList.set(i, updatedBook); // Cập nhật sách
                break;
            }
        }
    }
    public void refreshData() {
        updateSearchResults();
        loadCategories();
        loadRatingOptions();
    }

    private List<Book> applyAdvancedFilters(List<Book> books) {
        String category = safeLower(categoryField.getValue());
        String author = safeLower(authorField.getText());
        Double minRating = minRatingField.getValue();

        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (!category.isEmpty() && (book.getCategory() == null || !safeLower(book.getCategory()).contains(category))) {
                continue;
            }
            if (!author.isEmpty() && (book.getAuthor() == null || !safeLower(book.getAuthor()).contains(author))) {
                continue;
            }
            if (minRating != null) {
                double avg = reviewService.getAverageRating(book.getBookID());
                if (avg < minRating) {
                    continue;
                }
            }
            result.add(book);
        }
        return result;
    }

    private void loadCategories() {
        List<Book> allBooks = bookService.searchBooksValue("");
        LinkedHashSet<String> categories = new LinkedHashSet<>();
        for (Book b : allBooks) {
            if (b.getCategory() != null && !b.getCategory().isBlank()) {
                categories.add(b.getCategory());
            }
        }
        categoryField.getItems().setAll(categories);
        categoryField.getItems().add(0, "");
        categoryField.getSelectionModel().selectFirst();
    }

    private String safeLower(String value) {
        return value == null ? "" : value.toLowerCase().trim();
    }

    private void loadRatingOptions() {
        if (minRatingField == null) {
            return;
        }
        ObservableList<Double> ratings = FXCollections.observableArrayList();
        for (int i = 0; i <= 5; i++) {
            ratings.add((double) i);
        }
        minRatingField.setItems(ratings);
        minRatingField.getSelectionModel().clearSelection();
    }

    private void registerAdvancedSearchListeners() {
        if (categoryField != null) {
            categoryField.valueProperty().addListener((obs, oldV, newV) -> updateSearchResults());
        }
        if (authorField != null) {
            authorField.textProperty().addListener((obs, oldV, newV) -> updateSearchResults());
        }
        if (minRatingField != null) {
            minRatingField.valueProperty().addListener((obs, oldV, newV) -> updateSearchResults());
        }
    }
}
