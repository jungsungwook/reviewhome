# 타운 커뮤니티 리뷰 API 문서

## 📌 API 개요

- **Base URL**: `http://localhost:8080/api/review`
- **인증 방식**: JWT Bearer Token (작성/답글 작성 시 필요)
- **Content-Type**: `application/json`

타운 커뮤니티의 리뷰 기능은 빌딩 커뮤니티와 동일한 구조로 작동하며, `emdCd`(읍면동 코드)를 기준으로 리뷰를 작성하고 조회합니다.

---

## 📋 목차

1. [타운 리뷰 작성](#1-타운-리뷰-작성)
2. [타운 리뷰 조회](#2-타운-리뷰-조회)
3. [타운 리뷰 답글 작성](#3-타운-리뷰-답글-작성)
4. [리뷰 좋아요](#4-리뷰-좋아요-공통)
5. [데이터 구조](#-데이터-구조)
6. [JavaScript 사용 예시](#-javascript-사용-예시)
7. [React 예시](#-react-예시)

---

## 1. 타운 리뷰 작성

### API 정보
- **URL**: `POST /api/review/town`
- **설명**: 타운 커뮤니티에 새로운 리뷰를 작성합니다.
- **인증**: **필수** (JWT Token)

### 요청 헤더
```http
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

### 요청 바디
```json
{
  "targetId": "1168010100",
  "title": "역삼동 살기 좋은 동네입니다",
  "content": "교통이 편리하고 편의시설이 많아서 좋아요. 특히 지하철역이 가까워서 출퇴근이 편합니다.",
  "reviewType": "general",
  "rating": 5
}
```

### 요청 필드 설명
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| targetId | String | O | 읍면동 코드 (emdCd) |
| title | String | O | 리뷰 제목 |
| content | String | O | 리뷰 내용 |
| reviewType | String | O | 리뷰 타입 ("general", "living", "facility" 등) |
| rating | Integer | X | 평점 (1~5) |

### 응답 예시
```json
{
  "statusCode": 200
}
```

### 에러 응답
```json
{
  "statusCode": 401,
  "message": "인증이 필요합니다"
}
```

---

## 2. 타운 리뷰 조회

### API 정보
- **URL**: `GET /api/review/town`
- **설명**: 특정 읍면동의 모든 리뷰를 조회합니다.
- **인증**: 선택 (로그인 시 좋아요 여부 표시)

### 요청 파라미터
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| emdCd | String | O | 읍면동 코드 |

### 요청 예시
```http
GET /api/review/town?emdCd=1168010100
Authorization: Bearer {JWT_TOKEN}  (선택)
```

### 응답 예시
```json
{
  "buildingReviews": [
    {
      "review": {
        "id": 123,
        "type": "town",
        "targetId": "1168010100",
        "replyId": null,
        "createdBy": 45,
        "createdAt": "2025-01-20T10:30:00",
        "updatedAt": "2025-01-20T10:30:00",
        "likeCount": 15
      },
      "buildingReviewContent": {
        "id": 123,
        "reviewId": 123,
        "reviewType": "general",
        "title": "역삼동 살기 좋은 동네입니다",
        "content": "교통이 편리하고 편의시설이 많아서 좋아요.",
        "rating": 5,
        "createdAt": "2025-01-20T10:30:00",
        "updatedAt": "2025-01-20T10:30:00"
      },
      "alreadyLiked": false
    },
    {
      "review": {
        "id": 124,
        "type": "town",
        "targetId": "1168010100",
        "replyId": null,
        "createdBy": 67,
        "createdAt": "2025-01-19T15:20:00",
        "updatedAt": "2025-01-19T15:20:00",
        "likeCount": 8
      },
      "buildingReviewContent": {
        "id": 124,
        "reviewId": 124,
        "reviewType": "living",
        "title": "조용하고 살기 좋아요",
        "content": "주거 환경이 깔끔하고 주변이 조용해서 좋습니다.",
        "rating": 4,
        "createdAt": "2025-01-19T15:20:00",
        "updatedAt": "2025-01-19T15:20:00"
      },
      "alreadyLiked": true
    }
  ],
  "statusCode": 200
}
```

---

## 3. 타운 리뷰 답글 작성

### API 정보
- **URL**: `POST /api/review/town/reply`
- **설명**: 타운 리뷰에 답글을 작성합니다.
- **인증**: **필수** (JWT Token)

### 요청 헤더
```http
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

### 요청 바디
```json
{
  "uuid": "1168010100",
  "reviewId": 123,
  "content": "저도 동의합니다! 역삼동 정말 살기 좋은 곳이죠."
}
```

### 요청 필드 설명
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| uuid | String | O | 읍면동 코드 (emdCd) |
| reviewId | Integer | O | 답글을 작성할 리뷰 ID |
| content | String | O | 답글 내용 |

### 응답 예시
```json
{
  "statusCode": 200
}
```

---

## 4. 리뷰 좋아요 (공통)

### API 정보
- **URL**: `POST /api/review/like`
- **설명**: 리뷰에 좋아요를 누르거나 취소합니다. (타운/빌딩 공통)
- **인증**: **필수** (JWT Token)

### 요청 헤더
```http
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

### 요청 바디
```json
{
  "reviewId": 123
}
```

### 응답 예시
```json
{
  "statusCode": 200,
  "type": 1
}
```

### 응답 필드 설명
| 필드 | 타입 | 설명 |
|------|------|------|
| statusCode | Integer | 상태 코드 |
| type | Integer | 0: 좋아요 취소, 1: 좋아요 추가 |

---

## 📊 데이터 구조

### Review 객체
| 필드 | 타입 | 설명 |
|------|------|------|
| id | Integer | 리뷰 고유 ID |
| type | String | 리뷰 타입 ("town" 또는 "building") |
| targetId | String | 대상 ID (타운의 경우 emdCd) |
| replyId | Integer | 답글인 경우 원본 리뷰 ID (null이면 일반 리뷰) |
| createdBy | Long | 작성자 ID |
| createdAt | String | 작성일시 (ISO 8601) |
| updatedAt | String | 수정일시 (ISO 8601) |
| likeCount | Integer | 좋아요 수 |

### BuildingReviewContent 객체
| 필드 | 타입 | 설명 |
|------|------|------|
| id | Integer | 컨텐츠 고유 ID |
| reviewId | Integer | 연결된 리뷰 ID |
| reviewType | String | 리뷰 타입 ("general", "living", "facility", "reply") |
| title | String | 리뷰 제목 (답글인 경우 null) |
| content | String | 리뷰/답글 내용 |
| rating | Integer | 평점 1~5 (답글인 경우 null) |
| createdAt | String | 작성일시 (ISO 8601) |
| updatedAt | String | 수정일시 (ISO 8601) |

### BuildingReview 객체 (응답용)
| 필드 | 타입 | 설명 |
|------|------|------|
| review | Review | 리뷰 정보 |
| buildingReviewContent | BuildingReviewContent | 리뷰 컨텐츠 |
| alreadyLiked | Boolean | 현재 사용자의 좋아요 여부 (로그인 시) |

---

## 🔧 JavaScript 사용 예시

### 1. 타운 리뷰 작성
```javascript
async function createTownReview(emdCd, title, content, rating = 5) {
  try {
    const token = localStorage.getItem('jwtToken');
    const response = await fetch('http://localhost:8080/api/review/town', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        targetId: emdCd,
        title: title,
        content: content,
        reviewType: 'general',
        rating: rating
      })
    });
    
    const data = await response.json();
    if (data.statusCode === 200) {
      console.log('리뷰가 작성되었습니다');
      return true;
    }
  } catch (error) {
    console.error('리뷰 작성 실패:', error);
    return false;
  }
}

// 사용 예시
createTownReview(
  '1168010100',
  '역삼동 살기 좋은 동네입니다',
  '교통이 편리하고 편의시설이 많아요',
  5
);
```

### 2. 타운 리뷰 조회
```javascript
async function getTownReviews(emdCd) {
  try {
    const token = localStorage.getItem('jwtToken');
    const headers = {
      'Content-Type': 'application/json'
    };
    
    // 토큰이 있으면 헤더에 추가 (좋아요 여부 확인용)
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
    
    const response = await fetch(
      `http://localhost:8080/api/review/town?emdCd=${emdCd}`,
      { headers }
    );
    
    const data = await response.json();
    if (data.statusCode === 200) {
      return data.buildingReviews;
    }
  } catch (error) {
    console.error('리뷰 조회 실패:', error);
    return [];
  }
}

// 사용 예시
getTownReviews('1168010100').then(reviews => {
  console.log('리뷰 목록:', reviews);
  reviews.forEach(item => {
    console.log(`제목: ${item.buildingReviewContent.title}`);
    console.log(`내용: ${item.buildingReviewContent.content}`);
    console.log(`평점: ${item.buildingReviewContent.rating}`);
    console.log(`좋아요: ${item.review.likeCount}`);
    console.log(`이미 좋아요: ${item.alreadyLiked}`);
  });
});
```

### 3. 타운 리뷰 답글 작성
```javascript
async function createTownReviewReply(emdCd, reviewId, content) {
  try {
    const token = localStorage.getItem('jwtToken');
    const response = await fetch('http://localhost:8080/api/review/town/reply', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        uuid: emdCd,
        reviewId: reviewId,
        content: content
      })
    });
    
    const data = await response.json();
    if (data.statusCode === 200) {
      console.log('답글이 작성되었습니다');
      return true;
    }
  } catch (error) {
    console.error('답글 작성 실패:', error);
    return false;
  }
}

// 사용 예시
createTownReviewReply(
  '1168010100',
  123,
  '저도 동의합니다! 역삼동 정말 좋은 동네죠.'
);
```

### 4. 리뷰 좋아요
```javascript
async function toggleReviewLike(reviewId) {
  try {
    const token = localStorage.getItem('jwtToken');
    const response = await fetch('http://localhost:8080/api/review/like', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        reviewId: reviewId
      })
    });
    
    const data = await response.json();
    if (data.statusCode === 200) {
      return data.type; // 0: 취소, 1: 추가
    }
  } catch (error) {
    console.error('좋아요 처리 실패:', error);
    return null;
  }
}

