package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepository;
import hello.jdbc.repository.MemberRepositoryV5;
import hello.jdbc.repository.MemberRepositoryV6;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestConstructor;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class MemberServiceV6Test {

    @TestConfiguration
    static class TestConfig {

        private DataSource dataSource;

        public TestConfig(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Bean
        MemberRepository memberRepository() {
            return new MemberRepositoryV6(dataSource);
        }

        @Bean
        MemberServiceV4 memberService() {
            return new MemberServiceV4(memberRepository());
        }
    }

    public static String MEMBER_A = "memberA";
    public static String MEMBER_B = "memberB";
    public static String MEMBER_EX = "ex";


    private final MemberRepository memberRepository;
    private final MemberServiceV4 memberService;

    @AfterEach
    void afterEach(){
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);
    }
    @Test
    @DisplayName("정상 이체")
    void accountTransfer(){
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        log.info("Transaction Start");
        memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);
        log.info("Transaction End");

        Member findMemberA = memberRepository.findById(MEMBER_A);
        Member findMemberB = memberRepository.findById(MEMBER_B);
        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberB.getMoney()).isEqualTo(12000);
    }

    @Test
    @DisplayName("이체 실패")
    void accountTransferEx(){
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberEx = new Member(MEMBER_EX, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberEx);

        log.info("Transaction Start");
        assertThatThrownBy(()->memberService.accountTransfer(memberA.getMemberId(), memberEx.getMemberId(), 2000))
                .isInstanceOf(IllegalStateException.class);
        log.info("Transaction End");

        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberEx = memberRepository.findById(memberEx.getMemberId());
        //memberA의 돈만 2000원 줄었고, ex의 돈은 10000원 그대로이다.
        assertThat(findMemberA.getMoney()).isEqualTo(10000);
        assertThat(findMemberEx.getMoney()).isEqualTo(10000);
    }
}