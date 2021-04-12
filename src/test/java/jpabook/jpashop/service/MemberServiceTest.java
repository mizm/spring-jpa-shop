package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    @Test
    void 회원가입() {
        //given
        Member member = new Member();
        member.setName("memberA");
        //when
        Long saveId = memberService.join(member);
        //then
        assertEquals(member,memberService.findOne(saveId));
    }

    @Test
    void 중복회원_테스트() {
        Member member1 = new Member();
        member1.setName("kim");
        memberService.join(member1);
        Member member2 = new Member();
        member2.setName("kim");

        assertThrows(IllegalStateException.class,
                ()-> memberService.join(member2));
        //fail("예외가 나와야 한다.");
    }

}