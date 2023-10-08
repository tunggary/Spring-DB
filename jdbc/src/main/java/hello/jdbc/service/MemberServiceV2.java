package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceV2 {
    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection con = dataSource.getConnection();
        try {
            con.setAutoCommit(false); //트랜잭션 시작
            bizLogic(con, fromId, toId, money); //비즈니스 로직
            con.commit(); //트랜잭션 성공 커밋
        } catch (Exception e) {
            con.rollback(); //트랜잭션 실패 롤백
            throw new IllegalStateException(e);
        } finally {
            release(con);
        }

    }

    private void bizLogic(Connection con, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(con, fromId);
        Member toMember = memberRepository.findById(con, toId);

        memberRepository.update(con, fromMember.getMemberId(), fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(con, toMember.getMemberId(), toMember.getMoney() + money);
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId() == "ex") {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

    private void release(Connection con) {
        if (con == null) return;
        try {
            con.setAutoCommit(true);
            con.close();
        } catch (Exception e){
            log.error("error", e);
        }
    }
}
