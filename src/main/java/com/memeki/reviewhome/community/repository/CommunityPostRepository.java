package com.memeki.reviewhome.community.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import com.memeki.reviewhome.community.entity.CommunityPost;
import org.springframework.data.domain.Pageable;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
    Page<CommunityPost> findAllByCommunityUuid(String communityUuid, Pageable pageable);
}
