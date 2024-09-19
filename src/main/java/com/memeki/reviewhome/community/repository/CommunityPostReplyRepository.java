package com.memeki.reviewhome.community.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.memeki.reviewhome.community.entity.CommunityPostReply;

public interface CommunityPostReplyRepository extends JpaRepository<CommunityPostReply, Long> {
    Integer countCommunityPostReplyByPostId(Long postId);

    Integer countCommunityPostReplyByReplyId(Long replyId);

    Integer countCommunityPostReplyByPostIdAndIsReply(Long postId, Boolean isReply);

    Integer countCommunityPostReplyByReplyIdAndIsReply(Long replyId, Boolean isReply);

    List<CommunityPostReply> findAllByPostId(Long postId);

}
