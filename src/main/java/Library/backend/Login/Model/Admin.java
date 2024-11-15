package Library.backend.Login.Model;

import Library.backend.bookDao.BookDao;
import Library.backend.bookDao.MysqlBookDao;
import Library.backend.bookModel.Book;

import java.util.List;

public class Admin extends Member {
    private BookDao bookDao = MysqlBookDao.getInstance();

    public Admin(Member member) {
        this.setMemberID(member.getMemberID());
        this.setUserName(member.getUserName());
        this.setPassword(member.getPassword());
        this.setEmail(member.getEmail());
        this.setPhone(member.getPhone());
        this.setOtp(member.getOtp());
        this.setDuty(1);// Initialize the BookDao
    }

    public void addBook(Book book) {
        // Implementation for adding a book
        bookDao.addBook(book);
    }

    public void removeBook(String bookId) {
        // Implementation for removing a book
        bookDao.deleteBook(bookId);
    }

}