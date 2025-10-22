# 커뮤니티 목록 및 통계 API 문서

## 📌 API 개요

- **Base URL**: `http://localhost:8080/api/community`
- **인증 방식**: 인증 불필요 (공개 API)
- **Content-Type**: `application/json`

---

## 1. 전체 커뮤니티 목록 조회

### API 정보
- **URL**: `GET /api/community/list`
- **설명**: 현재 생성되어 있는 모든 커뮤니티 목록을 빌딩/타운 구분하여 제공합니다.
- **인증**: 불필요

### 요청 파라미터
| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|------|--------|------|
| limit | Integer | X | 10 | 조회할 커뮤니티 개수 |

### 요청 예시
```http
GET /api/community/list?limit=20
```

### 응답 예시
```json
{
  "buildingCommunities": [
    {
      "uuid": "a1b2c3d4e5f6",
      "type": "building",
      "name": "강남대학교 커뮤니티",
      "description": "강남대학교 건물 커뮤니티입니다.",
      "geoFeaturesName": "구갈동",
      "userCount": 152,
      "createdAt": "2025-01-15T10:30:00"
    },
    {
      "uuid": "g7h8i9j0k1l2",
      "type": "building",
      "name": "디지털미디어시티역 커뮤니티",
      "description": "디지털미디어시티역 커뮤니티입니다.",
      "geoFeaturesName": "상암동",
      "userCount": 89,
      "createdAt": "2025-01-14T15:20:00"
    }
  ],
  "townCommunities": [
    {
      "uuid": "m3n4o5p6q7r8",
      "type": "town",
      "name": "구의동 동네 커뮤니티",
      "description": "구의동 지역 주민들의 커뮤니티입니다.",
      "geoFeaturesName": "구의동",
      "userCount": 243,
      "createdAt": "2025-01-16T09:00:00"
    }
  ]
}
```

---

## 2. 인기 커뮤니티 목록 조회 (사용자 수 기준)

### API 정보
- **URL**: `GET /api/community/popular`
- **설명**: 가입한 사용자 수가 많은 순서대로 커뮤니티를 빌딩/타운 구분하여 제공합니다.
- **인증**: 불필요

### 요청 파라미터
| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|------|--------|------|
| limit | Integer | X | 10 | 조회할 커뮤니티 개수 |

### 요청 예시
```http
GET /api/community/popular?limit=5
```

### 응답 예시
```json
{
  "buildingCommunities": [
    {
      "uuid": "a1b2c3d4e5f6",
      "type": "building",
      "name": "강남대학교 커뮤니티",
      "description": "강남대학교 건물 커뮤니티입니다.",
      "geoFeaturesName": "구갈동",
      "userCount": 523,
      "createdAt": "2024-12-01T10:30:00"
    },
    {
      "uuid": "g7h8i9j0k1l2",
      "type": "building",
      "name": "롯데월드타워 커뮤니티",
      "description": "롯데월드타워 커뮤니티입니다.",
      "geoFeaturesName": "신천동",
      "userCount": 412,
      "createdAt": "2024-11-15T14:20:00"
    }
  ],
  "townCommunities": [
    {
      "uuid": "m3n4o5p6q7r8",
      "type": "town",
      "name": "역삼동 동네 커뮤니티",
      "description": "역삼동 지역 주민들의 커뮤니티입니다.",
      "geoFeaturesName": "역삼동",
      "userCount": 1245,
      "createdAt": "2024-10-20T09:00:00"
    }
  ]
}
```

---

## 3. 최근 생성된 커뮤니티 목록 조회

### API 정보
- **URL**: `GET /api/community/recent`
- **설명**: 최근에 생성된 커뮤니티를 빌딩/타운 구분하여 제공합니다.
- **인증**: 불필요

### 요청 파라미터
| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|------|--------|------|
| limit | Integer | X | 10 | 조회할 커뮤니티 개수 |

### 요청 예시
```http
GET /api/community/recent?limit=10
```

