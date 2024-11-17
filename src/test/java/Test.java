public class Test {
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

*//*
Book book1 = Book.fetchBookInfoFromAPI("9781466626874");
       System.out.println(book1);
       book1.addBook();
       */
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
