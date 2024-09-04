package com.memeki.reviewhome.auth.dto;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
public class CheckAuthenticationResponseDto {
    private long userId;
    private String userName;
    private String nickname;
    private int status;
}