### 응답 예시
```json
{
  "buildingCommunities": [
    {
      "uuid": "x1y2z3a4b5c6",
      "type": "building",
      "name": "서울시청 커뮤니티",
      "description": "서울시청 건물 커뮤니티입니다.",
      "geoFeaturesName": "을지로동",
      "userCount": 12,
      "createdAt": "2025-01-20T16:45:00"
    },
    {
      "uuid": "d7e8f9g0h1i2",
      "type": "building",
      "name": "삼성역 코엑스 커뮤니티",
      "description": "삼성역 코엑스 커뮤니티입니다.",
      "geoFeaturesName": "삼성동",
      "userCount": 8,
      "createdAt": "2025-01-19T11:20:00"
    }
  ],
  "townCommunities": [
    {
      "uuid": "j3k4l5m6n7o8",
      "type": "town",
      "name": "서초동 동네 커뮤니티",
      "description": "서초동 지역 주민들의 커뮤니티입니다.",
      "geoFeaturesName": "서초동",
      "userCount": 25,
      "createdAt": "2025-01-18T14:30:00"
    }
  ]
}
```

---

## 4. 최근 작성된 게시글 조회

### API 정보
- **URL**: `GET /api/community/posts/recent`
- **설명**: 최근에 작성된 커뮤니티 게시글을 빌딩/타운 구분하여 제공합니다. 각 게시글이 속한 커뮤니티 정보도 함께 제공됩니다.
- **인증**: 불필요

### 요청 파라미터
| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|------|--------|------|
| limit | Integer | X | 10 | 조회할 게시글 개수 |

### 요청 예시
```http
GET /api/community/posts/recent?limit=15
```

### 응답 예시
```json
{
  "buildingPosts": [
    {
      "postId": 12345,
      "title": "엘리베이터 공사 안내",
      "content": "내일 오전 10시부터 엘리베이터 공사가 예정되어 있습니다...",
      "createdBy": 9,
      "createdAt": "2025-01-21T09:15:00",
      "likeCount": 15,
      "viewCount": 234,
      "replyCount": 8,
      "community": {
        "uuid": "a1b2c3d4e5f6",
        "name": "강남대학교 커뮤니티",
        "type": "building",
        "geoFeaturesName": "구갈동"
      }
    },
    {
      "postId": 12346,
      "title": "주차 공간 문의",
      "content": "주말에 주차 가능한 곳이 있을까요?",
      "createdBy": 102,
      "createdAt": "2025-01-21T08:45:00",
      "likeCount": 3,
      "viewCount": 89,
      "replyCount": 12,
      "community": {
        "uuid": "g7h8i9j0k1l2",
        "name": "디지털미디어시티역 커뮤니티",
        "type": "building",
        "geoFeaturesName": "상암동"
      }
    }
  ],
  "townPosts": [
    {
      "postId": 12347,
      "title": "동네 맛집 추천해주세요",
      "content": "구의동에서 맛있는 한식당 추천 부탁드립니다!",
      "createdBy": 55,
      "createdAt": "2025-01-21T10:30:00",
      "likeCount": 28,
      "viewCount": 456,
      "replyCount": 23,
      "community": {
        "uuid": "m3n4o5p6q7r8",
        "name": "구의동 동네 커뮤니티",
        "type": "town",
        "geoFeaturesName": "구의동"
      }
    }
  ]
}
```

---

## 📊 응답 필드 설명

### CommunityItemDto
| 필드 | 타입 | 설명 |
|------|------|------|
| uuid | String | 커뮤니티 고유 ID |
| type | String | 커뮤니티 타입 ("building" 또는 "town") |
| name | String | 커뮤니티 이름 |
| description | String | 커뮤니티 설명 |
| geoFeaturesName | String | 지역 이름 (법정동명) |
| userCount | Integer | 가입한 사용자 수 |
| createdAt | String | 생성일시 (ISO 8601 형식) |

### PostItemDto
| 필드 | 타입 | 설명 |
|------|------|------|
| postId | Long | 게시글 ID |
| title | String | 게시글 제목 |
| content | String | 게시글 내용 |
| createdBy | Long | 작성자 ID |
| createdAt | String | 작성일시 (ISO 8601 형식) |
| likeCount | Long | 좋아요 수 |
| viewCount | Long | 조회수 |
| replyCount | Long | 댓글 수 |
| community | CommunityInfo | 게시글이 속한 커뮤니티 정보 |

