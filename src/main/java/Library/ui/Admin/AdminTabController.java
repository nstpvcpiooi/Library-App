package Library.ui.Admin;


public abstract class AdminTabController {
    /**
     * Controller chính của admin (đã được khởi tạo trong AdminMainController)
     */
    private AdminMainController MainController;

    public void setMainController(AdminMainController adminMainController) {
        this.MainController = adminMainController;
    }

    public AdminMainController getMainController() {
        return MainController;
    }
}
