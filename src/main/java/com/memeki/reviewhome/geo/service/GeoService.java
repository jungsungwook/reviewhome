package com.memeki.reviewhome.geo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.memeki.reviewhome.geo.dto.AddressToPointsResponseDto;
import com.memeki.reviewhome.geo.dto.VWorldApiResponseDto;
import com.memeki.reviewhome.geo.entity.GeoLocation;
import com.memeki.reviewhome.geo.repository.GeoLocationRepository;
import com.memeki.reviewhome.global.exception.DefaultException;
import com.memeki.reviewhome.global.exceptionHandler.ErrorCode;

@Service
public class GeoService {
    @Value("${vworld-api-key}")
    private String vworldApiKey;

    private final WebClient webClient;

    private final GeoLocationRepository geoLocationRepository;

    public GeoService(WebClient.Builder webClientBuilder, GeoLocationRepository geoLocationRepository) {
        this.webClient = webClientBuilder.baseUrl("https://api.vworld.kr").build();
        this.geoLocationRepository = geoLocationRepository;
    }

    public AddressToPointsResponseDto addressToPoints(String address, int post_address_id) throws DefaultException {
        VWorldApiResponseDto vworldApiResponseDto = this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/req/search")
                        .queryParam("key", vworldApiKey)
                        .queryParam("service", "search")
                        .queryParam("request", "search")
                        .queryParam("type", "address")
                        .queryParam("category", "road")
                        .queryParam("query", address)
                        .build())
                .retrieve()
                .bodyToMono(VWorldApiResponseDto.class)
                .block(); // 동기식 호출
        if (vworldApiResponseDto != null) {
            // 응답값에서 items를 추출합니다.
            List<VWorldApiResponseDto.ItemDTO> items = vworldApiResponseDto.getResponse().getResult().getItems();
            // items 리스트가 비어있지 않은 경우 point 값을 반환합니다.
            if (!items.isEmpty()) {
                VWorldApiResponseDto.PointDTO point = items.get(0).getPoint();
                GeoLocation geoLocation = new GeoLocation();
                // geoLocation.setPostAddressId(post_address_id);
                geoLocation.setPointX(point.getX());
                geoLocation.setPointY(point.getY());
                geoLocationRepository.save(geoLocation);

                AddressToPointsResponseDto addressToPointsResponseDto = new AddressToPointsResponseDto();
                addressToPointsResponseDto.setStatusCode(200);
                addressToPointsResponseDto.setPoint_x(point.getX());
                addressToPointsResponseDto.setPoint_y(point.getY());
                return addressToPointsResponseDto;
            } else {
                throw new DefaultException(ErrorCode.NOT_FOUND);
            }
        } else {
            throw new DefaultException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
