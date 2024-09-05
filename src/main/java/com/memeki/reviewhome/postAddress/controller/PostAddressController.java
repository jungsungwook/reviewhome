package com.memeki.reviewhome.postAddress.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.memeki.reviewhome.global.exception.DefaultException;
import com.memeki.reviewhome.postAddress.dto.SearchAddressDto;
import com.memeki.reviewhome.postAddress.dto.SearchAddressDto.GetResponse;
import com.memeki.reviewhome.postAddress.service.PostAddressService;

@RestController
@RequestMapping(value = "/api/post-address")
public class PostAddressController {
    @Autowired
    private PostAddressService postAddressService;

    @GetMapping("/test")
    public String test() {
        return new String();
    }

    @PostMapping(value = "/search")
    public ResponseEntity<SearchAddressDto.PostResponse> searchAddress(
            @RequestBody SearchAddressDto.Request addressInfo) throws Exception {
        try {
            SearchAddressDto.PostResponse response = postAddressService.findAddress(addressInfo);
            return ResponseEntity.ok(response);
        } catch (DefaultException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping(value = "/search")
    public ResponseEntity<GetResponse> searchAddressByPostAddressId(
            @RequestParam String uuid) throws Exception {
                System.out.println("uuid: " + uuid);
        try {
            GetResponse response = postAddressService.findAddressByPostAddressInfoId(uuid);
            return ResponseEntity.ok(response);
        } catch (DefaultException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
