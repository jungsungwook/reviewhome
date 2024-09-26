package com.memeki.reviewhome.image.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

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

    private static final List<String> SUPPORTED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/heic", "image/heif");

    public Image uploadImage(MultipartFile file, Long userId) throws IOException {
        try {
            String originObjectName = file.getOriginalFilename();
            String contentType = file.getContentType();

            System.out.println("originObjectName: " + originObjectName);
            if (contentType == null || contentType.isEmpty()) {
                throw new DefaultException(ErrorCode.INVALID_FILE);
            }

            System.out.println("contentType: " + contentType);
            if (!isValidImageFile(contentType, originObjectName)) {
                throw new DefaultException(ErrorCode.INVALID_FILE);
            }

            byte[] imageData;
            long fileSize;

            if (contentType.equals("image/heic") || contentType.equals("image/heif")) {
                imageData = convertHeicToJpeg(file);
                contentType = "image/jpeg";
                fileSize = imageData.length;
                originObjectName = originObjectName.replaceFirst("(?i)\\.heic$|\\.heif$", ".jpg");
            } else {
                imageData = file.getBytes();
                fileSize = file.getSize();
            }

            String uuid = UUID.randomUUID().toString();
            String savedObjectName = uuid + "_" + originObjectName;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucketName(bucketName)
                    .namespaceName(namespace)
                    .objectName(savedObjectName)
                    .contentLength(fileSize)
                    .contentType(contentType)
                    .putObjectBody(new ByteArrayInputStream(imageData))
                    .build();

            objectStorageClient.putObject(putObjectRequest);

            Image image = new Image();
            image.setUuid(uuid);
            image.setOriginalName(originObjectName);
            image.setSavedName(savedObjectName);
            image.setUrl("https://i.duriburn.com/" + savedObjectName);
            image.setSize(fileSize);
            image.setExtension(contentType);
            image.setCreatedBy(userId);

            return imageRepository.save(image);
        } catch (DefaultException e) {
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            throw new DefaultException(ErrorCode.FILE_PROCESSING_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DefaultException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isValidImageFile(String contentType, String fileName) {
        return SUPPORTED_IMAGE_TYPES.contains(contentType) ||
                fileName.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif|webp|heic|heif)$");
    }

    public byte[] convertHeicToJpeg(MultipartFile file) throws IOException {
        // HEIC 이미지를 읽음
        BufferedImage image = ImageIO.read(file.getInputStream());

        if (image == null) {
            throw new IOException("Failed to read HEIC image");
        }

        // JPEG로 변환
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        boolean success = ImageIO.write(image, "jpg", outputStream);

        if (!success) {
            throw new IOException("Failed to convert HEIC to JPEG");
        }

        return outputStream.toByteArray();
    }
}
