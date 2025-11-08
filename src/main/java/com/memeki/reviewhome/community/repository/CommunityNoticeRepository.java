package com.memeki.reviewhome.community.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.memeki.reviewhome.community.entity.CommunityNotice;

public interface CommunityNoticeRepository extends JpaRepository<CommunityNotice, Long> {
    CommunityNotice findByIdAndCommunityUuid(Long id, String communityUuid);
    List<CommunityNotice> findAllByCommunityUuidOrderByIsPinnedDescCreatedAtDesc(String communityUuid);
    List<CommunityNotice> findAllByCommunityUuidOrderByIsPinnedDescCreatedAtDesc(String communityUuid, Pageable pageable);
    List<CommunityNotice> findAllByCommunityUuidAndIsPinned(String communityUuid, Boolean isPinned);
}

