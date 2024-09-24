package com.memeki.reviewhome.community.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.memeki.reviewhome.community.dto.CommunityEnterRequest;
import com.memeki.reviewhome.community.dto.CommunityLeaveRequest;
import com.memeki.reviewhome.community.dto.CommunityPostPagination;
import com.memeki.reviewhome.community.dto.CommunityPostSimple;
import com.memeki.reviewhome.community.dto.CommunityPostsResponse;
import com.memeki.reviewhome.community.dto.CreatePostReplyRequest;
import com.memeki.reviewhome.community.dto.CreatePostRequest;
import com.memeki.reviewhome.community.entity.Community;
import com.memeki.reviewhome.community.entity.CommunityEnterHistory;
import com.memeki.reviewhome.community.entity.CommunityPost;
import com.memeki.reviewhome.community.entity.CommunityPostReply;
import com.memeki.reviewhome.community.entity.CommunityPostViewHistory;
import com.memeki.reviewhome.community.repository.CommunityEnterHistoryRepository;
import com.memeki.reviewhome.community.repository.CommunityPostLikeHistoryRepository;
import com.memeki.reviewhome.community.repository.CommunityPostReplyRepository;
import com.memeki.reviewhome.community.repository.CommunityPostRepository;
import com.memeki.reviewhome.community.repository.CommunityPostViewHistoryRepository;
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
    private CommunityPostReplyRepository postReplyRepository;

    @Autowired
    private CommunityPostLikeHistoryRepository postLikeHistoryRepository;

    @Autowired
    private CommunityPostViewHistoryRepository postViewHistoryRepository;

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
        List<CommunityPost> posts = communityPostRepository
                .findAllByCommunityUuidOrderByCreatedAtDesc(communityUuid, pageable)
                .getContent();
        for (CommunityPost post : posts) {
            long likeCount = postLikeHistoryRepository.countByPostId(post.getId());
            post.setLikeCount((int) likeCount);
            long replyCount = postReplyRepository.countCommunityPostReplyByPostId(post.getId());
            post.setReplyCount((int) replyCount);
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
                long replyCount = postReplyRepository.countCommunityPostReplyByPostId(post.getId());
                post.setReplyCount((int) replyCount);
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
            postsPage = communityPostRepository.findAllByCommunityUuidAndTitleContainingOrderByCreatedAtDesc(uuid,
                    search, pageable);
        }

        for (CommunityPost post : postsPage.getContent()) {
            long likeCount = postLikeHistoryRepository.countByPostId(post.getId());
            post.setLikeCount((int) likeCount);
            long replyCount = postReplyRepository.countCommunityPostReplyByPostId(post.getId());
            post.setReplyCount((int) replyCount);
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
                    simplePost.setReplyCount(post.getReplyCount());
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

    public CommunityPost getCommunityPostById(Long id, Long userId, HttpServletRequest request) {
        CommunityPost post = communityPostRepository.findById(id)
                .orElseThrow(() -> new DefaultException(ErrorCode.NOT_FOUND));
        List<CommunityPostReply> replies = postReplyRepository.findAllByPostId(id);
        post.setReplies(replies);
        post.setReplyCount(replies.size());
        if (userId != null) {
            post.setIsLiked(postLikeHistoryRepository.existsByPostIdAndCreatedBy(id, userId));
            post.setIsMine(post.getCreatedBy() == userId ? true : false);
            if (!postViewHistoryRepository.existsByPostIdAndCreatedBy(id, userId)) {
                CommunityPostViewHistory viewHistory = new CommunityPostViewHistory();
                viewHistory.setPostId(id);
                viewHistory.setCreatedBy(userId);
                postViewHistoryRepository.save(viewHistory);
            }
        } else {
            post.setIsLiked(false);
            post.setIsMine(false);
            CommunityPostViewHistory viewHistory = new CommunityPostViewHistory();
            viewHistory.setPostId(id);

        }
        post.setLikeCount(
                postLikeHistoryRepository.countByPostId(id).intValue());
        communityPostRepository.save(post);

        post.setViewCount(
                postViewHistoryRepository.countByPostId(id).intValue());
        return post;
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

    public void createCommunityPostComment(CreatePostReplyRequest body) {
        CommunityPostReply reply = new CommunityPostReply();
        reply.setPostId(body.getPostId());
        reply.setContent(body.getContent());
        reply.setCreatedBy(body.getCreatedBy());
        if (body.getIsReply() != null && body.getIsReply()) {
            reply.setIsReply(true);
            reply.setReplyId(body.getReplyId());
        }
        postReplyRepository.save(reply);
    }

    public CommunityPostSimple getPrevPost(Long id) {
        CommunityPost post = communityPostRepository.findById(id)
                .orElseThrow(() -> new DefaultException(ErrorCode.NOT_FOUND));
        CommunityPost prevPost = communityPostRepository.findTop1ByCommunityUuidAndCreatedAtBeforeOrderByCreatedAtDesc(
                post.getCommunityUuid(), post.getCreatedAt());
        if (prevPost == null) {
            return null;
        }
        return new CommunityPostSimple(prevPost);
    }

    public CommunityPostSimple getNextPost(Long id) {
        CommunityPost post = communityPostRepository.findById(id)
                .orElseThrow(() -> new DefaultException(ErrorCode.NOT_FOUND));
        CommunityPost nextPost = communityPostRepository.findTop1ByCommunityUuidAndCreatedAtAfterOrderByCreatedAtAsc(
                post.getCommunityUuid(), post.getCreatedAt());
        if (nextPost == null) {
            return null;
        }
        return new CommunityPostSimple(nextPost);
    }

    public List<CommunityPostSimple> getNearPosts(Long id, int count) {
        CommunityPost currentPost = communityPostRepository.findById(id)
                .orElseThrow(() -> new DefaultException(ErrorCode.NOT_FOUND));
        List<CommunityPost> posts = new ArrayList<>();
        int aboveCount = count / 2;
        int belowCount = count - aboveCount - 1; // 현재 포스트를 고려하여 1을 뺍니다.

        // 위쪽 포스트 가져오기
        List<CommunityPost> abovePosts = communityPostRepository
                .findAllByCommunityUuidAndCreatedAtBeforeOrderByCreatedAtDesc(
                        currentPost.getCommunityUuid(), currentPost.getCreatedAt(), PageRequest.of(0, aboveCount))
                .getContent();

        // 아래쪽 포스트 가져오기
        List<CommunityPost> belowPosts = communityPostRepository
                .findAllByCommunityUuidAndCreatedAtAfterOrderByCreatedAtAsc(
                        currentPost.getCommunityUuid(), currentPost.getCreatedAt(), PageRequest.of(0, belowCount))
                .getContent();

        // 위쪽 포스트 추가 (역순으로 추가)
        posts.addAll(abovePosts);

        // 현재 포스트 추가
        posts.add(currentPost);

        // 아래쪽 포스트 추가
        posts.addAll(belowPosts);

        // 부족한 경우 반대쪽에서 채우기
        int remaining = count - posts.size();
        if (remaining > 0) {
            if (abovePosts.size() < aboveCount) {
                // 위쪽이 부족한 경우, 아래쪽에서 더 가져오기
                List<CommunityPost> additionalBelowPosts = communityPostRepository
                        .findAllByCommunityUuidAndCreatedAtAfterOrderByCreatedAtAsc(
                                currentPost.getCommunityUuid(), currentPost.getCreatedAt(),
                                PageRequest.of(0, belowCount + remaining))
                        .getContent();
                posts.addAll(additionalBelowPosts.subList(belowPosts.size(),
                        Math.min(additionalBelowPosts.size(), belowCount + remaining)));
            } else {
                // 아래쪽이 부족한 경우, 위쪽에서 더 가져오기
                List<CommunityPost> additionalAbovePosts = communityPostRepository
                        .findAllByCommunityUuidAndCreatedAtBeforeOrderByCreatedAtDesc(
                                currentPost.getCommunityUuid(), currentPost.getCreatedAt(),
                                PageRequest.of(0, aboveCount + remaining))
                        .getContent();
                posts.addAll(0, additionalAbovePosts.subList(abovePosts.size(),
                        Math.min(additionalAbovePosts.size(), aboveCount + remaining)));
            }
        }

        // 최종적으로 시간 순으로 정렬
        posts.sort(Comparator.comparing(CommunityPost::getCreatedAt).reversed());

        // 현재 포스트의 인덱스 찾기
        int currentIndex = posts.indexOf(currentPost);

        // 현재 포스트를 중심으로 원하는 개수만큼 잘라내기
        int startIndex = Math.max(0, currentIndex - aboveCount);
        int endIndex = Math.min(posts.size(), startIndex + count);
        startIndex = Math.max(0, endIndex - count);

        return Arrays.asList(convertToCommunityPostSimple(posts.subList(startIndex, endIndex)));
    }
}
