package Library.ui.Admin;

// CLASS NÀY DÙNG LÀM DEMO THAY THẾ CHO USER PHẦN BACKEND (KHI NÀO NỐI BACKEND THÌ THAY THẾ VÀ XÓA CLASS NÀY)
public class demoUser {
    private String userName;
    private String password;
    private String email;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public int getDuty() {
        return duty;
    }

    public void setDuty(int duty) {
        this.duty = duty;
    }

    public String getPreference() {
        return preference;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }

    private String phone;
    private int duty;
    private String preference;

    public demoUser(String userName, String password, String email, String phone, int duty, String preference) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.duty = duty;
        this.preference = preference;
    }

    public demoUser() {}

}
