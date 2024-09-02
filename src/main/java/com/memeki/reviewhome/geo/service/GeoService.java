package com.memeki.reviewhome.geo.service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.memeki.reviewhome.geo.dto.AddressToPointsResponseDto;
import com.memeki.reviewhome.geo.dto.VWorldApiResponseDto;
import com.memeki.reviewhome.geo.entity.GeoLocation;
import com.memeki.reviewhome.geo.repository.GeoLocationRepository;
import com.memeki.reviewhome.global.exception.DefaultException;
import com.memeki.reviewhome.global.exceptionHandler.ErrorCode;

import reactor.util.annotation.Nullable;

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

    private static int extractDongNumber(String dong) {
        // 공백 제거하고 숫자만 추출
        String trimmed = dong.trim().replaceAll("[^0-9]", "");
        if (trimmed.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(trimmed);
    }

    public AddressToPointsResponseDto addressToPoints(String address, String uuid, int post_address_id,
            @Nullable String dongNm)
            throws DefaultException {
        AtomicInteger _page = new AtomicInteger(1); // AtomicInteger로 변경
        int _size = 10; // 페이지당 검색 결과 수
        boolean found = false;
        VWorldApiResponseDto.PointDTO point = null;

        while (!found) {
            // VWorld API 호출
            VWorldApiResponseDto vworldApiResponseDto = this.webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/req/search")
                            .queryParam("key", vworldApiKey)
                            .queryParam("service", "search")
                            .queryParam("request", "search")
                            .queryParam("type", "address")
                            .queryParam("category", "road")
                            .queryParam("query", address)
                            .queryParam("page", _page.get())
                            .queryParam("size", _size)
                            .build())
                    .retrieve()
                    .bodyToMono(VWorldApiResponseDto.class)
                    .block(); // 동기식 호출

            if (vworldApiResponseDto != null) {
                // 응답에서 items를 추출합니다.
                List<VWorldApiResponseDto.ItemDTO> items = vworldApiResponseDto.getResponse().getResult().getItems();

                // items가 비어있지 않은 경우 동 이름을 검사합니다.
                for (VWorldApiResponseDto.ItemDTO item : items) {
                    String dongNum = Integer.toString(extractDongNumber(item.getAddress().getBldnmdc()));
                    if (dongNm == null || dongNm.equals(dongNum)) {
                        // 일치하는 동이 있는 경우 처리
                        point = item.getPoint();
                        found = true;
                        break;
                    }
                }

                // 다음 페이지로 이동
                if (!found) {
                    _page.incrementAndGet();
                    if (items.isEmpty()) { // 더 이상 결과가 없으면 반복 종료
                        break;
                    }
                }
            } else {
                throw new DefaultException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
        if (point != null) {
            GeoLocation geoLocation = new GeoLocation();
            geoLocation.setPointX(point.getX());
            geoLocation.setPointY(point.getY());
            geoLocation.setPostAddressInfoUuid(uuid);
            geoLocationRepository.save(geoLocation);

            AddressToPointsResponseDto addressToPointsResponseDto = new AddressToPointsResponseDto();
            addressToPointsResponseDto.setStatusCode(200);
            addressToPointsResponseDto.setPoint_x(point.getX());
            addressToPointsResponseDto.setPoint_y(point.getY());
            return addressToPointsResponseDto;
        } else {
            throw new DefaultException(ErrorCode.NOT_FOUND);
        }
    }
}
