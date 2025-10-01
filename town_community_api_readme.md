# 동네 커뮤니티 API 명세서

## 개요
동네 커뮤니티 관련 API 명세서입니다. 읍면동 코드(`emdCd`)를 기준으로 동네별 커뮤니티를 생성하고 관리할 수 있습니다.

## 기본 정보
- **Base URL**: `http://localhost:8080/api/community/town`
- **인증 방식**: JWT Bearer Token (선택적)
- **Content-Type**: `application/json`

---

## 1. 동네 커뮤니티 메인 페이지 조회

### API 정보
- **URL**: `GET /api/community/town`
- **설명**: 읍면동 코드로 동네 커뮤니티를 조회합니다. 커뮤니티가 없으면 자동으로 생성됩니다.
- **인증**: 선택 (비로그인 시에도 조회 가능, 단 가입 여부는 확인 불가)

### 요청 파라미터
| 파라미터 | 타입 | 필수 | 설명 | 예시 |
|---------|------|------|------|------|
| emdCd | String | O | 읍면동 코드 (법정동 코드) | "1111010100" |

### 요청 예시
```http
GET /api/community/town?emdCd=1111010100
Authorization: Bearer {JWT_TOKEN}  # 선택사항
```

### 응답 예시
```json
{
  "statusCode": 200,
  "community": {
    "uuid": "a1b2c3d4e5f6",
    "type": "town",
    "targetId": "1111010100",
    "type2": "default",
    "name": "역삼동 동네 커뮤니티",
    "description": "역삼동 지역 주민들의 커뮤니티입니다.",
    "isPassword": false,
    "geoFeaturesId": 123,
    "geoFeaturesName": "역삼동",
    "createdBy": 0,
    "createdAt": "2023-09-27T10:30:00",
    "updatedAt": "2023-09-27T10:30:00",
    "isEnter": true,
    "isManager": false,
    "isOwner": false,
    "userCount": 45
  },
  "popularPosts": [
    {
      "id": 1,
      "postType": "general",
      "communityUuid": "a1b2c3d4e5f6",
      "title": "역삼동 맛집 추천",
      "content": "이 근처 맛집 아시는 분?",
      "createdAt": "2023-09-27T14:30:00",
      "createdBy": 5,
      "likeCount": 15,
      "viewCount": 120,
      "replyCount": 8
    }
  ],
  "recentPosts": [
    {
      "id": 2,
      "postType": "general",
      "communityUuid": "a1b2c3d4e5f6",
      "title": "안녕하세요",
      "content": "새로 이사왔어요!",
      "createdAt": "2023-09-27T15:00:00",
      "createdBy": 9,
      "likeCount": 3,
      "viewCount": 25,
      "replyCount": 2
    }
  ]
}
```

### 응답 필드 설명

#### Community 객체
| 필드명 | 타입 | 설명 | 비고 |
|--------|------|------|------|
| uuid | String | 커뮤니티 고유 ID | 자동 생성 |
| type | String | 커뮤니티 타입 | "town" 고정 |
| targetId | String | 읍면동 코드 | 요청한 emdCd 값 |
| type2 | String | 서브 타입 | "default", "mom", "sports" 등 |
| name | String | 커뮤니티 이름 | "역삼동 동네 커뮤니티" |
| description | String | 커뮤니티 설명 | |
| isPassword | Boolean | 비밀번호 설정 여부 | |
| geoFeaturesId | Integer | 지역 정보 ID | |
| geoFeaturesName | String | 지역 이름 | "역삼동" |
| createdBy | Long | 생성자 ID | 0 = 시스템 자동 생성 |
| createdAt | LocalDateTime | 생성일 | |
| updatedAt | LocalDateTime | 수정일 | |
| isEnter | Boolean | 현재 사용자 가입 여부 | 로그인 시에만 true/false |
| isManager | Boolean | 현재 사용자 관리자 여부 | |
| isOwner | Boolean | 현재 사용자 소유자 여부 | |
| userCount | Integer | 가입 회원 수 | |

