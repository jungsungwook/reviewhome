package com.memeki.reviewhome.community.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.memeki.reviewhome.community.dto.CommunityEnterRequest;
import com.memeki.reviewhome.community.dto.CommunityLeaveRequest;
import com.memeki.reviewhome.community.dto.CommunityPostLikeReqeust;
import com.memeki.reviewhome.community.dto.CommunityPostLikeResponse;
import com.memeki.reviewhome.community.dto.CommunityPostPagination;
import com.memeki.reviewhome.community.dto.CommunityPostSimple;
import com.memeki.reviewhome.community.dto.CommunityPostsResponse;
import com.memeki.reviewhome.community.dto.CreatePostReplyRequest;
import com.memeki.reviewhome.community.dto.CreatePostRequest;
import com.memeki.reviewhome.community.dto.DeletePostRequest;
import com.memeki.reviewhome.community.dto.EditPostRequest;
import com.memeki.reviewhome.community.entity.Community;
import com.memeki.reviewhome.community.entity.CommunityEnterHistory;
import com.memeki.reviewhome.community.entity.CommunityPost;
import com.memeki.reviewhome.community.entity.CommunityPostLikeHistory;
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
import com.memeki.reviewhome.geo.dto.GeoFeaturesByPostAddressInfoDto;
import com.memeki.reviewhome.geo.repository.GeoFeaturesRepositoryCustom;

