package com.imfine.ngs.user.service;

import com.imfine.ngs.user.entity.User;
import com.imfine.ngs.user.oauth.client.OauthClient;
import com.imfine.ngs.user.oauth.dto.OauthUserInfo;
import com.imfine.ngs.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserServiceSocialTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OauthClient oauthClient;

    @Test
    void socialSignUpNewUser() {
        OauthUserInfo info = new OauthUserInfo("new@b.com", "NewUser");

        given(oauthClient.getUserInfo("google", "token"))
                .willReturn(info);
        given(userRepository.findByEmail("new@b.com"))
                .willReturn(Optional.empty());
        given(userRepository.save(any(User.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        User user = userService.socialLogin("google", "token");

        // then
        assertEquals("new@b.com", user.getEmail());
        assertEquals("NewUser", user.getNickname());

    }

    @Test
    void socialLogin() {

    }
}
