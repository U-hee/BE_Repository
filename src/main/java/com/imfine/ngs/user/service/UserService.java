package com.imfine.ngs.user.service;

import com.imfine.ngs.user.dto.response.UserProfileDto;
import com.imfine.ngs.user.entity.User;
import com.imfine.ngs.user.oauth.client.OauthClient;
import com.imfine.ngs.user.oauth.dto.OauthUserInfo;
import com.imfine.ngs.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final OauthClient oauthClient;

    // 회원가입
    public Long signUp(String email, String pwd, String pwdCheck, String name) {
        if (email == null || pwd == null || pwdCheck == null || name == null) {
            throw new IllegalArgumentException("필수 입력값 누락");
        }
        if (!pwd.equals(pwdCheck)) {
            throw new IllegalArgumentException("비밀번호 불일치");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이메일 이미 존재");
        }

        User user = User.create(email, pwd, name, null);

        return userRepository.save(user).getId();
    }

    // 로그인
    public User signIn(String email, String pw) {
        if (email == null || pw == null) {
            throw new IllegalArgumentException("아이디/비밀번호 미입력");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디"));
        if (!user.getPwd().equals(pw)) {
            throw new IllegalArgumentException("비밀번호 불일치");
        }
        return user;
    }

    public void updatePwd(String email, String oldPwd, String newPwd) {
        if (oldPwd == null || newPwd == null) {
            throw new IllegalArgumentException("이전 비밀번호/새로운 비밀번호 미입력");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디"));
        if (!user.getPwd().equals(oldPwd)) {
            throw new IllegalArgumentException("이전 비밀번호와 동일 하지않음");
        }

        user.updatePassword(newPwd);
        userRepository.save(user);
    }

    public void updateNickname(String email, String newNickname) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디"));

        user.updateNickname(newNickname);
        userRepository.save(user);
    }

    public UserProfileDto getUserProfile(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디"));

        return new UserProfileDto(user.getEmail(), user.getNickname());
    }

    public void deleteUser(String emaill) {
        User user = userRepository.findByEmail(emaill).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디"));

        userRepository.delete(user);
    }

    /**
     * 소셜 로그인 (테스트용 단일 메서드)
     * @param provider   "google", "kakao", "naver" 등 공급자 이름
     * @param accessToken 소셜 서버에서 받은 액세스 토큰
     */
    public User socialLogin(String provider, String accessToken) {
        // 1. 액세스 토큰으로 사용자 정보 가져오기
        OauthUserInfo userInfo = oauthClient.getUserInfo(provider, accessToken);

        // 2. 이메일로 기존 회원 조회
        return userRepository.findByEmail(userInfo.getEmail())
                .orElseGet(() -> {
                    // 3. 없으면 새 회원 생성
                    User newUser = User.create(
                            userInfo.getEmail(),
                            "SOCIAL", // 임시 pwd (실제 로그인엔 사용 안 됨)
                            userInfo.getName(),
                            null
                    );
                    return userRepository.save(newUser);
                });
    }





}
