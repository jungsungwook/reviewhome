package com.memeki.reviewhome.image.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;

import io.jsonwebtoken.io.IOException;

@Configuration
public class OciConfig {
    @Value("${oci.config.file}")
    private String configurationFilePath;

    @Bean
    public ObjectStorage objectStorageClient() throws java.io.IOException {
        try {
            ConfigFileReader.ConfigFile configFile = ConfigFileReader.parse(configurationFilePath);
            AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(configFile);
            return ObjectStorageClient.builder().build(provider);
        } catch (IOException e) {
            throw new RuntimeException("OCI 설정 파일을 읽는 데 실패했습니다: " + e.getMessage(), e);
        }
    }
}