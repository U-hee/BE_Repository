package com.imfine.ngs.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name ="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String pwd;

    @Column(nullable = false)
    private String name;

    @Column
    private String nickname;

    public void updatePassword(String newPwd) {
        this.pwd = newPwd;
    }

    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }
}

