package com.memeki.reviewhome.geo.service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.memeki.reviewhome.geo.dto.AddressToPointsResponseDto;
import com.memeki.reviewhome.geo.dto.VWorldApiResponseDto;
import com.memeki.reviewhome.geo.entity.GeoLegalDong;
import com.memeki.reviewhome.geo.entity.GeoLocation;
import com.memeki.reviewhome.geo.repository.GeoLegalDongRepository;
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

    @Autowired
    private GeoLegalDongRepository geoLegalDongRepository;

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
            if (vworldApiResponseDto.getResponse().getResult() != null) {
                // 응답에서 items를 추출합니다.
                List<VWorldApiResponseDto.ItemDTO> items = vworldApiResponseDto.getResponse().getResult().getItems();

                // items가 비어있지 않은 경우 동 이름을 검사합니다.
                for (VWorldApiResponseDto.ItemDTO item : items) {
                    if (dongNm == null) {
                        String targetA = item.getAddress().getRoad().replaceAll("\\(.*?\\)", "").trim();
                        String targetB = address.replaceAll("\\(.*?\\)", "").trim();
                        if (targetA.equals(targetB)) {
                            point = item.getPoint();
                            found = true;
                            break;
                        }
                    }
                    String dong_road = item.getAddress().getRoad() != null
                            ? Integer.toString(extractDongNumber(item.getAddress().getRoad()))
                            : "";
                    String dong_bldnm = item.getAddress().getBldnm() != null
                            ? Integer.toString(extractDongNumber(item.getAddress().getBldnm()))
                            : "";
                    String dong_bldnmdc = item.getAddress().getBldnmdc() != null
                            ? Integer.toString(extractDongNumber(item.getAddress().getBldnmdc()))
                            : "";

                    if (dongNm != null && dongNm.equals(dong_bldnmdc)) {
                        point = item.getPoint();
                        found = true;
                        break;
                    } else if (dongNm != null && dongNm.equals(dong_bldnm)) {
                        point = item.getPoint();
                        found = true;
                        break;
                    } else if (dongNm != null && dongNm.equals(dong_road)) {
                        point = item.getPoint();
                        found = true;
                        break;
                    }
                }

                // 다음 페이지로 이동
                if (!found) {
                    // 만약 다음 페이지가 없으면 반복 종료
                    if (Integer.parseInt(vworldApiResponseDto.getResponse().getRecord().getTotal()) <= _page.get()
                            * _size) {
                        break;
                    }
                    _page.incrementAndGet();
                    if (items.isEmpty()) { // 더 이상 결과가 없으면 반복 종료
                        break;
                    }
                }
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

    public GeoLegalDong getBjdong(
            String bjdongCd,
            String bjdongNm,
            Integer bjdongType) {
        if (bjdongCd == null && bjdongNm == null) {
            throw new DefaultException(ErrorCode.INVALID_PARAMETER);
        }

        if (bjdongCd != null) {
            return geoLegalDongRepository.findByBjdCd(bjdongCd);
        } else {
            if (bjdongType == null) {
                return geoLegalDongRepository.findByLegalDongNm(bjdongNm);
            } else {
                switch (bjdongType) {
                    case 0:
                        return geoLegalDongRepository.findBySidoNm(bjdongNm);
                    case 1:
                        return geoLegalDongRepository.findBySigunguNm(bjdongNm);
                    case 2:
                        return geoLegalDongRepository.findByLegalDongNm(bjdongNm);
                    case 3:
                        return geoLegalDongRepository.findByLegalLiNm(bjdongNm);
                    default:
                        return geoLegalDongRepository.findByBjdCd(bjdongCd);
                }
            }
        }
    }
}
