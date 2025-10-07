package com.imfine.ngs.game.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 게임 미디어(스크린샷, 비디오) 엔티티 클래스.
 * Steam API에서 크롤링한 미디어 URL을 저장합니다.
 *
 * @author chan
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "game_main_media")
public class GameMainMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(name = "file_url", length = 512, nullable = false)
    private String fileUrl;
}
