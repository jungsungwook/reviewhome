package com.memeki.reviewhome.global.webClient.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${open-api-key}")
    private String openApiKey;

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.baseUrl("http://apis.data.go.kr")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .filter((request, next) -> {
                    URI originalUri = request.url();
                    String uriWithParams = originalUri.toString();
                    if (uriWithParams.contains("?")) {
                        uriWithParams += "&";
                    } else {
                        uriWithParams += "?";
                    }
                    uriWithParams += "serviceKey=" + openApiKey;
                    uriWithParams += "&_type=json";
                    URI newUri = URI.create(uriWithParams);
                    ClientRequest newRequest = ClientRequest.from(request)
                            .url(newUri)
                            .build();
                    return next.exchange(newRequest);
                })
                .build();
    }
}
