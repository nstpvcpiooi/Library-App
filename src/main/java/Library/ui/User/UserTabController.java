package Library.ui.User;

public abstract class UserTabController {
    protected UserMainController userMainController;

    public UserMainController getMainController() {
        return userMainController;
    }

    public void setMainController(UserMainController userMainController) {
        this.userMainController = userMainController;
    }
}
