package com.memeki.reviewhome.global.webClient.config;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
public class WebClientConfig {

    @Value("${open-api-key}")
    private String openApiKey;

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        // Connection Pool 설정
        // 장기간 미사용 시에도 안정적으로 작동하도록 설정
        ConnectionProvider connectionProvider = ConnectionProvider.builder("custom")
                .maxConnections(100)                        // 최대 연결 수
                .maxIdleTime(Duration.ofSeconds(20))        // 유휴 연결 유지 시간 (20초)
                .maxLifeTime(Duration.ofMinutes(5))         // 최대 연결 수명 (5분)
                .pendingAcquireTimeout(Duration.ofSeconds(45)) // 연결 대기 타임아웃
                .evictInBackground(Duration.ofSeconds(120)) // 백그라운드 정리 주기
                .build();

        // HttpClient 설정 - 타임아웃 및 Keep-Alive
        HttpClient httpClient = HttpClient.create(connectionProvider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000) // 연결 타임아웃 30초
                .responseTimeout(Duration.ofSeconds(60))     // 응답 타임아웃 60초
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(60, TimeUnit.SECONDS))  // 읽기 타임아웃
                        .addHandlerLast(new WriteTimeoutHandler(60, TimeUnit.SECONDS))) // 쓰기 타임아웃
                .keepAlive(true);                            // Keep-Alive 활성화

        return builder
                .baseUrl("http://apis.data.go.kr")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
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
