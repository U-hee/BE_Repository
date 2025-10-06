package com.imfine.ngs.community.service;

import com.imfine.ngs.community.entity.CommunityTag;
import com.imfine.ngs.community.repository.CommunityTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityTagService {
  private final CommunityTagRepository tagRepository;

  public CommunityTag addTag(String tagName) {
    CommunityTag tag = CommunityTag.builder()
            .name(tagName)
            .build();
    return tagRepository.save(tag);
  }

  public CommunityTag getTagByName(String tagName) {
    CommunityTag result = tagRepository.findByName(tagName).orElse(null);
    if (result == null) {
      result = addTag(tagName);
    }

    return result;
  }

  public List<CommunityTag> getTagsByName(String tmpText) {
    return tagRepository.findAllByName(tmpText);
  }


  private List<String> normalizeTagNames(List<String> tagNames) {
    if (tagNames == null || tagNames.isEmpty()) {
      return Collections.emptyList();
    }

    return tagNames.stream()
            .filter(StringUtils::hasText)
            .map(String::trim)
            .collect(Collectors.toList());
  }

  public List<CommunityTag> toTagsForMutation(List<String> tagNames) {
    List<String> normalizedNames = normalizeTagNames(tagNames);
    if (normalizedNames.isEmpty()) {
      return Collections.emptyList();
    }

    return normalizedNames.stream()
            .map(this::getTagByName)
            .collect(Collectors.toList());
  }

  public List<CommunityTag> toTagsForSearch(List<String> tagNames) {
    List<String> normalizedNames = normalizeTagNames(tagNames);
    if (normalizedNames.isEmpty()) {
      return Collections.emptyList();
    }

    return normalizedNames.stream()
            .map(name -> CommunityTag.builder().name(name).build())
            .collect(Collectors.toList());
  }
}
