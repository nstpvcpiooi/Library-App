package Library.backend.bookDao;

import Library.backend.bookModel.Book;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GoogleBookDao implements BookDao {
    private static GoogleBookDao instance; // Thể hiện duy nhất của lớp

    private static final String API_URL = "https://www.googleapis.com/books/v1/volumes?q=isbn:";

    // Hàm tạo private để ngăn tạo thể hiện bên ngoài
    private GoogleBookDao() {
    }

    // Phương thức tĩnh để lấy thể hiện duy nhất
    public static GoogleBookDao getInstance() {
        if (instance == null) {
            synchronized (GoogleBookDao.class) {
                if (instance == null) {
                    instance = new GoogleBookDao();
                }
            }
        }
        return instance;
    }

    @Override
    public void addBook(Book book) {
        // Chức năng này không áp dụng trong GoogleBookDao
        throw new UnsupportedOperationException("addBook not supported in GoogleBookDao.");
    }

    @Override
    public void deleteBook(String bookID) {
        // Chức năng này không áp dụng trong GoogleBookDao
        throw new UnsupportedOperationException("deleteBook not supported in GoogleBookDao.");
    }

    @Override
    public void updateBook(Book book) {
        // Chức năng này không áp dụng trong GoogleBookDao
        throw new UnsupportedOperationException("updateBook not supported in GoogleBookDao.");
    }

    @Override
    public List<Book> searchBooks(String criteria, String value) {
        return List.of();
    }

    @Override
    public List<Book> searchBooksValue(String value) {
        return List.of();
    }

    @Override
    public List<Book> advancedSearchBooks(String value, int limit, int offset) {return List.of();}


    @Override
    public Book fetchBookInfoFromAPI(String isbn) {
        if (isbn.length() != 10 && isbn.length() != 13) {
            System.out.println("Mã ISBN không hợp lệ.");
            return null;
        }

        try {
            // Tạo URL để gọi API Google Books với mã ISBN
            URL url = new URL(API_URL + isbn +"&key=AIzaSyAmWmP0OfkanSPfW72I5X0X4CwGn_84yTU");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Kiểm tra mã trạng thái HTTP
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Đọc dữ liệu trả về từ API
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // In ra phản hồi JSON để kiểm tra
                System.out.println("Response from API: " + response.toString());

                // Chuyển đổi chuỗi JSON thành đối tượng JSON
                JSONObject jsonResponse = new JSONObject(response.toString());

                // Kiểm tra tổng số mục trả về
                int totalItems = jsonResponse.optInt("totalItems", 0);
                if (totalItems > 0) {
                    JSONArray items = jsonResponse.getJSONArray("items");
                    JSONObject bookItem = items.getJSONObject(0);
                    JSONObject bookInfo = bookItem.getJSONObject("volumeInfo");

                    // Lấy các trường từ JSON
                    String bookID = bookItem.optString("id", "Unknown Book ID");
                    String title = bookInfo.optString("title", "Unknown Title");
                    String author = bookInfo.has("authors") ? bookInfo.getJSONArray("authors").optString(0, "Unknown Author") : "Unknown Author";
                    int publishYear = bookInfo.optInt("publishedDate", 0);
                    String category = bookInfo.has("categories") ? bookInfo.optJSONArray("categories").optString(0, "Unknown Category") : "Unknown Category";
                    String isbn13 = bookInfo.has("industryIdentifiers") ? bookInfo.getJSONArray("industryIdentifiers").getJSONObject(0).getString("identifier") : isbn;
                    String coverCode = bookInfo.has("imageLinks") ? bookInfo.getJSONObject("imageLinks").optString("thumbnail", "") : "";
                    // Tạo đối tượng Book và trả về
                    return new Book(bookID, title, author, publishYear, category, isbn13, coverCode, 1); // 1: trạng thái có sẵn
                } else {
                    System.out.println("Không tìm thấy sách với ISBN này.");
                    return null;
                }
            } else {
                System.out.println("Lỗi kết nối API. Mã phản hồi: " + responseCode);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void updateQuantity(String bookID, int n) {

    }


    public String fetchBookDescriptionFromAPI(Book book) {
        String isbn = book.getIsbn(); // Lấy ISBN từ đối tượng Book
        try {
            // Tạo URL để gọi API Google Books với mã ISBN
            URL url = new URL(API_URL + isbn +"&key=AIzaSyAmWmP0OfkanSPfW72I5X0X4CwGn_84yTU");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Kiểm tra mã trạng thái HTTP để chắc chắn API đã trả về kết quả
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Đọc dữ liệu trả về từ API
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Chuyển đổi chuỗi JSON thành đối tượng JSON
                JSONObject jsonResponse = new JSONObject(response.toString());

                // Kiểm tra nếu có kết quả trả về
                JSONArray items = jsonResponse.getJSONArray("items");
                if (items.length() > 0) {
                    JSONObject bookItem = items.getJSONObject(0);
                    JSONObject bookInfo = bookItem.getJSONObject("volumeInfo");

                    // Lấy trường "description" từ JSON (mô tả sách)
                    String description = bookInfo.optString("description", "Không có mô tả");

                    return description;
                } else {
                    System.out.println("Không tìm thấy sách với ISBN này.");
                    return "Không tìm thấy sách.";
                }
            } else {
                System.out.println("Lỗi kết nối API. Mã phản hồi: " + responseCode);
                return "Lỗi kết nối API.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Đã xảy ra lỗi.";
        }
    }

/*
    @Override
    public void updateBookStatus(String bookID, int newStatus) {
        // Chức năng này không áp dụng trong GoogleBookDao
        throw new UnsupportedOperationException("updateBookStatus not supported in GoogleBookDao.");
    }
    */

    // Lấy link preview từ API
    public String fetchBookPreviewLinkFromAPI(Book book) {
        String isbn = book.getIsbn(); // Lấy ISBN từ đối tượng Book
        try {
            // Tạo URL để gọi API Google Books với mã ISBN
            URL url = new URL(API_URL + isbn +"&key=AIzaSyAmWmP0OfkanSPfW72I5X0X4CwGn_84yTU");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Kiểm tra mã trạng thái HTTP để chắc chắn API đã trả về kết quả
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Đọc dữ liệu trả về từ API
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Chuyển đổi chuỗi JSON thành đối tượng JSON
                JSONObject jsonResponse = new JSONObject(response.toString());

                // Kiểm tra nếu có kết quả trả về
                JSONArray items = jsonResponse.optJSONArray("items");
                if (items != null && items.length() > 0) {
                    JSONObject bookItem = items.getJSONObject(0);
                    JSONObject bookInfo = bookItem.getJSONObject("volumeInfo");

                    // Lấy trường "previewLink" từ JSON
                    String previewLink = bookInfo.optString("previewLink", "Không có link xem trước");

                    return previewLink;
                } else {
                    System.out.println("Không tìm thấy sách với ISBN này.");
                    return "Không tìm thấy sách.";
                }
            } else {
                System.out.println("Lỗi kết nối API. Mã phản hồi: " + responseCode);
                return "Lỗi kết nối API.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Đã xảy ra lỗi.";
        }
    }


    // Cập nhật phương thức generateQrCodeForBook để sử dụng link preview

    @Override
    public void generateQrCodeForBook(String isbn) {
        try {
            // Kiểm tra xem ISBN có hợp lệ không
            if (isbn == null || isbn.trim().isEmpty()) {
                System.out.println("Mã ISBN không hợp lệ.");
                return;
            }

            // Tạo kết nối đến cơ sở dữ liệu hoặc API để lấy thông tin sách
            Book book = fetchBookInfoFromAPI(isbn); // Lấy sách từ API hoặc cơ sở dữ liệu

            if (book != null) {
                // Lấy link xem trước từ Google Books API
                String previewLink = fetchBookPreviewLinkFromAPI(book);
                if (!previewLink.equals("Không tìm thấy sách.")) {
                    // Đường dẫn lưu ảnh mã QR
                    String filePath = "src/main/resources/Library/" + book.getIsbn() + "_qr.png";
                    Path qrCodeFilePath = Paths.get(filePath);

                    // Kiểm tra nếu file ảnh mã QR đã tồn tại
                    if (Files.exists(qrCodeFilePath)) {
                        System.out.println("Mã QR đã tồn tại tại: " + filePath);
                        return; // Dừng lại nếu đã có file mã QR
                    }

                    // Tạo mã QR với link preview
                    QRCodeWriter qrCodeWriter = new QRCodeWriter();
                    BitMatrix bitMatrix = qrCodeWriter.encode(previewLink, BarcodeFormat.QR_CODE, 300, 300);

                    // Lưu mã QR dưới dạng hình ảnh
                    MatrixToImageWriter.writeToPath(bitMatrix, "PNG", qrCodeFilePath);
                    System.out.println("Mã QR đã được lưu vào: " + filePath);
                } else {
                    System.out.println("Không tìm thấy link xem trước cho sách với ID: " + book.getBookID());
                }
            } else {
                System.out.println("Không tìm thấy sách với ID: " + isbn);
            }
        } catch (WriterException e) {
            System.err.println("Lỗi khi tạo mã QR: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Lỗi khi lưu mã QR: " + e.getMessage());
        }
    }


    public List<Book> fetchAllBooksFromAPI() {
        List<Book> allBooks = new ArrayList<>();
        String searchTerm = "Romance"; // Chủ đề tìm kiếm, có thể thay đổi
        int startIndex = 0;
        final int maxResults = 40; // Số lượng sách tối đa mỗi lần
        final int maxAllowedResults = 1000; // Giới hạn tối đa của Google Books API

        try {
            while (startIndex < maxAllowedResults) { // Ngăn vượt giới hạn API
                // Mã hóa từ khóa tìm kiếm để tránh lỗi URL
                String encodedSearchTerm = URLEncoder.encode(searchTerm, StandardCharsets.UTF_8.toString());
                // Tạo URL để gọi API Google Books
                URL url = new URL("https://www.googleapis.com/books/v1/volumes?q=" + encodedSearchTerm + "&startIndex=" + startIndex + "&maxResults=" + maxResults);
                System.out.println("Đang gửi yêu cầu đến: " + url);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                // Kiểm tra mã trạng thái HTTP
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Đọc dữ liệu trả về từ API
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // Log phản hồi JSON
                    System.out.println("Phản hồi JSON: " + response.toString());

                    // Chuyển đổi chuỗi JSON thành đối tượng JSON
                    JSONObject jsonResponse = new JSONObject(response.toString());

                    // Kiểm tra tổng số mục trả về
                    int totalItems = jsonResponse.optInt("totalItems", 0);
                    if (totalItems == 0 || startIndex >= totalItems) {
                        System.out.println("Không còn sách nào để lấy.");
                        break; // Không còn sách nào
                    }

                    // Kiểm tra xem trường "items" có tồn tại hay không
                    if (!jsonResponse.has("items")) {
                        System.out.println("Không có trường 'items' trong phản hồi JSON.");
                        break; // Thoát nếu không có sách
                    }

                    JSONArray items = jsonResponse.getJSONArray("items");
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject bookItem = items.getJSONObject(i);
                        JSONObject bookInfo = bookItem.getJSONObject("volumeInfo");

                        // Lấy các trường từ JSON
                        String bookID = bookItem.optString("id", "Unknown Book ID");
                        String title = bookInfo.optString("title", "Unknown Title");
                        String author = bookInfo.has("authors") ? bookInfo.getJSONArray("authors").optString(0, "Unknown Author") : "Unknown Author";
                        String publishYear = bookInfo.optString("publishedDate", "Unknown Year");
                        String category = bookInfo.has("categories") ? bookInfo.getJSONArray("categories").optString(0, "Unknown Category") : "Unknown Category";

                        // Xử lý ISBN-13
                        String isbn13 = "Unknown ISBN";
                        if (bookInfo.has("industryIdentifiers")) {
                            JSONArray identifiers = bookInfo.getJSONArray("industryIdentifiers");
                            for (int j = 0; j < identifiers.length(); j++) {
                                JSONObject identifier = identifiers.getJSONObject(j);
                                if ("ISBN_13".equals(identifier.optString("type"))) {
                                    isbn13 = identifier.optString("identifier", "Unknown ISBN");
                                    break;
                                }
                            }
                        }

                        String coverCode = bookInfo.has("imageLinks") ? bookInfo.getJSONObject("imageLinks").optString("thumbnail", "") : "";

                        // Phân tích năm từ chuỗi ngày tháng
                        int year = 0;
                        if (!publishYear.equals("Unknown Year")) {
                            try {
                                year = Integer.parseInt(publishYear.split("-")[0]); // Lấy phần năm
                            } catch (NumberFormatException e) {
                                System.out.println("Lỗi phân tích năm: " + publishYear);
                            }
                        }

                        // Tạo đối tượng Book và thêm vào danh sách
                        allBooks.add(new Book(bookID, title, author, year, category, isbn13, coverCode, 100)); // 100: trạng thái có sẵn
                    }

                    // Tăng chỉ số để lấy sách tiếp theo
                    startIndex += maxResults;
                } else {
                    System.out.println("Lỗi kết nối API. Mã phản hồi: " + responseCode);
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi gọi API Google Books:");
            e.printStackTrace();
        }

        return allBooks;
    }
}
