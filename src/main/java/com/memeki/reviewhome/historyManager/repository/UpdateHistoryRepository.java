package com.memeki.reviewhome.historyManager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.memeki.reviewhome.historyManager.entity.UpdateHistory;

public interface UpdateHistoryRepository extends JpaRepository<UpdateHistory, Integer> {
    List<UpdateHistory> findAll();
    List<UpdateHistory> findByType(String type);
    List<UpdateHistory> findByUuid(String uuid);
    UpdateHistory findTopByTypeOrderByCreatedAtDesc(String type);
}
