package com.imfine.ngs.user.service;

import com.imfine.ngs.user.entity.User;
import com.imfine.ngs.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password="
})
class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository UserRepository;
    @Autowired
    private UserRepository userRepository;

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


}