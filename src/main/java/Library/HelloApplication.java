package Library;

import Library.backend.bookDao.GoogleBookDao;
import Library.backend.bookDao.MysqlBookDao;
import Library.backend.bookModel.Book;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

import static Library.backend.bookModel.Book.searchBooks;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
  /*  Book book = new Book("001", "Lập trình Java", "Nguyễn Văn A", 2022,
                "CNTT", "978-3-16-148410-0", "link-to-cover-image", 1);
     //  book.addBook();
        System.out.println(book.toString());
*//*
        // Tìm kiếm sách theo tiêu chí
        List<Book> foundBooks = Book.searchBooks("title", "Lập trình Java");
        if (!foundBooks.isEmpty()) {
            System.out.println("Sách tìm thấy: ");
            for (Book foundBook : foundBooks) {
                System.out.println(foundBook);
            }
        } else {
            System.out.println("Không tìm thấy sách nào.");
        }
        */
/*
  List<Book> books = Book.searchBooks("category","fiction");
for(Book b : books){
    System.out.println(b);
}

*/
ArrayList<Book> b= (ArrayList<Book>) searchBooks("bookid","--AMAQAAIAAJ");
       for(Book bo : b){
           System.out.println(bo);
       }
      // book1.addBook();

     //   System.out.println(book1.fetchBookDescriptionFromAPI());
/*
        List<Book> allBooks = GoogleBookDao.getInstance().fetchAllBooksFromAPI();
        for (Book book1 : allBooks) {
            book1.addBook();
        }


        // Tạo mã QR cho sách
        //     System.out.println(book1.generateQrCodeForBook());
        ;/*
        System.out.println("Đường dẫn đến file mã QR: " + book1.getQrCodePath());
*/
        // Cập nhật tình trạng tài liệu
/*
        book.updateBookStatus(0); // Giả sử 0 là đã mượn
        System.out.println("Tình trạng sách sau khi cập nhật: " + book.getStatus());

        // Xóa sách
        book.deleteBook();
        System.out.println("Đã xóa sách có ID: " + book.getBookID());
*/

    }


}