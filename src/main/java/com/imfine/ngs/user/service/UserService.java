package com.imfine.ngs.user.service;

import com.imfine.ngs.user.entity.User;
import com.imfine.ngs.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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

        User user = User.builder()
                .email(email)
                .pwd(pwd)
                .name(name)
                .build();

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
}
