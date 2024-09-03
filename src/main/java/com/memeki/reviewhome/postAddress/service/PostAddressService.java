package com.memeki.reviewhome.postAddress.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.memeki.reviewhome.geo.dto.AddressToPointsResponseDto;
import com.memeki.reviewhome.geo.entity.GeoLocation;
import com.memeki.reviewhome.geo.repository.GeoLocationRepository;
import com.memeki.reviewhome.geo.service.GeoService;
import com.memeki.reviewhome.global.exception.DefaultException;
import com.memeki.reviewhome.global.exceptionHandler.ErrorCode;
import com.memeki.reviewhome.postAddress.dto.GetBrTitleInfoResponseDto;
import com.memeki.reviewhome.postAddress.dto.GetBrTitleInfoResponseDto.ItemDto;
import com.memeki.reviewhome.postAddress.dto.SearchAddressDto;
import com.memeki.reviewhome.postAddress.entity.PostAddress;
import com.memeki.reviewhome.postAddress.entity.PostAddressInfo;
import com.memeki.reviewhome.postAddress.repository.PostAddressInfoRepository;
import com.memeki.reviewhome.postAddress.repository.PostAddressRepository;

/*
 * @Todo
 * 1. 모든 로직을 세분화하여 각각의 메소드로 분리해야함.
 * 2. DongNm에 대한 전처리가 부족함.
 *    동 이름에는 1, 2, 3같은 알 수 없는 숫자와 관리실, 상가 등과 같은 단어가 포함되어있음.
 *    현재는 100미만의 숫자는 제외하고 저장하도록 되어있음.
 */
@Service
public class PostAddressService {
    @Autowired
    WebClient webClient;

    @Autowired
    private GeoService geoService;

    private final PostAddressRepository postAddressRepository;
    private final PostAddressInfoRepository postAddressInfoRepository;
    private final GeoLocationRepository geoLocationRepository;

    public PostAddressService(
            PostAddressRepository postAddressRepository,
            PostAddressInfoRepository postAddressInfoRepository,
            GeoLocationRepository geoLocationRepository) {
        this.postAddressRepository = postAddressRepository;
        this.postAddressInfoRepository = postAddressInfoRepository;
        this.geoLocationRepository = geoLocationRepository;
    }

