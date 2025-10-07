package com.imfine.ngs.community.service;

import com.imfine.ngs._global.config.security.jwt.JwtUserPrincipal;
import com.imfine.ngs.community.dto.CommunityUser;
import com.imfine.ngs.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class CommunityUserService {
  private final UserRepository userRepository;

  private CommunityUser loadUser(Long userId, HttpStatus status, String message) {
    return userRepository.findById(userId)
            .map(CommunityUser::of)
            .orElseThrow(() -> new ResponseStatusException(status, message));
  }

  public CommunityUser getAuthorOrThrow(Long authorId) {
    return loadUser(authorId, HttpStatus.NOT_FOUND, "작성자를 찾을 수 없습니다.");
  }

  public CommunityUser getCommunityUserOrAnonymous(JwtUserPrincipal principal) {
    if (principal == null) {
      return CommunityUser.builder().build();
    }

    return loadUser(principal.getUserId(), HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다.");
  }

  public CommunityUser getCommunityUserOrThrow(JwtUserPrincipal principal) {
    if (principal == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.");
    }
    return getCommunityUserOrAnonymous(principal);
  }
}
