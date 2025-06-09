package Library.ui.Admin;

import Library.backend.bookModel.Book;
import Library.ui.BookCard.BookCardCell;
import Library.ui.Utils.SearchUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import static Library.ui.BookCard.BookCardCell.BookCardType.LARGE;

/**
 * Controller cho giao diện quản lý thư viện của admin
 */
public class LibraryManageController extends AdminTabController implements Initializable, SearchUtils {

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

    public ListView<Book> getSearchResult() {
        return SearchResult;
    }

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

    /**
     * Hàm xử lý sự kiện khi nhấn vào nút thêm sách (AddButton)
     * @param event sự kiện chuột
     */
    @FXML
    void AddBook(MouseEvent event) {
        getMainController().getPopUpWindow().displayAdd(SearchResult);
    }

    /**
     * Lấy danh sách kết quả tìm kiếm từ query
     *
     * @param query từ khóa tìm kiếm
     * @return danh sách kết quả tìm kiếm
     */
    private List<Book> getSearchList(String query) {
        if(query.isEmpty()) {
            return Book.searchBooksValue("");
        }
        List<Book> ls = Book.searchBooks("title", query);
        if (ls != null) {
            return ls.subList(0, Math.min(ls.size(), 4));
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Sau khi xóa sách, xoá sách khỏi danh sách kết quả tìm kiếm
     */
    public void removeBook(Book book) {
        bookList.remove(book);
        SearchResult.getItems().remove(book);
    }

    /**
     * Hàm xử lý sự kiện khi chọn một sách trong danh sách kết quả tìm kiếm (SearchResult)
     * @param event sự kiện chuột
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
        // Khởi tạo book card cell
        SearchResult.setCellFactory(lv -> new BookCardCell(LARGE));

        // Đăng ký listener cho ô tìm kiếm
        SearchText.textProperty().addListener((obs, oldText, newText) -> {
            triggerSearch(newText, SearchResult);
        });

        // Tải danh sách ban đầu
        triggerSearch("", SearchResult);
    }

    public void updateBookInList(Book updatedBook) {
        for (int i = 0; i < bookList.size(); i++) {
            if (bookList.get(i).getIsbn().equals(updatedBook.getIsbn())) {
                bookList.set(i, updatedBook);
                break;
            }
        }
    }

    public void refreshData() {
        triggerSearch("", SearchResult);
    }
}