// 사용 예시
toggleReviewLike(123).then(type => {
  if (type === 1) {
    console.log('좋아요 추가됨');
  } else if (type === 0) {
    console.log('좋아요 취소됨');
  }
});
```

---

## 📱 React 예시

### TownReviewList 컴포넌트
```jsx
import React, { useState, useEffect } from 'react';

function TownReviewList({ emdCd }) {
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchReviews();
  }, [emdCd]);

  const fetchReviews = async () => {
    try {
      setLoading(true);
      const token = localStorage.getItem('jwtToken');
      const headers = { 'Content-Type': 'application/json' };
      
      if (token) {
        headers['Authorization'] = `Bearer ${token}`;
      }
      
      const response = await fetch(
        `http://localhost:8080/api/review/town?emdCd=${emdCd}`,
        { headers }
      );
      const data = await response.json();
      
      if (data.statusCode === 200) {
        setReviews(data.buildingReviews);
      }
    } catch (error) {
      console.error('리뷰 조회 실패:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleLike = async (reviewId) => {
    const token = localStorage.getItem('jwtToken');
    if (!token) {
      alert('로그인이 필요합니다');
      return;
    }

    try {
      const response = await fetch('http://localhost:8080/api/review/like', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ reviewId })
      });
      
      const data = await response.json();
      if (data.statusCode === 200) {
        // 리뷰 목록 새로고침
        fetchReviews();
      }
    } catch (error) {
      console.error('좋아요 처리 실패:', error);
    }
  };

  if (loading) return <div>로딩 중...</div>;

  return (
    <div className="town-review-list">
      <h2>동네 리뷰 ({reviews.length})</h2>
      {reviews.map((item) => (
        <div key={item.review.id} className="review-card">
          <h3>{item.buildingReviewContent.title}</h3>
          <div className="rating">
            {'⭐'.repeat(item.buildingReviewContent.rating || 0)}
          </div>
          <p>{item.buildingReviewContent.content}</p>
          <div className="review-meta">
            <span>작성일: {new Date(item.review.createdAt).toLocaleDateString()}</span>
            <button 
              onClick={() => handleLike(item.review.id)}
              className={item.alreadyLiked ? 'liked' : ''}
            >
              ❤️ {item.review.likeCount}
            </button>
          </div>
        </div>
      ))}
    </div>
  );
}

