package com.imfine.ngs.support.integration;

import com.imfine.ngs.support.entity.Support;
import com.imfine.ngs.support.entity.SupportAnswer;
import com.imfine.ngs.support.entity.SupportCategory;
import com.imfine.ngs.support.repository.SupportAnswerRepository;
import com.imfine.ngs.support.repository.SupportCategoryRepository;
import com.imfine.ngs.support.repository.SupportRepository;
import com.imfine.ngs.support.service.SupportAnswerService;
import com.imfine.ngs.support.service.SupportCategoryService;
import com.imfine.ngs.support.service.SupportService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SupportServiceTest {

    // repo
    @Autowired
    private SupportRepository supportRepository;
    @Autowired
    private SupportCategoryRepository supportCategoryRepository;
    @Autowired
    private SupportAnswerRepository supportAnswerRepository;

    // service
    @Autowired
    private SupportService supportService;
    @Autowired
    private SupportCategoryService supportCategoryService;
    @Autowired
    private SupportAnswerService supportAnswerService;

    @BeforeEach
    void setUp() {
        SupportTestData supportTestData = new SupportTestData();
        supportTestData.createCategoryRepo();
        supportTestData.createSupportRepo();
        supportTestData.createSupportAnswerRepo();
    }

    @Test
    @DisplayName("(USER) 고객센터에서 문의작성이 가능하다.")
    void insertSupportRepo() {
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
    }


    @Test
    @DisplayName("(USER, ADM) User 별 문의조회 가능하다.")
    void findByUserId() {
        //given
        long userId = 1L;

        //when
        List<Support> supports = supportService.findSupportByUserId(userId);

        //then
        assertThat(supports).isNotEmpty();
        supports.forEach(support -> assertThat(support.getUserId()).isEqualTo(userId));
    }

    @Test
    @DisplayName("(USER, ADM) Category별 문의조회 가능하다.")
    void findByCategoryId() {
        //given
        SupportCategory supportCategory = supportCategoryService.findByName("REFUND");

        supportCategoryRepository.findAll().forEach(System.out::println);

        //when
        List<Support> supports = supportService.findSupportByCategoryId(supportCategory.getId());

        //then
        assertThat(supports).isNotEmpty();
        supports.forEach(support -> assertThat(support.getCategoryId()).isEqualTo(supportCategory.getId()));
    }


    @Test
    @DisplayName("(ADM) 문의 답변이 가능하다.")
    void insertSupportAnswer() {
        // given
        // @BeforeEach를 통한 사용자 문의글 생성완료

        //when
        SupportAnswer supportAnswer = SupportAnswer.builder()
                .supportId(2L)
                .content("답변 완료했습니다.")
                .build();
        supportAnswerRepository.save(supportAnswer);

        //then
        assertThat(supportAnswer).isNotNull();
        assertThat(supportAnswer.getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("(USER, ADM) 유저 별 답변여부로 문의 분류가 가능하다.")
    void findSupportByIsAnswer() {
        // given
        //세션에서 userid를 가져온다고 가정
        long userId = 1L;

        // when
        List<Support> supports = supportRepository.findByUserId(1L);

        //then
        supports.forEach(support -> {
            if (supportAnswerService.isAnswer(support.getId())) {
                System.out.println(support.getId() + "COMPLETED");
            } else {
                System.out.println(support.getId() + "PENDING");
            }
        });
    }

    @Test
    @DisplayName("(USER, ADM) 날짜 오래된순으로 정렬이 가능하다.")
    void sortByDateASC() {
        //given
        long userId = 1L;

        //when
        List<Support> supports = supportService.findSupportByUserId(userId);
        List<Support> sortedASCSupportsByUserID = supportService.sortByDate("ASC", supports);

        //then
        assertThat(sortedASCSupportsByUserID).isNotEmpty();
        assertThat(sortedASCSupportsByUserID).extracting(Support::getCreatedAt)
                .isSorted();
    }

    @Test
    @DisplayName("(USER, ADM) 날짜 최신순으로 정렬이 가능하다.")
    void sortByDateDESC() {
        //given
        long userId = 1L;

        //when
        List<Support> supports = supportService.findSupportByUserId(userId);
        List<Support> sortedDESCSupportsByUserID = supportService.sortByDate("DESC", supports);

        //then
        assertThat(sortedDESCSupportsByUserID).isNotEmpty();
        assertThat(sortedDESCSupportsByUserID).extracting(Support::getCreatedAt)
                .isSortedAccordingTo(Comparator.reverseOrder());
    }

    @Test
    @DisplayName("(USER) 문의글 상세조회")
    void findBySupportId() {
        //given
        long supportId = 1L;

        //when
        Support support = supportService.findById(supportId);

        //then
        assertThat(support).isNotNull();
        assertThat(support.getUserId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("(ADM) 문의 답변 상세조회")
    void findByAnswer() {
        //given
        long supportId = 8L;

        //when
        SupportAnswer supportAnswer = supportAnswerService.findBySupportId(supportId);

        //then
        assertThat(supportAnswer).isNotNull();
    }

}