import org.springframework.data.domain.Pageable;

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

    @Autowired
    private GeoFeaturesRepositoryCustom geoFeaturesRepositoryCustom;

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
            long viewCount = postViewHistoryRepository.countByPostId(post.getId());
            post.setViewCount((int) viewCount);
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
            long viewCount = postViewHistoryRepository.countByPostId(post.getId());
            post.setViewCount((int) viewCount);
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

    public CommunityPost getCommunityPostById(Long id, Long userId, HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
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

            HttpSession session = httpRequest.getSession(false);
            String guestId = (session != null) ? (String) session.getAttribute("guestId") : null;

            if (guestId != null) {
                // 세션 ID를 사용하여 조회 기록 확인
                if (!postViewHistoryRepository.existsByPostIdAndGuestId(id, guestId)) {
                    CommunityPostViewHistory viewHistory = new CommunityPostViewHistory();
                    viewHistory.setPostId(id);
                    viewHistory.setGuestId(guestId);
                    postViewHistoryRepository.save(viewHistory);
                }
            } else {
                CommunityPostViewHistory viewHistory = new CommunityPostViewHistory();
                viewHistory.setPostId(id);
                viewHistory.setGuestId("ERROR");
                postViewHistoryRepository.save(viewHistory);
            }
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

    public void postLike(CommunityPostLikeReqeust body, CommunityPostLikeResponse response) {
        CommunityPostLikeHistory existHistory = postLikeHistoryRepository
                .findByPostIdAndCreatedBy(body.getPostId(), body.getCreatedBy());
        if (existHistory != null) {
            postLikeHistoryRepository.delete(existHistory);
            response.setLiked(0);
        } else {
            CommunityPostLikeHistory newHistory = new CommunityPostLikeHistory();
            newHistory.setPostId(body.getPostId());
            newHistory.setCreatedBy(body.getCreatedBy());
            postLikeHistoryRepository.save(newHistory);
            response.setLiked(1);
        }
    }

    public void editCommunityPost(EditPostRequest request) {
        CommunityPost post = communityPostRepository.findById(request.getPostId())
                .orElseThrow(() -> new DefaultException(ErrorCode.NOT_FOUND));
        if (post.getCreatedBy() != request.getCreatedBy()) {
            throw new DefaultException(ErrorCode.FORBIDDEN);
        }
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setPostType(request.getPostType());
        communityPostRepository.save(post);
    }

    public void deleteCommunityPost(DeletePostRequest request) {
        CommunityPost post = communityPostRepository.findById(request.getPostId())
                .orElseThrow(() -> new DefaultException(ErrorCode.NOT_FOUND));
        if (post.getCreatedBy() != request.getCreatedBy()) {
            throw new DefaultException(ErrorCode.FORBIDDEN);
        }
        communityPostRepository.delete(post);
    }

    /**
     * 동네 커뮤니티 조회 또는 생성
     * emdCd(읍면동 코드)를 기준으로 동네 커뮤니티를 찾거나 없으면 새로 생성
     */
    public Community getTownCommunity(String emdCd, String type2, Long userId) throws Exception {
        List<Community> community = communityRepository.findAllByTypeAndTargetIdAndType2(
                "town", emdCd, type2);
        
        if (community.isEmpty() && type2.equals("default")) {
            // GeoFeatures에서 동네 정보 조회 (DTO 사용)
            GeoFeaturesByPostAddressInfoDto geoFeaturesDto = geoFeaturesRepositoryCustom.findByEmdCd(emdCd);
            if (geoFeaturesDto == null) {
                throw new DefaultException(ErrorCode.NOT_FOUND);
            }
            
            // 새로운 동네 커뮤니티 생성
            Community newCommunity = new Community();
            newCommunity.setGeoFeaturesId(geoFeaturesDto.getId());
            newCommunity.setGeoFeaturesName(geoFeaturesDto.getEmdKorNm());
            newCommunity.setTargetId(emdCd);
            newCommunity.setType("town");
            newCommunity.setType2("default");
            newCommunity.setCreatedBy(0);
            newCommunity.setName(geoFeaturesDto.getEmdKorNm() + " 동네 커뮤니티");
            newCommunity.setDescription(geoFeaturesDto.getEmdKorNm() + " 지역 주민들의 커뮤니티입니다.");
            newCommunity.setIsPassword(false);
            
            communityRepository.save(newCommunity);
            return newCommunity;
        }
        
        return community.get(0);
    }
    
    /**
     * 커뮤니티 목록 조회 (빌딩/타운 구분)
     */
    public com.memeki.reviewhome.community.dto.CommunityListResponseDto getAllCommunities(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        
        List<Community> buildingCommunities = communityRepository.findAllByType("building");
        List<Community> townCommunities = communityRepository.findAllByType("town");
        
        com.memeki.reviewhome.community.dto.CommunityListResponseDto response = 
                new com.memeki.reviewhome.community.dto.CommunityListResponseDto();
        
        response.setBuildingCommunities(convertToListDto(buildingCommunities));
        response.setTownCommunities(convertToListDto(townCommunities));
        
        return response;
    }
    
    /**
     * 사용자 수가 많은 커뮤니티 조회
     */
    public com.memeki.reviewhome.community.dto.CommunityListResponseDto getPopularCommunities(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        
        List<Community> buildingCommunities = communityRepository.findAllByTypeOrderByUserCountDesc("building", pageable);
        List<Community> townCommunities = communityRepository.findAllByTypeOrderByUserCountDesc("town", pageable);
        
        com.memeki.reviewhome.community.dto.CommunityListResponseDto response = 
                new com.memeki.reviewhome.community.dto.CommunityListResponseDto();
        
        response.setBuildingCommunities(convertToListDto(buildingCommunities));
        response.setTownCommunities(convertToListDto(townCommunities));
        
        return response;
    }
    
    /**
     * 최근 생성된 커뮤니티 조회
     */
    public com.memeki.reviewhome.community.dto.CommunityListResponseDto getRecentCommunities(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        
        List<Community> buildingCommunities = communityRepository.findAllByTypeOrderByCreatedAtDesc("building", pageable);
        List<Community> townCommunities = communityRepository.findAllByTypeOrderByCreatedAtDesc("town", pageable);
        
        com.memeki.reviewhome.community.dto.CommunityListResponseDto response = 
                new com.memeki.reviewhome.community.dto.CommunityListResponseDto();
        
        response.setBuildingCommunities(convertToListDto(buildingCommunities));
        response.setTownCommunities(convertToListDto(townCommunities));
        
        return response;
    }
    
    /**
     * 최근 작성된 게시글 조회
     */
    public com.memeki.reviewhome.community.dto.RecentPostResponseDto getRecentPosts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        
        List<CommunityPost> buildingPosts = communityPostRepository.findRecentPostsByCommunityType("building", pageable);
        List<CommunityPost> townPosts = communityPostRepository.findRecentPostsByCommunityType("town", pageable);
        
        com.memeki.reviewhome.community.dto.RecentPostResponseDto response = 
                new com.memeki.reviewhome.community.dto.RecentPostResponseDto();
        
        response.setBuildingPosts(convertToPostListDto(buildingPosts));
        response.setTownPosts(convertToPostListDto(townPosts));
        
        return response;
    }
    
    // DTO 변환 헬퍼 메소드
    private List<com.memeki.reviewhome.community.dto.CommunityListResponseDto.CommunityItemDto> convertToListDto(List<Community> communities) {
        return communities.stream().map(community -> {
            com.memeki.reviewhome.community.dto.CommunityListResponseDto.CommunityItemDto dto = 
                    new com.memeki.reviewhome.community.dto.CommunityListResponseDto.CommunityItemDto();
            dto.setUuid(community.getUuid());
            dto.setType(community.getType());
            dto.setName(community.getName());
            dto.setDescription(community.getDescription());
            dto.setGeoFeaturesName(community.getGeoFeaturesName());
            
            // 사용자 수 조회
            Integer userCount = communityEnterHistoryRepository.countCommunityEnterHistoryByCommunityUuid(community.getUuid());
            dto.setUserCount(userCount != null ? userCount : 0);
            
            // 날짜 포맷
            dto.setCreatedAt(community.getCreatedAt().toString());
            
            return dto;
        }).collect(Collectors.toList());
    }
    
    private List<com.memeki.reviewhome.community.dto.RecentPostResponseDto.PostItemDto> convertToPostListDto(List<CommunityPost> posts) {
        return posts.stream().map(post -> {
            com.memeki.reviewhome.community.dto.RecentPostResponseDto.PostItemDto dto = 
                    new com.memeki.reviewhome.community.dto.RecentPostResponseDto.PostItemDto();
            dto.setPostId(post.getId());
            dto.setTitle(post.getTitle());
            dto.setContent(post.getContent());
            dto.setCreatedBy(post.getCreatedBy());
            dto.setCreatedAt(post.getCreatedAt().toString());
            
            // 좋아요, 조회수, 댓글 수
            Long likeCount = postLikeHistoryRepository.countByPostId(post.getId());
            Long viewCount = postViewHistoryRepository.countByPostId(post.getId());
            Integer replyCount = postReplyRepository.countCommunityPostReplyByPostId(post.getId());
            
            dto.setLikeCount(likeCount != null ? likeCount : 0L);
            dto.setViewCount(viewCount != null ? viewCount : 0L);
            dto.setReplyCount(replyCount != null ? Long.valueOf(replyCount) : 0L);
            
            // 커뮤니티 정보
            Community community = communityRepository.findCommunityByUuid(post.getCommunityUuid());
            if (community != null) {
                com.memeki.reviewhome.community.dto.RecentPostResponseDto.CommunityInfo communityInfo = 
                        new com.memeki.reviewhome.community.dto.RecentPostResponseDto.CommunityInfo();
                communityInfo.setUuid(community.getUuid());
                communityInfo.setName(community.getName());
                communityInfo.setType(community.getType());
                communityInfo.setGeoFeaturesName(community.getGeoFeaturesName());
                dto.setCommunity(communityInfo);
            }
            
            return dto;
        }).collect(Collectors.toList());
    }
}