export default TownReviewList;
```

### TownReviewForm 컴포넌트
```jsx
import React, { useState } from 'react';

function TownReviewForm({ emdCd, onSuccess }) {
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [rating, setRating] = useState(5);
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    const token = localStorage.getItem('jwtToken');
    if (!token) {
      alert('로그인이 필요합니다');
      return;
    }

    setSubmitting(true);
    try {
      const response = await fetch('http://localhost:8080/api/review/town', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          targetId: emdCd,
          title,
          content,
          reviewType: 'general',
          rating
        })
      });

      const data = await response.json();
      if (data.statusCode === 200) {
        alert('리뷰가 작성되었습니다');
        setTitle('');
        setContent('');
        setRating(5);
        onSuccess && onSuccess();
      }
    } catch (error) {
      console.error('리뷰 작성 실패:', error);
      alert('리뷰 작성에 실패했습니다');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="review-form">
      <h3>동네 리뷰 작성</h3>
      
      <div className="form-group">
        <label>제목</label>
        <input
          type="text"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="리뷰 제목을 입력하세요"
          required
        />
      </div>

      <div className="form-group">
        <label>평점</label>
        <select value={rating} onChange={(e) => setRating(Number(e.target.value))}>
          <option value={5}>⭐⭐⭐⭐⭐ (5점)</option>
          <option value={4}>⭐⭐⭐⭐ (4점)</option>
          <option value={3}>⭐⭐⭐ (3점)</option>
          <option value={2}>⭐⭐ (2점)</option>
          <option value={1}>⭐ (1점)</option>
        </select>
      </div>

      <div className="form-group">
        <label>내용</label>
        <textarea
          value={content}
          onChange={(e) => setContent(e.target.value)}
          placeholder="이 동네에 대한 솔직한 리뷰를 작성해주세요"
          rows={5}
          required
        />
      </div>

      <button type="submit" disabled={submitting}>
        {submitting ? '작성 중...' : '리뷰 작성'}
      </button>
    </form>
  );
}

