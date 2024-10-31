package Library.backend.Login.Test;

import Library.backend.Login.Controller.ForgotPassController;
import Library.backend.Login.Controller.LoginController;
import Library.backend.Login.DAO.MemberDAO;
import Library.backend.Login.DAO.MemberDAOImpl;
import Library.backend.Login.Model.Admin;
import Library.backend.Login.Model.Member;
import Library.backend.Request.Controller.RequestController;

import java.util.List;

public class LoginTest {
    public static void main(String[] args) {
        MemberDAO memberDAO = MemberDAOImpl.getInstance();
        LoginController loginController = new LoginController();
        Member member1 = loginController.authenticate("admin4", "admin4");
        List<Member> members = memberDAO.searchMembers("userName", "admin4");






    }
}
