package com.memeki.reviewhome.community.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.memeki.reviewhome.community.entity.CommunityAdminRequest;

public interface CommunityAdminRequestRepository extends JpaRepository<CommunityAdminRequest, Integer> {
    CommunityAdminRequest findByIdAndCommunityUuid(int id, String communityUuid);
    List<CommunityAdminRequest> findAllByCommunityUuidAndStatus(String communityUuid, String status);
    List<CommunityAdminRequest> findAllByCommunityUuid(String communityUuid);
    CommunityAdminRequest findByCommunityUuidAndUserIdAndStatus(String communityUuid, long userId, String status);
    Boolean existsByCommunityUuidAndUserIdAndStatus(String communityUuid, long userId, String status);
}