---

## 2. 동네 커뮤니티 찾기

### API 정보
- **URL**: `GET /api/community/town/find`
- **설명**: 동네 커뮤니티를 찾습니다. 없으면 자동 생성합니다. (커뮤니티 정보만 반환)
- **인증**: 선택

### 요청 파라미터
| 파라미터 | 타입 | 필수 | 설명 | 예시 |
|---------|------|------|------|------|
| emdCd | String | O | 읍면동 코드 | "1111010100" |
| type2 | String | X | 서브 타입 | "default" (기본값) |

### 요청 예시
```http
GET /api/community/town/find?emdCd=1111010100&type2=default
```

### 응답 예시
```json
{
  "statusCode": 200,
  "community": {
    "uuid": "a1b2c3d4e5f6",
    "type": "town",
    "targetId": "1111010100",
    "type2": "default",
    "name": "역삼동 동네 커뮤니티",
    "description": "역삼동 지역 주민들의 커뮤니티입니다.",
    "isPassword": false,
    "geoFeaturesId": 123,
    "geoFeaturesName": "역삼동",
    "createdBy": 0,
    "createdAt": "2023-09-27T10:30:00",
    "updatedAt": "2023-09-27T10:30:00"
  }
}
```

---

## 3. 동네 커뮤니티 가입

### API 정보
- **URL**: `POST /api/community/town/enter`
- **설명**: 동네 커뮤니티에 가입합니다.
- **인증**: 필수 (JWT Token)

### 요청 헤더
```http
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

### 요청 본문
```json
{
  "communityUuid": "a1b2c3d4e5f6",
  "nickname": "동네주민",
  "password": "1234"
}
```

### 요청 필드 설명
| 필드명 | 타입 | 필수 | 설명 | 제약사항 |
|--------|------|------|------|----------|
| communityUuid | String | O | 커뮤니티 UUID | |
| nickname | String | O | 커뮤니티 내 사용할 닉네임 | 2-8자, 특수문자 불가 |
| password | String | X | 비밀번호 | isPassword가 true인 경우 필수 |

### 응답 예시

#### 성공 (200 OK)
```json
{
  "statusCode": 200,
  "community": {
    "uuid": "a1b2c3d4e5f6",
    "type": "town",
    "targetId": "1111010100",
    "type2": "default",
    "name": "역삼동 동네 커뮤니티",
    "description": "역삼동 지역 주민들의 커뮤니티입니다.",
    "isPassword": false,
    "geoFeaturesId": 123,
    "geoFeaturesName": "역삼동",
    "createdBy": 0,
    "createdAt": "2023-09-27T10:30:00",
    "updatedAt": "2023-09-27T10:30:00"
  }
}
```

#### 실패 - 닉네임 중복 (409 Conflict)
```json
{
  "status": 409,
  "message": "이미 사용중인 닉네임입니다."
}
```

#### 실패 - 이미 가입 (409 Conflict)
```json
{
  "status": 409,
  "message": "이미 가입한 커뮤니티입니다."
}
```

#### 실패 - 닉네임 조건 위반 (400 Bad Request)
```json
{
  "status": 400,
  "message": "유효하지 않은 파라미터입니다."
}
```

### 닉네임 규칙
- **길이**: 2자 이상 8자 이하
- **허용 문자**: 한글, 영문, 숫자만 가능
- **금지 문자**: 특수문자 (!@#$%^&* 등) 불가
- **중복 불가**: 같은 커뮤니티 내에서 중복 불가

---

## 4. 동네 커뮤니티 탈퇴

### API 정보
- **URL**: `POST /api/community/town/leave`
- **설명**: 동네 커뮤니티에서 탈퇴합니다.
- **인증**: 필수 (JWT Token)

### 요청 헤더
```http
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

### 요청 본문
```json
{
  "communityUuid": "a1b2c3d4e5f6"
}
```

