package com.imfine.ngs.game.service.support;

import com.imfine.ngs.game.dto.mapper.GameCardMapper;
import com.imfine.ngs.game.dto.request.GameTagRequest;
import com.imfine.ngs.game.dto.response.GameCardResponse;
import com.imfine.ngs.game.dto.response.GameTagResponse;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.tag.GameTag;
import com.imfine.ngs.game.enums.GameStatusType;
import com.imfine.ngs.game.enums.GameTagType;
import com.imfine.ngs.game.repository.tag.GameTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameTagService {

    private final GameTagRepository gameTagRepository;

    public void createTag(GameTagRequest gameTagRequest) {
        gameTagRepository.save(GameTag.builder()
                .tagType(gameTagRequest.getGameTagType())
                .build());
    }

    public GameTag findByTagId(long tagId) {
        return gameTagRepository.findById(tagId)
                .orElseThrow(() -> new IllegalArgumentException("해당 태그는 존재하지 않습니다. tagId : " + tagId));
    }

    public GameTag findByTagType(GameTagType gameTagType) {
        return gameTagRepository.findByTagType(gameTagType)
                .orElseThrow(() -> new IllegalArgumentException("해당 태그는 존재하지 않습니다." + gameTagType));
    }

    /**
     * GameTag 조회 또는 생성 (없으면 자동 생성)
     * 동시성 환경에서 중복 생성 방지
     *
     * @param tagType GameTagType enum
     * @return 조회되거나 생성된 GameTag
     */
    @Transactional
    public GameTag findOrCreateByTagType(GameTagType tagType) {
        // 1. 먼저 조회
        Optional<GameTag> existing = gameTagRepository.findByTagType(tagType);
        if (existing.isPresent()) {
            log.debug("Found existing GameTag: {}", tagType);
            return existing.get();
        }

        // 2. 없으면 생성 시도
        try {
            GameTag newTag = GameTag.builder()
                    .tagType(tagType)
                    .build();
            GameTag savedTag = gameTagRepository.save(newTag);
            log.info("Created new GameTag: {}", tagType);
            return savedTag;
        } catch (DataIntegrityViolationException e) {
            // 3. 중복 예외 발생 (다른 스레드가 이미 생성) → 다시 조회
            log.debug("Tag already created by another thread, fetching: {}", tagType);
            return gameTagRepository.findByTagType(tagType)
                    .orElseThrow(() -> new IllegalStateException(
                            "Tag should exist after concurrent creation: " + tagType));
        }
    }

    public List<GameTag> findByGameTagTypes(List<GameTagRequest> gameTagRequest) {
        return gameTagRequest.stream()
                .map(this::getGameTagOrThrow)
                .toList();
    }

    private GameTag getGameTagOrThrow(GameTagRequest tagRequest) {
        return gameTagRepository.findByTagType(tagRequest.getGameTagType())
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 태그는 존재하지 않습니다: " + tagRequest.getGameTagType()));
    }
}
