package com.memeki.reviewhome.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.memeki.reviewhome.community.entity.CommunityEnterHistory;


public interface CommunityEnterHistoryRepository extends JpaRepository<CommunityEnterHistory, Integer> {
    CommunityEnterHistory findCommunityEnterHistoryByUserIdAndCommunityUuid(long userId, String communityUuid);
    Integer countCommunityEnterHistoryByCommunityUuid(String communityUuid);
    Boolean existsCommunityEnterHistoryByUserIdAndCommunityUuid(long userId, String communityUuid);
    Boolean existsCommunityEnterHistoryByNicknameAndCommunityUuid(String nickname, String communityUuid);
}
