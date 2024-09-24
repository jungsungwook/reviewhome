package com.memeki.reviewhome.image.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.memeki.reviewhome.global.exception.DefaultException;
import com.memeki.reviewhome.global.exceptionHandler.ErrorCode;
import com.memeki.reviewhome.image.entity.Image;
import com.memeki.reviewhome.image.repository.ImageRepository;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.PutObjectResponse;

@Service
public class ImageService {
    @Autowired
    private ObjectStorage objectStorageClient;

    @Autowired
    private ImageRepository imageRepository;

    @Value("${oci.bucket.name}")
    private String bucketName;

    @Value("${oci.namespace}")
    private String namespace;

    public Image uploadImage(MultipartFile file, Long userId) throws IOException {
        Image image = new Image();
        String originObjectName = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String savedObjectName = uuid + "_" + originObjectName;

        String contentType = file.getContentType();
        if (contentType == null || contentType.isEmpty()) {
            throw new DefaultException(ErrorCode.INVALID_FILE);
        }

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucketName(bucketName)
                .namespaceName(namespace)
                .objectName(savedObjectName)
                .contentLength(file.getSize())
                .contentType(contentType)
                .putObjectBody(file.getInputStream())
                .build();

        PutObjectResponse response = objectStorageClient.putObject(putObjectRequest);
        image.setUuid(uuid);
        image.setOriginalName(originObjectName);
        image.setSavedName(savedObjectName);
        image.setUrl("https://i.duriburn.com/" + savedObjectName);
        image.setSize(file.getSize());
        image.setExtension(contentType);
        image.setCreatedBy(userId);

        Image saveImage = imageRepository.save(image);
        return saveImage;
    }
}
