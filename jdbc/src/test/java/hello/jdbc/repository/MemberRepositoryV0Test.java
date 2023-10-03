package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.*;

class MemberRepositoryV0Test {

    MemberRepositoryV0 repository = new MemberRepositoryV0();
    @Test
    void crud() throws SQLException {
        //create
        Member member = new Member("memberV2", 1000);
        repository.save(member);

        //read
        Member findMember = repository.findById(member.getMemberId());
        assertThat(findMember).isEqualTo(member); //@Data 내부에 @EqualsAndHashCode 있어서 비교 가능

        //update: money: 10000 -> 20000
        repository.update(member.getMemberId(), 20000);
        Member updatedMember = repository.findById(member.getMemberId());
        assertThat(updatedMember.getMoney()).isEqualTo(20000);
    }
}