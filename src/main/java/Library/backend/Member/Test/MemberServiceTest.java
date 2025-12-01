package Library.backend.Member.Test;

import Library.backend.Member.DAO.MemberDAO;
import Library.backend.Member.Model.Member;
import Library.backend.Member.Service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

// Sử dụng Phân hoạch tương đương kiểm thử đơn vị cho các unit trong MemberService.
public class MemberServiceTest {

    private static class RecordingMemberDao implements MemberDAO {
        AtomicReference<String> lastUsername = new AtomicReference<>();
        AtomicReference<String> lastPassword = new AtomicReference<>();
        AtomicReference<String> lastEmail = new AtomicReference<>();
        AtomicReference<Integer> lastId = new AtomicReference<>();
        AtomicReference<Member> lastMember = new AtomicReference<>();
        Member memberResult;
        boolean createResult = true;
        boolean updateResult = true;
        List<Member> membersResult = List.of();

        @Override
        public Member getMemberByUserNameAndPassword(String userName, String password) {
            lastUsername.set(userName);
            lastPassword.set(password);
            return memberResult;
        }

        @Override
        public boolean createMember(Member member) {
            lastMember.set(member);
            return createResult;
        }

        @Override
        public Member getMemberByUsername(String username) {
            lastUsername.set(username);
            return memberResult;
        }

        @Override
        public Member getMemberByEmail(String email) {
            lastEmail.set(email);
            return memberResult;
        }

        @Override
        public boolean updateMember(Member member) {
            lastMember.set(member);
            return updateResult;
        }

        @Override
        public void deleteMemberById(int memberId) {
            lastId.set(memberId);
        }

        @Override
        public List<Member> DisplayMembers() {
            return membersResult;
        }
    }

    @Test
    // TC-01: username chỉ chứa khoảng trắng khi đăng nhập => Expected output: getMemberByUserNameAndPassword ném IllegalArgumentException, không gọi DAO.
    @DisplayName("getMemberByUserNameAndPassword: username blank -> throws IllegalArgumentException")
    void login_blankUsername_throwsException() {
        MemberService service = MemberService.create(new RecordingMemberDao());
        assertThrows(IllegalArgumentException.class, () -> service.getMemberByUserNameAndPassword("   ", "pass"));
    }

    @Test
    // TC-02: password chỉ chứa khoảng trắng khi đăng nhập => Expected output: getMemberByUserNameAndPassword ném IllegalArgumentException, không gọi DAO.
    @DisplayName("getMemberByUserNameAndPassword: password blank -> throws IllegalArgumentException")
    void login_blankPassword_throwsException() {
        MemberService service = MemberService.create(new RecordingMemberDao());
        assertThrows(IllegalArgumentException.class, () -> service.getMemberByUserNameAndPassword("user", "  "));
    }

    @Test
    // TC-03: username/password hợp lệ, DAO trả về một Member => Expected output: service trả đúng cùng instance Member từ DAO và truyền đúng username/password xuống DAO.
    @DisplayName("getMemberByUserNameAndPassword: valid credentials -> returns member from DAO")
    void login_valid_delegates() {
        RecordingMemberDao dao = new RecordingMemberDao();
        Member expected = new Member();
        dao.memberResult = expected;
        MemberService service = MemberService.create(dao);

        Member result = service.getMemberByUserNameAndPassword("user", "pass");

        assertSame(expected, result);
        assertEquals("user", dao.lastUsername.get());
        assertEquals("pass", dao.lastPassword.get());
    }


    @Test
    // TC-04: truyền member = null vào createMember => Expected output: createMember ném IllegalArgumentException, không gọi DAO.
    @DisplayName("createMember: null member -> throws IllegalArgumentException")
    void createMember_null_throwsException() {
        MemberService service = MemberService.create(new RecordingMemberDao());
        assertThrows(IllegalArgumentException.class, () -> service.createMember(null));
    }

