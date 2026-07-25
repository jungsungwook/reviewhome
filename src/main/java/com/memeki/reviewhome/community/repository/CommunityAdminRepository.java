package com.memeki.reviewhome.community.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.memeki.reviewhome.community.entity.CommunityAdmin;

public interface CommunityAdminRepository extends JpaRepository<CommunityAdmin, Integer> {
    CommunityAdmin findByCommunityUuidAndUserId(String communityUuid, long userId);
    List<CommunityAdmin> findAllByCommunityUuid(String communityUuid);
    Boolean existsByCommunityUuid(String communityUuid);
    Boolean existsByCommunityUuidAndUserId(String communityUuid, long userId);
    Integer countByCommunityUuid(String communityUuid);
}

