# 상권 조회 API 비활성화 안내

## ⚠️ 현재 상태

**상권 조회 API가 일시중단 상태입니다.**

- **API URL**: `https://apis.data.go.kr/B553077/api/open/sdsc2/storeListInRadius`
- **상태**: 서비스 일시중단 (2025년 기준)
- **조치**: 자동 비활성화 처리 완료

---

## 🔧 적용된 변경사항

### 1. `application.properties` 설정 추가

```properties
# 상권 조회 API 활성화 여부 (현재 API 서비스 일시중단으로 false 설정)
# TODO: 상권 조회 API 복구 시 true로 변경 필요
commercial-api-enabled = false
```

### 2. `CommercialService.java` 수정

- `commercial-api-enabled` 플래그 추가
- `fetchAndSaveCommercialInfo()` 메서드에 비활성화 로직 추가
- API 호출 전 플래그 체크하여 비활성화 시 즉시 리턴

**수정된 로직:**

```java
@Value("${commercial-api-enabled:false}")
private boolean commercialApiEnabled;

@Transactional
public void fetchAndSaveCommercialInfo(String postAddressInfoUuid, double pointX, double pointY) {
    // 상권 API가 비활성화된 경우 스킵
    if (!commercialApiEnabled) {
        log.info("⚠️ 상권 조회 API 비활성화 상태 - 조회 스킵 - UUID: {}", postAddressInfoUuid);
        log.info("💡 활성화 방법: application.properties에서 commercial-api-enabled=true로 설정");
        return;
    }
    
    // ... 기존 로직
}
```

---

## 📋 영향 범위

### ✅ 정상 작동하는 기능
- 건물 검색 (주소 검색)
- 건물 정보 조회
- 커뮤니티 기능
- 리뷰 기능
- 모든 기타 API

### ⚠️ 비활성화된 기능
- 상권 정보 자동 수집
- 건물 주변 상가업소 정보 조회

---

## 🔄 복구 방법

### API 복구 시 활성화 절차

1. **`application.properties` 수정**
   ```properties
   commercial-api-enabled = true
   ```

2. **서버 재시작**
   ```bash
   ./gradlew clean build
   java -jar build/libs/reviewhome-*.jar
   ```

3. **동작 확인**
   - 건물 검색 후 로그 확인
   - `상권 정보 조회 시작` 로그 출력 확인
   - 상권 데이터 DB 저장 확인

---

## 📊 로그 메시지

### 비활성화 상태 (현재)
```
⚠️ 상권 조회 API 비활성화 상태 - 조회 스킵 - UUID: {uuid}
💡 활성화 방법: application.properties에서 commercial-api-enabled=true로 설정
```

### 활성화 상태 (복구 후)
```
상권 정보 조회 시작 - UUID: {uuid}, X: {경도}, Y: {위도}
상권 정보 저장 완료 - UUID: {uuid}, 저장된 개수: {count}
```

---

## 🐛 문제 해결

### Q1: 상권 정보가 조회되지 않아요
**A**: 현재 API가 일시중단 상태입니다. 복구 전까지는 정상입니다.

### Q2: 기존에 저장된 상권 정보는 어떻게 되나요?
**A**: 기존 DB에 저장된 데이터는 그대로 유지됩니다. 새로운 조회만 중단됩니다.

### Q3: API 복구 여부는 어떻게 확인하나요?
**A**: 공공데이터포털(https://www.data.go.kr)에서 API 상태를 확인하거나, 직접 API를 호출해보세요.

---

## 📞 문의

API 복구 요청이나 관련 문의사항은 아래로 연락주세요:
- **공공데이터포털**: https://www.data.go.kr
- **API 문의**: 소상공인시장진흥공단

---

**마지막 업데이트**: 2025-11-09  
**작성자**: AI Assistant  
**상태**: ⚠️ 비활성화 (API 일시중단)