    @Test
    // TC-05: username chỉ chứa khoảng trắng khi tạo mới => Expected output: createMember ném IllegalArgumentException, không gọi DAO createMember.
    @DisplayName("createMember: username blank -> throws IllegalArgumentException")
    void createMember_blankUsername_throwsException() {
        MemberService service = MemberService.create(new RecordingMemberDao());
        Member invalid = new Member();
        invalid.setUserName("   ");
        invalid.setPassword("password");
        invalid.setEmail("e");
        invalid.setPhone("0123456789");
        assertThrows(IllegalArgumentException.class, () -> service.createMember(invalid));
    }

    @Test
    // TC-06: password chỉ chứa khoảng trắng khi tạo mới => Expected output: createMember ném IllegalArgumentException, không gọi DAO createMember.
    @DisplayName("createMember: password blank -> throws IllegalArgumentException")
    void createMember_blankPassword_throwsException() {
        MemberService service = MemberService.create(new RecordingMemberDao());
        Member invalid = new Member();
        invalid.setUserName("u");
        invalid.setPassword(" ");
        invalid.setEmail("e");
        invalid.setPhone("0123456789");
        assertThrows(IllegalArgumentException.class, () -> service.createMember(invalid));
    }

    @Test
    // TC-07: email chỉ chứa khoảng trắng khi tạo mới => Expected output: createMember ném IllegalArgumentException, không gọi DAO createMember.
    @DisplayName("createMember: email blank -> throws IllegalArgumentException")
    void createMember_blankEmail_throwsException() {
        MemberService service = MemberService.create(new RecordingMemberDao());
        Member invalid = new Member();
        invalid.setUserName("u");
        invalid.setPassword("password");
        invalid.setEmail(" ");
        invalid.setPhone("0123456789");
        assertThrows(IllegalArgumentException.class, () -> service.createMember(invalid));
    }

    @Test
    // TC-08: phone chỉ chứa khoảng trắng khi tạo mới => Expected output: createMember ném IllegalArgumentException, không gọi DAO createMember.
    @DisplayName("createMember: phone blank -> throws IllegalArgumentException")
    void createMember_blankPhone_throwsException() {
        MemberService service = MemberService.create(new RecordingMemberDao());
        Member invalid = new Member();
        invalid.setUserName("u");
        invalid.setPassword("password");
        invalid.setEmail("e");
        invalid.setPhone(" ");
        assertThrows(IllegalArgumentException.class, () -> service.createMember(invalid));
    }

    @Test
    // TC-09: password ngắn hơn 8 ký tự khi tạo mới => Expected output: createMember ném IllegalArgumentException, không gọi DAO createMember.
    @DisplayName("createMember: password shorter than 8 chars -> throws IllegalArgumentException")
    void createMember_shortPassword_throwsException() {
        MemberService service = MemberService.create(new RecordingMemberDao());
        Member invalid = new Member();
        invalid.setUserName("u");
        invalid.setPassword("short");
        invalid.setEmail("e");
        invalid.setPhone("0123456789");
        assertThrows(IllegalArgumentException.class, () -> service.createMember(invalid));
    }

    @Test
    // TC-10: phone không đúng 10 ký tự số (có ký tự không phải số) khi tạo mới => Expected output: createMember ném IllegalArgumentException, không gọi DAO createMember.
    @DisplayName("createMember: phone not 10-digit numeric string -> throws IllegalArgumentException")
    void createMember_phoneNotAllDigits_throwsException() {
        MemberService service = MemberService.create(new RecordingMemberDao());
        Member invalid = new Member();
        invalid.setUserName("u");
        invalid.setPassword("password");
        invalid.setEmail("e");
        invalid.setPhone("012345678a"); // 10 ký tự nhưng không phải toàn số
        assertThrows(IllegalArgumentException.class, () -> service.createMember(invalid));
    }

    @Test
    // TC-11: tất cả trường hợp lệ, DAO trả true => Expected output: service trả true và truyền đúng đối tượng Member sang DAO.
    @DisplayName("createMember: valid data -> returns DAO result")
    void createMember_valid_delegates() {
        RecordingMemberDao dao = new RecordingMemberDao();
        dao.createResult = true;
        MemberService service = MemberService.create(dao);
        Member member = new Member();
        member.setUserName("u");
        member.setPassword("password");
        member.setEmail("e");
        member.setPhone("0123456789");

        boolean result = service.createMember(member);

        assertTrue(result);
        assertSame(member, dao.lastMember.get());
    }


