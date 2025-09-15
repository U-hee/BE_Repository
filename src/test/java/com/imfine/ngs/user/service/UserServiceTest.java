package com.imfine.ngs.user.service;

import com.imfine.ngs.user.entity.User;
import com.imfine.ngs.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

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
    void loginSuccess() {
        userService.signUp("a@b.com", "1234", "1234", "Alice");
        User user = userService.login("a@b.com", "1234");

        assertEquals("a@b.com", user.getEmail());
    }

    @Test
    void loginFailPwdDontMatch() {
        userService.signUp("a@b.com", "1234", "1234", "Hun");

        assertThrows(IllegalArgumentException.class, () -> userService.login("a@b.com", "12345"));
    }

    @Test
    void loginFailEmailDontMatch() {
        assertThrows(IllegalArgumentException.class, () -> userService.login("hi", "1234"));
    }
}

