package com.memeki.reviewhome.mypage.controller;

import com.memeki.reviewhome.global.security.oauth2.UserPrincipal;
import com.memeki.reviewhome.mypage.dto.MyPageResponseDto;
import com.memeki.reviewhome.mypage.dto.UpdateNicknameRequestDto;
import com.memeki.reviewhome.mypage.dto.UpdateNicknameResponseDto;
import com.memeki.reviewhome.mypage.service.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/mypage")
@Tag(name = "MyPage", description = "마이페이지 API")
public class MyPageController {
    
    @Autowired
    private MyPageService myPageService;
    
    @GetMapping
    @Operation(summary = "마이페이지 정보 조회", description = "사용자의 마이페이지 정보를 조회합니다.")
    public ResponseEntity<MyPageResponseDto> getMyPageInfo(
            @Parameter(description = "인증된 사용자 정보", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        MyPageResponseDto response = myPageService.getMyPageInfo(userPrincipal.getId());
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/nickname")
    @Operation(summary = "닉네임 수정", description = "사용자의 닉네임을 수정합니다.")
    public ResponseEntity<UpdateNicknameResponseDto> updateNickname(
            @Parameter(description = "인증된 사용자 정보", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "닉네임 수정 요청", required = true)
            @Valid @RequestBody UpdateNicknameRequestDto request) {
        
        UpdateNicknameResponseDto response = myPageService.updateNickname(userPrincipal.getId(), request);
        return ResponseEntity.ok(response);
    }
}
