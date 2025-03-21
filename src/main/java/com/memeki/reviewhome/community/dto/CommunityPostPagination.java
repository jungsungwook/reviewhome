package com.memeki.reviewhome.community.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class CommunityPostPagination {
    private int page;
    private int size;
    private Long totalElements;
    private int totalPages;
    private boolean last;
}
