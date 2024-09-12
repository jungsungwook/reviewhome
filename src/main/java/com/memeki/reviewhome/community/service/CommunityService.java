package com.memeki.reviewhome.community.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.memeki.reviewhome.community.dto.CommunityEnterRequest;
import com.memeki.reviewhome.community.dto.CommunityLeaveRequest;
import com.memeki.reviewhome.community.dto.CommunityPostPagination;
import com.memeki.reviewhome.community.dto.CommunityPostSimple;
import com.memeki.reviewhome.community.dto.CommunityPostsResponse;
import com.memeki.reviewhome.community.dto.CreatePostRequest;
import com.memeki.reviewhome.community.entity.Community;
import com.memeki.reviewhome.community.entity.CommunityEnterHistory;
import com.memeki.reviewhome.community.entity.CommunityPost;
import com.memeki.reviewhome.community.repository.CommunityEnterHistoryRepository;
import com.memeki.reviewhome.community.repository.CommunityPostLikeHistoryRepository;
import com.memeki.reviewhome.community.repository.CommunityPostRepository;
import com.memeki.reviewhome.community.repository.CommunityRepository;
import com.memeki.reviewhome.global.exception.DefaultException;
import com.memeki.reviewhome.global.exceptionHandler.ErrorCode;
import com.memeki.reviewhome.postAddress.entity.PostAddressInfo;
import com.memeki.reviewhome.postAddress.repository.PostAddressInfoRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class CommunityService {

    @Autowired
    private PostAddressInfoRepository postAddressInfoRepository;

    @Autowired
    private CommunityEnterHistoryRepository communityEnterHistoryRepository;

    @Autowired
    private CommunityPostRepository communityPostRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private CommunityPostLikeHistoryRepository postLikeHistoryRepository;

    public CommunityService(
            PostAddressInfoRepository postAddressInfoRepository,
            CommunityPostRepository communityPostRepository,
            CommunityEnterHistoryRepository communityEnterHistoryRepository,
            CommunityRepository communityRepository,
            CommunityPostLikeHistoryRepository postLikeHistoryRepository) {
        this.postAddressInfoRepository = postAddressInfoRepository;
        this.communityEnterHistoryRepository = communityEnterHistoryRepository;
        this.communityRepository = communityRepository;
        this.communityPostRepository = communityPostRepository;
        this.postLikeHistoryRepository = postLikeHistoryRepository;
    }

    public Community getBuildingCommunity(String uuid, String type2, Long userId) throws Exception {
        List<Community> community = communityRepository.findAllByTypeAndTargetIdAndType2(
                "building", uuid, type2);
        if (community.isEmpty() && type2.equals("default")) {
            PostAddressInfo postAddressInfo = postAddressInfoRepository.findPostAddressInfoByUuid(uuid);
            if (postAddressInfo == null) {
                throw new DefaultException(ErrorCode.NOT_FOUND);
            }
            Community newCommunity = new Community();
            newCommunity.setGeoFeaturesId(postAddressInfo.getGeoFeaturesId());
            newCommunity.setGeoFeaturesName(postAddressInfo.getGeoFeaturesName());
            newCommunity.setTargetId(uuid);
            newCommunity.setType("building");
            newCommunity.setType2("default");
            newCommunity.setCreatedBy(0);
            if (postAddressInfo.getDongNm() != null &&
                    postAddressInfo.getDongNm().trim().length() > 0) {
                newCommunity.setName(postAddressInfo.getBldNm() + " " + postAddressInfo.getDongNm() + "동");
                newCommunity
                        .setDescription(postAddressInfo.getBldNm() + " " + postAddressInfo.getDongNm() + "동 커뮤니티 입니다.");
            } else {
                newCommunity.setName(postAddressInfo.getBldNm());
                newCommunity.setDescription(postAddressInfo.getBldNm() + " 커뮤니티 입니다.");
            }
            newCommunity.setIsPassword(false);
            communityRepository.save(newCommunity);
            return newCommunity;
        }

        return community.get(0);
    }

    public Community getCommunityByCommunityUuid(String uuid, Long userId) {
        Community community = communityRepository.findCommunityByUuid(uuid);
        if (community == null) {
            throw new DefaultException(ErrorCode.NOT_FOUND);
        }
        if (userId != null)
            community.setIsEnter(isEnteredCommunity(uuid, userId));

        community.setUserCount(
                communityEnterHistoryRepository.countCommunityEnterHistoryByCommunityUuid(uuid));

        return community;
    }

    public Community enterCommunity(CommunityEnterRequest body) {
        Community community = communityRepository.findCommunityByUuid(body.getCommunityUuid());
        if (community == null) {
            throw new DefaultException(ErrorCode.NOT_FOUND);
        }
        if (body.getNickname() == null) {
            throw new DefaultException(ErrorCode.INVALID_PARAMETER);
        }
        // 닉네임 중복 체크
        if (communityEnterHistoryRepository.existsCommunityEnterHistoryByNicknameAndCommunityUuid(
                body.getNickname(), body.getCommunityUuid())) {
            throw new DefaultException(ErrorCode.ALREADY_NICKNAME);
        }
        // 중복 가입 방지
        if (communityEnterHistoryRepository.existsCommunityEnterHistoryByUserIdAndCommunityUuid(
                body.getUserId(), body.getCommunityUuid())) {
            throw new DefaultException(ErrorCode.ALREADY_ENTERED);
        }
        // 닉네임은 2자~8자
        if (body.getNickname().length() < 2 || body.getNickname().length() > 8) {
            throw new DefaultException(ErrorCode.INVALID_PARAMETER);
        }
        // 특수문자가 있을 경우 제외
        if (!body.getNickname().matches("^[a-zA-Z0-9가-힣]*$")) {
            throw new DefaultException(ErrorCode.INVALID_PARAMETER);
        }

        if (community.getIsPassword()) {
            if (body.getPassword() == null) {
                throw new DefaultException(ErrorCode.NEED_PASSWORD);
            } else if (!community.getPassword().equals(body.getPassword())) {
                throw new DefaultException(ErrorCode.INVALID_PASSWORD);
            }
        }
        CommunityEnterHistory newCommunityEnterHistory = new CommunityEnterHistory();
        newCommunityEnterHistory.setUserId(body.getUserId());
        newCommunityEnterHistory.setCommunityUuid(community.getUuid());
        newCommunityEnterHistory.setType(community.getType());
        newCommunityEnterHistory.setNickname(body.getNickname());
        communityEnterHistoryRepository.save(newCommunityEnterHistory);
        return community;
    }

    public void leaveCommunity(CommunityLeaveRequest body) {
        Community community = communityRepository.findCommunityByUuid(body.getCommunityUuid());
        if (community == null) {
            throw new DefaultException(ErrorCode.NOT_FOUND);
        }
        CommunityEnterHistory communityEnterHistory = communityEnterHistoryRepository
                .findCommunityEnterHistoryByUserIdAndCommunityUuid(body.getUserId(), body.getCommunityUuid());
        if (communityEnterHistory == null) {
            throw new DefaultException(ErrorCode.NOT_FOUND);
        }
        communityEnterHistoryRepository.delete(communityEnterHistory);
    }

    public List<CommunityPost> getCommunityPosts(String communityUuid, Pageable pageable, Long userId) {
        Community community = communityRepository.findCommunityByUuid(communityUuid);
        if (community.getIsPassword()) {
            CommunityEnterHistory communityEnterHistory = communityEnterHistoryRepository
                    .findCommunityEnterHistoryByUserIdAndCommunityUuid(userId, communityUuid);
            if (communityEnterHistory == null) {
                throw new DefaultException(ErrorCode.NEED_ENTER);
            }
        }
        List<CommunityPost> posts = communityPostRepository.findAllByCommunityUuidOrderByCreatedAtDesc(communityUuid, pageable)
                .getContent();
        for (CommunityPost post : posts) {
            long likeCount = postLikeHistoryRepository.countByPostId(post.getId());
            post.setLikeCount((int) likeCount);
        }
        return posts;
    }

    public List<CommunityPost> getPopularPosts(String communityUuid, Pageable pageable, Long userId) {
        Community community = communityRepository.findCommunityByUuid(communityUuid);
        if (community.getIsPassword()) {
            CommunityEnterHistory communityEnterHistory = communityEnterHistoryRepository
                    .findCommunityEnterHistoryByUserIdAndCommunityUuid(userId, communityUuid);
            if (communityEnterHistory == null) {
                throw new DefaultException(ErrorCode.NEED_ENTER);
            }
        }
        List<CommunityPost> allPosts = communityPostRepository.findAllByCommunityUuid(communityUuid, pageable)
                .getContent();
        List<CommunityPost> popularPosts = new ArrayList<>();

        for (CommunityPost post : allPosts) {
            long likeCount = postLikeHistoryRepository.countByPostId(post.getId());
            if (likeCount >= 10) {
                post.setLikeCount((int) likeCount);
                popularPosts.add(post);
            }
        }

        return popularPosts.stream()
                .sorted(Comparator.comparing(CommunityPost::getLikeCount).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    public Boolean isEnteredCommunity(String communityUuid, Long userId) {
        CommunityEnterHistory communityEnterHistory = communityEnterHistoryRepository
                .findCommunityEnterHistoryByUserIdAndCommunityUuid(userId, communityUuid);
        return communityEnterHistory != null;
    }

    public CommunityPostsResponse getCommunityPosts(String uuid, Pageable pageable, String search) {
        Page<CommunityPost> postsPage;
        if (search == null) {
            postsPage = communityPostRepository.findAllByCommunityUuidOrderByCreatedAtDesc(uuid, pageable);
        } else {
            postsPage = communityPostRepository.findAllByCommunityUuidAndTitleContainingOrderByCreatedAtDesc(uuid, search, pageable);
        }

        CommunityPostsResponse response = new CommunityPostsResponse();
        response.setPosts(convertToCommunityPostSimple(postsPage.getContent()));
        response.setPagination(createPagination(postsPage));
        return response;
    }

    private CommunityPostSimple[] convertToCommunityPostSimple(List<CommunityPost> posts) {
        return posts.stream()
                .map(post -> {
                    CommunityPostSimple simplePost = new CommunityPostSimple();
                    simplePost.setId(post.getId());
                    simplePost.setPostType(post.getPostType());
                    simplePost.setCommunityUuid(post.getCommunityUuid());
                    simplePost.setTitle(post.getTitle());
                    simplePost.setContent(post.getContent());
                    simplePost.setCreatedAt(post.getCreatedAt());
                    simplePost.setCreatedBy(post.getCreatedBy());
                    simplePost.setLikeCount(post.getLikeCount());
                    simplePost.setViewCount(post.getViewCount());
                    return simplePost;
                })
                .toArray(CommunityPostSimple[]::new);
    }

    private CommunityPostPagination createPagination(Page<CommunityPost> postsPage) {
        CommunityPostPagination pagination = new CommunityPostPagination();
        pagination.setTotalElements(postsPage.getTotalElements());
        pagination.setTotalPages(postsPage.getTotalPages());
        pagination.setPage(postsPage.getNumber());
        pagination.setSize(postsPage.getSize());
        pagination.setLast(postsPage.isLast());
        return pagination;
    }

    public CommunityPost getCommunityPostById(Long id) {
        return communityPostRepository.findById(id)
                .orElseThrow(() -> new DefaultException(ErrorCode.NOT_FOUND));
    }

    public CreatePostRequest createCommunityPost(CreatePostRequest request) {
        CommunityPost post = new CommunityPost();
        post.setCommunityUuid(request.getCommunityUuid());
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setPostType(request.getPostType());
        post.setCreatedBy(request.getCreatedBy());
        communityPostRepository.save(post);
        return request;
    }
}
