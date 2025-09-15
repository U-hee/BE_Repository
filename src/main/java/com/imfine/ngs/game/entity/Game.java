package com.imfine.ngs.game.entity;

import com.imfine.ngs.game.enums.Env;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

/**
 * 게임(Game) 엔티티 클래스.
 * 현재는 테스트코드를 위해 간소화 된 상태이다.
 *
 * @author chan
 */

@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 게임 식별 아이디

    @Column(nullable = false)
    private String name; // 게임 이름

    @Column(nullable = false)
    private Long price; // 게임 가격

    @Column(nullable = false)
    private String env; // 게임 OS
    private String tag; // 게임 태그

    @CreatedDate
    private LocalDateTime createdAt; // 게임 등록 날짜
}
