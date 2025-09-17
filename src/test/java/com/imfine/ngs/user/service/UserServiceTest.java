package com.imfine.ngs.user.service;

import com.imfine.ngs.user.dto.response.UserProfileDto;
import com.imfine.ngs.user.entity.User;
import com.imfine.ngs.user.oauth.client.OauthClient;
import com.imfine.ngs.user.oauth.dto.OauthUserInfo;
import com.imfine.ngs.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository UserRepository;
    @Autowired
    private UserRepository userRepository;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public OauthClient oauthClient() {
            // 테스트용 더미 객체 반환
            return (provider, token) -> new OauthUserInfo("dummy@test.com", "Dummy");
        }
    }


    @Test
    @DisplayName("회원가입 성공")
    void signUpSuccess() {
        Long id = userService.signUp("a@b.com", "1234", "1234", "Hun");
        assertNotNull(id);
    }

    @Test
    @DisplayName("회원가입 실패 - 비밀번호 불일치")
    void signUpFailPwdDontMatch() {
        assertThrows(IllegalArgumentException.class, () -> userService.signUp("a@b.com", "12345", "1234", "Hun" ));
    }

    @Test
    @DisplayName("회원가입 실패 - 중복 이메일")
    void signUpFailDuplicateEmail() {
        userService.signUp("a@b.com", "1234", "1234", "Hun");

       assertThrows(IllegalArgumentException.class, () -> userService.signUp("a@b.com", "1234", "1234", "Hun" ));
    }

    @Test
    @DisplayName("로그인 성공")
    void signInSuccess() {
        userService.signUp("a@b.com", "1234", "1234", "Hun");
        User user = userService.signIn("a@b.com", "1234");

        assertEquals("a@b.com", user.getEmail());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void signInFailPwdDontMatch() {
        userService.signUp("a@b.com", "1234", "1234", "Hun");

        assertThrows(IllegalArgumentException.class, () -> userService.signIn("a@b.com", "12345"));
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 이메일")
    void signInFailEmailDontMatch() {
        assertThrows(IllegalArgumentException.class, () -> userService.signIn("hi", "1234"));
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void updatePwdSuccess() {
        userService.signUp("a@b.com", "1234", "1234", "Hun");
        User user = userService.signIn("a@b.com", "1234");

        userService.updatePwd(user.getEmail(), "1234", "5678");
        assertEquals("5678", user.getPwd());
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 이전 비밀번호 불일치")
    void updatePwdFailPwdDontMatch() {
        userService.signUp("a@b.com", "1234", "1234", "Hun");
        assertThrows(IllegalArgumentException.class, () ->
                userService.updatePwd("a@b.com", "wrongOldPw", "5678")
        );
    }

    @Test
    @DisplayName("닉네임 변경 성공")
    void updateNicknameSuccess() {
        userService.signUp("a@b.com", "1234", "1234", "Hun");

        userService.updateNickname("a@b.com", "NewHun");

        User user = userRepository.findByEmail("a@b.com").orElseThrow();
        assertEquals("NewHun", user.getNickname());
    }

    @Test
    @DisplayName("프로필 조회 성공")
    void userDetailSuccess() {
        userService.signUp("a@b.com", "1234", "1234", "Hun");

        UserProfileDto profile = userService.getUserProfile("a@b.com");
        assertEquals("a@b.com", profile.getEmail());
        assertEquals("Hun", profile.getNickname());
    }

    @Test
    @DisplayName("프로필 조회 실패")
    void userDetailFail() {
        userService.signUp("a@b.com", "1234", "1234", "Hun");
        assertThrows(IllegalArgumentException.class, () -> userService.getUserProfile("c@d.com"));
    }

    @Test
    @DisplayName("회원 삭제 성공")
    void deleteUserSuccess() {
        userService.signUp("a@b.com", "1234","1234", "Hun");

        userService.deleteUser("a@b.com");

        assertFalse(userRepository.findByEmail("a@b.com").isPresent());
    }

    @Test
    @DisplayName("회원 삭제 실패 - 존재하지 않는 이메일")
    void deleteUserFail() {
        userService.signUp("a@b.com", "1234", "1234", "Hun");

        assertThrows(IllegalArgumentException.class, () -> userService.deleteUser("c@d.com"));
    }


}