    @Test
    // TC-12: gọi getMemberByUsername với username hợp lệ, DAO trả về một Member => Expected output: service trả đúng Member từ DAO và truyền đúng username xuống DAO.
    @DisplayName("getMemberByUsername: valid username -> returns member from DAO")
    void getMemberByUsername_delegates() {
        RecordingMemberDao dao = new RecordingMemberDao();
        Member expected = new Member();
        dao.memberResult = expected;
        MemberService service = MemberService.create(dao);

        Member result = service.getMemberByUsername("u");

        assertSame(expected, result);
        assertEquals("u", dao.lastUsername.get());
    }
    @Test
    // TC-13: DAO trả null khi tìm username => Expected output: getMemberByUsername trả null và truyền đúng username xuống DAO.
    @DisplayName("getMemberByUsername: DAO returns null -> returns null")
    void getMemberByUsername_missing_returnsNull() {
        RecordingMemberDao dao = new RecordingMemberDao();
        MemberService service = MemberService.create(dao);

        assertNull(service.getMemberByUsername("missing"));
        assertEquals("missing", dao.lastUsername.get());
    }

    @Test
    // TC-14: truyền username blank vào getMemberByUsername => Expected output: service vẫn gọi DAO, DAO trả null.
    @DisplayName("getMemberByUsername: blank username -> still delegates")
    void getMemberByUsername_blank_delegates() {
        RecordingMemberDao dao = new RecordingMemberDao();
        MemberService service = MemberService.create(dao);

        assertNull(service.getMemberByUsername("  "));
        assertEquals("  ", dao.lastUsername.get());
    }
    @Test
    // TC-15: gọi getMemberByEmail với email hợp lệ, DAO trả về một Member => Expected output: service trả đúng Member từ DAO và truyền đúng email xuống DAO.
    @DisplayName("getMemberByEmail: valid email -> returns member from DAO")
    void getMemberByEmail_delegates() {
        RecordingMemberDao dao = new RecordingMemberDao();
        Member expected = new Member();
        dao.memberResult = expected;
        MemberService service = MemberService.create(dao);

        Member result = service.getMemberByEmail("e");

        assertSame(expected, result);
        assertEquals("e", dao.lastEmail.get());
    }
    @Test
    // TC-16: DAO trả null khi tìm email => Expected output: getMemberByEmail trả null và truyền đúng email xuống DAO.
    @DisplayName("getMemberByEmail: DAO returns null -> returns null")
    void getMemberByEmail_missing_returnsNull() {
        RecordingMemberDao dao = new RecordingMemberDao();
        MemberService service = MemberService.create(dao);

        assertNull(service.getMemberByEmail("missing@mail.com"));
        assertEquals("missing@mail.com", dao.lastEmail.get());
    }

    @Test
    // TC-17: truyền email blank vào getMemberByEmail => Expected output: service vẫn gọi DAO, DAO trả null.
    @DisplayName("getMemberByEmail: blank email -> still delegates")
    void getMemberByEmail_blank_delegates() {
        RecordingMemberDao dao = new RecordingMemberDao();
        MemberService service = MemberService.create(dao);

        assertNull(service.getMemberByEmail(" "));
        assertEquals(" ", dao.lastEmail.get());
    }
    @Test
    // TC-18: truyền member = null vào updateMember => Expected output: updateMember ném IllegalArgumentException, không gọi DAO.
    @DisplayName("updateMember: null member -> throws IllegalArgumentException")
    void updateMember_null_throwsException() {
        MemberService service = MemberService.create(new RecordingMemberDao());
        assertThrows(IllegalArgumentException.class, () -> service.updateMember(null));
    }

    @Test
    // TC-19: memberID hợp lệ nhưng username chỉ chứa khoảng trắng khi cập nhật => Expected output: updateMember ném IllegalArgumentException, không gọi DAO updateMember.
    @DisplayName("updateMember: username blank -> throws IllegalArgumentException")
    void updateMember_blankUsername_throwsException() {
        MemberService service = MemberService.create(new RecordingMemberDao());
        Member invalid = new Member();
        invalid.setMemberID(1);
        invalid.setUserName(" ");
        invalid.setPassword("password");
        invalid.setEmail("e");
        invalid.setPhone("0123456789");
        assertThrows(IllegalArgumentException.class, () -> service.updateMember(invalid));
    }

