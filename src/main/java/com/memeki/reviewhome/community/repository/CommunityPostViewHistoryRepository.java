package com.memeki.reviewhome.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.memeki.reviewhome.community.entity.CommunityPostViewHistory;

public interface CommunityPostViewHistoryRepository extends JpaRepository<CommunityPostViewHistory, Long> {
    public Long countByPostId(Long postId);
    public Boolean existsByPostIdAndCreatedBy(Long postId, Long createdBy);
    
}
