package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepository;
import hello.jdbc.repository.MemberRepositoryV3;
import hello.jdbc.repository.MemberRepositoryV4_1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceV4 {
    private final MemberRepository memberRepository;

    @Transactional
    public void accountTransfer(String fromId, String toId, int money) {
        //비즈니스 로직
        bizLogic(fromId, toId, money);
    }

    private void bizLogic(String fromId, String toId, int money) {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromMember.getMemberId(), fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toMember.getMemberId(), toMember.getMoney() + money);
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId() == "ex") {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}
