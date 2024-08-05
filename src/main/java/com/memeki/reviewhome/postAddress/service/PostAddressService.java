package com.memeki.reviewhome.postAddress.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.memeki.reviewhome.geo.dto.AddressToPointsResponseDto;
import com.memeki.reviewhome.geo.service.GeoService;
import com.memeki.reviewhome.global.exception.DefaultException;
import com.memeki.reviewhome.global.exceptionHandler.ErrorCode;
import com.memeki.reviewhome.postAddress.dto.GetBrTitleInfoResponseDto;
import com.memeki.reviewhome.postAddress.dto.SearchAddressDto;
import com.memeki.reviewhome.postAddress.entity.PostAddress;
import com.memeki.reviewhome.postAddress.repository.PostAddressRepository;

@Service
public class PostAddressService {
    @Autowired
    WebClient webClient;

    @Autowired
    private GeoService geoService;

    private final PostAddressRepository postAddressRepository;

    public PostAddressService(PostAddressRepository postAddressRepository) {
        this.postAddressRepository = postAddressRepository;
    }

    /**
     * DB에서 주소를 찾는 서비스 로직
     * 
     * @param addressInfo
     * @throws Exception
     */
    public SearchAddressDto.Response findAddress(
            SearchAddressDto.Request addressInfo) throws Exception {
        try {
            PostAddress postAddress = postAddressRepository.findPostAddressBySigunguCdAndBjdongCdAndBunAndJi(
                    addressInfo.getSigunguCd(),
                    addressInfo.getBjdongCd(),
                    addressInfo.getBun(),
                    addressInfo.getJi());
            if (postAddress == null) {
                return searchAddress(addressInfo);
            }
            SearchAddressDto.Response response = new SearchAddressDto.Response();
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 공공API에서 정보를 가져온 뒤 DB에 저장하는 서비스 로직
     * 
     * @param addressInfo
     * @throws Exception
     */
    public SearchAddressDto.Response searchAddress(SearchAddressDto.Request addressInfo) throws Exception {
        GetBrTitleInfoResponseDto result = webClient.get()
                .uri(
                        uriBuilder -> uriBuilder
                                .path("/1613000/BldRgstService_v2/getBrTitleInfo")
                                .queryParam("sigunguCd", addressInfo.getSigunguCd())
                                .queryParam("bjdongCd", addressInfo.getBjdongCd())
                                .queryParam("bun", addressInfo.getBun())
                                .queryParam("ji", addressInfo.getJi())
                                .build())
                .retrieve()
                .bodyToMono(GetBrTitleInfoResponseDto.class)
                .doOnError(WebClientResponseException.class, ex -> {
                    // 응답값 확인
                    System.out.println("GetBrTitleInfoResponseDto ---> " + ex.getResponseBodyAsString());
                }).block();
        PostAddress postAddress = new PostAddress();
        postAddress.setSigunguCd(addressInfo.getSigunguCd());
        postAddress.setBjdongCd(addressInfo.getBjdongCd());
        postAddress.setBun(addressInfo.getBun());
        postAddress.setJi(addressInfo.getJi());
        postAddress.setMultiple(false);
        
        if (result.getResponse().getBody().getTotalCount() == 0) {
            throw new DefaultException(ErrorCode.NOT_FOUND);
        } else if (result.getResponse().getBody().getTotalCount() > 1) {
            // throw new DefaultException(ErrorCode.MULTIPLE_RESULT);
            postAddress.setMultiple(true);
        }

        List<GetBrTitleInfoResponseDto.ItemDto> items = result.getResponse().getBody().getItems().getItem();
        String newPlatPlc = items.get(0).getNewPlatPlc();
        postAddress.setNewPlatPlc(newPlatPlc);

        PostAddress savePostAddress = postAddressRepository.save(postAddress);
        AddressToPointsResponseDto point = geoService.addressToPoints(newPlatPlc, savePostAddress.getId());
        if (point.getStatusCode() == 404) {
            postAddressRepository.delete(savePostAddress);
            throw new DefaultException(ErrorCode.NOT_FOUND);
        }

        SearchAddressDto.Response response = new SearchAddressDto.Response();
        response.setStatusCode(200);
        response.setItem(items.get(0));
        response.setPoint_x(point.getPoint_x());
        response.setPoint_y(point.getPoint_y());

        return response;
    }
}
