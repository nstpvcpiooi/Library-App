package Library.backend.Login.DAO;


import Library.backend.Login.Model.Member;

import java.util.List;

public interface MemberDAO {
    Member getMemberByUserNameAndPassword(String userName, String password);
    boolean createMember(Member member);
    Member getMemberByEmail(String email);
    boolean updateMember(Member member);
    void updateOtp(Member member);
    String getOtpByEmail(String email);
    boolean deleteMemberById(int memberId);
    List<Member> searchMembers(String criteria, String value);


    
}