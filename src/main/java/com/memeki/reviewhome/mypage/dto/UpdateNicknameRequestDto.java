package com.memeki.reviewhome.mypage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Schema(description = "닉네임 수정 요청 DTO")
public class UpdateNicknameRequestDto {
    
    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하여야 합니다.")
    @Schema(description = "새로운 닉네임", example = "새로운닉네임", required = true)
    private String nickname;
}
