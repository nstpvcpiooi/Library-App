package Library.backend.Member.DAO;


import Library.backend.Member.Model.Member;

import java.util.List;

public interface MemberDAO {
    Member getMemberByUserNameAndPassword(String userName, String password);
    boolean createMember(Member member);
    Member getMemberByUsername(String username);
    Member getMemberByEmail(String email);
    boolean updateMember(Member member);
    void deleteMemberById(int memberId);
    List<Member> DisplayMembers();

}
