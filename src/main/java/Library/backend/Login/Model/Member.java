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

    private static MemberDAO memberDAO = MemberDAOImpl.getInstance();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int OTP_LENGTH = 6;

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

    // Methods for login, create account, and delete member
    public static Member authenticate(String userName, String password) {
        return memberDAO.getMemberByUserNameAndPassword(userName, password);
    }

    public static boolean createAccount(Member member) {
        return memberDAO.createMember(member);
    }

    public static boolean deleteMemberById(int memberId) {
        return memberDAO.deleteMemberById(memberId);
    }

    // Methods for forgot password functionality
    private static String generateOTP() {
        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            int index = (int) (Math.random() * CHARACTERS.length());
            otp.append(CHARACTERS.charAt(index));
        }
        return otp.toString();
    }

    public static void forgotPass(String email) {
        EmailUtil emailUtil = new EmailUtil();
        String otp = generateOTP();
        emailUtil.sendEmail(email, "OTP", otp);

        Member member = memberDAO.getMemberByEmail(email);
        if (member != null) {
            member.setOtp(otp);
            memberDAO.updateOtp(member);
        }
    }

    public static boolean checkOTP(String email, String input) {
        String storedOtp = memberDAO.getOtpByEmail(email);
        return storedOtp != null && storedOtp.equals(input);
    }

    public static boolean changePass(String email, String newPassword) {
        Member member = memberDAO.getMemberByEmail(email);
        if (member != null) {
            member.setPassword(newPassword);
            return memberDAO.updateMember(member);
        }
        return false;
    }
}