package Library.backend.Session;

import Library.backend.Login.Model.Member;

public class SessionManager {
    private static SessionManager instance;
    private Member loggedInMember;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public Member getLoggedInMember() {
        return loggedInMember;
    }

    public void setLoggedInMember(Member loggedInMember) {
        this.loggedInMember = loggedInMember;
    }

    public void clearSession() {
        this.loggedInMember = null;
    }
}