    /**
     * DB에서 주소를 찾는 서비스 로직
     * DB에 주소가 없을 경우 공공API에서 정보를 가져온 뒤 DB에 저장
     * 
     * @param addressInfo
     * @throws Exception
     */
    public SearchAddressDto.Response findAddress(
            SearchAddressDto.Request addressInfo) {
        PostAddress postAddress = postAddressRepository.findPostAddressBySigunguCdAndBjdongCdAndBunAndJi(
                addressInfo.getSigunguCd(),
                addressInfo.getBjdongCd(),
                addressInfo.getBun(),
                addressInfo.getJi());
        if (postAddress == null) {
            return searchAddress(addressInfo);
        }
        if (postAddress.isMultiple()) {
            if (addressInfo.getDongNm() == null) {
                List<PostAddressInfo> postAddressInfos = postAddressInfoRepository
                        .findAllByPostAddressId(postAddress.getId());
                List<String> dongNm = new ArrayList<>();
                for (int i = 0; i < postAddressInfos.size(); i++) {
                    dongNm.add(postAddressInfos.get(i).getDongNm());
                }
                Collections.sort(dongNm, new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        int number1 = extractDongNumber(s1);
                        int number2 = extractDongNumber(s2);
                        return Integer.compare(number1, number2);
                    }
                });
                SearchAddressDto.Response response = new SearchAddressDto.Response();
                response.setDongNm(dongNm);
                response.setStatusCode(400);
                return response;
            } else {
                PostAddressInfo postAddressInfo = postAddressInfoRepository
                        .findPostAddressInfoByDongNm(addressInfo.getDongNm());
                GeoLocation geoLocation = geoLocationRepository
                        .findGeoLocationByPostAddressInfoUuid(postAddressInfo.getUuid());
                SearchAddressDto.Response response = new SearchAddressDto.Response();
                response.setItem(postAddressInfo.toItemDto());
                response.setPoint_x(geoLocation.getPointX());
                response.setPoint_y(geoLocation.getPointY());
                response.setStatusCode(200);
                return response;
            }
        }
        SearchAddressDto.Response response = new SearchAddressDto.Response();
        PostAddressInfo postAddressInfo = postAddressInfoRepository
                .findPostAddressInfoByPostAddressId(postAddress.getId());
        GeoLocation geoLocation = geoLocationRepository
                .findGeoLocationByPostAddressInfoUuid(postAddressInfo.getUuid());
        response.setItem(postAddressInfo.toItemDto());
        response.setPoint_x(geoLocation.getPointX());
        response.setPoint_y(geoLocation.getPointY());
        response.setStatusCode(200);
        return response;
    }

    /**
     * 공공API에서 정보를 가져온 뒤 DB에 저장하는 서비스 로직
     * 해당 로직에 접근했다는건 DB에 주소가 없다는 뜻.
     * 
     * @param addressInfo
     * @throws Exception
     */
    public SearchAddressDto.Response searchAddress(SearchAddressDto.Request addressInfo) {

        SearchAddressDto.Response response = new SearchAddressDto.Response();
        AtomicInteger pageNo = new AtomicInteger(1); // 페이지 번호 초기화
        int numOfRows = 10; // 페이지 당 결과 수
        int totalCount = 0; // 총 결과 개수
        boolean allResultsFetched = false; // 모든 결과를 가져왔는지 여부
        List<GetBrTitleInfoResponseDto.ItemDto> allItems = new ArrayList<>(); // 모든 아이템을 저장할 리스트
        List<String> dongNmList = new ArrayList<>();
        while (!allResultsFetched) {
            GetBrTitleInfoResponseDto result = webClient.get()
                    .uri(
                            uriBuilder -> uriBuilder
                                    .path("/1613000/BldRgstService_v2/getBrTitleInfo")
                                    .queryParam("sigunguCd", addressInfo.getSigunguCd())
                                    .queryParam("bjdongCd", addressInfo.getBjdongCd())
                                    .queryParam("bun", addressInfo.getBun())
                                    .queryParam("ji", addressInfo.getJi())
                                    .queryParam("numOfRows", numOfRows)
                                    .queryParam("pageNo", pageNo.get()) // 현재 페이지 번호
                                    .build())
                    .retrieve()
                    .bodyToMono(GetBrTitleInfoResponseDto.class)
                    .doOnError(WebClientResponseException.class, ex -> {
                        // 응답값 확인
                        System.out.println("GetBrTitleInfoResponseDto ---> " + ex.getResponseBodyAsString());
                    }).block();

            if (result == null || result.getResponse() == null || result.getResponse().getBody() == null) {
                throw new DefaultException(ErrorCode.INTERNAL_SERVER_ERROR);
            }

            List<GetBrTitleInfoResponseDto.ItemDto> items = result.getResponse().getBody().getItems().getItem();

            // 첫 번째 응답에서 totalCount를 가져옵니다.
            if (totalCount == 0) {
                totalCount = result.getResponse().getBody().getTotalCount();
            }
            if (totalCount == 0) {
                throw new DefaultException(ErrorCode.NOT_FOUND);
            }
            // 동 이름은 숫자부분만 추출하여 String으로 저장
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getDongNm().isBlank() || items.get(i).getDongNm().isEmpty()) {
                    continue;
                }
                int dongNum = extractDongNumber(items.get(i).getDongNm());
                if (dongNum < 100) {
                    continue;
                }
                dongNmList.add(Integer.toString(dongNum));
                items.get(i).setDongNm(Integer.toString(dongNum));
            }

            allItems.addAll(items); // 현재 페이지의 아이템들을 모두 저장

            if (pageNo.get() * numOfRows >= totalCount) {
                allResultsFetched = true; // 모든 결과를 가져왔으면 반복 종료
            } else {
                pageNo.incrementAndGet(); // 다음 페이지로 이동
            }
        }
        PostAddress postAddress = new PostAddress();
        postAddress.setSigunguCd(addressInfo.getSigunguCd());
        postAddress.setBjdongCd(addressInfo.getBjdongCd());
        postAddress.setBun(addressInfo.getBun());
        postAddress.setJi(addressInfo.getJi());
        postAddress.setMultiple(false);

        if (allItems.size() == 0) {
            throw new DefaultException(ErrorCode.NOT_FOUND);
        }
        if (dongNmList.size() > 1) {
            postAddress.setMultiple(true);
        }

        /*
         * 우선 postAddress를 저장한다.
         */
        String newPlatPlc = "";
        int sameCount = 0;
        for (int i = 0; i < allItems.size(); i++) {
            if (allItems.get(i).getNewPlatPlc() != null && !allItems.get(i).getNewPlatPlc().isBlank()
                    && !allItems.get(i).getNewPlatPlc().isEmpty()) {
                String target = allItems.get(i).getNewPlatPlc().trim();
                if (target.equals(addressInfo.getNewPlatPlc())) {
                    // 만약 bldNm, dongNm에 상가, 관리실 등이 포함되어있으면 제외
                    /*
                     * ItemDto{mainPurpsCdNm='공동주택', hhldCnt=0, grndFlrCnt=3, ugrndFlrCnt=0,
                     * indrAutoUtcnt=0, oudrAutoUtcnt=0, indrMechUtcnt=0, oudrMechUtcnt=0,
                     * stcnsDay='19970222', useAprDay='19990612', newPlatPlc=' 서울특별시 송파구 거마로9길 19',
                     * platPlc='서울특별시 송파구 거여동 136번지', rideUseElvtCnt=0, bldNm='삼호아파트 상가동', dongNm='
                     * ', bun='0136', ji='0000'}
                     * 
                     * ItemDto{mainPurpsCdNm='공동주택', hhldCnt=142, grndFlrCnt=19, ugrndFlrCnt=2,
                     * indrAutoUtcnt=94, oudrAutoUtcnt=53, indrMechUtcnt=0, oudrMechUtcnt=0,
                     * stcnsDay='19970222', useAprDay='19990612', newPlatPlc=' 서울특별시 송파구 거마로9길 19',
                     * platPlc='서울특별시 송파구 거여동 136번지', rideUseElvtCnt=2, bldNm='삼호아파트 제101동',
                     * dongNm=' ', bun='0136', ji='0000'}
                     * sameCount = 2
                     * 위와 같은 어이없는 사례도 있기 때문에 이렇게 처리해줘야함.
                     */
                    if (allItems.get(i).getBldNm().contains("상가") || allItems.get(i).getBldNm().contains("관리실")) {
                        continue;
                    }
                    if (allItems.get(i).getDongNm().contains("상가") || allItems.get(i).getDongNm().contains("관리실")) {
                        continue;
                    }
                    newPlatPlc = target;
                    sameCount += 1;
                }
            }
        }
        postAddress.setMultiple(
                sameCount > 1 ? true : false);

        if (newPlatPlc.equals("")) {
            newPlatPlc = allItems.get(0).getNewPlatPlc();
        }
        postAddress.setNewPlatPlc(newPlatPlc);

        /*
         * 여러개의 결과인데 동 이름이 없는 경우 동이름을 반환하여 사용자에게 선택하게 한다.
         */
        if (postAddress.isMultiple() && addressInfo.getDongNm() == null) {
            response.setStatusCode(400);

            // for (int i = 0; i < allItems.size(); i++) {
            // if (allItems.get(i).getDongNm().isBlank() ||
            // allItems.get(i).getDongNm().isEmpty()) {
            // continue;
            // }
            // int dong = extractDongNumber(allItems.get(i).getDongNm());
            // if (dong == 0)
            // continue;
            // dongNmList.add(Integer.toString(dong));
            // }

            Collections.sort(dongNmList, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    int number1 = extractDongNumber(s1);
                    int number2 = extractDongNumber(s2);
                    return Integer.compare(number1, number2);
                }
            });
            response.setDongNm(dongNmList);
            return response;
        }
        response.setDongNm(null);
        PostAddress savePostAddress = postAddressRepository.save(postAddress);

        /*
         * 여러개 동이 아닌 경우 해당 주소를 저장하고 좌표를 반환한다.
         */
        if (!postAddress.isMultiple()) {
            ItemDto saveItem = allItems.get(0);

            PostAddressInfo postAddressInfo = new PostAddressInfo();
            postAddressInfo.setPostAddressId(savePostAddress.getId());
            // 동 이름이 있는 경우
            if (saveItem.getDongNm() != null &&
                    !saveItem.getDongNm().isBlank() &&
                    !saveItem.getDongNm().isEmpty()) {
                saveItem.setDongNm(Integer.toString(extractDongNumber(saveItem.getDongNm())));
            }
            postAddressInfo.copyFromItemDto(saveItem);
            PostAddressInfo savePostAddressInfo = postAddressInfoRepository.save(postAddressInfo);

            AddressToPointsResponseDto point = geoService.addressToPoints(newPlatPlc, savePostAddressInfo.getUuid(),
                    savePostAddress.getId(), null);
            if (point.getStatusCode() == 404) {
                postAddressRepository.delete(savePostAddress);
                postAddressInfoRepository.delete(savePostAddressInfo);
                throw new DefaultException(ErrorCode.NOT_FOUND);
            }
            response.setStatusCode(200);

            response.setItem(saveItem);
            response.setPoint_x(point.getPoint_x());
            response.setPoint_y(point.getPoint_y());

            return response;

        } else {
            for (int i = 0; i < allItems.size(); i++) {
                // 우선 모든 동의 정보를 저장한다.
                PostAddressInfo postAddressInfo = new PostAddressInfo();
                postAddressInfo.setPostAddressId(savePostAddress.getId());
                if (allItems.get(i).getDongNm().isBlank() || allItems.get(i).getDongNm().isEmpty()) {
                    continue;
                }
                int dongNum = extractDongNumber(allItems.get(i).getDongNm());
                if (dongNum < 100) {
                    continue;
                }
                allItems.get(i).setDongNm(Integer.toString(dongNum));
                postAddressInfo.copyFromItemDto(allItems.get(i));
                PostAddressInfo savePostAddressInfo = postAddressInfoRepository.save(postAddressInfo);

                // 좌표를 저장한다.
                AddressToPointsResponseDto point = geoService.addressToPoints(newPlatPlc,
                        savePostAddressInfo.getUuid(),
                        savePostAddress.getId(), Integer.toString(dongNum));
                if (point.getStatusCode() == 404) {
                    postAddressRepository.delete(savePostAddress);
                    postAddressInfoRepository.delete(savePostAddressInfo);
                    throw new DefaultException(ErrorCode.NOT_FOUND);
                }
            }

            // 이제 선택한 동의 정보를 가져온다
            PostAddressInfo postAddressInfo = postAddressInfoRepository
                    .findPostAddressInfoByDongNm(addressInfo.getDongNm());
            GeoLocation geoLocation = geoLocationRepository
                    .findGeoLocationByPostAddressInfoUuid(postAddressInfo.getUuid());
            response.setItem(postAddressInfo.toItemDto());
            response.setPoint_x(geoLocation.getPointX());
            response.setPoint_y(geoLocation.getPointY());
            response.setStatusCode(200);
            return response;
        }
    }

    private static int extractDongNumber(String dong) {
        // 공백 제거하고 숫자만 추출
        String trimmed = dong.trim().replaceAll("[^0-9]", "");
        if (trimmed.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(trimmed);
    }
}