### CommunityInfo
| 필드 | 타입 | 설명 |
|------|------|------|
| uuid | String | 커뮤니티 고유 ID |
| name | String | 커뮤니티 이름 |
| type | String | 커뮤니티 타입 ("building" 또는 "town") |
| geoFeaturesName | String | 지역 이름 (법정동명) |

---

## 🔧 JavaScript 사용 예시

### 1. 전체 커뮤니티 목록 조회
```javascript
async function getAllCommunities(limit = 10) {
  try {
    const response = await fetch(
      `http://localhost:8080/api/community/list?limit=${limit}`,
      {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json'
        }
      }
    );
    
    const data = await response.json();
    console.log('빌딩 커뮤니티:', data.buildingCommunities);
    console.log('타운 커뮤니티:', data.townCommunities);
    return data;
  } catch (error) {
    console.error('커뮤니티 목록 조회 실패:', error);
    throw error;
  }
}
```

### 2. 인기 커뮤니티 조회
```javascript
async function getPopularCommunities(limit = 5) {
  try {
    const response = await fetch(
      `http://localhost:8080/api/community/popular?limit=${limit}`,
      {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json'
        }
      }
    );
    
    const data = await response.json();
    return data;
  } catch (error) {
    console.error('인기 커뮤니티 조회 실패:', error);
    throw error;
  }
}
```

### 3. 최근 생성된 커뮤니티 조회
```javascript
async function getRecentCommunities(limit = 10) {
  try {
    const response = await fetch(
      `http://localhost:8080/api/community/recent?limit=${limit}`,
      {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json'
        }
      }
    );
    
    const data = await response.json();
    return data;
  } catch (error) {
    console.error('최근 커뮤니티 조회 실패:', error);
    throw error;
  }
}
```

### 4. 최근 작성된 게시글 조회
```javascript
async function getRecentPosts(limit = 10) {
  try {
    const response = await fetch(
      `http://localhost:8080/api/community/posts/recent?limit=${limit}`,
      {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json'
        }
      }
    );
    
    const data = await response.json();
    console.log('빌딩 커뮤니티 게시글:', data.buildingPosts);
    console.log('타운 커뮤니티 게시글:', data.townPosts);
    return data;
  } catch (error) {
    console.error('최근 게시글 조회 실패:', error);
    throw error;
  }
}
```

---

## 📱 React Hook 예시

### CommunityList 컴포넌트
```jsx
import React, { useState, useEffect } from 'react';

