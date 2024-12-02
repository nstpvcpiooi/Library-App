package Library.backend.bookDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Library.backend.bookModel.Book;
import Library.backend.database.JDBCUtil;

public class MysqlBookDao implements BookDao {

    // Thể hiện duy nhất của lớp
    private static MysqlBookDao instance;

    // Hàm tạo private để ngăn tạo thể hiện bên ngoài
    private MysqlBookDao() {
    }

    // Phương thức tĩnh để lấy thể hiện duy nhất
    public static MysqlBookDao getInstance() {
        if (instance == null) {
            synchronized (MysqlBookDao.class) {
                if (instance == null) {
                    instance = new MysqlBookDao();
                }
            }
        }
        return instance;
    }

    @Override
    public void addBook(Book t) {
        try {
            // Kết nối với cơ sở dữ liệu
            Connection con = JDBCUtil.getConnection();

            // Câu lệnh SQL INSERT
            String sql = "INSERT INTO Books (bookID, title, author, publishYear, category, isbn, coverCode, quantity) " +
                    "VALUES (?,?,?,?,?,?,?,?);";
            PreparedStatement pst = con.prepareStatement(sql);

            // Gán giá trị cho các tham số
            pst.setString(1, t.getBookID());
            pst.setString(2, t.getTitle());
            pst.setString(3, t.getAuthor());
            pst.setInt(4, t.getPublishYear());
            pst.setString(5, t.getCategory());
            pst.setString(6, t.getIsbn());
            pst.setString(7, t.getCoverCode());
            pst.setInt(8, t.getQuantity()); // Tham số thứ 8 (quantity)

            // Thực thi câu lệnh INSERT
            pst.executeUpdate();

            // Đóng kết nối
            JDBCUtil.closeConnection(con);
            System.out.println("Sách đã được thêm thành công: " + t.getTitle());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteBook(String bookID) {
        try {
            Connection con = JDBCUtil.getConnection();

            String sql = "DELETE FROM Books WHERE bookID = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, bookID);

            // Thực thi câu lệnh DELETE
            pst.executeUpdate();

            JDBCUtil.closeConnection(con);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateBook(Book t) {
        try {
            Connection con = JDBCUtil.getConnection();

            String sql = "UPDATE Books SET title=?, author=?, publishYear=?, category=?, isbn=?, coverCode=?, quantity=? WHERE bookID = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, t.getTitle());
            pst.setString(2, t.getAuthor());
            pst.setInt(3, t.getPublishYear());
            pst.setString(4, t.getCategory());
            pst.setString(5, t.getIsbn());
            pst.setString(6, t.getCoverCode());
            //      pst.setInt(7, t.getStatus());
            pst.setInt(7, t.getQuantity());  // Cập nhật giá trị quantity
            pst.setString(8, t.getBookID());

            // Thực thi câu lệnh UPDATE
            pst.executeUpdate();

            JDBCUtil.closeConnection(con);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Book> searchBooks(String criteria, String value) {
        List<Book> books = new ArrayList<>();
        try {
            Connection con = JDBCUtil.getConnection();

            String sql = "SELECT * FROM Books WHERE " + criteria + " LIKE ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, "%" + value + "%"); // Tìm kiếm với giá trị có thể có trong tiêu chí

            ResultSet rs = pst.executeQuery(); // Thực thi truy vấn và lấy kết quả
            while (rs.next()) {
                // Tạo đối tượng Book từ kết quả truy vấn và thêm vào danh sách
                Book book = new Book(
                        rs.getString("bookID"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("publishYear"),
                        rs.getString("category"),
                        rs.getString("isbn"),
                        rs.getString("coverCode"),
                        rs.getInt("quantity") // Lấy giá trị quantity từ cơ sở dữ liệu
                );
                books.add(book);
            }

            JDBCUtil.closeConnection(con);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    @Override
    public List<Book> searchBooksValue(String value) {
        List<Book> books = new ArrayList<>();
        try {
            Connection con = JDBCUtil.getConnection();

            // Tìm kiếm trên tất cả các cột trong một truy vấn
            String sql = "SELECT * FROM Books WHERE title LIKE ? OR author LIKE ? OR category LIKE ? OR isbn LIKE ?";
            PreparedStatement pst = con.prepareStatement(sql);
            String searchValue = "%" + value + "%";
            pst.setString(1, searchValue);
            pst.setString(2, searchValue);
            pst.setString(3, searchValue);
            pst.setString(4, searchValue);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                books.add(new Book(
                        rs.getString("bookID"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("publishYear"),
                        rs.getString("category"),
                        rs.getString("isbn"),
                        rs.getString("coverCode"),
                        rs.getInt("quantity")
                ));
            }

            JDBCUtil.closeConnection(con);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    @Override
    public Book fetchBookInfoFromAPI(String isbn) {
        // ko viết trong lớp này
        return null;
    }

    @Override
    public void updateQuantity(String bookID, int quantity) {
        try {
            // Lấy kết nối từ JDBCUtil
            Connection con = JDBCUtil.getConnection();

            // Câu lệnh SQL để cập nhật số lượng sách
            String sql = "UPDATE Books SET quantity = quantity + ? WHERE bookID = ?";

            // Tạo PreparedStatement
            PreparedStatement pst = con.prepareStatement(sql);

            // Thiết lập các giá trị cho câu lệnh SQL
            pst.setInt(1, quantity);   // Cập nhật số lượng sách
            pst.setString(2, bookID);  // Điều kiện tìm kiếm sách theo bookID

            // Thực thi câu lệnh UPDATE
            pst.executeUpdate();

            // Đóng kết nối
            JDBCUtil.closeConnection(con);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

/*
    @Override
    public void updateBookStatus(String bookID, int newStatus) {
        try {
            Connection con = JDBCUtil.getConnection();

            String sql = "UPDATE Books SET status = ? WHERE bookID = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, newStatus);
            pst.setString(2, bookID);

            pst.executeUpdate(); // Thực thi câu lệnh
            JDBCUtil.closeConnection(con);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    */


    @Override
    public void generateQrCodeForBook(String bookID) {
        // ko viet trong lop nay
    }

    @Override
    public String fetchBookDescriptionFromAPI(Book book) {
        // TODO Auto-generated method stub
        return null;
    }


}

