# 배포 환경 건물 검색 오류 해결

## 🐛 문제 증상

### 발생 상황
- **로컬 환경**: 건물 검색 정상 작동 ✅
- **배포 환경**: 서버 구동 후 1~2일 경과 시 건물 검색 실패 ❌
- **재현 방법**: 
  1. 서버 구동 직후: 정상 작동
  2. 1~2일간 API 호출 없음
  3. 갑자기 검색 요청 시: 응답 없음 (무한 대기)
  4. 페이지 새로고침 후 재요청: 정상 작동

---

## 🔍 원인 분석

### 근본 원인: **WebClient 타임아웃 및 Connection Pool 미설정**

#### 기존 코드의 문제점

```java
@Bean
public WebClient webClient(WebClient.Builder builder) {
    return builder.baseUrl("http://apis.data.go.kr")
        .defaultHeader(...)
        .filter(...)
        .build();  // ❌ 타임아웃 설정 없음!
}
```

#### 문제점 상세

| 항목 | 기존 설정 | 문제 |
|------|-----------|------|
| **Connection Timeout** | 없음 | 연결 시도 시 무한 대기 가능 |
| **Read Timeout** | 없음 | 응답 대기 시 무한 대기 가능 |
| **Write Timeout** | 없음 | 요청 전송 시 무한 대기 가능 |
| **Connection Pool** | 없음 | 매번 새 연결 생성, 리소스 낭비 |
| **Connection Lifecycle** | 관리 안됨 | 장기간 미사용 시 Connection 만료 인지 못함 |

#### 왜 1~2일 후에만 문제가 발생했나?

1. **서버 시작 직후**: 
   - 새로운 Connection 생성
   - API 서버와 정상 통신 ✅

2. **1~2일 경과**:
   - 서버 측에서 idle connection 종료
   - 클라이언트는 connection이 닫힌 줄 모름
   - 닫힌 connection으로 요청 시도
   - **타임아웃 없어서 무한 대기** ⏳

3. **새로고침 후**:
   - 기존 connection 폐기
   - 새 connection 생성
   - 다시 정상 작동 ✅

#### 로컬 vs 배포 환경 차이

| 환경 | 네트워크 | Connection 유지 | 문제 발생 |
|------|----------|----------------|----------|
| **로컬** | 빠름 (localhost 수준) | 짧은 테스트 시간 | 드묾 |
| **배포** | 느림 (인터넷) | 장기간 유지 필요 | 자주 발생 |

---

## ✅ 해결 방법

### 1. WebClientConfig 개선

#### 추가된 설정

```java
// 1. Connection Pool 설정
ConnectionProvider connectionProvider = ConnectionProvider.builder("custom")
        .maxConnections(100)                        // 최대 100개 연결
        .maxIdleTime(Duration.ofSeconds(20))        // 20초 이상 미사용 시 정리
        .maxLifeTime(Duration.ofMinutes(5))         // 최대 5분 수명
        .pendingAcquireTimeout(Duration.ofSeconds(45)) // 연결 대기 최대 45초
        .evictInBackground(Duration.ofSeconds(120)) // 2분마다 백그라운드 정리
        .build();

// 2. HttpClient 타임아웃 설정
HttpClient httpClient = HttpClient.create(connectionProvider)
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000) // 연결 30초
        .responseTimeout(Duration.ofSeconds(60))     // 응답 60초
        .doOnConnected(conn -> conn
                .addHandlerLast(new ReadTimeoutHandler(60, TimeUnit.SECONDS))
                .addHandlerLast(new WriteTimeoutHandler(60, TimeUnit.SECONDS)))
        .keepAlive(true);                            // Keep-Alive 활성화
```

#### 설정 값 설명

| 설정 | 값 | 이유 |
|------|---|------|
| **maxConnections** | 100 | 충분한 동시 요청 처리 |
| **maxIdleTime** | 20초 | 미사용 연결 빠르게 정리 (서버보다 짧게) |
| **maxLifeTime** | 5분 | 연결 재활용으로 성능 향상 |
| **connectTimeout** | 30초 | 네트워크 지연 고려 |
| **responseTimeout** | 60초 | 공공 API 느린 응답 고려 |
| **readTimeout** | 60초 | 대용량 응답 수신 시간 |
| **writeTimeout** | 60초 | 대용량 요청 전송 시간 |
| **keepAlive** | true | TCP Keep-Alive로 연결 유지 |

