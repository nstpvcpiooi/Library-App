package Library.backend.Member.Test;

import Library.backend.Member.DAO.MemberDAOImpl;
import Library.backend.Member.Model.Member;
import Library.backend.database.JDBCUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Sử dụng Phân hoạch tương đương kiểm thử đơn vị cho các unit trong MemberDAOImpl.
public class MemberDAOTest {

    private static final String H2_URL = "jdbc:h2:mem:library_member;MODE=MySQL;DB_CLOSE_DELAY=-1";
    private static final String CREATE_MEMBERS_TABLE = """
            CREATE TABLE IF NOT EXISTS Members (
                memberID INT AUTO_INCREMENT PRIMARY KEY,
                userName VARCHAR(255),
                password VARCHAR(255),
                email VARCHAR(255),
                phone VARCHAR(20),
                duty INT
            )
            """;

    private MemberDAOImpl dao;

    @BeforeAll
    static void initDatabase() throws SQLException {
        try (Connection con = DriverManager.getConnection(H2_URL);
             Statement st = con.createStatement()) {
            st.execute(CREATE_MEMBERS_TABLE);
        }
        JDBCUtil.setConnectionSupplier(() -> {
            try {
                return DriverManager.getConnection(H2_URL);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @AfterAll
    static void resetConnection() {
        JDBCUtil.resetConnectionSupplier();
    }

    @BeforeEach
    void resetState() throws SQLException {
        dao = MemberDAOImpl.getInstance();
        try (Connection con = DriverManager.getConnection(H2_URL);
             Statement st = con.createStatement()) {
            st.execute("TRUNCATE TABLE Members");
        }
    }

    @Test
    // TC-01: truyền Member với đầy đủ thông tin hợp lệ vào createMember => Expected output: insert thành công (trả true), gán memberID tự tăng và có thể đọc lại Member đúng từ DB.
    @DisplayName("createMember: valid data -> returns true and assigns ID")
    void createMember_valid_insertsRow() {
        Member member = buildMember("u1", "password", "u1@mail.com", "0123456789", 0);

        boolean result = dao.createMember(member);

        assertTrue(result);
        assertTrue(member.getMemberID() > 0);
        Member stored = dao.getMemberByUsername("u1");
        assertNotNull(stored);
        assertEquals("u1@mail.com", stored.getEmail());
    }

    @Test
    // TC-02: truyền tham số member = null vào createMember => Expected output: DAO ném NullPointerException (không tự validate đầu vào).
    @DisplayName("createMember: null member -> throws NullPointerException")
    void createMember_null_throwsNullPointer() {
        assertThrows(NullPointerException.class, () -> dao.createMember(null));
    }

    @Test
    // TC-03: gọi getMemberByUsername với username đã tồn tại trong bảng => Expected output: trả về đối tượng Member tương ứng, có dữ liệu (email) khớp với bản ghi đã insert.
    @DisplayName("getMemberByUsername: existing username -> returns Member")
    void getMemberByUsername_existing_returnsMember() {
        insertMember(buildMember("userA", "pass", "a@mail.com", "0123456789", 0));

        Member found = dao.getMemberByUsername("userA");

        assertNotNull(found);
        assertEquals("a@mail.com", found.getEmail());
    }

    @Test
    // TC-04: gọi getMemberByUsername với username chưa tồn tại trong bảng => Expected output: trả null (không tìm thấy).
    @DisplayName("getMemberByUsername: missing username -> returns null")
    void getMemberByUsername_missing_returnsNull() {
        assertNull(dao.getMemberByUsername("missing"));
    }
    @Test
    // TC-05: gọi getMemberByUsername với chuỗi blank => Expected output: trả null vì không khớp bản ghi nào.
    @DisplayName("getMemberByUsername: blank username -> returns null")
    void getMemberByUsername_blank_returnsNull() {
        assertNull(dao.getMemberByUsername("   "));
    }
    @Test
    // TC-06: gọi getMemberByEmail với email đã tồn tại trong bảng => Expected output: trả về đối tượng Member tương ứng, có username khớp với bản ghi đã insert.
    @DisplayName("getMemberByEmail: existing email -> returns Member")
    void getMemberByEmail_existing_returnsMember() {
        insertMember(buildMember("userA", "pass", "a@mail.com", "0123456789", 0));

        Member found = dao.getMemberByEmail("a@mail.com");

        assertNotNull(found);
        assertEquals("userA", found.getUserName());
    }

    @Test
    // TC-07: gọi getMemberByEmail với email chưa tồn tại trong bảng => Expected output: trả null (không tìm thấy).
    @DisplayName("getMemberByEmail: missing email -> returns null")
    void getMemberByEmail_missing_returnsNull() {
        assertNull(dao.getMemberByEmail("missing@mail.com"));
    }
    @Test
    // TC-08: gọi getMemberByEmail với chuỗi blank => Expected output: trả null.
    @DisplayName("getMemberByEmail: blank email -> returns null")
    void getMemberByEmail_blank_returnsNull() {
        assertNull(dao.getMemberByEmail(" "));
    }
    @Test
    // TC-09: username/password khớp đúng bản ghi trong bảng => Expected output: getMemberByUserNameAndPassword trả về Member tương ứng.
    @DisplayName("getMemberByUserNameAndPassword: valid credentials -> returns Member")
    void getMemberByUserNameAndPassword_match_returnsMember() {
        insertMember(buildMember("userA", "secret", "a@mail.com", "0123456789", 0));

        Member found = dao.getMemberByUserNameAndPassword("userA", "secret");

        assertNotNull(found);
        assertEquals("a@mail.com", found.getEmail());
    }

    @Test
    // TC-10: username đúng nhưng password sai => Expected output: getMemberByUserNameAndPassword trả null (không đăng nhập được).
    @DisplayName("getMemberByUserNameAndPassword: wrong password -> returns null")
    void getMemberByUserNameAndPassword_wrong_returnsNull() {
        insertMember(buildMember("userA", "secret", "a@mail.com", "0123456789", 0));

        assertNull(dao.getMemberByUserNameAndPassword("userA", "wrong"));
    }
    @Test
    // TC-11: username null khi đăng nhập => Expected output: getMemberByUserNameAndPassword trả null (không lỗi).
    @DisplayName("getMemberByUserNameAndPassword: null username -> returns null")
    void getMemberByUserNameAndPassword_nullUsername_returnsNull() {
        assertNull(dao.getMemberByUserNameAndPassword(null, "pass"));
    }
    @Test
    // TC-12: cập nhật một Member đã tồn tại (memberID hợp lệ) => Expected output: updateMember trả true và các trường email/phone trong DB được cập nhật theo giá trị mới.
    @DisplayName("updateMember: existing member -> updates fields")
    void updateMember_existing_updatesRow() {
        Member member = insertMember(buildMember("userA", "secret", "a@mail.com", "0123456789", 0));
        member.setEmail("new@mail.com");
        member.setPhone("0987654321");

        boolean result = dao.updateMember(member);

        assertTrue(result);
        Member stored = dao.getMemberByUsername("userA");
        assertEquals("new@mail.com", stored.getEmail());
        assertEquals("0987654321", stored.getPhone());
    }

    @Test
    // TC-13: cập nhật Member với memberID không tồn tại trong bảng => Expected output: updateMember trả false, không có bản ghi nào bị thay đổi.
    @DisplayName("updateMember: missing member -> returns false")
    void updateMember_missing_returnsFalse() {
        Member member = buildMember("userA", "secret", "a@mail.com", "0123456789", 0);
        member.setMemberID(999);

        boolean result = dao.updateMember(member);

        assertFalse(result);
    }
    @Test
    // TC-14: updateMember với memberID chưa gán (mặc định 0) => Expected output: updateMember trả false, không có bản ghi nào bị ảnh hưởng.
    @DisplayName("updateMember: memberID not set -> returns false")
    void updateMember_idNotSet_returnsFalse() {
        Member member = buildMember("userC", "secret", "c@mail.com", "0222222222", 0);

        assertFalse(dao.updateMember(member));
    }
    @Test
    // TC-15: xóa Member bằng memberID tồn tại trong bảng => Expected output: bản ghi tương ứng bị xóa, getMemberByUsername sau đó trả null.
    @DisplayName("deleteMemberById: existing ID -> deletes record")
    void deleteMemberById_existing_deletesRow() {
        Member member = insertMember(buildMember("userA", "secret", "a@mail.com", "0123456789", 0));

        dao.deleteMemberById(member.getMemberID());

        assertNull(dao.getMemberByUsername("userA"));
    }

    @Test
    // TC-16: xóa Member với memberID không tồn tại trong bảng => Expected output: không ném exception và dữ liệu các Member hiện có không thay đổi.
    @DisplayName("deleteMemberById: missing ID -> leaves data unchanged")
    void deleteMemberById_missing_noChange() {
        insertMember(buildMember("userA", "secret", "a@mail.com", "0123456789", 0));

        dao.deleteMemberById(999);

        assertNotNull(dao.getMemberByUsername("userA"));
    }
    @Test
    // TC-17: deleteMemberById với ID âm => Expected output: không ném lỗi và các bản ghi hiện có vẫn giữ nguyên.
    @DisplayName("deleteMemberById: negative ID -> no change")
    void deleteMemberById_negative_noChange() {
        Member member = insertMember(buildMember("userD", "secret", "d@mail.com", "0333333333", 0));

        dao.deleteMemberById(-5);

        assertNotNull(dao.getMemberByUsername(member.getUserName()));
    }
    @Test
    // TC-18: bảng có cả member duty = 0 và duty = 1 => Expected output: DisplayMembers chỉ trả về danh sách các member có duty = 0 (lọc bỏ admin).
    @DisplayName("DisplayMembers: returns only members with duty = 0")
    void displayMembers_filtersDutyZero() {
        insertMember(buildMember("userA", "secret", "a@mail.com", "0123456789", 0));
        insertMember(buildMember("userB", "secret", "b@mail.com", "0987654321", 1));

        List<Member> members = dao.DisplayMembers();

        assertEquals(1, members.size());
        assertEquals("userA", members.get(0).getUserName());
    }

    @Test
    // TC-19: bảng Members rỗng (không có bản ghi) => Expected output: DisplayMembers trả về list rỗng.
    @DisplayName("DisplayMembers: empty table -> returns empty list")
    void displayMembers_empty_returnsEmptyList() {
        assertTrue(dao.DisplayMembers().isEmpty());
    }
    @Test
    // TC-20: DisplayMembers có nhiều record duty = 0 => Expected output: trả về đủ tất cả member duty = 0, giữ đúng thứ tự insert.
    @DisplayName("DisplayMembers: multiple duty=0 rows -> returns all")
    void displayMembers_multipleUsers_returnsAllPatrons() {
        insertMember(buildMember("patron1", "secret", "p1@mail.com", "0444444444", 0));
        insertMember(buildMember("patron2", "secret", "p2@mail.com", "0555555555", 0));
        insertMember(buildMember("admin", "secret", "admin@mail.com", "0666666666", 1));

        List<Member> patrons = dao.DisplayMembers();

        assertEquals(2, patrons.size());
        assertEquals("patron1", patrons.get(0).getUserName());
        assertEquals("patron2", patrons.get(1).getUserName());
    }
    @Test
    // TC-21: tạo member trùng username/email/phone => Expected output: createMember trả false (phát hiện trùng lặp), memberID không được gán mới.
    @DisplayName("createMember: duplicate username/email -> returns false and no new ID")
    void createMember_duplicate_returnsFalse() {
        Member first = buildMember("dupUser", "secret", "dup@mail.com", "0111111111", 0);
        Member duplicate = buildMember("dupUser", "secret2", "dup@mail.com", "0111111111", 0);
        assertTrue(dao.createMember(first));

        boolean result = dao.createMember(duplicate);

        assertFalse(result);
        assertEquals(0, duplicate.getMemberID());
    }


    private Member insertMember(Member member) {
        boolean ok = dao.createMember(member);
        assertTrue(ok, "Insert that should succeed failed");
        return member;
    }

    private static Member buildMember(String username, String password, String email, String phone, int duty) {
        Member m = new Member();
        m.setUserName(username);
        m.setPassword(password);
        m.setEmail(email);
        m.setPhone(phone);
        m.setDuty(duty);
        return m;
    }
}
