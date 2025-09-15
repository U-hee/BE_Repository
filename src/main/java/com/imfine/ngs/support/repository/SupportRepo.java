package com.imfine.ngs.support.repository;

import com.imfine.ngs.support.entity.Support;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportRepo extends JpaRepository<Support, Long> {
    List<Support> findAllByCategoryId(long categoryId);
    List<Support> findAllByUserId(long userId);
}