### 요청 필드 설명
| 필드명 | 타입 | 필수 | 설명 |
|--------|------|------|------|
| communityUuid | String | O | 탈퇴할 커뮤니티 UUID |

### 응답 예시

#### 성공 (200 OK)
```json
{
  "statusCode": 200
}
```

#### 실패 - 가입 이력 없음 (404 Not Found)
```json
{
  "status": 404,
  "message": "검색 결과가 없습니다. 다시 시도해주세요."
}
```

---

## 5. 게시글 관련 API (빌딩 커뮤니티와 공통)

동네 커뮤니티의 게시글 기능은 빌딩 커뮤니티와 완전히 동일합니다.

### 게시글 목록 조회
```http
GET /api/community/building/posts?uuid={communityUuid}&page=0&size=10&search=검색어
```

### 게시글 상세 조회
```http
GET /api/community/building/post/detail?id={postId}
```

### 게시글 작성
```http
POST /api/community/building/post
Authorization: Bearer {JWT_TOKEN}

{
  "communityUuid": "a1b2c3d4e5f6",
  "title": "제목",
  "content": "내용",
  "postType": "general"
}
```

### 게시글 수정
```http
POST /api/community/building/post/edit
Authorization: Bearer {JWT_TOKEN}

{
  "postId": 1,
  "title": "수정된 제목",
  "content": "수정된 내용",
  "postType": "general"
}
```

### 게시글 삭제
```http
POST /api/community/building/post/delete
Authorization: Bearer {JWT_TOKEN}

{
  "postId": 1
}
```

### 게시글 좋아요
```http
POST /api/community/building/post/like
Authorization: Bearer {JWT_TOKEN}

{
  "postId": 1
}
```

**응답:**
```json
{
  "statusCode": 200,
  "liked": 1  // 1: 좋아요 추가, 0: 좋아요 취소
}
```

### 댓글 작성
```http
POST /api/community/building/post/comment
Authorization: Bearer {JWT_TOKEN}

{
  "postId": 1,
  "content": "댓글 내용",
  "isReply": false  // true면 대댓글
}
```

---

## JavaScript 사용 예시

### 1. 동네 커뮤니티 조회 (자동 생성 포함)
```javascript
async function getTownCommunity(emdCd) {
  try {
    const response = await fetch(`http://localhost:8080/api/community/town?emdCd=${emdCd}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
        'Content-Type': 'application/json'
      }
    });
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    const data = await response.json();
    console.log('동네 커뮤니티:', data);
    return data;
  } catch (error) {
    console.error('동네 커뮤니티 조회 실패:', error);
    throw error;
  }
}

// 사용 예시
const communityData = await getTownCommunity('1111010100');
```

### 2. 동네 커뮤니티 가입
```javascript
async function joinTownCommunity(communityUuid, nickname) {
  try {
    const response = await fetch('http://localhost:8080/api/community/town/enter', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        communityUuid: communityUuid,
        nickname: nickname
      })
    });
    
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
    }
    
    const data = await response.json();
    console.log('커뮤니티 가입 성공:', data);
    return data;
  } catch (error) {
    console.error('커뮤니티 가입 실패:', error);
    throw error;
  }
}

