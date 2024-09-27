package com.memeki.reviewhome.banner.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.memeki.reviewhome.banner.dto.BannerDto;
import com.memeki.reviewhome.banner.dto.BannerResponseDto;
import com.memeki.reviewhome.banner.entity.Banner;
import com.memeki.reviewhome.banner.entity.BannerContent;
import com.memeki.reviewhome.banner.repository.BannerContentRepository;
import com.memeki.reviewhome.banner.repository.BannerRepository;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BannerService {
    @Autowired
    private BannerRepository bannerRepository;

    @Autowired
    private BannerContentRepository bannerContentRepository;

    public BannerResponseDto searchBanners(Long id, String type, String route, String name) {
        List<Banner> banners;

        if (id != null) {
            banners = bannerRepository.findById(id).map(Collections::singletonList).orElse(Collections.emptyList());
        } else if (type != null) {
            if (route != null) {
                if (name != null) {
                    banners = bannerRepository.findByBannerTypeAndBannerRouteAndBannerName(type, route, name);
                } else {
                    banners = bannerRepository.findByBannerTypeAndBannerRoute(type, route);
                }
            } else {
                banners = bannerRepository.findByBannerType(type);
            }
        } else if (route != null) {
            if (name != null) {
                banners = bannerRepository.findByBannerRouteAndBannerName(route, name);
            } else {
                banners = bannerRepository.findByBannerRoute(route);
            }
        } else if (name != null) {
            banners = bannerRepository.findByBannerName(name);
        } else {
            banners = bannerRepository.findAll();
        }

        List<BannerDto> bannerDtos = banners.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        BannerResponseDto responseDto = new BannerResponseDto();
        responseDto.setStatusCode(200);
        responseDto.setBannerDto(bannerDtos);

        return responseDto;
    }

    private BannerDto convertToDto(Banner banner) {
        BannerDto dto = new BannerDto();
        dto.setBanner(banner);
        List<BannerContent> bannerContents = bannerContentRepository.findByBannerId(banner.getId());
        dto.setBannerContentList(bannerContents);
        return dto;
    }
}
