package com.imfine.ngs.user.service;

import com.imfine.ngs.user.dto.response.UserProfileDto;
import com.imfine.ngs.user.entity.User;
import com.imfine.ngs.user.oauth.client.OauthClient;
import com.imfine.ngs.user.oauth.dto.OauthUserInfo;
import com.imfine.ngs.user.repository.UserRepository;
import jakarta.transaction.Transactional;
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
    void signUpSuccess() {
        Long id = userService.signUp("a@b.com", "1234", "1234", "Hun");
        assertNotNull(id);
    }

    @Test
    void signUpFailPwdDontMatch() {
        assertThrows(IllegalArgumentException.class, () -> userService.signUp("a@b.com", "12345", "1234", "Hun" ));
    }

    @Test
    void signUpFailDuplicateEmail() {
        userService.signUp("a@b.com", "1234", "1234", "Hun");

       assertThrows(IllegalArgumentException.class, () -> userService.signUp("a@b.com", "1234", "1234", "Hun" ));
    }

    @Test
    void signInSuccess() {
        userService.signUp("a@b.com", "1234", "1234", "Hun");
        User user = userService.signIn("a@b.com", "1234");

        assertEquals("a@b.com", user.getEmail());
    }

    @Test
    void signInFailPwdDontMatch() {
        userService.signUp("a@b.com", "1234", "1234", "Hun");

        assertThrows(IllegalArgumentException.class, () -> userService.signIn("a@b.com", "12345"));
    }

    @Test
    void signInFailEmailDontMatch() {
        assertThrows(IllegalArgumentException.class, () -> userService.signIn("hi", "1234"));
    }

    @Test
    void updatePwdSuccess() {
        userService.signUp("a@b.com", "1234", "1234", "Hun");
        User user = userService.signIn("a@b.com", "1234");

        userService.updatePwd(user.getEmail(), "1234", "5678");
        assertEquals("5678", user.getPwd());
    }

    @Test
    void updatePwdFailPwdDontMatch() {
        userService.signUp("a@b.com", "1234", "1234", "Hun");
        assertThrows(IllegalArgumentException.class, () ->
                userService.updatePwd("a@b.com", "wrongOldPw", "5678")
        );
    }

    @Test
    void updateNicknameSuccess() {
        userService.signUp("a@b.com", "1234", "1234", "Hun");

        userService.updateNickname("a@b.com", "NewHun");

        User user = userRepository.findByEmail("a@b.com").orElseThrow();
        assertEquals("NewHun", user.getNickname());
    }

    @Test
    void userDetailSuccess() {
        userService.signUp("a@b.com", "1234", "1234", "Hun");

        UserProfileDto profile = userService.getUserProfile("a@b.com");
        assertEquals("a@b.com", profile.getEmail());
        assertEquals("Hun", profile.getNickname());
    }

    void userDetailFail() {
        userService.signUp("a@b.com", "1234", "1234", "Hun");
        assertThrows(IllegalArgumentException.class, () -> userService.getUserProfile("c@d.com"));
    }

    @Test
    void deleteUserSuccess() {
        userService.signUp("a@b.com", "1234","1234", "Hun");

        userService.deleteUser("a@b.com");

        assertFalse(userRepository.findByEmail("a@b.com").isPresent());
    }

    @Test
    void deleteUserFail() {
        userService.signUp("a@b.com", "1234", "1234", "Hun");

        assertThrows(IllegalArgumentException.class, () -> userService.deleteUser("c@d.com"));
    }


}