// 사용 예시
await joinTownCommunity('a1b2c3d4e5f6', '동네주민');
```

### 3. 동네 커뮤니티 탈퇴
```javascript
async function leaveTownCommunity(communityUuid) {
  try {
    const response = await fetch('http://localhost:8080/api/community/town/leave', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        communityUuid: communityUuid
      })
    });
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    const data = await response.json();
    console.log('커뮤니티 탈퇴 성공:', data);
    return data;
  } catch (error) {
    console.error('커뮤니티 탈퇴 실패:', error);
    throw error;
  }
}
```

### 4. 게시글 작성
```javascript
async function createPost(communityUuid, title, content) {
  try {
    const response = await fetch('http://localhost:8080/api/community/building/post', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        communityUuid: communityUuid,
        title: title,
        content: content,
        postType: 'general'
      })
    });
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    console.log('게시글 작성 성공');
    return true;
  } catch (error) {
    console.error('게시글 작성 실패:', error);
    throw error;
  }
}
```

### 5. 게시글 목록 조회 (페이징)
```javascript
async function getCommunityPosts(communityUuid, page = 0, size = 10, search = '') {
  try {
    let url = `http://localhost:8080/api/community/building/posts?uuid=${communityUuid}&page=${page}&size=${size}`;
    if (search) {
      url += `&search=${encodeURIComponent(search)}`;
    }
    
    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    });
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    const data = await response.json();
    return data;
  } catch (error) {
    console.error('게시글 목록 조회 실패:', error);
    throw error;
  }
}

// 응답 예시
{
  "statusCode": 200,
  "posts": [
    {
      "id": 1,
      "postType": "general",
      "communityUuid": "a1b2c3d4e5f6",
      "title": "게시글 제목",
      "content": "게시글 내용",
      "createdAt": "2023-09-27T14:30:00",
      "createdBy": 5,
      "likeCount": 15,
      "viewCount": 120,
      "replyCount": 8
    }
  ],
  "pagination": {
    "totalElements": 100,
    "totalPages": 10,
    "page": 0,
    "size": 10,
    "last": false
  }
}
```

### 6. 게시글 상세 조회
```javascript
async function getPostDetail(postId) {
  try {
    const response = await fetch(`http://localhost:8080/api/community/building/post/detail?id=${postId}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
        'Content-Type': 'application/json'
      }
    });
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    const data = await response.json();
    return data;
  } catch (error) {
    console.error('게시글 상세 조회 실패:', error);
    throw error;
  }
}

// 응답 예시
{
  "statusCode": 200,
  "communityPost": {
    "id": 1,
    "postType": "general",
    "communityUuid": "a1b2c3d4e5f6",
    "title": "게시글 제목",
    "content": "게시글 내용",
    "createdAt": "2023-09-27T14:30:00",
    "createdBy": 5,
    "likeCount": 15,
    "viewCount": 121,  // 조회수 1 증가
    "replyCount": 8,
    "isLiked": false,
    "isMine": true,
    "replies": [
      {
        "id": 1,
        "postId": 1,
        "content": "댓글 내용",
        "isReply": false,
        "createdBy": 7,
        "createdAt": "2023-09-27T15:00:00"
      }
    ]
  },
  "previousPost": {
    "id": 2,
    "title": "이전 게시글"
  },
  "nextPost": {
    "id": 3,
    "title": "다음 게시글"
  },
  "nearPosts": [
    // 현재 게시글 주변의 게시글 목록 (11개)
  ]
}
```

---

## React 컴포넌트 예시

### 동네 커뮤니티 메인 페이지
```jsx
import React, { useState, useEffect } from 'react';

function TownCommunityPage({ emdCd }) {
  const [community, setCommunity] = useState(null);
  const [popularPosts, setPopularPosts] = useState([]);
  const [recentPosts, setRecentPosts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchCommunity = async () => {
      try {
        setLoading(true);
        const response = await fetch(
          `http://localhost:8080/api/community/town?emdCd=${emdCd}`,
          {
            headers: {
              'Authorization': `Bearer ${localStorage.getItem('token')}`,
              'Content-Type': 'application/json'
            }
          }
        );
        
        const data = await response.json();
        setCommunity(data.community);
        setPopularPosts(data.popularPosts || []);
        setRecentPosts(data.recentPosts || []);
      } catch (error) {
        console.error('커뮤니티 조회 실패:', error);
      } finally {
        setLoading(false);
      }
    };

    if (emdCd) {
      fetchCommunity();
    }
  }, [emdCd]);

  if (loading) return <div>로딩중...</div>;

  return (
    <div className="town-community">
      <h1>{community?.name}</h1>
      <p>{community?.description}</p>
      <p>회원 수: {community?.userCount}명</p>
      
      {!community?.isEnter && (
        <button onClick={() => handleJoinCommunity()}>
          커뮤니티 가입
        </button>
      )}
      
      <section>
        <h2>인기 게시글</h2>
        {popularPosts.map(post => (
          <PostCard key={post.id} post={post} />
        ))}
      </section>
      
      <section>
        <h2>최근 게시글</h2>
        {recentPosts.map(post => (
          <PostCard key={post.id} post={post} />
        ))}
      </section>
    </div>
  );
}
```

### 커뮤니티 가입 모달
```jsx
import React, { useState } from 'react';

