package com.memeki.reviewhome.mypage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Schema(description = "닉네임 수정 응답 DTO")
public class UpdateNicknameResponseDto {
    
    @Schema(description = "상태 코드", example = "200")
    private int statusCode;
    
    @Schema(description = "메시지", example = "닉네임이 성공적으로 변경되었습니다.")
    private String message;
    
    @Schema(description = "변경된 닉네임", example = "새로운닉네임")
    private String nickname;
}
