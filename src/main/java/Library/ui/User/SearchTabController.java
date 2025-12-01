package Library.ui.User;

import Library.backend.Book.Model.Book;
import Library.backend.Book.Service.BookService;
import Library.backend.Review.service.ReviewService;
import Library.ui.BookCard.BookCardCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static Library.ui.BookCard.BookCardCell.BookCardType.LARGE;

public class SearchTabController implements Initializable {

    private final BookService bookService = BookService.getInstance();
    private final ReviewService reviewService = ReviewService.getInstance();

    @FXML
    private Button backButton;

    @FXML
    private HBox SearchBar;

    @FXML
    private TextField SearchText;

    @FXML
    private ComboBox<String> categoryField;

    @FXML
    private TextField authorField;

    @FXML
    private ComboBox<Double> minRatingField;

    @FXML
    private ListView<Book> SearchResult;

    private UserMainController userMainController;

    @FXML
    void BackToHome(ActionEvent event) {
        if (userMainController != null) {
            userMainController.setContentPane(userMainController.homeTab);
            userMainController.setCurrentTab(userMainController.getHomeButton());
        }
    }

    @FXML
    void search(KeyEvent event) {
        updateSearchResults();
    }

    @FXML
    void SelectBook(MouseEvent event) {
        Book selectedBook = SearchResult.getSelectionModel().getSelectedItem();
        if (selectedBook != null && userMainController != null) {
            userMainController.getPopUpWindow().displayInfo(selectedBook);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SearchResult.setCellFactory(lv -> new BookCardCell(LARGE));
        loadCategories();
        loadRatingOptions();
        registerAdvancedListeners();
        updateSearchResults();
    }

    private List<Book> getSearchList(String query) {
        if (query == null || query.trim().isEmpty()) {
            return bookService.searchBooksValue("");
        }
        return bookService.searchBooksValue(query.trim());
    }

    private void updateSearchResults() {
        List<Book> base = getSearchList(SearchText.getText());
        List<Book> filtered = applyAdvancedFilters(base);
        SearchResult.getItems().setAll(filtered);
    }

    private List<Book> applyAdvancedFilters(List<Book> books) {
        String category = safeLower(categoryField.getValue());
        String author = safeLower(authorField.getText());
        Double minRating = minRatingField.getValue();

        return books.stream().filter(book -> {
            if (!category.isEmpty()) {
                String bookCategory = safeLower(book.getCategory());
                if (bookCategory.isEmpty() || !bookCategory.contains(category)) {
                    return false;
                }
            }
            if (!author.isEmpty()) {
                String bookAuthor = safeLower(book.getAuthor());
                if (bookAuthor.isEmpty() || !bookAuthor.contains(author)) {
                    return false;
                }
            }
            if (minRating != null) {
                double avg = reviewService.getAverageRating(book.getBookID());
                if (avg < minRating) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
    }

    private void loadCategories() {
        List<Book> allBooks = bookService.searchBooksValue("");
        LinkedHashSet<String> categories = new LinkedHashSet<>();
        for (Book b : allBooks) {
            if (b.getCategory() != null && !b.getCategory().isBlank()) {
                categories.add(b.getCategory());
            }
        }
        categoryField.getItems().clear();
        categoryField.getItems().add("");
        categoryField.getItems().addAll(categories);
        categoryField.setValue("");
    }

    private void loadRatingOptions() {
        ObservableList<Double> ratings = FXCollections.observableArrayList();
        for (int i = 0; i <= 5; i++) {
            ratings.add((double) i);
        }
        minRatingField.setItems(ratings);
        minRatingField.getSelectionModel().clearSelection();
    }

    private void registerAdvancedListeners() {
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

    private String safeLower(String value) {
        return value == null ? "" : value.toLowerCase().trim();
    }

    public UserMainController getMainController() {
        return userMainController;
    }

    public void setMainController(UserMainController userMainController) {
        this.userMainController = userMainController;
    }
}
