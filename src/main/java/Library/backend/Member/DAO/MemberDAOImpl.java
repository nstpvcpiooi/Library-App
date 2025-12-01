package Library.backend.Member.DAO;


import Library.backend.Member.Model.Member;
import Library.backend.database.DatabaseConnectionException;
import Library.backend.database.JDBCUtil;

import java.sql.*;
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
            throw new DatabaseConnectionException("Không thể kết nối tới cơ sở dữ liệu khi tìm kiếm email.", e);
        }
        return member;
    }
    @Override
    public boolean updateMember(Member member) {
        try (Connection connection = JDBCUtil.getConnection()) {
            String sql = "UPDATE members SET userName = ?, password = ?, email = ?, phone = ? WHERE memberID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, member.getUserName());
            preparedStatement.setString(2, member.getPassword());
            preparedStatement.setString(3, member.getEmail());
            preparedStatement.setString(4, member.getPhone());
            preparedStatement.setInt(5, member.getMemberID());
            int rowsUpdated = preparedStatement.executeUpdate();
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
    public List<Member> DisplayMembers() {
        List<Member> users = new ArrayList<>();
        String query = "SELECT * FROM Members WHERE duty = 0";

        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Member user = new Member();
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
