package Library.backend.Login.Model;


import Library.backend.Request.DAO.RequestDAO;
import Library.backend.Request.DAO.RequestDAOImpl;
import Library.backend.Request.Model.Request;
import Library.backend.bookDao.BookDao;

import java.util.List;

public class User extends Member {
    private RequestDAO requestDAO = RequestDAOImpl.getInstance();

    public User(Member member) {
        this.setMemberID(member.getMemberID());
        this.setUserName(member.getUserName());
        this.setPassword(member.getPassword());
        this.setEmail(member.getEmail());
        this.setPhone(member.getPhone());
        this.setOtp(member.getOtp());
        this.setDuty(0);
    }
    public void borrowBook(int bookID){
        requestDAO.borrowRequest(this.getMemberID(), bookID);
    }
    public void returnBook(int bookID){
        requestDAO.returnBook(this.getMemberID(), bookID);
    }
    public List<Request> getRequests(){
        return requestDAO.getMemberBorrowHistory(this.getMemberID());
    }

}