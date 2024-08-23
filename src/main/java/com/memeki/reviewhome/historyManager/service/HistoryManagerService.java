package com.memeki.reviewhome.historyManager.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.memeki.reviewhome.historyManager.entity.UpdateHistory;
import com.memeki.reviewhome.historyManager.repository.UpdateHistoryRepository;

public class HistoryManagerService {
    @Autowired
    private UpdateHistoryRepository updateHistoryRepository;

    @Transactional
    public List<UpdateHistory> getUpdateHistory(
        String type
    ) {
        return updateHistoryRepository.findByType(type);
    }

    @Transactional
    public UpdateHistory getRecentUpdateHistory(
        String type
    ) {
        return updateHistoryRepository.findTopByTypeOrderByCreatedAtDesc(type);
    }
}
