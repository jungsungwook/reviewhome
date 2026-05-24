package com.memeki.reviewhome.community.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.memeki.reviewhome.community.entity.Community;

public interface CommunityRepository extends JpaRepository<Community, String>{
    public Community findCommunityByUuid(String uuid);
    public List<Community> findAllByTypeAndTargetId(String type, String targetId);
    public List<Community> findAllByTypeAndTargetIdAndType2(String type, String targetId, String type2);
    
    // 타입별 최근 생성된 커뮤니티 조회
    public List<Community> findAllByTypeOrderByCreatedAtDesc(String type, Pageable pageable);
    
    // 타입별 모든 커뮤니티 조회
    public List<Community> findAllByType(String type);
    
    // 사용자 수가 많은 커뮤니티 조회 (쿼리 필요)
    @Query(value = "SELECT c.* FROM community c " +
           "LEFT JOIN (SELECT community_uuid, COUNT(*) as user_count FROM community_enter_history GROUP BY community_uuid) h " +
           "ON c.uuid = h.community_uuid " +
           "WHERE c.type = ?1 AND c.is_deleted = false " +
           "ORDER BY COALESCE(h.user_count, 0) DESC", 
           nativeQuery = true)
    public List<Community> findAllByTypeOrderByUserCountDesc(String type, Pageable pageable);
}
