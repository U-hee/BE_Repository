package com.imfine.ngs.support.integration;

import com.imfine.ngs.support.controller.SupportController;
import com.imfine.ngs.support.entity.Support;
import com.imfine.ngs.support.repository.SupportRepository;
import com.imfine.ngs.support.service.SupportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class SupportControllerTest {

    @Autowired
    private SupportController supportController;

    @Autowired
    private SupportService supportService;

    @Autowired
    SupportRepository supportRepository;



    /**
     * 음..어떤 기능이 들어가야할까?
     * 1. 서포트 메인화면
     *  1.1. 따로 줘야할 기능은 없음.
     * 2. 서포트 메인화면 내 각 상세문의
     *  2.1. GAME(GET/POST)
     *    2.1.1. 로그인 한 유저의 게임목록 불러오기
     *  2.2. REFUND(GET/POST)
     *    2.2.1. 로그인 한 유저의 게임목록 불러오기
     *  2.3. INQUIRIES(GET/POST)
     *    2.3.1. 따로 보내줘야할 건 없음
     *  2.4. ETC(GET/POST)
     *    2.4.1. 따로 보내줘야할 건 없음
     */

    @DisplayName("create: 문의 생성에 성공한다.")
    @Test
    public void create() {
        //given
        final long userId = 1L;

        //given : 준비
        Support support = Support.builder()
                .userId(1L)
                .orderId(1L)
                .categoryId(2L)
                .content("test 입력")
                .createdAt(LocalDateTime.now())
                .build();

        //when : 실행
        Support saved = supportService.insertSupportRepo(support);

        //then : 검증
        assertThat(saved).isNotNull();

        Support find = supportRepository.findById(saved.getId()).orElseThrow();
        assertThat(find.getId()).isEqualTo(11L);


        //when


        //then
    }

}
