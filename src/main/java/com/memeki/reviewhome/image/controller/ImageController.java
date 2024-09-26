package com.memeki.reviewhome.image.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.memeki.reviewhome.global.security.oauth2.UserPrincipal;
import com.memeki.reviewhome.image.dto.ImageCreateResponse;
import com.memeki.reviewhome.image.entity.Image;
import com.memeki.reviewhome.image.service.ImageService;

import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping(value = "/api/image")
public class ImageController {
    @Autowired
    private ImageService imageService;

    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ImageCreateResponse> uploadImage(
            @RequestParam(value = "file", required = true) MultipartFile file,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userDetails) throws IOException {
        System.out.println("ImageController.uploadImage");
        try {
            Image result = imageService.uploadImage(file, userDetails.getId());
            ImageCreateResponse response = new ImageCreateResponse();
            response.setStatusCode(201);
            response.setImageUrl(result.getUrl());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
