package hello.jdbc.exception.translator;

import hello.jdbc.domain.Member;
import hello.jdbc.exception.MyDBException;
import hello.jdbc.exception.MyDuplicatedKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class ExTranslatorV1Test {
    private Repository repository;
    private Service service;
    @RequiredArgsConstructor
    static class Repository {
        private final DataSource dataSource;

        public Member save(Member member) {
            String sql = "insert into member(member_id, money) values(?, ?)";
            Connection con = null;
            PreparedStatement pstmt = null;
            try {
                con = dataSource.getConnection();
                pstmt = con.prepareStatement(sql);
                pstmt.setString(1, member.getMemberId());
                pstmt.setInt(2, member.getMoney());
                pstmt.executeUpdate();
                return member;
            } catch (SQLException e) {
                if (e.getErrorCode() == 23505) {
                    throw new MyDuplicatedKeyException(e);
                }
                throw new MyDBException(e);
            } finally {
                JdbcUtils.closeConnection(con);
                JdbcUtils.closeStatement(pstmt);
            }
        }
    }

    @Slf4j
    @RequiredArgsConstructor
    static class Service {
        private final Repository repository;
        public void create(String memberId) {
            try {
                repository.save(new Member(memberId, 0));
                log.info("memberId = {}", memberId);
            } catch (MyDuplicatedKeyException e) {
                String newId = generateNewId(memberId);
                repository.save(new Member(newId, 0));
                log.info("retryId = {}", newId);
            }
        }

        private String generateNewId(String memberId) {
            return memberId + new Random().nextInt(100);
        }
    }

    @BeforeEach
    void beforeEach(){
        DataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        repository = new Repository(dataSource);
        service = new Service(repository);
    }

    @Test
    void duplicatedKeyTest(){
        service.create("member1");
        service.create("member2");

    }
}
