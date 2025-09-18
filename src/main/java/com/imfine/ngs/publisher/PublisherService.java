package com.imfine.ngs.publisher;

import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PublisherService {

    // 게임 저장소
    private final GameRepository gameRepository;

    // 게임을 등록한다.
    public void registrationGame() {

        // 유효성 검사 : 유저가 publisher인가요? -> 이건 security에서 처리할 수 있지 않나요?
        // 유효성 검사 : 동일한 게임이 이미 저장되어 있나요?
        // 유효성 검사 : Game의 값이 유효한가요?

        // TODO: 게임 조회 로직

        // 게임 생성

        // 게임을 저장한다.
    }

    // 게임을 조회한다.
    public void findByIdGame(Long id) {

        // id가 0인가요?

        // 등록된 게임이 있나요?

        // 게임을 조회한다.
    }
}
