package com.memeki.reviewhome.community.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import com.memeki.reviewhome.community.dto.CommunityPostSimple;
import com.memeki.reviewhome.community.entity.CommunityPost;
import org.springframework.data.domain.Pageable;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
    Page<CommunityPost> findAllByCommunityUuid(String communityUuid, Pageable pageable);
    Page<CommunityPost> findAllByCommunityUuidAndTitleContaining(String communityUuid, String title, Pageable pageable);
    Page<CommunityPost> findAllByCommunityUuidOrderByCreatedAtDesc(String communityUuid, Pageable pageable);
    Page<CommunityPost> findAllByCommunityUuidAndTitleContainingOrderByCreatedAtDesc(String communityUuid, String search, Pageable pageable);
}
