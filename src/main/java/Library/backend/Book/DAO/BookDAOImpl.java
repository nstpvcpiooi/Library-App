package Library.backend.Book.DAO;

import Library.backend.Book.Model.Book;
import Library.backend.database.JDBCUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Concrete DAO implementation that encapsulates both local MySQL persistence and Google Books API access.
 */
public class BookDAOImpl implements BookDAO {

    private static volatile BookDAOImpl instance;

    private static final String DEFAULT_GOOGLE_API_URL = "https://www.googleapis.com/books/v1/volumes?q=isbn:";
    private static final String DEFAULT_API_KEY = "AIzaSyAmWmP0OfkanSPfW72I5X0X4CwGn_84yTU";
    private static volatile String googleApiUrl = DEFAULT_GOOGLE_API_URL;
    private static volatile String apiKey = DEFAULT_API_KEY;
    private static volatile String qrOutputDir = "src/main/resources/Library";
    private static volatile HttpConnectionFactory httpConnectionFactory = BookDAOImpl::openUrlConnection;

    private BookDAOImpl() {
    }

    public static BookDAOImpl getInstance() {
        if (instance == null) {
            synchronized (BookDAOImpl.class) {
                if (instance == null) {
                    instance = new BookDAOImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public void addBook(Book book) {
        String sql = "INSERT INTO Books (bookID, title, author, publishYear, category, isbn, coverCode, quantity) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, book.getBookID());
            pst.setString(2, book.getTitle());
            pst.setString(3, book.getAuthor());
            pst.setInt(4, book.getPublishYear());
            pst.setString(5, book.getCategory());
            pst.setString(6, book.getIsbn());
            pst.setString(7, book.getCoverCode());
            pst.setInt(8, book.getQuantity());
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteBook(String bookID) {
        String sql = "DELETE FROM Books WHERE bookID = ?";
        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, bookID);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateBook(Book book) {
        String sql = "UPDATE Books SET title=?, author=?, publishYear=?, category=?, isbn=?, coverCode=?, quantity=? WHERE bookID = ?";
        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, book.getTitle());
            pst.setString(2, book.getAuthor());
            pst.setInt(3, book.getPublishYear());
            pst.setString(4, book.getCategory());
            pst.setString(5, book.getIsbn());
            pst.setString(6, book.getCoverCode());
            pst.setInt(7, book.getQuantity());
            pst.setString(8, book.getBookID());
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public List<Book> searchBooksValue(String value) {
        List<Book> books = new ArrayList<>();
        String searchValue = "%" + value + "%";
        String sql = "SELECT * FROM Books WHERE title LIKE ? OR author LIKE ? OR category LIKE ? OR isbn LIKE ?";
        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, searchValue);
            pst.setString(2, searchValue);
            pst.setString(3, searchValue);
            pst.setString(4, searchValue);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    books.add(mapRowToBook(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    @Override
    public void updateQuantity(String bookID, int delta) {
        String sql = "UPDATE Books SET quantity = quantity + ? WHERE bookID = ?";
        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, delta);
            pst.setString(2, bookID);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Book findBookById(String bookID) {
        String sql = "SELECT * FROM Books WHERE bookID = ?";
        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, bookID);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapRowToBook(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Book findBookByIsbn(String isbn) {
        String sql = "SELECT * FROM Books WHERE isbn = ?";
        try (Connection con = JDBCUtil.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, isbn);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapRowToBook(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Book fetchBookInfoFromAPI(String isbn) {
        if (isbn == null || (isbn.length() != 10 && isbn.length() != 13)) {
            return null;
        }

        try {
            URL url = new URL(googleApiUrl + isbn + "&key=" + apiKey);
            HttpURLConnection connection = httpConnectionFactory.open(url);
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            String response = readResponse(connection);
            JSONObject jsonResponse = new JSONObject(response);
            if (jsonResponse.optInt("totalItems", 0) <= 0 || !jsonResponse.has("items")) {
                return null;
            }

            JSONObject bookItem = jsonResponse.getJSONArray("items").getJSONObject(0);
            JSONObject bookInfo = bookItem.getJSONObject("volumeInfo");

            String bookID = bookItem.optString("id", "Unknown Book ID");
            String title = bookInfo.optString("title", "Unknown Title");
            String author = bookInfo.has("authors") ? bookInfo.getJSONArray("authors").optString(0, "Unknown Author") : "Unknown Author";
            int publishYear = bookInfo.optInt("publishedDate", 0);
            String category = bookInfo.has("categories") ? bookInfo.optJSONArray("categories").optString(0, "Unknown Category") : "Unknown Category";
            String isbn13 = bookInfo.has("industryIdentifiers")
                    ? bookInfo.getJSONArray("industryIdentifiers").getJSONObject(0).optString("identifier", isbn)
                    : isbn;
            String coverCode = bookInfo.has("imageLinks") ? bookInfo.getJSONObject("imageLinks").optString("thumbnail", "") : "";

            return new Book(bookID, title, author, publishYear, category, isbn13, coverCode, 1);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String fetchBookDescriptionFromAPI(Book book) {
        if (book == null) {
            return "No book provided.";
        }
        try {
            URL url = new URL(googleApiUrl + book.getIsbn() + "&key=" + apiKey);
            HttpURLConnection connection = httpConnectionFactory.open(url);
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "API connection error.";
            }

            String response = readResponse(connection);
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray items = jsonResponse.optJSONArray("items");
            if (items != null && items.length() > 0) {
                JSONObject bookInfo = items.getJSONObject(0).getJSONObject("volumeInfo");
                return bookInfo.optString("description", "No description available.");
            }
            return "No book found.";
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return "Error occurred.";
        }
    }


    public String generateQrCodeForBook(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return null;
        }
        String previewLink = "https://books.google.com/books?vid=ISBN" + isbn;
        Path qrCodeFilePath = Paths.get(qrOutputDir, isbn + "_qr.png");
        try {
            if (Files.exists(qrCodeFilePath)) {
                return qrCodeFilePath.toString();
            }
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(previewLink, BarcodeFormat.QR_CODE, 300, 300);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", qrCodeFilePath);
            return qrCodeFilePath.toString();
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private Book mapRowToBook(ResultSet rs) throws SQLException {
        return new Book(
                rs.getString("bookID"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getInt("publishYear"),
                rs.getString("category"),
                rs.getString("isbn"),
                rs.getString("coverCode"),
                rs.getInt("quantity")
        );
    }


    private String readResponse(HttpURLConnection connection) throws IOException {
        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }
        return response.toString();
    }

    /**
     * Allow tests to adjust API key/base URL/HTTP connection/QR output path without touching production defaults.
     */
    public static void setHttpConnectionFactory(HttpConnectionFactory factory) {
        httpConnectionFactory = Objects.requireNonNull(factory);
    }

    public static void setGoogleApiUrl(String url) {
        googleApiUrl = Objects.requireNonNull(url);
    }

    public static void setApiKey(String key) {
        apiKey = Objects.requireNonNull(key);
    }

    public static void setQrOutputDir(String outputDir) {
        qrOutputDir = Objects.requireNonNull(outputDir);
    }

    public static void resetTestHooks() {
        googleApiUrl = DEFAULT_GOOGLE_API_URL;
        apiKey = DEFAULT_API_KEY;
        qrOutputDir = "src/main/resources/Library";
        httpConnectionFactory = BookDAOImpl::openUrlConnection;
    }

    private static HttpURLConnection openUrlConnection(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }

    @FunctionalInterface
    public interface HttpConnectionFactory {
        HttpURLConnection open(URL url) throws IOException;
    }
}