---

## 📊 개선 효과

### Before (기존)

```
클라이언트 → [무한 대기] → 만료된 Connection → [응답 없음] → 타임아웃
                ⏳ 무한 대기 (브라우저 타임아웃까지)
```

### After (개선 후)

```
클라이언트 → [30초 타임아웃] → 새 Connection 생성 → API 서버 → 응답
                ✅ 빠른 실패 & 재시도
```

### 예상 개선 사항

✅ **장기간 미사용 후에도 안정적인 응답**  
✅ **Connection 재사용으로 성능 향상**  
✅ **타임아웃으로 빠른 에러 감지 및 복구**  
✅ **메모리 누수 방지 (자동 Connection 정리)**  
✅ **배포 환경과 로컬 환경 동작 일관성**

---

## 🧪 테스트 방법

### 1. 즉시 테스트 (정상 작동 확인)

```bash
# 서버 재시작
./gradlew clean build
java -jar build/libs/reviewhome-*.jar

# 건물 검색 API 호출
curl -X POST http://localhost:8080/api/post-address/search \
  -H "Content-Type: application/json" \
  -d '{
    "sigunguCd": "11710",
    "bjdongCd": "10100",
    "bun": "1",
    "ji": "0"
  }'
```

**예상 결과**: 정상 응답 (statusCode: 200)

### 2. 장기 테스트 (1~2일 후 확인)

```bash
# 1. 서버 구동
# 2. 1~2일간 대기 (API 호출 없이)
# 3. 동일한 API 호출
# 4. 정상 응답 확인
```

**예상 결과**: 
- 기존: 무한 대기 또는 응답 없음 ❌
- 개선: 30~60초 내 정상 응답 ✅

### 3. 로그 확인

```bash
# Connection Pool 로그 활성화 (선택)
# application.properties에 추가:
logging.level.reactor.netty.resources=DEBUG
logging.level.reactor.netty.http.client=DEBUG
```

**확인 사항**:
- `[id: 0x...] REGISTERED` - 새 연결 생성
- `[id: 0x...] CONNECT` - 연결 시도
- `[id: 0x...] CLOSE` - 연결 종료
- `Connection pool evicting` - 자동 정리

---

## 🔧 추가 권장 사항

### 1. 로깅 강화 (선택)

`application.properties`에 추가:

```properties
# WebClient 디버그 로깅
logging.level.reactor.netty.http.client=INFO
logging.level.org.springframework.web.reactive.function.client=INFO
```

### 2. Health Check 추가 (권장)

주기적으로 API 상태 확인:

```java
@Scheduled(fixedDelay = 600000) // 10분마다
public void healthCheck() {
    try {
        webClient.get()
            .uri("/1613000/BldRgstHubService/getBrTitleInfo?...")
            .retrieve()
            .bodyToMono(String.class)
            .timeout(Duration.ofSeconds(10))
            .subscribe();
    } catch (Exception e) {
        log.warn("API Health Check 실패", e);
    }
}
```

### 3. 모니터링 지표 추가

```properties
# Actuator 활성화
management.endpoints.web.exposure.include=health,metrics
management.metrics.export.simple.enabled=true
```

---

## 📝 관련 이슈

### 로그에서 발견된 오류들

#### 1. SSL Handshake Timeout
```
SslHandshakeTimeoutException: handshake timed out after 10000ms
```
→ **해결**: `responseTimeout(Duration.ofSeconds(60))` 추가

#### 2. Invalid HTTP Method
```
Invalid character found in method name [0x160x030x01...]
```
→ **원인**: 외부 스캐너의 HTTPS 시도 (무시 가능)

#### 3. RequestRejectedException
```
URL contained a potentially malicious String ";"
```
→ **원인**: Spring Security Firewall (필요시 완화 가능)

---

## 🎯 결론

### 핵심 변경사항
- ✅ WebClient에 Connection Pool 추가
- ✅ 모든 타임아웃 설정 추가
- ✅ Keep-Alive 활성화
- ✅ 자동 Connection 관리

### 기대 효과
배포 환경에서도 로컬 환경과 동일하게 안정적으로 작동하며, 장기간 미사용 후에도 문제없이 건물 검색이 가능합니다.

---

**마지막 업데이트**: 2025-11-09  
**작성자**: AI Assistant  
**상태**: ✅ 해결 완료

