package hello.jdbc.repository;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;

class MemberRepositoryV1Test {

    MemberRepositoryV1 repository;

    @BeforeEach
    void beforeEach() throws Exception {
        //기본 DriverManager - 항상 새로운 커넥션 획득
//        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        //커넥션 풀링:
        // HikariProxyConnection -> JdbcConnection
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        repository = new MemberRepositoryV1(dataSource);
    }
    @Test
    void crud() throws SQLException, InterruptedException {
        //create
        Member member = new Member("memberV3", 1000);
        repository.save(member);

        //read
        Member findMember = repository.findById(member.getMemberId());
        assertThat(findMember).isEqualTo(member); //@Data 내부에 @EqualsAndHashCode 있어서 비교 가능

        //update: money: 10000 -> 20000
        repository.update(member.getMemberId(), 20000);
        Member updatedMember = repository.findById(member.getMemberId());
        assertThat(updatedMember.getMoney()).isEqualTo(20000);

        //delete
        repository.delete(member.getMemberId());
        Assertions.assertThatThrownBy(()->repository.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);
        Thread.sleep(1000);
    }
}