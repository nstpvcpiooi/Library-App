package Library.backend.Login.Model;

import Library.backend.Login.DAO.MemberDAO;
import Library.backend.Login.DAO.MemberDAOImpl;
import Library.backend.util.EmailUtil;


public class Member {
    private int memberID;
    private String userName;
    private String password;
    private String email;
    private String phone;
    private String otp;
    private int duty;




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





}