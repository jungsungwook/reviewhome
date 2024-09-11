package com.memeki.reviewhome.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.memeki.reviewhome.community.entity.CommunityPostLikeHistory;

public interface CommunityPostLikeHistoryRepository extends JpaRepository<CommunityPostLikeHistory, Long> {
    public Long countByPostId(Long postId);
}
