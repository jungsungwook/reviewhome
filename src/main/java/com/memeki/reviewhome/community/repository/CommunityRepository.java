package com.memeki.reviewhome.community.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.memeki.reviewhome.community.entity.Community;

public interface CommunityRepository extends JpaRepository<Community, String>{
    public Community findCommunityByUuid(String uuid);
    public List<Community> findAllByTypeAndTargetId(String type, String targetId);
    public List<Community> findAllByTypeAndTargetIdAndType2(String type, String targetId, String type2);
}
