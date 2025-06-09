package Library.backend.Login.DAO;


import Library.backend.Login.Model.Member;
import Library.backend.Login.Model.User;
import Library.backend.database.JDBCUtil;
import Library.backend.util.EmailUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MemberDAOImpl implements MemberDAO {

    private static MemberDAOImpl instance;

    private MemberDAOImpl() {
        // Private constructor to prevent instantiation
    }

    public static MemberDAOImpl getInstance() {
        if (instance == null) {
            synchronized (MemberDAOImpl.class) {
                if (instance == null) {
                    instance = new MemberDAOImpl();
                }
            }
        }
        return instance;
    }
    @Override
    public Member getMemberByUserNameAndPassword(String userName, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Member member = null;

        try {

            connection = JDBCUtil.getConnection();
            String query = "SELECT * FROM Members WHERE userName = ? AND password = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                member = new Member();
                member.setMemberID(resultSet.getInt("memberID"));
                member.setUserName(resultSet.getString("userName"));
                member.setPassword(resultSet.getString("password"));
                member.setEmail(resultSet.getString("email"));
                member.setPhone(resultSet.getString("phone"));
                member.setDuty(resultSet.getInt("duty"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return member;
    }

    @Override
    public String getUserNameByID(int memberId) {
        try (Connection connection = JDBCUtil.getConnection()) {
            String query = "SELECT userName FROM Members WHERE memberID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, memberId);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getString("userName");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean createMember(Member member) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = JDBCUtil.getConnection();

            // Check for duplicate userName, email, or phone
            String checkQuery = "SELECT COUNT(*) FROM Members WHERE userName = ? OR email = ? OR phone = ?";
            preparedStatement = connection.prepareStatement(checkQuery);
            preparedStatement.setString(1, member.getUserName());
            preparedStatement.setString(2, member.getEmail());
            preparedStatement.setString(3, member.getPhone());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                return false; // Duplicate found
            }

            // No duplicates, proceed with insert
            String insertQuery = "INSERT INTO Members (userName, password, email, phone, duty) VALUES (?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, member.getUserName());
            preparedStatement.setString(2, member.getPassword());
            preparedStatement.setString(3, member.getEmail());
            preparedStatement.setString(4, member.getPhone());
            preparedStatement.setInt(5, member.getDuty());

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    member.setMemberID(resultSet.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Member getMemberByUsername(String username) {
        try (Connection connection = JDBCUtil.getConnection()) {
            String query = "SELECT * FROM Members WHERE userName = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Member member = new Member();
                member.setMemberID(resultSet.getInt("memberID"));
                member.setUserName(resultSet.getString("userName"));
                member.setPassword(resultSet.getString("password"));
                member.setEmail(resultSet.getString("email"));
                member.setPhone(resultSet.getString("phone"));
                return member;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void updateOtp(Member member) {
        try (Connection connection = JDBCUtil.getConnection()) {
            String sql = "UPDATE members SET otp = ? WHERE email = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, member.getOtp());
            preparedStatement.setString(2, member.getEmail());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getOtpByEmail(String email) {
        try (Connection connection = JDBCUtil.getConnection()) {
            String sql = "SELECT otp FROM members WHERE email = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("otp");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Member getMemberByEmail(String email) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Member member = null;

        try {
            connection = JDBCUtil.getConnection();
            String query = "SELECT * FROM Members WHERE email = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                member = new Member();
                member.setMemberID(resultSet.getInt("memberID"));
                member.setUserName(resultSet.getString("userName"));
                member.setPassword(resultSet.getString("password"));
                member.setEmail(resultSet.getString("email"));
                member.setPhone(resultSet.getString("phone"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return member;
    }
    @Override
    public boolean updateMember(Member member) {
        try (Connection connection = JDBCUtil.getConnection()) {
            // First check if the member exists
            String checkQuery = "SELECT * FROM Members WHERE memberID = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
            checkStmt.setInt(1, member.getMemberID());
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                return false; // Member not found
            }

            // Check for duplicate username or email (excluding current member)
            String duplicateQuery = "SELECT COUNT(*) FROM Members WHERE (userName = ? OR email = ?) AND memberID != ?";
            PreparedStatement duplicateStmt = connection.prepareStatement(duplicateQuery);
            duplicateStmt.setString(1, member.getUserName());
            duplicateStmt.setString(2, member.getEmail());
            duplicateStmt.setInt(3, member.getMemberID());
            ResultSet duplicateRs = duplicateStmt.executeQuery();

            if (duplicateRs.next() && duplicateRs.getInt(1) > 0) {
                return false; // Duplicate username or email found
            }

            // Update member information
            String updateQuery = "UPDATE Members SET userName = ?, password = ?, email = ?, phone = ? WHERE memberID = ?";
            PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
            updateStmt.setString(1, member.getUserName());
            updateStmt.setString(2, member.getPassword());
            updateStmt.setString(3, member.getEmail());
            updateStmt.setString(4, member.getPhone());
            updateStmt.setInt(5, member.getMemberID());

            int rowsUpdated = updateStmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public void deleteMemberById(int memberId) {
        try (Connection connection = JDBCUtil.getConnection()) {
            String sql = "DELETE FROM members WHERE memberID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, memberId);
            int rowsDeleted = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    @Override
    public List<Member> searchMembers(String criteria, String value) {
        List<Member> members = new ArrayList<>();
        String query = "SELECT * FROM Members WHERE " + criteria + " = ?";

        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, value);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Member member = new Member();
                member.setMemberID(resultSet.getInt("memberID"));
                member.setUserName(resultSet.getString("userName"));
                member.setPassword(resultSet.getString("password"));
                member.setEmail(resultSet.getString("email"));
                member.setPhone(resultSet.getString("phone"));
                member.setOtp(resultSet.getString("otp"));
                member.setDuty(resultSet.getInt("duty"));
                members.add(member);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int OTP_LENGTH = 6;

    private String generateOTP() {
        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            int index = (int) (Math.random() * CHARACTERS.length());
            otp.append(CHARACTERS.charAt(index));
        }
        return otp.toString();
    }

    private void sendEmail(String to, String subject, String body) {
        EmailUtil.sendEmail(to, subject, body);
    }

    @Override
    public void forgotPass(String email) {
        String otp = generateOTP();
        sendEmail(email, "OTP", otp);
        Member member = getMemberByEmail(email);
        if (member != null) {
            member.setOtp(otp);
            updateOtp(member);
        }
    }

    @Override
    public boolean checkOTP(String email, String input) {
        Member member = getMemberByEmail(email);
        return member != null && member.getOtp().equals(input);
    }

    @Override
    public boolean changePass(String email, String newPassword) {
        Member member = getMemberByEmail(email);
        if (member != null) {
            member.setPassword(newPassword);
            return updateMember(member);
        }
        return false;
    }
    @Override
    public Member login(String userName, String password) {
        return getMemberByUserNameAndPassword(userName, password);
    }
    @Override
    public List<User> DisplayMembers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM Members WHERE duty = 0";

        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                User user = new User();
                user.setMemberID(resultSet.getInt("memberID"));
                user.setUserName(resultSet.getString("userName"));
                user.setPassword(resultSet.getString("password"));
                user.setEmail(resultSet.getString("email"));
                user.setPhone(resultSet.getString("phone"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

}