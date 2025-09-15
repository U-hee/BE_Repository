package com.imfine.ngs.support.unitH2;

import com.imfine.ngs.support.entity.Support;
import com.imfine.ngs.support.repository.SupportRepo;
import com.imfine.ngs.support.service.SupportService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class SupportH2Test {

    @Autowired
    private SupportRepo supportRepo;

    @Autowired
    private SupportService supportService;

    @Test
    @DisplayName("고객센터에서 문의작성이 가능하다.")
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
        assertThat(saved.getId()).isNotNull();

        Support find = supportRepo.findById(saved.getId()).orElseThrow();
        assertThat(find.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("User별 문의조회 가능하다.")
    void findByUserId() {
        long userId = 1L;

        List<Support> s = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            s.add(Support.builder()
                    .userId(1L)
                    .orderId(5L)
                    .categoryId(1L)
                    .content("조회 테스트")
                    .createdAt(LocalDateTime.now())
                    .build()
            );
            supportRepo.save(s.get(i));

        }


        List<Support> supports = supportService.findSupportByUserId(userId);
        assertThat(supports).isNotEmpty();
        supports.forEach(support -> assertThat(support.getUserId()).isEqualTo(userId));
    }

    @Test
    @DisplayName("Category별 문의조회 가능하다.")
    void findByCategoryId() {
        long categoryId = 1L;

        for (int i = 0; i < 5; i++) {
            Support s = Support.builder()
                    .userId(2L)
                    .orderId(5L)
                    .categoryId(1L)
                    .content("조회 테스트" + i)
                    .build();
            supportRepo.save(s);
            System.out.println("Saved id=" + s.getId() + ", categoryId=" + s.getCategoryId());

        }


        List<Support> supports = supportService.findSupportByCategoryId(categoryId);
        assertThat(supports).isNotEmpty();
        supports.forEach(support -> assertThat(support.getCategoryId()).isEqualTo(categoryId));
    }
}