function JoinCommunityModal({ communityUuid, onSuccess }) {
  const [nickname, setNickname] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // 닉네임 검증
    if (nickname.length < 2 || nickname.length > 8) {
      setError('닉네임은 2자 이상 8자 이하여야 합니다.');
      return;
    }
    
    if (!/^[a-zA-Z0-9가-힣]*$/.test(nickname)) {
      setError('닉네임은 한글, 영문, 숫자만 사용 가능합니다.');
      return;
    }

    try {
      const response = await fetch('http://localhost:8080/api/community/town/enter', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          communityUuid: communityUuid,
          nickname: nickname
        })
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message);
      }

      const data = await response.json();
      onSuccess(data);
    } catch (error) {
      setError(error.message);
    }
  };

  return (
    <div className="modal">
      <h2>커뮤니티 가입</h2>
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          value={nickname}
          onChange={(e) => setNickname(e.target.value)}
          placeholder="닉네임을 입력하세요 (2-8자)"
          maxLength={8}
        />
        {error && <p className="error">{error}</p>}
        <button type="submit">가입하기</button>
      </form>
    </div>
  );
}
```

---

## 에러 코드 정리

| 상태 코드 | 에러 메시지 | 설명 | 발생 상황 |
|-----------|-------------|------|-----------|
| 400 | 유효하지 않은 파라미터입니다. | 요청 데이터 오류 | 닉네임 조건 위반, 필수 파라미터 누락 |
| 401 | 인증되지 않은 사용자입니다. | 인증 실패 | JWT 토큰 없음/만료 |
| 401 | 비밀번호가 필요합니다. | 비밀번호 커뮤니티 | 비밀번호 미입력 |
| 401 | 비밀번호가 일치하지 않습니다. | 비밀번호 오류 | 잘못된 비밀번호 |
| 403 | 접근 권한이 없습니다. | 권한 없음 | 다른 사용자의 글 수정/삭제 |
| 403 | 커뮤니티에 가입해야 이용할 수 있습니다. | 미가입 상태 | 비가입자의 게시글 조회 시도 |
| 404 | 검색 결과가 없습니다. | 데이터 없음 | 존재하지 않는 emdCd, 가입 이력 없음 |
| 409 | 이미 사용중인 닉네임입니다. | 닉네임 중복 | 커뮤니티 내 닉네임 중복 |
| 409 | 이미 가입한 커뮤니티입니다. | 중복 가입 | 이미 가입한 커뮤니티 재가입 |
| 500 | 서버에러. 관리자 문의 바람. | 서버 오류 | 예상치 못한 오류 |

---

## 읍면동 코드(emdCd) 가져오기

동네 커뮤니티는 읍면동 코드(`emdCd`)를 기준으로 생성됩니다. 

### emdCd 확인 방법

1. **건물 검색 후 얻기**
   - PostAddressInfo에 `geoFeaturesId`와 `geoFeaturesName`이 저장되어 있음
   - GeoFeatures 테이블에서 `emdCd` 조회 가능

2. **사용자 위치 기반**
   - 사용자의 현재 위치(위도/경도)로 해당 지역의 `emdCd` 조회
   - Geo API를 통해 위치 → emdCd 변환

### emdCd 예시
```
서울특별시 강남구 역삼동: "1168010100"
서울특별시 송파구 가락동: "1171010700"
서울특별시 관악구 봉천동: "1162010100"
```

---

## 빌딩 커뮤니티 vs 동네 커뮤니티

| 구분 | 빌딩 커뮤니티 | 동네 커뮤니티 |
|------|--------------|--------------|
| **타입** | `type: "building"` | `type: "town"` |
| **식별자** | `targetId: postAddressInfoUuid` | `targetId: emdCd` |
| **생성 기준** | 건물 UUID | 읍면동 코드 |
| **범위** | 특정 건물 (아파트 동 단위) | 법정동 전체 |
| **API 경로** | `/api/community/building/*` | `/api/community/town/*` |
| **게시글 API** | 공통 사용 (`/api/community/building/post*`) | |
| **자동 생성** | PostAddressInfo 기반 | GeoFeatures 기반 |

---

## 주요 워크플로우

### 1️⃣ 동네 커뮤니티 입장 플로우
```
1. 사용자 위치 확인 → emdCd 획득
2. GET /api/community/town?emdCd={emdCd}
3. 커뮤니티 조회/생성 → communityUuid 획득
4. isEnter 확인
   - false: 가입 필요 → POST /api/community/town/enter
   - true: 바로 입장
5. 게시글 목록 표시
```

### 2️⃣ 게시글 작성 플로우
```
1. 커뮤니티 가입 확인 (isEnter === true)
2. POST /api/community/building/post
3. 작성 완료 → 게시글 목록 새로고침
```

### 3️⃣ 커뮤니티 탈퇴 플로우
```
1. POST /api/community/town/leave
2. 탈퇴 완료 → isEnter: false로 변경
3. 메인 페이지로 리다이렉트
```

---

## 주의사항

1. **인증 토큰**
   - 게시글 작성/수정/삭제, 좋아요, 댓글은 JWT 토큰 필수
   - 조회는 비로그인 가능 (단, 가입 여부 확인 불가)

2. **닉네임 규칙**
   - 2-8자 제한
   - 한글, 영문, 숫자만 허용
   - 커뮤니티 내 중복 불가

3. **자동 생성**
   - `emdCd`에 해당하는 `default` 타입 커뮤니티가 없으면 자동 생성
   - GeoFeatures 테이블에 `emdCd`가 없으면 404 에러

4. **type2 활용**
   - 현재는 `"default"`만 자동 생성
   - 향후 주제별 커뮤니티 확장 가능

5. **조회수 증가**
   - 게시글 상세 조회 시 자동으로 조회수 1 증가
   - 같은 사용자는 중복 카운트 안 됨

6. **게시글 API 공통 사용**
   - 동네 커뮤니티도 게시글 관련 API는 `/api/community/building/*` 사용
   - `communityUuid`로 구분되므로 타입 상관없이 동일하게 작동

---

## 개발 팁

### emdCd 얻는 방법
```javascript
// 건물 검색 후 PostAddressInfo에서 얻기
const postAddressInfo = await searchBuilding(address);
const emdCd = postAddressInfo.geoFeaturesId; // 실제로는 geo_features 테이블 조회 필요

// 또는 사용자 주소에서 직접 얻기
const userAddress = "서울특별시 강남구 역삼동";
// Geo API 호출하여 emdCd 획득
```

### 로그인/비로그인 대응
```javascript
const headers = {
  'Content-Type': 'application/json'
};

const token = localStorage.getItem('token');
if (token) {
  headers['Authorization'] = `Bearer ${token}`;
}

fetch(url, { headers });
```

---

## Swagger UI
서버 실행 후 다음 주소에서 API 테스트 가능:
- **URL**: `http://localhost:8080/swagger-ui.html`
- **API Docs**: `http://localhost:8080/v3/api-docs`

---

## 문의사항
- **프로젝트**: ReviewHome
- **문서 버전**: 1.0
- **최종 업데이트**: 2024-10-01
