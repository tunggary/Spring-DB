package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Slf4j
@Repository
public class MemberRepositoryV0 {
    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values (?, ?)";
        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
            return member;
        } catch (SQLException e) {
            throw e;
        } finally {
            close(connection, pstmt, null);
        }

    }

    public void close(Connection con, Statement st, ResultSet rs) {
        if (con != null) {
            try{
                con.close();
            } catch (SQLException e) {
                log.error("Connection close error = {}", e);
            }
        }
        if (st != null) {
            try{
                st.close();
            } catch (SQLException e) {
                log.error("PrepareStatement close error = {}", e);
            }
        }

        if (rs != null) {
            try{
                rs.close();
            } catch (SQLException e) {
                log.error("ResultSet close error = {}", e);
            }
        }
    }
    private Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }
}
