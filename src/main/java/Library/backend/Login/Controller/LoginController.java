package Library.backend.Login.Controller;

import Library.backend.Login.DAO.MemberDAO;
import Library.backend.Login.DAO.MemberDAOImpl;
import Library.backend.Login.Model.Admin;
import Library.backend.Login.Model.Member;
import Library.backend.Login.Model.User;

public class LoginController {
    private MemberDAO memberDAO;

    public LoginController() {
        this.memberDAO = (MemberDAO) MemberDAOImpl.getInstance();
    }

    public Member authenticate(String userName, String password) {
        Member member = memberDAO.getMemberByUserNameAndPassword(userName, password);
        if (member != null) {
            if (member.getDuty() == 1) {
                return new Admin(member);
            } else {
                return new User(member);
            }
        }
        return null;
    }

    public boolean createAccount(Member member) {
        return memberDAO.createMember(member);
    }

    public boolean deleteMemberById(int memberId) {
        return memberDAO.deleteMemberById(memberId);
    }
}