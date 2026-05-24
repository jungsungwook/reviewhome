package com.memeki.reviewhome.commercial.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.memeki.reviewhome.commercial.dto.CommercialApiResponseDto;
import com.memeki.reviewhome.commercial.dto.CommercialItemDto;
import com.memeki.reviewhome.commercial.dto.CommercialResponseDto;
import com.memeki.reviewhome.commercial.entity.CommercialInfo;
import com.memeki.reviewhome.commercial.repository.CommercialInfoRepository;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommercialService {
    
    @Autowired
    private CommercialInfoRepository commercialInfoRepository;
    
    @Autowired
    private WebClient.Builder webClientBuilder;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Value("${open-api-key}")
    private String openApiKey;
    
    private static final int MAX_RADIUS = 2000; // 최대 반경 2000미터
    
    /**
     * 외부 API에서 상권 정보를 가져와서 저장
     */
    @Transactional
    public void fetchAndSaveCommercialInfo(String postAddressInfoUuid, double pointX, double pointY) {
        try {
            log.info("상권 정보 조회 시작 - UUID: {}, X: {}, Y: {}", postAddressInfoUuid, pointX, pointY);
            
            // 기존 데이터가 있으면 삭제
            if (commercialInfoRepository.existsByPostAddressInfoUuid(postAddressInfoUuid)) {
                commercialInfoRepository.deleteByPostAddressInfoUuid(postAddressInfoUuid);
                log.info("기존 상권 정보 삭제 완료 - UUID: {}", postAddressInfoUuid);
            }
            
            // 외부 API 호출
            CommercialApiResponseDto response = callCommercialApi(pointX, pointY);
            
            if (response != null && response.getBody() != null && response.getBody().getItems() != null) {
                List<CommercialInfo> commercialInfoList = new ArrayList<>();
                
                for (Object item : response.getBody().getItems()) {
                    try {
                        // Object를 CommercialItemDto로 변환
                        CommercialItemDto commercialItemDto = objectMapper.convertValue(item, CommercialItemDto.class);
                        CommercialInfo commercialInfo = convertToEntity(commercialItemDto, postAddressInfoUuid);
                        commercialInfoList.add(commercialInfo);
                    } catch (Exception e) {
                        log.warn("상권 정보 항목 변환 실패 - UUID: {}, 항목: {}", postAddressInfoUuid, item, e);
                    }
                }
                
                // 일괄 저장
                commercialInfoRepository.saveAll(commercialInfoList);
                log.info("상권 정보 저장 완료 - UUID: {}, 저장된 개수: {}", postAddressInfoUuid, commercialInfoList.size());
            } else {
                log.warn("상권 정보 응답이 비어있음 - UUID: {}", postAddressInfoUuid);
            }
            
        } catch (Exception e) {
            log.error("상권 정보 조회 및 저장 중 오류 발생 - UUID: {}", postAddressInfoUuid, e);
            throw new RuntimeException("상권 정보 처리 중 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * 외부 API 호출
     */
    private CommercialApiResponseDto callCommercialApi(double cx, double cy) {
        try {
            WebClient webClient = webClientBuilder.build();
            
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("apis.data.go.kr")
                            .path("/B553077/api/open/sdsc2/storeListInRadius")
                            .queryParam("ServiceKey", openApiKey)
                            .queryParam("pageNo", 1)
                            .queryParam("numOfRows", 1000)
                            .queryParam("radius", MAX_RADIUS)
                            .queryParam("cx", cx)
                            .queryParam("cy", cy)
                            .build())
                    .retrieve()
                    .bodyToMono(CommercialApiResponseDto.class)
                    .block();
                    
        } catch (Exception e) {
            log.error("상권 API 호출 중 오류 발생 - X: {}, Y: {}", cx, cy, e);
            return null;
        }
    }
    
    /**
     * DTO를 Entity로 변환
     */
    private CommercialInfo convertToEntity(CommercialItemDto dto, String postAddressInfoUuid) {
        CommercialInfo entity = new CommercialInfo();
        entity.setPostAddressInfoUuid(postAddressInfoUuid);
        entity.setBizesId(dto.getBizesId());
        entity.setBizesNm(dto.getBizesNm());
        entity.setBrchNm(dto.getBrchNm());
        entity.setIndsLclsCd(dto.getIndsLclsCd());
        entity.setIndsLclsNm(dto.getIndsLclsNm());
        entity.setIndsMclsCd(dto.getIndsMclsCd());
        entity.setIndsMclsNm(dto.getIndsMclsNm());
        entity.setIndsSclsCd(dto.getIndsSclsCd());
        entity.setIndsSclsNm(dto.getIndsSclsNm());
        entity.setKsicCd(dto.getKsicCd());
        entity.setKsicNm(dto.getKsicNm());
        entity.setCtprvnCd(dto.getCtprvnCd());
        entity.setCtprvnNm(dto.getCtprvnNm());
        entity.setSignguCd(dto.getSignguCd());
        entity.setSignguNm(dto.getSignguNm());
        entity.setAdongCd(dto.getAdongCd());
        entity.setAdongNm(dto.getAdongNm());
        entity.setLdongCd(dto.getLdongCd());
        entity.setLdongNm(dto.getLdongNm());
        entity.setLnoCd(dto.getLnoCd());
        entity.setPlotSctCd(dto.getPlotSctCd());
        entity.setPlotSctNm(dto.getPlotSctNm());
        entity.setLnoMnno(dto.getLnoMnno());
        entity.setLnoSlno(dto.getLnoSlno());
        entity.setLnoAdr(dto.getLnoAdr());
        entity.setRdnmCd(dto.getRdnmCd());
        entity.setRdnm(dto.getRdnm());
        entity.setBldMnno(dto.getBldMnno());
        entity.setBldSlno(dto.getBldSlno());
        entity.setBldMngNo(dto.getBldMngNo());
        entity.setBldNm(dto.getBldNm());
        entity.setRdnmAdr(dto.getRdnmAdr());
        entity.setOldZipcd(dto.getOldZipcd());
        entity.setNewZipcd(dto.getNewZipcd());
        entity.setDongNo(dto.getDongNo());
        entity.setFlrNo(dto.getFlrNo());
        entity.setHoNo(dto.getHoNo());
        entity.setLon(dto.getLon());
        entity.setLat(dto.getLat());
        return entity;
    }
    
    /**
     * Entity를 DTO로 변환
     */
    private CommercialItemDto convertToDto(CommercialInfo entity) {
        CommercialItemDto dto = new CommercialItemDto();
        dto.setBizesId(entity.getBizesId());
        dto.setBizesNm(entity.getBizesNm());
        dto.setBrchNm(entity.getBrchNm());
        dto.setIndsLclsCd(entity.getIndsLclsCd());
        dto.setIndsLclsNm(entity.getIndsLclsNm());
        dto.setIndsMclsCd(entity.getIndsMclsCd());
        dto.setIndsMclsNm(entity.getIndsMclsNm());
        dto.setIndsSclsCd(entity.getIndsSclsCd());
        dto.setIndsSclsNm(entity.getIndsSclsNm());
        dto.setKsicCd(entity.getKsicCd());
        dto.setKsicNm(entity.getKsicNm());
        dto.setCtprvnCd(entity.getCtprvnCd());
        dto.setCtprvnNm(entity.getCtprvnNm());
        dto.setSignguCd(entity.getSignguCd());
        dto.setSignguNm(entity.getSignguNm());
        dto.setAdongCd(entity.getAdongCd());
        dto.setAdongNm(entity.getAdongNm());
        dto.setLdongCd(entity.getLdongCd());
        dto.setLdongNm(entity.getLdongNm());
        dto.setLnoCd(entity.getLnoCd());
        dto.setPlotSctCd(entity.getPlotSctCd());
        dto.setPlotSctNm(entity.getPlotSctNm());
        dto.setLnoMnno(entity.getLnoMnno());
        dto.setLnoSlno(entity.getLnoSlno());
        dto.setLnoAdr(entity.getLnoAdr());
        dto.setRdnmCd(entity.getRdnmCd());
        dto.setRdnm(entity.getRdnm());
        dto.setBldMnno(entity.getBldMnno());
        dto.setBldSlno(entity.getBldSlno());
        dto.setBldMngNo(entity.getBldMngNo());
        dto.setBldNm(entity.getBldNm());
        dto.setRdnmAdr(entity.getRdnmAdr());
        dto.setOldZipcd(entity.getOldZipcd());
        dto.setNewZipcd(entity.getNewZipcd());
        dto.setDongNo(entity.getDongNo());
        dto.setFlrNo(entity.getFlrNo());
        dto.setHoNo(entity.getHoNo());
        dto.setLon(entity.getLon());
        dto.setLat(entity.getLat());
        return dto;
    }
    
    /**
     * 특정 건물의 상권 정보 조회
     */
    public CommercialResponseDto getCommercialInfoByUuid(String postAddressInfoUuid) {
        try {
            List<CommercialInfo> commercialInfoList = commercialInfoRepository.findByPostAddressInfoUuid(postAddressInfoUuid);
            
            List<CommercialItemDto> commercialItemDtos = commercialInfoList.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            
            CommercialResponseDto response = new CommercialResponseDto();
            response.setStatusCode(200);
            response.setMessage("성공");
            response.setCommercialList(commercialItemDtos);
            response.setTotalCount(commercialItemDtos.size());
            
            return response;
            
        } catch (Exception e) {
            log.error("상권 정보 조회 중 오류 발생 - UUID: {}", postAddressInfoUuid, e);
            
            CommercialResponseDto response = new CommercialResponseDto();
            response.setStatusCode(500);
            response.setMessage("상권 정보 조회 중 오류가 발생했습니다.");
            response.setCommercialList(new ArrayList<>());
            response.setTotalCount(0);
            
            return response;
        }
    }
    
    /**
     * 업종별 상권 정보 조회
     */
    public CommercialResponseDto getCommercialInfoByCategory(String categoryCode, String categoryType) {
        try {
            List<CommercialInfo> commercialInfoList = new ArrayList<>();
            
            switch (categoryType) {
                case "large":
                    commercialInfoList = commercialInfoRepository.findByIndsLclsCd(categoryCode);
                    break;
                case "medium":
                    commercialInfoList = commercialInfoRepository.findByIndsMclsCd(categoryCode);
                    break;
                case "small":
                    commercialInfoList = commercialInfoRepository.findByIndsSclsCd(categoryCode);
                    break;
                default:
                    throw new IllegalArgumentException("유효하지 않은 카테고리 타입입니다: " + categoryType);
            }
            
            List<CommercialItemDto> commercialItemDtos = commercialInfoList.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            
            CommercialResponseDto response = new CommercialResponseDto();
            response.setStatusCode(200);
            response.setMessage("성공");
            response.setCommercialList(commercialItemDtos);
            response.setTotalCount(commercialItemDtos.size());
            
            return response;
            
        } catch (Exception e) {
            log.error("업종별 상권 정보 조회 중 오류 발생 - 카테고리 코드: {}, 타입: {}", categoryCode, categoryType, e);
            
            CommercialResponseDto response = new CommercialResponseDto();
            response.setStatusCode(500);
            response.setMessage("업종별 상권 정보 조회 중 오류가 발생했습니다.");
            response.setCommercialList(new ArrayList<>());
            response.setTotalCount(0);
            
            return response;
        }
    }
}
