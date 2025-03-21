package com.memeki.reviewhome.image.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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

import lombok.extern.slf4j.Slf4j;

@Slf4j
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

    private static final List<String> SUPPORTED_MEDIA_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp",
            "video/mp4", "video/mpeg", "video/quicktime", "video/x-msvideo");

    public Image uploadMedia(MultipartFile file, Long userId) throws IOException {
        try {
            String originObjectName = file.getOriginalFilename();
            String contentType = file.getContentType();

            if (contentType == null || contentType.isEmpty() || contentType.equals("application/octet-stream")) {
                String fileExtension = getFileExtension(originObjectName);
                contentType = getContentTypeFromExtension(fileExtension);
            }

            if (!isValidMediaFile(contentType, originObjectName)) {
                throw new DefaultException(ErrorCode.INVALID_FILE);
            }

            byte[] mediaData = file.getBytes();
            long fileSize = file.getSize();

            String uuid = UUID.randomUUID().toString();
            String savedObjectName = uuid + "_" + originObjectName;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucketName(bucketName)
                    .namespaceName(namespace)
                    .objectName(savedObjectName)
                    .contentLength(fileSize)
                    .contentType(contentType)
                    .putObjectBody(new ByteArrayInputStream(mediaData))
                    .build();

            objectStorageClient.putObject(putObjectRequest);

            Image media = new Image();
            media.setUuid(uuid);
            media.setOriginalName(originObjectName);
            media.setSavedName(savedObjectName);
            media.setUrl("https://i.duriburn.com/" + savedObjectName);
            media.setSize(fileSize);
            media.setExtension(contentType);
            media.setCreatedBy(userId);

            return imageRepository.save(media);
        } catch (DefaultException e) {
            throw e;
        } catch (IOException e) {
            throw new DefaultException(ErrorCode.FILE_PROCESSING_ERROR);
        } catch (Exception e) {
            throw new DefaultException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isValidMediaFile(String contentType, String fileName) {
        return SUPPORTED_MEDIA_TYPES.contains(contentType) ||
                fileName.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif|webp|heic|heif|mp4|mpeg|mov|avi)$");
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    private String getContentTypeFromExtension(String extension) {
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "webp":
                return "image/webp";
            case "mp4":
                return "video/mp4";
            case "mpeg":
                return "video/mpeg";
            case "mov":
                return "video/quicktime";
            case "avi":
                return "video/x-msvideo";
            default:
                return "application/octet-stream";
        }
    }
}