    @Test
    // TC-20: memberID hợp lệ nhưng password chỉ chứa khoảng trắng khi cập nhật => Expected output: updateMember ném IllegalArgumentException, không gọi DAO updateMember.
    @DisplayName("updateMember: password blank -> throws IllegalArgumentException")
    void updateMember_blankPassword_throwsException() {
        MemberService service = MemberService.create(new RecordingMemberDao());
        Member invalid = new Member();
        invalid.setMemberID(1);
        invalid.setUserName("u");
        invalid.setPassword(" ");
        invalid.setEmail("e");
        invalid.setPhone("0123456789");
        assertThrows(IllegalArgumentException.class, () -> service.updateMember(invalid));
    }

    @Test
    // TC-21: memberID hợp lệ nhưng email chỉ chứa khoảng trắng khi cập nhật => Expected output: updateMember ném IllegalArgumentException, không gọi DAO updateMember.
    @DisplayName("updateMember: email blank -> throws IllegalArgumentException")
    void updateMember_blankEmail_throwsException() {
        MemberService service = MemberService.create(new RecordingMemberDao());
        Member invalid = new Member();
        invalid.setMemberID(1);
        invalid.setUserName("u");
        invalid.setPassword("password");
        invalid.setEmail(" ");
        invalid.setPhone("0123456789");
        assertThrows(IllegalArgumentException.class, () -> service.updateMember(invalid));
    }

    @Test
    // TC-22: memberID hợp lệ nhưng phone chỉ chứa khoảng trắng khi cập nhật => Expected output: updateMember ném IllegalArgumentException, không gọi DAO updateMember.
    @DisplayName("updateMember: phone blank -> throws IllegalArgumentException")
    void updateMember_blankPhone_throwsException() {
        MemberService service = MemberService.create(new RecordingMemberDao());
        Member invalid = new Member();
        invalid.setMemberID(1);
        invalid.setUserName("u");
        invalid.setPassword("password");
        invalid.setEmail("e");
        invalid.setPhone(" ");
        assertThrows(IllegalArgumentException.class, () -> service.updateMember(invalid));
    }

    @Test
    // TC-23: memberID hợp lệ nhưng password ngắn hơn 8 ký tự khi cập nhật => Expected output: updateMember ném IllegalArgumentException, không gọi DAO updateMember.
    @DisplayName("updateMember: password shorter than 8 chars -> throws IllegalArgumentException")
    void updateMember_shortPassword_throwsException() {
        MemberService service = MemberService.create(new RecordingMemberDao());
        Member invalid = new Member();
        invalid.setMemberID(1);
        invalid.setUserName("u");
        invalid.setPassword("short");
        invalid.setEmail("e");
        invalid.setPhone("0123456789");
        assertThrows(IllegalArgumentException.class, () -> service.updateMember(invalid));
    }

    @Test
    // TC-24: memberID hợp lệ nhưng phone không đúng 10 ký tự số (có ký tự không phải số) khi cập nhật => Expected output: updateMember ném IllegalArgumentException, không gọi DAO updateMember.
    @DisplayName("updateMember: invalid phone format -> throws IllegalArgumentException")
    void updateMember_invalidPhone_throwsException() {
        MemberService service = MemberService.create(new RecordingMemberDao());
        Member invalid = new Member();
        invalid.setMemberID(1);
        invalid.setUserName("u");
        invalid.setPassword("password");
        invalid.setEmail("e");
        invalid.setPhone("012345678a"); // 10 ký tự nhưng không phải toàn số
        assertThrows(IllegalArgumentException.class, () -> service.updateMember(invalid));
    }

