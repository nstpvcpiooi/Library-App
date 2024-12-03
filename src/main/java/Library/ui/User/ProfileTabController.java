package Library.ui.User;
import Library.backend.Login.Model.Member;
import Library.backend.Login.Model.User;
import Library.backend.Session.SessionManager;
import Library.ui.LogIn.UserLogInController;

public class ProfileTabController {

    // ???

    private UserMainController userMainController;

    public UserMainController getMainController() {
        return userMainController;
    }

    public void setMainController(UserMainController userMainController) {

        this.userMainController = userMainController;
    }
}
