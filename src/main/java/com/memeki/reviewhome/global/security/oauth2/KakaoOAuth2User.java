package com.memeki.reviewhome.global.security.oauth2;

import java.util.Map;

public class KakaoOAuth2User extends OAuth2UserInfo {

    private Integer id;

    public KakaoOAuth2User(Map<String, Object> attributes) {
        super((Map<String, Object>) attributes.get("kakao_account"));
        Long kakao_raw_id = (Long) attributes.get("id");
        int kakao_id = kakao_raw_id.intValue();
        this.id = kakao_id;
    }

    @Override
    public String getOAuth2Id() {
        return this.id.toString();
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) ((Map<String, Object>) attributes.get("profile")).get("nickname");
    }
}