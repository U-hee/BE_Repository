package com.imfine.ngs.support.service;

import com.imfine.ngs.support.entity.Support;
import com.imfine.ngs.support.repository.SupportRepo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SupportService {

    private final SupportRepo supportRepo;

    public Support insertSupportRepo(Support support) {
        return supportRepo.save(support);
    }
    public List<Support> findSupportByUserId(long userId) {
        return supportRepo.findAllByUserId(userId);
    }
    public List<Support> findSupportByCategoryId(long categoryId) {
        return supportRepo.findAllByCategoryId(categoryId);
    }
}
