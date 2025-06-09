package Library.backend.Login.Model;

import Library.backend.bookDao.BookDao;
import Library.backend.bookDao.MysqlBookDao;
import Library.backend.bookModel.Book;

import java.util.List;


public class Member {
    private BookDao bookDao= MysqlBookDao.getInstance();
    private int memberID;
    private String userName;
    private String password;
    private String email;
    private String phone;
    private String otp;
    private int duty;
    @Override
    public String toString() {
        return "Member{" +
                "memberID=" + memberID +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", otp='" + otp + '\'' +
                ", duty=" + duty +
                '}';
    }


    // Getters and Setters
    public int getMemberID() {
        return memberID;
    }

    public void setMemberID(int memberID) {
        this.memberID = memberID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public int getDuty() {
        return duty;
    }

    public void setDuty(int duty) {
        this.duty = duty;
    }
    public List<Book> searchBooks(String criteria, String value) {
        // Implementation for searching books
        return bookDao.searchBooks(criteria,value);
    }




}