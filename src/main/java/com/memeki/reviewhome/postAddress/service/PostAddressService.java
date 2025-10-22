package com.memeki.reviewhome.postAddress.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;

import com.memeki.reviewhome.geo.dto.AddressToPointsResponseDto;
import com.memeki.reviewhome.geo.dto.GeoFeaturesByPostAddressInfoDto;
import com.memeki.reviewhome.geo.entity.GeoLocation;
import com.memeki.reviewhome.geo.repository.GeoLocationRepository;
import com.memeki.reviewhome.geo.service.GeoFeaturesService;
import com.memeki.reviewhome.geo.service.GeoService;
import com.memeki.reviewhome.global.exception.DefaultException;
import com.memeki.reviewhome.global.exceptionHandler.ErrorCode;
import com.memeki.reviewhome.postAddress.dto.GetBrTitleInfoResponseDto;
import com.memeki.reviewhome.postAddress.dto.GetBrTitleInfoResponseDto.ItemDto;
import com.memeki.reviewhome.postAddress.dto.SearchAddressDto;
import com.memeki.reviewhome.postAddress.dto.SearchAddressDto.GetResponse;
import com.memeki.reviewhome.postAddress.dto.SeoulBuildingResponseDto;
import com.memeki.reviewhome.postAddress.entity.PostAddress;
import com.memeki.reviewhome.postAddress.entity.PostAddressInfo;
import com.memeki.reviewhome.postAddress.repository.PostAddressInfoRepository;
import com.memeki.reviewhome.postAddress.repository.PostAddressRepository;
import com.memeki.reviewhome.commercial.service.CommercialService;

/*
 * @Todo
 * 1. 모든 로직을 세분화하여 각각의 메소드로 분리해야함.
 * 2. DongNm에 대한 전처리가 부족함.
 *    동 이름에는 1, 2, 3같은 알 수 없는 숫자와 관리실, 상가 등과 같은 단어가 포함되어있음.
 *    현재는 100미만의 숫자는 제외하고 저장하도록 되어있음.
 * 3. 중복되는 ItemDto 정보를 equals 와 hashcode 메서드로 통합시켜야함.
 */
@Service
public class PostAddressService {
    @Autowired
    WebClient webClient;

    @Autowired
    private GeoService geoService;

    @Autowired
    private GeoFeaturesService geoFeaturesService;

    @Autowired
    private CommercialService commercialService;

    @Value("${is-open-api-temp}")
    private boolean isOpenApiTemp;

