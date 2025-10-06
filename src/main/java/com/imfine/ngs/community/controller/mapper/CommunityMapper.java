package com.imfine.ngs.community.controller.mapper;

import com.imfine.ngs._global.config.security.jwt.JwtUserPrincipal;
import com.imfine.ngs.community.dto.CommunityUser;
import com.imfine.ngs.community.dto.request.CommunityBoardCreateRequest;
import com.imfine.ngs.community.dto.request.CommunityCommentRequest;
import com.imfine.ngs.community.dto.response.CommunityBoardResponse;
import com.imfine.ngs.community.dto.response.CommunityCommentResponse;
import com.imfine.ngs.community.dto.response.CommunityPostResponse;
import com.imfine.ngs.community.entity.CommunityBoard;
import com.imfine.ngs.community.entity.CommunityComment;
import com.imfine.ngs.community.entity.CommunityPost;
import com.imfine.ngs.community.entity.CommunityTag;
import com.imfine.ngs.community.service.CommunityTagService;
import com.imfine.ngs.user.entity.User;
import com.imfine.ngs.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CommunityMapper {
  private static final String DEFAULT_ROLE = "USER";

  public static CommunityBoard toCommunityBoard(
          CommunityBoardCreateRequest request,
          CommunityUser user) {
    return CommunityBoard.builder()
            .gameId(request.getGameId())
            .title(request.getTitle())
            .description(request.getDescription())
            .managerId(user.getId())
            .build();
  }

  public static CommunityBoardResponse toCommunityBoardResponse(CommunityBoard board) {
    return CommunityBoardResponse.builder()
            .id(board.getId())
            .title(board.getTitle())
            .description(board.getDescription())
            .createdAt(board.getCreatedAt())
            .updatedAt(board.getUpdatedAt())
            .build();
  }

  public static CommunityPostResponse toCommunityPostResponse(CommunityPost post, CommunityUser author) {
    return CommunityPostResponse.from(post, author);
  }

  public static CommunityComment toComment(CommunityCommentRequest request) {
    return CommunityComment.builder()
            .postId(request.getPostId())
            .authorId(request.getAuthor().getId())
            .parentId(request.getParentId())
            .content(request.getContent())
            .build();
  }

  public static CommunityCommentResponse toCommentResponse(CommunityComment comment, CommunityUser author) {
    return CommunityCommentResponse.builder()
            .id(comment.getId())
            .parentId(comment.getParentId())
            .user(author)
            .content(comment.getContent())
            .createdAt(comment.getCreatedAt())
            .updatedAt(comment.getUpdatedAt())
            .build();
  }
}
