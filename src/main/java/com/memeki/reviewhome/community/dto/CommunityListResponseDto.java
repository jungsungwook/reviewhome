package com.memeki.reviewhome.community.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CommunityListResponseDto {
    private List<CommunityItemDto> buildingCommunities;
    private List<CommunityItemDto> townCommunities;
    
    @Getter
    @Setter
    @NoArgsConstructor
    public static class CommunityItemDto {
        private String uuid;
        private String type;
        private String name;
        private String description;
        private String geoFeaturesName;
        private int userCount;
        private String createdAt;
    }
}

