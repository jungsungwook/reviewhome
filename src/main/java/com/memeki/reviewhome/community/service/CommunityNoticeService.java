package com.memeki.reviewhome.community.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.memeki.reviewhome.community.dto.CommunityNoticeDto;
import com.memeki.reviewhome.community.entity.CommunityNotice;
import com.memeki.reviewhome.community.repository.CommunityNoticeRepository;
import com.memeki.reviewhome.global.exception.DefaultException;
import com.memeki.reviewhome.global.exceptionHandler.ErrorCode;
import com.memeki.reviewhome.global.security.entity.User;
import com.memeki.reviewhome.global.security.repository.UserRepository;

@Service
public class CommunityNoticeService {

    @Autowired
    private CommunityNoticeRepository communityNoticeRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 공지사항 생성
     */
    @Transactional
    public CommunityNotice createNotice(String communityUuid, String title, String content, 
                                       Boolean isPinned, long createdBy) {
        CommunityNotice notice = new CommunityNotice();
        notice.setCommunityUuid(communityUuid);
        notice.setTitle(title);
        notice.setContent(content);
        notice.setIsPinned(isPinned != null ? isPinned : false);
        notice.setCreatedBy(createdBy);

        return communityNoticeRepository.save(notice);
    }

    /**
     * 공지사항 수정
     */
    @Transactional
    public CommunityNotice updateNotice(Long noticeId, String communityUuid, String title, 
                                       String content, Boolean isPinned) throws Exception {
        CommunityNotice notice = communityNoticeRepository.findByIdAndCommunityUuid(noticeId, communityUuid);
        if (notice == null) {
            throw new DefaultException(ErrorCode.NOT_FOUND);
        }

        if (title != null) {
            notice.setTitle(title);
        }
        if (content != null) {
            notice.setContent(content);
        }
        if (isPinned != null) {
            notice.setIsPinned(isPinned);
        }

        return communityNoticeRepository.save(notice);
    }

    /**
     * 공지사항 삭제
     */
    @Transactional
    public void deleteNotice(Long noticeId, String communityUuid) throws Exception {
        CommunityNotice notice = communityNoticeRepository.findByIdAndCommunityUuid(noticeId, communityUuid);
        if (notice == null) {
            throw new DefaultException(ErrorCode.NOT_FOUND);
        }

        communityNoticeRepository.delete(notice);
    }

    /**
     * 공지사항 목록 조회
     */
    public List<CommunityNoticeDto.NoticeInfo> getNotices(String communityUuid, Integer limit) {
        List<CommunityNotice> notices;
        
        if (limit != null && limit > 0) {
            Pageable pageable = PageRequest.of(0, limit);
            notices = communityNoticeRepository
                    .findAllByCommunityUuidOrderByIsPinnedDescCreatedAtDesc(communityUuid, pageable);
        } else {
            notices = communityNoticeRepository
                    .findAllByCommunityUuidOrderByIsPinnedDescCreatedAtDesc(communityUuid);
        }

        return notices.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    /**
     * 고정된 공지사항 조회
     */
    public List<CommunityNoticeDto.NoticeInfo> getPinnedNotices(String communityUuid) {
        List<CommunityNotice> notices = communityNoticeRepository
                .findAllByCommunityUuidAndIsPinned(communityUuid, true);

        return notices.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    /**
     * 공지사항 단일 조회
     */
    public CommunityNoticeDto.NoticeInfo getNotice(Long noticeId, String communityUuid) throws Exception {
        CommunityNotice notice = communityNoticeRepository.findByIdAndCommunityUuid(noticeId, communityUuid);
        if (notice == null) {
            throw new DefaultException(ErrorCode.NOT_FOUND);
        }

        return convertToDto(notice);
    }

    /**
     * Entity를 DTO로 변환
     */
    private CommunityNoticeDto.NoticeInfo convertToDto(CommunityNotice notice) {
        CommunityNoticeDto.NoticeInfo dto = new CommunityNoticeDto.NoticeInfo();
        dto.setId(notice.getId());
        dto.setCommunityUuid(notice.getCommunityUuid());
        dto.setTitle(notice.getTitle());
        dto.setContent(notice.getContent());
        dto.setIsPinned(notice.getIsPinned());
        dto.setCreatedBy(notice.getCreatedBy());
        
        // 작성자 닉네임 조회
        User user = userRepository.findById(notice.getCreatedBy()).orElse(null);
        if (user != null) {
            dto.setCreatedByNickname(user.getNickname());
        }
        
        dto.setCreatedAt(notice.getCreatedAt().toString());
        dto.setUpdatedAt(notice.getUpdatedAt().toString());

        return dto;
    }
}

