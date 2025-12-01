package Library.backend.Member.Service;

import Library.backend.Member.DAO.MemberDAO;
import Library.backend.Member.DAO.MemberDAOImpl;
import Library.backend.Member.Model.Member;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Service facade cho Member, tách phần phối ghép DAO và kiểm tra đầu vào cơ bản.
 */
public class MemberService {

    private static volatile MemberService instance;

    private final MemberDAO memberDAO;

    private MemberService(MemberDAO memberDAO) {
        this.memberDAO = Objects.requireNonNull(memberDAO);
    }

    public static MemberService getInstance() {
        if (instance == null) {
            synchronized (MemberService.class) {
                if (instance == null) {
                    instance = new MemberService(MemberDAOImpl.getInstance());
                }
            }
        }
        return instance;
    }

    /**
     * Factory cho test hoặc tự cấu hình mà không dùng singleton.
     */
    public static MemberService create(MemberDAO memberDAO) {
        return new MemberService(memberDAO);
    }

    public Member getMemberByUserNameAndPassword(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Username/password must not be blank");
        }
        return memberDAO.getMemberByUserNameAndPassword(username, password);
    }

    public boolean createMember(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member must not be null");
        }
        if (member.getUserName() == null || member.getUserName().isBlank()) {
            throw new IllegalArgumentException("Username must not be blank");
        }
        if (member.getPassword() == null || member.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password must not be blank");
        }
        if (member.getEmail() == null || member.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email must not be blank");
        }
        if (member.getPhone() == null || member.getPhone().isBlank()) {
            throw new IllegalArgumentException("Phone must not be blank");
        }
        return memberDAO.createMember(member);
    }

    public Member getMemberByUsername(String username) {
        return memberDAO.getMemberByUsername(username);
    }

    public Member getMemberByEmail(String email) {
        return memberDAO.getMemberByEmail(email);
    }

    public boolean updateMember(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member must not be null");
        }
        if (member.getUserName() == null || member.getUserName().isBlank()) {
            throw new IllegalArgumentException("Username must not be blank");
        }
        if (member.getPassword() == null || member.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password must not be blank");
        }
        if (member.getEmail() == null || member.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email must not be blank");
        }
        if (member.getPhone() == null || member.getPhone().isBlank()) {
            throw new IllegalArgumentException("Phone must not be blank");
        }
        if (member.getMemberID() <= 0) {
            throw new IllegalArgumentException("Member ID must be positive");
        }
        return memberDAO.updateMember(member);
    }

    public void deleteMemberById(int memberId) {
        if (memberId <= 0) {
            throw new IllegalArgumentException("Member ID must be positive");
        }
        memberDAO.deleteMemberById(memberId);
    }

    public List<Member> displayMembers() {
        List<Member> members = memberDAO.DisplayMembers();
        return members == null ? Collections.emptyList() : members;
    }
}