function CommunityList() {
  const [communities, setCommunities] = useState({
    buildingCommunities: [],
    townCommunities: []
  });
  const [activeTab, setActiveTab] = useState('all'); // 'all', 'popular', 'recent'
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchCommunities();
  }, [activeTab]);

  const fetchCommunities = async () => {
    try {
      setLoading(true);
      let endpoint;
      switch (activeTab) {
        case 'popular':
          endpoint = '/api/community/popular?limit=20';
          break;
        case 'recent':
          endpoint = '/api/community/recent?limit=20';
          break;
        default:
          endpoint = '/api/community/list?limit=50';
      }
      
      const response = await fetch(`http://localhost:8080${endpoint}`);
      const data = await response.json();
      setCommunities(data);
    } catch (error) {
      console.error('커뮤니티 조회 실패:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div>로딩중...</div>;

  return (
    <div>
      <h1>커뮤니티 목록</h1>
      
      {/* 탭 버튼 */}
      <div className="tabs">
        <button onClick={() => setActiveTab('all')}>전체</button>
        <button onClick={() => setActiveTab('popular')}>인기</button>
        <button onClick={() => setActiveTab('recent')}>최근</button>
      </div>
      
      {/* 빌딩 커뮤니티 */}
      <section>
        <h2>빌딩 커뮤니티</h2>
        {communities.buildingCommunities.map(community => (
          <div key={community.uuid} className="community-card">
            <h3>{community.name}</h3>
            <p>{community.description}</p>
            <span>{community.geoFeaturesName}</span>
            <span>멤버 {community.userCount}명</span>
          </div>
        ))}
      </section>
      
      {/* 타운 커뮤니티 */}
      <section>
        <h2>동네 커뮤니티</h2>
        {communities.townCommunities.map(community => (
          <div key={community.uuid} className="community-card">
            <h3>{community.name}</h3>
            <p>{community.description}</p>
            <span>{community.geoFeaturesName}</span>
            <span>멤버 {community.userCount}명</span>
          </div>
        ))}
      </section>
    </div>
  );
}

export default CommunityList;
```

### RecentPosts 컴포넌트
```jsx
import React, { useState, useEffect } from 'react';

function RecentPosts() {
  const [posts, setPosts] = useState({
    buildingPosts: [],
    townPosts: []
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchRecentPosts();
  }, []);

  const fetchRecentPosts = async () => {
    try {
      setLoading(true);
      const response = await fetch(
        'http://localhost:8080/api/community/posts/recent?limit=15'
      );
      const data = await response.json();
      setPosts(data);
    } catch (error) {
      console.error('최근 게시글 조회 실패:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div>로딩중...</div>;

  return (
    <div>
      <h1>최근 게시글</h1>
      
      {/* 빌딩 커뮤니티 게시글 */}
      <section>
        <h2>빌딩 커뮤니티 게시글</h2>
        {posts.buildingPosts.map(post => (
          <div key={post.postId} className="post-card">
            <h3>{post.title}</h3>
            <p>{post.content}</p>
            <div className="post-meta">
              <span>커뮤니티: {post.community?.name}</span>
              <span>좋아요 {post.likeCount}</span>
              <span>조회 {post.viewCount}</span>
              <span>댓글 {post.replyCount}</span>
            </div>
          </div>
        ))}
      </section>
      
      {/* 타운 커뮤니티 게시글 */}
      <section>
        <h2>동네 커뮤니티 게시글</h2>
        {posts.townPosts.map(post => (
          <div key={post.postId} className="post-card">
            <h3>{post.title}</h3>
            <p>{post.content}</p>
            <div className="post-meta">
              <span>커뮤니티: {post.community?.name}</span>
              <span>좋아요 {post.likeCount}</span>
              <span>조회 {post.viewCount}</span>
              <span>댓글 {post.replyCount}</span>
            </div>
          </div>
        ))}
      </section>
    </div>
  );
}

export default RecentPosts;
```

---

## 💡 개발 팁

### 1. 한 번의 요청으로 모든 데이터 받기
모든 API는 빌딩/타운 커뮤니티를 한 번의 요청으로 함께 제공합니다. 별도의 요청이 필요 없습니다.

### 2. limit 파라미터 활용
```javascript
// 메인 페이지: 각 타입별로 5개씩만
getPopularCommunities(5);

// 커뮤니티 목록 페이지: 더 많이
getAllCommunities(50);
```

### 3. 게시글의 커뮤니티 정보 활용
최근 게시글 API는 각 게시글이 어떤 커뮤니티의 글인지 정보를 함께 제공하므로, 추가 API 호출 없이 바로 표시할 수 있습니다.

### 4. 캐싱 전략
인기 커뮤니티나 최근 게시글 같은 데이터는 자주 변하지 않으므로, 적절한 캐싱을 통해 성능을 향상시킬 수 있습니다.

```javascript
// 5분마다 갱신
const CACHE_DURATION = 5 * 60 * 1000;
let cachedData = null;
let cacheTime = null;

async function getCachedCommunities() {
  const now = Date.now();
  if (cachedData && cacheTime && (now - cacheTime) < CACHE_DURATION) {
    return cachedData;
  }
  
  const data = await getPopularCommunities(10);
  cachedData = data;
  cacheTime = now;
  return data;
}
```

---

## 📞 문의 및 지원

API 사용 중 문제가 발생하거나 추가 기능이 필요한 경우, 백엔드 팀에 문의해주세요.

**Swagger UI**: `http://localhost:8080/swagger-ui.html`

