package Library.backend.Login.Controller;


import Library.backend.util.EmailUtil;
import Library.backend.Login.DAO.MemberDAO;
import Library.backend.Login.DAO.MemberDAOImpl;
import Library.backend.Login.Model.Member;

public class ForgotPassController {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int OTP_LENGTH = 6;
    private MemberDAO memberDAO;

    public ForgotPassController() {
        this.memberDAO = (MemberDAO) MemberDAOImpl.getInstance();
    }

    private String generateOTP() {
        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            int index = (int) (Math.random() * CHARACTERS.length());
            otp.append(CHARACTERS.charAt(index));
        }
        return otp.toString();
    }

    public void forgotPass(String email) {
        EmailUtil emailUtil = new EmailUtil();
        String otp = generateOTP();
        emailUtil.sendEmail(email, "OTP", otp);

        Member member = memberDAO.getMemberByEmail(email);
        if (member != null) {
            member.setOtp(otp);
            memberDAO.updateOtp(member);
        }
    }

    public boolean checkOTP(String email, String input) {
        String storedOtp = memberDAO.getOtpByEmail(email);
        return storedOtp != null && storedOtp.equals(input);
    }
    public boolean changePass(String email, String newPassword) {
        Member member = memberDAO.getMemberByEmail(email);
        if (member != null) {
            member.setPassword(newPassword);
            return memberDAO.updateMember(member);
        }
        return false;
    }
}