    @Value("${temp-open-api-key}")
    private String tempOpenApiKey;

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
    public SearchAddressDto.PostResponse findAddress(
            SearchAddressDto.Request addressInfo) throws Exception {
        PostAddress postAddress = postAddressRepository.findPostAddressBySigunguCdAndBjdongCdAndBunAndJi(
                addressInfo.getSigunguCd(),
                addressInfo.getBjdongCd(),
                addressInfo.getBun(),
                addressInfo.getJi());
        if (postAddress == null) {
            // is-open-api-temp 값에 따라 서울시 API 또는 기존 API 사용
            if (isOpenApiTemp && addressInfo.getSigunguCd().startsWith("11")) {
                return searchAddressFromSeoul(addressInfo);
            } else {
                return searchAddress(addressInfo);
            }
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
                SearchAddressDto.PostResponse response = new SearchAddressDto.PostResponse();
                response.setDongNm(dongNm);
                response.setStatusCode(400);
                return response;
            } else {
                PostAddressInfo postAddressInfo = postAddressInfoRepository
                        .findPostAddressInfoByDongNmAndPostAddressId(
                                addressInfo.getDongNm(),
                                postAddress.getId());
                SearchAddressDto.PostResponse response = new SearchAddressDto.PostResponse();
                response.setStatusCode(200);
                response.setUuid(postAddressInfo.getUuid());
                return response;
            }
        }
        SearchAddressDto.PostResponse response = new SearchAddressDto.PostResponse();
        PostAddressInfo postAddressInfo = postAddressInfoRepository
                .findPostAddressInfoByPostAddressId(postAddress.getId());
        response.setUuid(postAddressInfo.getUuid());
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
    public SearchAddressDto.PostResponse searchAddress(SearchAddressDto.Request addressInfo) throws Exception {
        try {
            SearchAddressDto.PostResponse response = new SearchAddressDto.PostResponse();
            AtomicInteger pageNo = new AtomicInteger(1); // 페이지 번호 초기화
            int numOfRows = 10; // 페이지 당 결과 수
            int totalCount = 0; // 총 결과 개수
            boolean allResultsFetched = false; // 모든 결과를 가져왔는지 여부
            List<GetBrTitleInfoResponseDto.ItemDto> allItems = new ArrayList<>(); // 모든 아이템을 저장할 리스트
            List<String> dongNmList = new ArrayList<>();
            while (!allResultsFetched) {
                GetBrTitleInfoResponseDto result = webClient.get()
                        .uri(
                                uriBuilder -> {
                                    UriBuilder builder = uriBuilder.path("/1613000/BldRgstHubService/getBrTitleInfo")
                                            .queryParam("sigunguCd", addressInfo.getSigunguCd())
                                            .queryParam("bjdongCd", addressInfo.getBjdongCd())
                                            .queryParam("bun", addressInfo.getBun())
                                            .queryParam("numOfRows", numOfRows)
                                            .queryParam("pageNo", pageNo.get());
                        
                                    // ji가 null이 아닌 경우에만 쿼리 파라미터 추가
                                    if (addressInfo.getJi() != null) {
                                        builder.queryParam("ji", addressInfo.getJi());
                                    }
                                    return builder.build();
                                })
                        .retrieve()
                        .bodyToMono(GetBrTitleInfoResponseDto.class)
                        .doOnError(WebClientResponseException.class, ex -> {
                            // 응답값 확인
                            System.out.println("GetBrTitleInfoResponseDto ---> " + ex.getResponseBodyAsString());
                        }).block();
                System.out.println("GetBrTitleInfoResponseDto ---> " + result.toString());

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

            Set<ItemDto> uniqueItems = new HashSet<>(allItems);
            allItems = new ArrayList<>(uniqueItems);

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
            GeoFeaturesByPostAddressInfoDto geoFeatures = geoFeaturesService.getFeatureByPostAddress(savePostAddress);
            /*
             * 여러개 동이 아닌 경우 해당 주소를 저장하고 좌표를 반환한다.
             */
            if (!postAddress.isMultiple()) {
                ItemDto saveItem = allItems.get(0);

                PostAddressInfo postAddressInfo = new PostAddressInfo();
                postAddressInfo.setPostAddressId(savePostAddress.getId());
                postAddressInfo.setGeoFeaturesId(geoFeatures.getId());
                postAddressInfo.setGeoFeaturesName(geoFeatures.getEmdKorNm());
                // 동 이름이 있는 경우
                if (saveItem.getDongNm() != null &&
                        !saveItem.getDongNm().isBlank() &&
                        !saveItem.getDongNm().isEmpty()) {
                    saveItem.setDongNm(Integer.toString(extractDongNumber(saveItem.getDongNm())));
                }
                if (saveItem.getBldNm() != null && !saveItem.getBldNm().isBlank() && !saveItem.getBldNm().isEmpty()) {
                    saveItem.setBldNm(saveItem.getBldNm().trim());
                }
                if (saveItem.getBldNm() == null || saveItem.getBldNm().isBlank() || saveItem.getBldNm().isEmpty()) {
                    saveItem.setBldNm(newPlatPlc);
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
                
                // 좌표가 저장된 후 상권 정보 저장
                saveCommercialInfoAsync(savePostAddressInfo.getUuid());
                response.setStatusCode(200);
                response.setUuid(savePostAddressInfo.getUuid());

                return response;

            } else {
                for (int i = 0; i < allItems.size(); i++) {
                    // 우선 모든 동의 정보를 저장한다.
                    PostAddressInfo postAddressInfo = new PostAddressInfo();
                    postAddressInfo.setPostAddressId(savePostAddress.getId());
                    postAddressInfo.setGeoFeaturesId(geoFeatures.getId());
                    postAddressInfo.setGeoFeaturesName(geoFeatures.getEmdKorNm());
                    if (allItems.get(i).getDongNm().isBlank() || allItems.get(i).getDongNm().isEmpty()) {
                        continue;
                    }
                    int dongNum = extractDongNumber(allItems.get(i).getDongNm());
                    if (dongNum < 100) {
                        continue;
                    }
                    allItems.get(i).setDongNm(Integer.toString(dongNum));
                    if (allItems.get(i).getBldNm() != null && !allItems.get(i).getBldNm().isBlank()
                            && !allItems.get(i).getBldNm().isEmpty()) {
                        allItems.get(i).setBldNm(allItems.get(i).getBldNm().trim());
                    }
                    if (allItems.get(i).getBldNm() == null || allItems.get(i).getBldNm().isBlank()
                            || allItems.get(i).getBldNm().isEmpty()) {
                        allItems.get(i).setBldNm(newPlatPlc);
                    }
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
                    
                    // 좌표가 저장된 후 상권 정보 저장
                    saveCommercialInfoAsync(savePostAddressInfo.getUuid());
                }

                // 이제 선택한 동의 정보를 가져온다
                PostAddressInfo postAddressInfo = postAddressInfoRepository
                        .findPostAddressInfoByDongNmAndPostAddressId(
                                addressInfo.getDongNm(),
                                savePostAddress.getId());
                response.setUuid(postAddressInfo.getUuid());
                response.setStatusCode(200);
                return response;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
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

    public GetResponse findAddressByPostAddressInfoId(String postAddressInfoId) throws Exception {
        PostAddressInfo postAddressInfo = postAddressInfoRepository.findPostAddressInfoByUuid(postAddressInfoId);
        if (postAddressInfo == null) {
            throw new DefaultException(ErrorCode.NOT_FOUND);
        }
        PostAddress postAddress = postAddressRepository.findPostAddressById(postAddressInfo.getPostAddressId());
        GeoLocation geoLocation = geoLocationRepository.findGeoLocationByPostAddressInfoUuid(postAddressInfo.getUuid());
        if (geoLocation == null) {
            AddressToPointsResponseDto point = geoService.addressToPoints(postAddress.getNewPlatPlc(),
                    postAddressInfo.getUuid(), postAddress.getId(), null);
            if (point.getStatusCode() == 404) {
                throw new DefaultException(ErrorCode.NOT_FOUND);
            }
            geoLocation = geoLocationRepository.findGeoLocationByPostAddressInfoUuid(postAddressInfo.getUuid());
        }
        GetResponse response = new GetResponse();
        response.setStatusCode(200);
        response.setItem(postAddressInfo.toItemDto());
        response.setPoint_x(geoLocation.getPointX());
        response.setPoint_y(geoLocation.getPointY());
        return response;

    }
    
    /**
     * 상권 정보를 비동기로 저장하는 메소드
     * 좌표 정보를 가져와서 상권 API를 호출하여 상권 정보를 저장합니다.
     */
    /**
     * 서울시 임시 API를 통해 건물 정보를 가져오는 서비스 로직
     * 데이터센터 화재로 인한 기존 API 장애 시 사용
     * 
     * @param addressInfo
     * @throws Exception
     */
    public SearchAddressDto.PostResponse searchAddressFromSeoul(SearchAddressDto.Request addressInfo) throws Exception {
        try {
            SearchAddressDto.PostResponse response = new SearchAddressDto.PostResponse();
            List<GetBrTitleInfoResponseDto.ItemDto> allItems = new ArrayList<>();
            List<String> dongNmList = new ArrayList<>();
            
            // 서울시 API는 시군구코드가 11로 시작하는 경우만 처리
            if (!addressInfo.getSigunguCd().startsWith("11")) {
                throw new DefaultException(ErrorCode.NOT_FOUND);
            }
            
            // 서울시 API 호출 (WebClient 없이 직접 URL 생성)
            String apiUrl = String.format(
                "http://openapi.seoul.go.kr:8088/%s/json/vBigDjrTitle/1/1000",
                tempOpenApiKey
            );
            
            WebClient seoulWebClient = WebClient.builder()
                    .baseUrl(apiUrl)
                    .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10MB로 증가
                    .build();
            
            SeoulBuildingResponseDto result = seoulWebClient.get()
                    .retrieve()
                    .bodyToMono(SeoulBuildingResponseDto.class)
                    .doOnError(WebClientResponseException.class, ex -> {
                        System.out.println("SeoulBuildingResponseDto Error ---> " + ex.getResponseBodyAsString());
                    })
                    .block();
            
            System.out.println("=== 서울시 API 응답 시작 ===");
            System.out.println("요청 정보: sigunguCd=" + addressInfo.getSigunguCd() + 
                             ", bjdongCd=" + addressInfo.getBjdongCd() + 
                             ", bun=" + addressInfo.getBun() + 
                             ", ji=" + addressInfo.getJi());
            
            if (result == null || result.getVBigDjrTitle() == null || 
                result.getVBigDjrTitle().getRow() == null || 
                result.getVBigDjrTitle().getRow().isEmpty()) {
                System.out.println("서울시 API 응답 없음 또는 비어있음");
                throw new DefaultException(ErrorCode.NOT_FOUND);
            }
            
            System.out.println("전체 데이터 수: " + result.getVBigDjrTitle().getListTotalCount());
            System.out.println("받은 Row 수: " + result.getVBigDjrTitle().getRow().size());
            
            // 서울시 API 응답을 기존 ItemDto 형식으로 변환하면서 필터링
            List<SeoulBuildingResponseDto.Row> rows = result.getVBigDjrTitle().getRow();
            
            int matchCount = 0;
            for (SeoulBuildingResponseDto.Row row : rows) {
                // 서울시 API의 PLAT_PLC(대지위치)에서 시군구와 법정동 확인
                String platPlc = row.getPlatPlc();
                if (platPlc == null || platPlc.isEmpty()) {
                    continue;
                }
                
                // 주소 필터링: 주지번과 부지번이 일치하는지 확인
                String mnLotno = row.getMnLotno();
                String subLotno = row.getSubLotno();
                
                // 앞에 0을 제거하여 비교
                if (mnLotno != null) {
                    mnLotno = mnLotno.replaceFirst("^0+(?!$)", "");
                }
                if (subLotno != null) {
                    subLotno = subLotno.replaceFirst("^0+(?!$)", "");
                }
                
                String requestBun = addressInfo.getBun();
                String requestJi = addressInfo.getJi();
                if (requestBun != null) {
                    requestBun = requestBun.replaceFirst("^0+(?!$)", "");
                }
                if (requestJi != null) {
                    requestJi = requestJi.replaceFirst("^0+(?!$)", "");
                }
                
                // 주지번 매칭
                boolean bunMatch = (requestBun != null && mnLotno != null && requestBun.equals(mnLotno));
                
                // 부지번 매칭 (부지번이 없거나 0000인 경우 무시)
                boolean jiMatch = true;
                if (requestJi != null && !requestJi.isEmpty() && !requestJi.equals("0") && !requestJi.equals("0000")) {
                    if (subLotno != null && !subLotno.isEmpty() && !subLotno.equals("0") && !subLotno.equals("0000")) {
                        jiMatch = requestJi.equals(subLotno);
                    }
                }
                
                // 매칭 로그
                if (matchCount < 3) { // 처음 3개만 로그 출력
                    System.out.println("Row 검사: platPlc=" + platPlc + 
                                     ", mnLotno(원본)=" + row.getMnLotno() + "→" + mnLotno + 
                                     ", subLotno(원본)=" + row.getSubLotno() + "→" + subLotno + 
                                     ", bunMatch=" + bunMatch + ", jiMatch=" + jiMatch);
                }
                
                if (!bunMatch || !jiMatch) {
                    continue; // 주소가 일치하지 않으면 스킵
                }
                
                matchCount++;
                
                // ItemDto로 변환
                GetBrTitleInfoResponseDto.ItemDto item = row.toItemDto();
                
                // 동 이름 처리
                if (item.getDongNm() != null && !item.getDongNm().isBlank() && !item.getDongNm().isEmpty()) {
                    int dongNum = extractDongNumber(item.getDongNm());
                    if (dongNum >= 100) {
                        dongNmList.add(Integer.toString(dongNum));
                        item.setDongNm(Integer.toString(dongNum));
                        allItems.add(item);
                    }
                } else {
                    // 동명이 없는 경우에도 추가
                    allItems.add(item);
                }
            }
            
            System.out.println("필터링 완료: 매칭된 건물 수=" + matchCount + ", 최종 아이템 수=" + allItems.size());
            System.out.println("=== 서울시 API 응답 종료 ===");
            
            if (allItems.isEmpty()) {
                System.out.println("매칭된 건물이 없어 NOT_FOUND 예외 발생");
                throw new DefaultException(ErrorCode.NOT_FOUND);
            }
            
            // 중복 제거
            Set<ItemDto> uniqueItems = new HashSet<>(allItems);
            allItems = new ArrayList<>(uniqueItems);
            
            // PostAddress 저장
            PostAddress postAddress = new PostAddress();
            postAddress.setSigunguCd(addressInfo.getSigunguCd());
            postAddress.setBjdongCd(addressInfo.getBjdongCd());
            postAddress.setBun(addressInfo.getBun());
            postAddress.setJi(addressInfo.getJi());
            postAddress.setMultiple(dongNmList.size() > 1);
            
            // 이하 로직은 기존 searchAddress와 동일하게 처리
            // (DB 저장 로직은 기존 메소드와 동일하므로 공통 메소드로 추출하는 것이 좋지만,
            // 여기서는 임시 대응이므로 기존 로직을 그대로 사용)
            
            String newPlatPlc = "";
            int sameCount = 0;
            for (int i = 0; i < allItems.size(); i++) {
                if (allItems.get(i).getNewPlatPlc() != null && !allItems.get(i).getNewPlatPlc().isBlank()
                        && !allItems.get(i).getNewPlatPlc().isEmpty()) {
                    String target = allItems.get(i).getNewPlatPlc().trim();
                    if (target.equals(addressInfo.getNewPlatPlc())) {
                        newPlatPlc = allItems.get(i).getNewPlatPlc();
                        sameCount++;
                    }
                }
            }
            if (sameCount == 0 && allItems.size() != 0) {
                newPlatPlc = allItems.get(0).getNewPlatPlc();
            }

            postAddress.setNewPlatPlc(newPlatPlc);
            PostAddress savePostAddress = postAddressRepository.save(postAddress);
            GeoFeaturesByPostAddressInfoDto geoFeatures = geoFeaturesService.getFeatureByPostAddress(savePostAddress);

            // PostAddressInfo 저장
            for (int i = 0; i < allItems.size(); i++) {
                PostAddressInfo postAddressInfo = new PostAddressInfo();
                postAddressInfo.setPostAddressId(savePostAddress.getId());
                postAddressInfo.setGeoFeaturesId(geoFeatures.getId());
                postAddressInfo.setGeoFeaturesName(geoFeatures.getEmdKorNm());
                postAddressInfo.setMainPurpsCdNm(allItems.get(i).getMainPurpsCdNm());
                postAddressInfo.setHhldCnt(allItems.get(i).getHhldCnt());
                postAddressInfo.setGrndFlrCnt(allItems.get(i).getGrndFlrCnt());
                postAddressInfo.setUgrndFlrCnt(allItems.get(i).getUgrndFlrCnt());
                postAddressInfo.setIndrAutoUtcnt(allItems.get(i).getIndrAutoUtcnt());
                postAddressInfo.setOudrAutoUtcnt(allItems.get(i).getOudrAutoUtcnt());
                postAddressInfo.setIndrMechUtcnt(allItems.get(i).getIndrMechUtcnt());
                postAddressInfo.setOudrMechUtcnt(allItems.get(i).getOudrMechUtcnt());
                postAddressInfo.setStcnsDay(allItems.get(i).getStcnsDay());
                postAddressInfo.setUseAprDay(allItems.get(i).getUseAprDay());
                postAddressInfo.setNewPlatPlc(allItems.get(i).getNewPlatPlc());
                postAddressInfo.setPlatPlc(allItems.get(i).getPlatPlc());
                postAddressInfo.setRideUseElvtCnt(allItems.get(i).getRideUseElvtCnt());
                postAddressInfo.setBldNm(allItems.get(i).getBldNm());
                postAddressInfo.setDongNm(allItems.get(i).getDongNm());

                PostAddressInfo savePostAddressInfo = postAddressInfoRepository.save(postAddressInfo);

                // 좌표 저장
                try {
                    String dongNm = allItems.get(i).getDongNm();
                    System.out.println("좌표 저장 시도: newPlatPlc=" + newPlatPlc + 
                                     ", uuid=" + savePostAddressInfo.getUuid() + 
                                     ", dongNm=" + dongNm);
                    
                    AddressToPointsResponseDto point = geoService.addressToPoints(
                            newPlatPlc,
                            savePostAddressInfo.getUuid(),
                            savePostAddress.getId(),
                            (dongNm != null && !dongNm.isEmpty()) ? dongNm : null);
                    
                    System.out.println("좌표 저장 결과: statusCode=" + 
                                     (point != null ? point.getStatusCode() : "null"));
                    
                    if (point != null && point.getStatusCode() == 200) {
                        // 상권 정보 저장 (비동기)
                        saveCommercialInfoAsync(savePostAddressInfo.getUuid());
                    }
                } catch (Exception e) {
                    System.err.println("좌표 저장 중 오류 발생: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            if (savePostAddress.isMultiple()) {
                if (addressInfo.getDongNm() == null) {
                    Collections.sort(dongNmList, new Comparator<String>() {
                        @Override
                        public int compare(String s1, String s2) {
                            int number1 = extractDongNumber(s1);
                            int number2 = extractDongNumber(s2);
                            return Integer.compare(number1, number2);
                        }
                    });
                    response.setDongNm(dongNmList);
                    response.setStatusCode(400);
                    return response;
                } else {
                    PostAddressInfo postAddressInfo = postAddressInfoRepository
                            .findPostAddressInfoByDongNmAndPostAddressId(
                                    addressInfo.getDongNm(),
                                    savePostAddress.getId());
                    response.setStatusCode(200);
                    response.setUuid(postAddressInfo.getUuid());
                    return response;
                }
            }

            PostAddressInfo postAddressInfo = postAddressInfoRepository
                    .findPostAddressInfoByPostAddressId(savePostAddress.getId());
            response.setUuid(postAddressInfo.getUuid());
            response.setStatusCode(200);
            return response;

        } catch (DefaultException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("서울시 API 호출 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw new DefaultException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void saveCommercialInfoAsync(String postAddressInfoUuid) {
        try {
            // 좌표 정보 조회
            GeoLocation geoLocation = geoLocationRepository.findGeoLocationByPostAddressInfoUuid(postAddressInfoUuid);
            if (geoLocation != null && geoLocation.getPointX() != null && geoLocation.getPointY() != null) {
                // String 타입의 좌표를 double로 변환
                double pointX = Double.parseDouble(geoLocation.getPointX());
                double pointY = Double.parseDouble(geoLocation.getPointY());
                
                // 상권 정보 저장 (비동기 처리)
                commercialService.fetchAndSaveCommercialInfo(postAddressInfoUuid, pointX, pointY);
            }
        } catch (Exception e) {
            // 상권 정보 저장 실패는 전체 프로세스에 영향을 주지 않도록 로그만 남김
            System.err.println("상권 정보 저장 중 오류 발생 - UUID: " + postAddressInfoUuid + ", 오류: " + e.getMessage());
        }
    }
}