    @Test
    // TC-25: memberID <= 0 khi cập nhật => Expected output: updateMember ném IllegalArgumentException, không gọi DAO updateMember.
    @DisplayName("updateMember: memberID <= 0 -> throws IllegalArgumentException")
    void updateMember_invalidId_throwsException() {
        MemberService service = MemberService.create(new RecordingMemberDao());
        Member invalid = new Member();
        invalid.setMemberID(0);
        invalid.setUserName("u");
        invalid.setPassword("password");
        invalid.setEmail("e");
        invalid.setPhone("0123456789");
        assertThrows(IllegalArgumentException.class, () -> service.updateMember(invalid));
    }

    @Test
    // TC-26: memberID > 0, các trường còn lại hợp lệ và DAO trả true => Expected output: service trả true và truyền đúng đối tượng Member sang DAO.
    @DisplayName("updateMember: valid data -> returns DAO result")
    void updateMember_valid_delegates() {
        RecordingMemberDao dao = new RecordingMemberDao();
        dao.updateResult = true;
        MemberService service = MemberService.create(dao);
        Member member = new Member();
        member.setMemberID(1);
        member.setUserName("u");
        member.setPassword("password");
        member.setEmail("e");
        member.setPhone("0123456789");

        boolean result = service.updateMember(member);

        assertTrue(result);
        assertSame(member, dao.lastMember.get());
    }

    @Test
    // TC-27: memberId <= 0 khi xóa => Expected output: deleteMemberById ném IllegalArgumentException, không gọi DAO.
    @DisplayName("deleteMemberById: memberID <= 0 -> throws IllegalArgumentException")
    void deleteMemberById_invalidId_throwsException() {
        MemberService service = MemberService.create(new RecordingMemberDao());
        assertThrows(IllegalArgumentException.class, () -> service.deleteMemberById(0));
    }

    @Test
    // TC-28: memberId hợp lệ (>0) khi xóa => Expected output: service gọi DAO deleteMemberById với đúng memberId.
    @DisplayName("deleteMemberById: valid ID -> calls DAO")
    void deleteMemberById_valid_callsDao() {
        RecordingMemberDao dao = new RecordingMemberDao();
        MemberService service = MemberService.create(dao);

        service.deleteMemberById(5);

        assertEquals(5, dao.lastId.get());
    }
    @Test
    // TC-29: deleteMemberById với giá trị biên nhỏ nhất hợp lệ (=1) => Expected output: không ném lỗi và DAO được gọi với ID = 1.
    @DisplayName("deleteMemberById: boundary ID 1 -> calls DAO")
    void deleteMemberById_boundary_callsDao() {
        RecordingMemberDao dao = new RecordingMemberDao();
        MemberService service = MemberService.create(dao);

        service.deleteMemberById(1);

        assertEquals(1, dao.lastId.get());
    }

    @Test
    // TC-30: DAO trả về null khi lấy danh sách member => Expected output: service trả về list rỗng (không null).
    @DisplayName("displayMembers: DAO returns null -> returns empty list")
    void displayMembers_null_returnsEmptyList() {
        RecordingMemberDao dao = new RecordingMemberDao();
        dao.membersResult = null;
        MemberService service = MemberService.create(dao);

        List<Member> result = service.displayMembers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    // TC-31: DAO trả về một danh sách Member hợp lệ => Expected output: service trả lại đúng cùng danh sách (cùng reference) từ DAO.
    @DisplayName("displayMembers: DAO returns list -> returns same list")
    void displayMembers_valid_returnsDaoList() {
        RecordingMemberDao dao = new RecordingMemberDao();
        Member m = new Member();
        dao.membersResult = List.of(m);
        MemberService service = MemberService.create(dao);

        List<Member> result = service.displayMembers();

        assertEquals(1, result.size());
        assertSame(m, result.get(0));
    }
    @Test
    // TC-32: DAO trả về list rỗng (không null) => Expected output: displayMembers trả cùng list rỗng (cùng reference).
    @DisplayName("displayMembers: DAO returns empty list -> returns same empty list")
    void displayMembers_emptyList_returnsSameInstance() {
        RecordingMemberDao dao = new RecordingMemberDao();
        dao.membersResult = List.of();
        MemberService service = MemberService.create(dao);

        List<Member> result = service.displayMembers();

        assertTrue(result.isEmpty());
        assertSame(dao.membersResult, result);
    }






}
