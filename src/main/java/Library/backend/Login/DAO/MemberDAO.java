package Library.backend.Login.DAO;


import Library.backend.Login.Model.Member;
import Library.backend.Login.Model.User;

import java.util.List;

public interface MemberDAO {
    Member getMemberByUserNameAndPassword(String userName, String password);
    boolean createMember(Member member);
    Member getMemberByEmail(String email);
    boolean updateMember(Member member);
    void updateOtp(Member member);
    String getOtpByEmail(String email);
    void deleteMemberById(int memberId);
    List<Member> searchMembers(String criteria, String value);
    void forgotPass(String email);
    boolean checkOTP(String email, String input);
    boolean changePass(String email, String newPassword);
    Member login(String userName, String password);
    List<User> DisplayMembers();

}