export default TownReviewForm;
```

### 통합 사용 예시
```jsx
import React from 'react';
import TownReviewList from './TownReviewList';
import TownReviewForm from './TownReviewForm';

function TownReviewPage({ emdCd }) {
  const [refreshKey, setRefreshKey] = useState(0);

  const handleReviewSuccess = () => {
    // 리뷰 작성 성공 시 목록 새로고침
    setRefreshKey(prev => prev + 1);
  };

  return (
    <div className="town-review-page">
      <TownReviewForm 
        emdCd={emdCd} 
        onSuccess={handleReviewSuccess} 
      />
      <TownReviewList 
        key={refreshKey}
        emdCd={emdCd} 
      />
    </div>
  );
}

export default TownReviewPage;
```

---

## 🔑 주요 특징

### 1. 타운 vs 빌딩 리뷰 차이점

| 구분 | 타운 리뷰 | 빌딩 리뷰 |
|------|----------|----------|
| **식별자** | emdCd (읍면동 코드) | uuid (건물 UUID) |
| **API 경로** | `/api/review/town` | `/api/review/building` |
| **type 값** | "town" | "building" |
| **대상 범위** | 법정동 전체 | 특정 건물 |

### 2. 공통 API
- **리뷰 좋아요**: `/api/review/like` (타운/빌딩 공통 사용)
- 좋아요는 reviewId로 구분하므로 동일한 API 사용

### 3. 인증 정책
- **조회**: 인증 선택 (비로그인 시에도 조회 가능, 좋아요 여부는 false로 표시)
- **작성/답글/좋아요**: 인증 필수 (JWT Token 필요)

---

## 💡 개발 팁

### 1. emdCd 확인 방법
```javascript
// 타운 커뮤니티 조회 시 emdCd 얻기
async function getTownCommunity(emdCd) {
  const response = await fetch(
    `http://localhost:8080/api/community/town?emdCd=${emdCd}`
  );
  const data = await response.json();
  return data.community; // targetId에 emdCd가 저장되어 있음
}
```

### 2. 리뷰 타입 활용
```javascript
const REVIEW_TYPES = {
  general: '일반 리뷰',
  living: '주거 환경',
  facility: '편의시설',
  safety: '치안/안전',
  traffic: '교통',
  reply: '답글'
};

// 리뷰 타입별 필터링
const filteredReviews = reviews.filter(
  item => item.buildingReviewContent.reviewType === 'living'
);
```

### 3. 답글 구분하기
```javascript
// 일반 리뷰와 답글 구분
reviews.forEach(item => {
  if (item.review.replyId === null) {
    console.log('일반 리뷰:', item);
  } else {
    console.log('답글:', item, '원본 리뷰 ID:', item.review.replyId);
  }
});
```

### 4. 에러 처리
```javascript
async function createReviewWithErrorHandling(emdCd, title, content) {
  try {
    const response = await fetch('http://localhost:8080/api/review/town', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
      },
      body: JSON.stringify({
        targetId: emdCd,
        title,
        content,
        reviewType: 'general',
        rating: 5
      })
    });

    if (response.status === 401) {
      alert('로그인이 필요합니다');
      // 로그인 페이지로 이동
      window.location.href = '/login';
      return;
    }

    if (response.status === 403) {
      alert('권한이 없습니다');
      return;
    }

    const data = await response.json();
    if (data.statusCode === 200) {
      alert('리뷰가 작성되었습니다');
      return true;
    } else {
      alert('리뷰 작성에 실패했습니다');
      return false;
    }
  } catch (error) {
    console.error('리뷰 작성 오류:', error);
    alert('네트워크 오류가 발생했습니다');
    return false;
  }
}
```

---

## 📞 문의 및 지원

API 사용 중 문제가 발생하거나 추가 기능이 필요한 경우, 백엔드 팀에 문의해주세요.

**Swagger UI**: `http://localhost:8080/swagger-ui.html`

---

## 📝 변경 이력

- **2025-01-21**: 타운 커뮤니티 리뷰 API 추가
  - `POST /api/review/town` - 타운 리뷰 작성
  - `GET /api/review/town` - 타운 리뷰 조회
  - `POST /api/review/town/reply` - 타운 리뷰 답글 작성

