# 커뮤니티 관리자 API 문서

## 📌 API 개요

- **Base URL**: `http://localhost:8080/api/community/admin`
- **인증 방식**: JWT Bearer Token (대부분의 API에서 필수)
- **Content-Type**: `application/json`

커뮤니티 관리자 기능은 빌딩 커뮤니티와 타운 커뮤니티 모두에 적용됩니다.

---

## 📋 목차

1. [관리자 시스템 개요](#-관리자-시스템-개요)
2. [관리자 신청 API](#-관리자-신청-api)
3. [관리자 권한 확인 API](#-관리자-권한-확인-api)
4. [회원 관리 API](#-회원-관리-api)
5. [게시글 관리 API](#-게시글-관리-api)
6. [공지사항 관리 API](#-공지사항-관리-api)
7. [DB 테이블 구조](#-db-테이블-구조)
8. [JavaScript 사용 예시](#-javascript-사용-예시)
9. [React 예시](#-react-예시)
10. [워크플로우](#-워크플로우)

---

## 🎯 관리자 시스템 개요

### 관리자 권한 획득 조건

1. **커뮤니티에 관리자가 없는 경우**
   - 일반 사용자가 관리자 신청
   - 웹 운영자가 직접 DB에서 승인 처리

2. **커뮤니티에 관리자가 있는 경우**
   - 일반 사용자가 관리자 신청
   - 기존 관리자가 승인/거절 처리

### 관리자 기능

1. ✅ **회원 관리**: 가입된 회원 목록 조회 및 추방
2. ✅ **게시글 관리**: 커뮤니티 게시글 삭제
3. ✅ **공지사항 관리**: 공지사항 작성/수정/삭제/고정

---

## 🔐 관리자 신청 API

### 1. 관리자 신청

#### API 정보
- **URL**: `POST /api/community/admin/request`
- **설명**: 커뮤니티 관리자 신청
- **인증**: **필수** (JWT Token)

#### 요청 헤더
```http
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

#### 요청 바디
```json
{
  "communityUuid": "a1b2c3d4e5f6",
  "requestMessage": "이 커뮤니티를 적극적으로 관리하고 싶습니다."
}
```

#### 응답 예시
```json
{
  "statusCode": 200,
  "message": "관리자 신청이 완료되었습니다"
}
```

#### 에러 응답
```json
{
  "statusCode": 409,
  "message": "이미 신청한 내역이 있습니다"
}
```

---

### 2. 관리자 신청 목록 조회

#### API 정보
- **URL**: `GET /api/community/admin/requests`
- **설명**: 커뮤니티의 관리자 신청 목록 조회
- **인증**: 선택 (관리자/운영자만 의미 있음)

#### 요청 파라미터
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| communityUuid | String | O | 커뮤니티 UUID |
| status | String | X | pending(대기), approved(승인), rejected(거절) |

#### 요청 예시
```http
GET /api/community/admin/requests?communityUuid=a1b2c3d4e5f6&status=pending
```

#### 응답 예시
```json
[
  {
    "id": 1,
    "communityUuid": "a1b2c3d4e5f6",
    "userId": 123,
    "nickname": "홍길동",
    "status": "pending",
    "requestMessage": "이 커뮤니티를 적극적으로 관리하고 싶습니다.",
    "responseMessage": null,
    "processedBy": null,
    "processedAt": null,
    "createdAt": "2025-01-20T10:30:00"
  }
]
```

---

### 3. 관리자 신청 처리 (승인/거절)

#### API 정보
- **URL**: `POST /api/community/admin/process`
- **설명**: 관리자 신청 승인 또는 거절
- **인증**: **필수** (관리자만 가능)

#### 요청 헤더
```http
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

#### 요청 바디
```json
{
  "requestId": 1,
  "communityUuid": "a1b2c3d4e5f6",
  "status": "approved",
  "responseMessage": "승인합니다. 커뮤니티를 잘 관리해주세요."
}
```

#### 요청 필드 설명
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| requestId | Integer | O | 신청 ID |
| communityUuid | String | O | 커뮤니티 UUID |
| status | String | O | "approved" 또는 "rejected" |
| responseMessage | String | X | 응답 메시지 |

#### 응답 예시
```json
{
  "statusCode": 200,
  "message": "처리가 완료되었습니다"
}
```

---

## 🔍 관리자 권한 확인 API

### 4. 관리자 목록 조회

#### API 정보
- **URL**: `GET /api/community/admin/list`
- **설명**: 커뮤니티의 관리자 목록 조회
- **인증**: 불필요

#### 요청 파라미터
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| communityUuid | String | O | 커뮤니티 UUID |

#### 요청 예시
```http
GET /api/community/admin/list?communityUuid=a1b2c3d4e5f6
```

#### 응답 예시
```json
[
  {
    "id": 1,
    "communityUuid": "a1b2c3d4e5f6",
    "userId": 123,
    "nickname": "홍길동",
    "appointedAt": "2025-01-20T10:30:00"
  }
]
```

---

### 5. 내 관리자 권한 확인

#### API 정보
- **URL**: `GET /api/community/admin/check`
- **설명**: 현재 사용자가 해당 커뮤니티의 관리자인지 확인
- **인증**: **필수** (JWT Token)

#### 요청 파라미터
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| communityUuid | String | O | 커뮤니티 UUID |

#### 요청 예시
```http
GET /api/community/admin/check?communityUuid=a1b2c3d4e5f6
Authorization: Bearer {JWT_TOKEN}
```

#### 응답 예시
```json
true
```

---

### 6. 커뮤니티 관리자 존재 여부 확인

#### API 정보
- **URL**: `GET /api/community/admin/has-admin`
- **설명**: 커뮤니티에 관리자가 존재하는지 확인
- **인증**: 불필요

#### 요청 파라미터
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| communityUuid | String | O | 커뮤니티 UUID |

#### 요청 예시
```http
GET /api/community/admin/has-admin?communityUuid=a1b2c3d4e5f6
```

#### 응답 예시
```json
true
```

---

## 👥 회원 관리 API

### 7. 커뮤니티 회원 목록 조회

#### API 정보
- **URL**: `GET /api/community/admin/members`
- **설명**: 커뮤니티 가입 회원 목록 조회 (관리자 전용)
- **인증**: **필수** (관리자만 가능)

#### 요청 파라미터
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| communityUuid | String | O | 커뮤니티 UUID |

#### 요청 예시
```http
GET /api/community/admin/members?communityUuid=a1b2c3d4e5f6
Authorization: Bearer {JWT_TOKEN}
```

#### 응답 예시
```json
[
  {
    "userId": 456,
    "nickname": "김철수",
    "joinedAt": "2025-01-15T14:20:00",
    "postCount": 15,
    "replyCount": 42
  },
  {
    "userId": 789,
    "nickname": "이영희",
    "joinedAt": "2025-01-18T09:30:00",
    "postCount": 8,
    "replyCount": 23
  }
]
```

---

### 8. 회원 추방

#### API 정보
- **URL**: `POST /api/community/admin/kick`
- **설명**: 커뮤니티 회원 추방 (관리자 전용)
- **인증**: **필수** (관리자만 가능)

#### 요청 헤더
```http
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

#### 요청 바디
```json
{
  "communityUuid": "a1b2c3d4e5f6",
  "targetUserId": 456,
  "reason": "커뮤니티 규칙 위반"
}
```

#### 응답 예시
```json
{
  "statusCode": 200,
  "message": "회원이 추방되었습니다"
}
```

#### 주의사항
- 관리자 본인은 추방 불가
- 다른 관리자는 추방 불가

---

## 📝 게시글 관리 API

### 9. 게시글 삭제

#### API 정보
- **URL**: `DELETE /api/community/admin/post`
- **설명**: 커뮤니티 게시글 삭제 (관리자 전용)
- **인증**: **필수** (관리자만 가능)

#### 요청 헤더
```http
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

#### 요청 바디
```json
{
  "communityUuid": "a1b2c3d4e5f6",
  "postId": 123,
  "reason": "부적절한 내용"
}
```

#### 응답 예시
```json
{
  "statusCode": 200,
  "message": "게시글이 삭제되었습니다"
}
```

---

## 📢 공지사항 관리 API

### 10. 공지사항 작성

#### API 정보
- **URL**: `POST /api/community/admin/notice`
- **설명**: 커뮤니티 공지사항 작성 (관리자 전용)
- **인증**: **필수** (관리자만 가능)

#### 요청 헤더
```http
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

#### 요청 바디
```json
{
  "communityUuid": "a1b2c3d4e5f6",
  "title": "커뮤니티 이용 규칙",
  "content": "1. 서로 존중해주세요.\n2. 욕설 및 비방 금지\n3. 광고성 게시글 금지",
  "isPinned": true
}
```

#### 요청 필드 설명
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| communityUuid | String | O | 커뮤니티 UUID |
| title | String | O | 공지사항 제목 |
| content | String | O | 공지사항 내용 |
| isPinned | Boolean | X | 상단 고정 여부 (기본값: false) |

#### 응답 예시
```json
{
  "statusCode": 200,
  "message": "공지사항이 작성되었습니다",
  "notice": {
    "id": 1,
    "communityUuid": "a1b2c3d4e5f6",
    "title": "커뮤니티 이용 규칙",
    "content": "1. 서로 존중해주세요.\n2. 욕설 및 비방 금지\n3. 광고성 게시글 금지",
    "isPinned": true,
    "createdBy": 123,
    "createdByNickname": "홍길동",
    "createdAt": "2025-01-20T10:30:00",
    "updatedAt": "2025-01-20T10:30:00"
  }
}
```

---

### 11. 공지사항 수정

#### API 정보
- **URL**: `PUT /api/community/admin/notice`
- **설명**: 커뮤니티 공지사항 수정 (관리자 전용)
- **인증**: **필수** (관리자만 가능)

#### 요청 바디
```json
{
  "noticeId": 1,
  "communityUuid": "a1b2c3d4e5f6",
  "title": "커뮤니티 이용 규칙 (수정)",
  "content": "1. 서로 존중해주세요.\n2. 욕설 및 비방 금지\n3. 광고성 게시글 금지\n4. 개인정보 공유 금지",
  "isPinned": true
}
```

#### 응답 예시
```json
{
  "statusCode": 200,
  "message": "공지사항이 수정되었습니다",
  "notice": {
    "id": 1,
    "communityUuid": "a1b2c3d4e5f6",
    "title": "커뮤니티 이용 규칙 (수정)",
    "content": "1. 서로 존중해주세요.\n2. 욕설 및 비방 금지\n3. 광고성 게시글 금지\n4. 개인정보 공유 금지",
    "isPinned": true,
    "createdBy": 123,
    "createdByNickname": "홍길동",
    "createdAt": "2025-01-20T10:30:00",
    "updatedAt": "2025-01-20T11:00:00"
  }
}
```

---

### 12. 공지사항 삭제

#### API 정보
- **URL**: `DELETE /api/community/admin/notice`
- **설명**: 커뮤니티 공지사항 삭제 (관리자 전용)
- **인증**: **필수** (관리자만 가능)

#### 요청 바디
```json
{
  "noticeId": 1,
  "communityUuid": "a1b2c3d4e5f6"
}
```

#### 응답 예시
```json
{
  "statusCode": 200,
  "message": "공지사항이 삭제되었습니다"
}
```

---

### 13. 공지사항 목록 조회

#### API 정보
- **URL**: `GET /api/community/admin/notice/list`
- **설명**: 커뮤니티 공지사항 목록 조회 (모든 사용자 가능)
- **인증**: 불필요

#### 요청 파라미터
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| communityUuid | String | O | 커뮤니티 UUID |
| limit | Integer | X | 조회 개수 제한 |

#### 요청 예시
```http
GET /api/community/admin/notice/list?communityUuid=a1b2c3d4e5f6&limit=10
```

#### 응답 예시
```json
{
  "notices": [
    {
      "id": 1,
      "communityUuid": "a1b2c3d4e5f6",
      "title": "커뮤니티 이용 규칙",
      "content": "1. 서로 존중해주세요...",
      "isPinned": true,
      "createdBy": 123,
      "createdByNickname": "홍길동",
      "createdAt": "2025-01-20T10:30:00",
      "updatedAt": "2025-01-20T10:30:00"
    }
  ],
  "statusCode": 200
}
```

---

### 14. 고정된 공지사항 조회

#### API 정보
- **URL**: `GET /api/community/admin/notice/pinned`
- **설명**: 상단 고정된 공지사항만 조회 (모든 사용자 가능)
- **인증**: 불필요

#### 요청 파라미터
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| communityUuid | String | O | 커뮤니티 UUID |

#### 요청 예시
```http
GET /api/community/admin/notice/pinned?communityUuid=a1b2c3d4e5f6
```

#### 응답 예시
```json
{
  "notices": [
    {
      "id": 1,
      "communityUuid": "a1b2c3d4e5f6",
      "title": "커뮤니티 이용 규칙",
      "content": "1. 서로 존중해주세요...",
      "isPinned": true,
      "createdBy": 123,
      "createdByNickname": "홍길동",
      "createdAt": "2025-01-20T10:30:00",
      "updatedAt": "2025-01-20T10:30:00"
    }
  ],
  "statusCode": 200
}
```

---

### 15. 공지사항 단일 조회

#### API 정보
- **URL**: `GET /api/community/admin/notice`
- **설명**: 특정 공지사항 상세 조회 (모든 사용자 가능)
- **인증**: 불필요

#### 요청 파라미터
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| noticeId | Long | O | 공지사항 ID |
| communityUuid | String | O | 커뮤니티 UUID |

#### 요청 예시
```http
GET /api/community/admin/notice?noticeId=1&communityUuid=a1b2c3d4e5f6
```

#### 응답 예시
```json
{
  "id": 1,
  "communityUuid": "a1b2c3d4e5f6",
  "title": "커뮤니티 이용 규칙",
  "content": "1. 서로 존중해주세요.\n2. 욕설 및 비방 금지\n3. 광고성 게시글 금지",
  "isPinned": true,
  "createdBy": 123,
  "createdByNickname": "홍길동",
  "createdAt": "2025-01-20T10:30:00",
  "updatedAt": "2025-01-20T10:30:00"
}
```

---

## 🗄️ DB 테이블 구조

### community_admin (관리자 테이블)
```sql
CREATE TABLE community_admin (
  id INT PRIMARY KEY AUTO_INCREMENT,
  community_uuid VARCHAR(255) NOT NULL,
  user_id BIGINT NOT NULL,
  nickname VARCHAR(255) NOT NULL,
  appointed_by BIGINT,
  appointed_at DATETIME,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  deleted_at DATETIME,
  is_deleted BOOLEAN DEFAULT FALSE
);
```

### community_admin_request (관리자 신청 테이블)
```sql
CREATE TABLE community_admin_request (
  id INT PRIMARY KEY AUTO_INCREMENT,
  community_uuid VARCHAR(255) NOT NULL,
  user_id BIGINT NOT NULL,
  nickname VARCHAR(255) NOT NULL,
  status VARCHAR(50) DEFAULT 'pending',
  request_message TEXT,
  response_message TEXT,
  processed_by BIGINT,
  processed_at DATETIME,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  deleted_at DATETIME,
  is_deleted BOOLEAN DEFAULT FALSE
);
```

### community_notice (공지사항 테이블)
```sql
CREATE TABLE community_notice (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  community_uuid VARCHAR(255) NOT NULL,
  title VARCHAR(500) NOT NULL,
  content TEXT NOT NULL,
  is_pinned BOOLEAN DEFAULT FALSE,
  created_by BIGINT NOT NULL,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  deleted_at DATETIME,
  is_deleted BOOLEAN DEFAULT FALSE
);
```

---

## 🔧 JavaScript 사용 예시

### 1. 관리자 신청
```javascript
async function requestAdmin(communityUuid, message) {
  try {
    const token = localStorage.getItem('jwtToken');
    const response = await fetch('http://localhost:8080/api/community/admin/request', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        communityUuid: communityUuid,
        requestMessage: message
      })
    });

    const data = await response.json();
    if (data.statusCode === 200) {
      alert(data.message);
      return true;
    }
  } catch (error) {
    console.error('관리자 신청 실패:', error);
    return false;
  }
}

// 사용 예시
requestAdmin('a1b2c3d4e5f6', '이 커뮤니티를 적극적으로 관리하고 싶습니다.');
```

### 2. 관리자 권한 확인
```javascript
async function checkAdmin(communityUuid) {
  try {
    const token = localStorage.getItem('jwtToken');
    if (!token) return false;

    const response = await fetch(
      `http://localhost:8080/api/community/admin/check?communityUuid=${communityUuid}`,
      {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      }
    );

    const isAdmin = await response.json();
    return isAdmin;
  } catch (error) {
    console.error('관리자 권한 확인 실패:', error);
    return false;
  }
}

// 사용 예시
checkAdmin('a1b2c3d4e5f6').then(isAdmin => {
  if (isAdmin) {
    console.log('관리자입니다');
    // 관리자 메뉴 표시
  }
});
```

### 3. 회원 목록 조회 및 추방
```javascript
async function getMembers(communityUuid) {
  try {
    const token = localStorage.getItem('jwtToken');
    const response = await fetch(
      `http://localhost:8080/api/community/admin/members?communityUuid=${communityUuid}`,
      {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      }
    );

    const members = await response.json();
    return members;
  } catch (error) {
    console.error('회원 목록 조회 실패:', error);
    return [];
  }
}

async function kickMember(communityUuid, targetUserId, reason) {
  try {
    const token = localStorage.getItem('jwtToken');
    const response = await fetch('http://localhost:8080/api/community/admin/kick', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        communityUuid: communityUuid,
        targetUserId: targetUserId,
        reason: reason
      })
    });

    const data = await response.json();
    if (data.statusCode === 200) {
      alert(data.message);
      return true;
    }
  } catch (error) {
    console.error('회원 추방 실패:', error);
    return false;
  }
}
```

### 4. 공지사항 작성/수정/삭제
```javascript
async function createNotice(communityUuid, title, content, isPinned = false) {
  try {
    const token = localStorage.getItem('jwtToken');
    const response = await fetch('http://localhost:8080/api/community/admin/notice', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        communityUuid: communityUuid,
        title: title,
        content: content,
        isPinned: isPinned
      })
    });

    const data = await response.json();
    if (data.statusCode === 200) {
      return data.notice;
    }
  } catch (error) {
    console.error('공지사항 작성 실패:', error);
    return null;
  }
}

async function getNotices(communityUuid, limit = 10) {
  try {
    const response = await fetch(
      `http://localhost:8080/api/community/admin/notice/list?communityUuid=${communityUuid}&limit=${limit}`
    );

    const data = await response.json();
    return data.notices;
  } catch (error) {
    console.error('공지사항 조회 실패:', error);
    return [];
  }
}
```

---

## 📱 React 예시

### AdminPanel 컴포넌트
```jsx
import React, { useState, useEffect } from 'react';

function AdminPanel({ communityUuid }) {
  const [isAdmin, setIsAdmin] = useState(false);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('members'); // members, posts, notices

  useEffect(() => {
    checkAdminStatus();
  }, [communityUuid]);

  const checkAdminStatus = async () => {
    try {
      const token = localStorage.getItem('jwtToken');
      const response = await fetch(
        `http://localhost:8080/api/community/admin/check?communityUuid=${communityUuid}`,
        {
          headers: { 'Authorization': `Bearer ${token}` }
        }
      );
      const isAdmin = await response.json();
      setIsAdmin(isAdmin);
    } catch (error) {
      console.error('관리자 권한 확인 실패:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div>로딩 중...</div>;
  if (!isAdmin) return <div>관리자 권한이 필요합니다.</div>;

  return (
    <div className="admin-panel">
      <h2>관리자 패널</h2>
      
      <div className="tab-buttons">
        <button onClick={() => setActiveTab('members')}>회원 관리</button>
        <button onClick={() => setActiveTab('posts')}>게시글 관리</button>
        <button onClick={() => setActiveTab('notices')}>공지사항 관리</button>
      </div>

      {activeTab === 'members' && <MemberManagement communityUuid={communityUuid} />}
      {activeTab === 'posts' && <PostManagement communityUuid={communityUuid} />}
      {activeTab === 'notices' && <NoticeManagement communityUuid={communityUuid} />}
    </div>
  );
}

export default AdminPanel;
```

### MemberManagement 컴포넌트
```jsx
function MemberManagement({ communityUuid }) {
  const [members, setMembers] = useState([]);

  useEffect(() => {
    fetchMembers();
  }, [communityUuid]);

  const fetchMembers = async () => {
    try {
      const token = localStorage.getItem('jwtToken');
      const response = await fetch(
        `http://localhost:8080/api/community/admin/members?communityUuid=${communityUuid}`,
        {
          headers: { 'Authorization': `Bearer ${token}` }
        }
      );
      const data = await response.json();
      setMembers(data);
    } catch (error) {
      console.error('회원 목록 조회 실패:', error);
    }
  };

  const handleKick = async (userId, nickname) => {
    if (!confirm(`${nickname} 님을 추방하시겠습니까?`)) return;

    const reason = prompt('추방 사유를 입력하세요:');
    if (!reason) return;

    try {
      const token = localStorage.getItem('jwtToken');
      const response = await fetch('http://localhost:8080/api/community/admin/kick', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          communityUuid,
          targetUserId: userId,
          reason
        })
      });

      const data = await response.json();
      if (data.statusCode === 200) {
        alert(data.message);
        fetchMembers();
      }
    } catch (error) {
      console.error('회원 추방 실패:', error);
    }
  };

  return (
    <div className="member-management">
      <h3>회원 관리 ({members.length}명)</h3>
      <table>
        <thead>
          <tr>
            <th>닉네임</th>
            <th>가입일</th>
            <th>게시글</th>
            <th>댓글</th>
            <th>관리</th>
          </tr>
        </thead>
        <tbody>
          {members.map(member => (
            <tr key={member.userId}>
              <td>{member.nickname}</td>
              <td>{new Date(member.joinedAt).toLocaleDateString()}</td>
              <td>{member.postCount}</td>
              <td>{member.replyCount}</td>
              <td>
                <button onClick={() => handleKick(member.userId, member.nickname)}>
                  추방
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
```

### NoticeManagement 컴포넌트
```jsx
function NoticeManagement({ communityUuid }) {
  const [notices, setNotices] = useState([]);
  const [isEditing, setIsEditing] = useState(false);
  const [editingNotice, setEditingNotice] = useState(null);

  useEffect(() => {
    fetchNotices();
  }, [communityUuid]);

  const fetchNotices = async () => {
    try {
      const response = await fetch(
        `http://localhost:8080/api/community/admin/notice/list?communityUuid=${communityUuid}`
      );
      const data = await response.json();
      setNotices(data.notices);
    } catch (error) {
      console.error('공지사항 조회 실패:', error);
    }
  };

  const handleCreate = () => {
    setEditingNotice({ title: '', content: '', isPinned: false });
    setIsEditing(true);
  };

  const handleSave = async () => {
    const token = localStorage.getItem('jwtToken');
    const url = editingNotice.id
      ? 'http://localhost:8080/api/community/admin/notice'
      : 'http://localhost:8080/api/community/admin/notice';
    const method = editingNotice.id ? 'PUT' : 'POST';

    try {
      const response = await fetch(url, {
        method,
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          ...editingNotice,
          communityUuid
        })
      });

      const data = await response.json();
      if (data.statusCode === 200) {
        alert(data.message);
        setIsEditing(false);
        setEditingNotice(null);
        fetchNotices();
      }
    } catch (error) {
      console.error('공지사항 저장 실패:', error);
    }
  };

  const handleDelete = async (noticeId) => {
    if (!confirm('공지사항을 삭제하시겠습니까?')) return;

    try {
      const token = localStorage.getItem('jwtToken');
      const response = await fetch('http://localhost:8080/api/community/admin/notice', {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          noticeId,
          communityUuid
        })
      });

      const data = await response.json();
      if (data.statusCode === 200) {
        alert(data.message);
        fetchNotices();
      }
    } catch (error) {
      console.error('공지사항 삭제 실패:', error);
    }
  };

  if (isEditing) {
    return (
      <div className="notice-editor">
        <h3>{editingNotice.id ? '공지사항 수정' : '공지사항 작성'}</h3>
        <input
          type="text"
          placeholder="제목"
          value={editingNotice.title}
          onChange={(e) => setEditingNotice({...editingNotice, title: e.target.value})}
        />
        <textarea
          placeholder="내용"
          value={editingNotice.content}
          onChange={(e) => setEditingNotice({...editingNotice, content: e.target.value})}
          rows={10}
        />
        <label>
          <input
            type="checkbox"
            checked={editingNotice.isPinned}
            onChange={(e) => setEditingNotice({...editingNotice, isPinned: e.target.checked})}
          />
          상단 고정
        </label>
        <button onClick={handleSave}>저장</button>
        <button onClick={() => setIsEditing(false)}>취소</button>
      </div>
    );
  }

  return (
    <div className="notice-management">
      <h3>공지사항 관리</h3>
      <button onClick={handleCreate}>새 공지사항</button>
      
      {notices.map(notice => (
        <div key={notice.id} className="notice-item">
          <h4>
            {notice.isPinned && '📌 '}
            {notice.title}
          </h4>
          <p>{notice.content}</p>
          <div className="notice-meta">
            <span>작성자: {notice.createdByNickname}</span>
            <span>{new Date(notice.createdAt).toLocaleString()}</span>
          </div>
          <button onClick={() => { setEditingNotice(notice); setIsEditing(true); }}>
            수정
          </button>
          <button onClick={() => handleDelete(notice.id)}>삭제</button>
        </div>
      ))}
    </div>
  );
}
```

---

## 🔄 워크플로우

### 1. 관리자 신청 플로우 (커뮤니티에 관리자 없음)

```
1. 사용자가 커뮤니티 가입
2. GET /api/community/admin/has-admin 호출
   → false 반환
3. 사용자가 "관리자 신청" 버튼 클릭
4. POST /api/community/admin/request 호출
5. 웹 운영자가 DB에서 직접 승인 처리
   (community_admin_request 테이블의 status를 'approved'로 변경 후
    community_admin 테이블에 레코드 추가)
6. 사용자가 관리자 권한 획득
```

### 2. 관리자 신청 플로우 (커뮤니티에 관리자 있음)

```
1. 사용자가 커뮤니티 가입
2. GET /api/community/admin/has-admin 호출
   → true 반환
3. 사용자가 "관리자 신청" 버튼 클릭
4. POST /api/community/admin/request 호출
5. 기존 관리자가 관리자 패널에서 신청 목록 확인
   GET /api/community/admin/requests?status=pending
6. 관리자가 승인/거절 처리
   POST /api/community/admin/process
7. 승인 시 사용자가 관리자 권한 획득
```

### 3. 회원 관리 플로우

```
1. 관리자가 회원 목록 조회
   GET /api/community/admin/members
2. 문제 회원 발견
3. "추방" 버튼 클릭
4. POST /api/community/admin/kick 호출
5. 해당 회원의 커뮤니티 가입 이력 삭제
   (커뮤니티 접근 불가)
```

### 4. 공지사항 관리 플로우

```
1. 관리자가 공지사항 작성
   POST /api/community/admin/notice
2. 일반 사용자가 커뮤니티 입장 시
   GET /api/community/admin/notice/pinned 호출
   → 고정된 공지사항 상단 표시
3. 관리자가 필요 시 수정
   PUT /api/community/admin/notice
4. 불필요 시 삭제
   DELETE /api/community/admin/notice
```

---

## 💡 개발 팁

### 1. 관리자 권한 체크
```javascript
// 페이지 로드 시 관리자 권한 확인하여 UI 분기
async function initializePage(communityUuid) {
  const isAdmin = await checkAdmin(communityUuid);
  
  if (isAdmin) {
    // 관리자 메뉴 표시
    document.getElementById('admin-menu').style.display = 'block';
  } else {
    // 관리자 신청 버튼 표시
    const hasAdmin = await checkHasAdmin(communityUuid);
    if (!hasAdmin) {
      document.getElementById('first-admin-request').style.display = 'block';
    } else {
      document.getElementById('admin-request').style.display = 'block';
    }
  }
}
```

### 2. 공지사항 표시
```javascript
// 커뮤니티 메인 페이지에 고정 공지 표시
async function displayPinnedNotices(communityUuid) {
  const response = await fetch(
    `http://localhost:8080/api/community/admin/notice/pinned?communityUuid=${communityUuid}`
  );
  const data = await response.json();
  
  const noticeContainer = document.getElementById('pinned-notices');
  data.notices.forEach(notice => {
    const div = document.createElement('div');
    div.className = 'pinned-notice';
    div.innerHTML = `
      <h4>📌 ${notice.title}</h4>
      <p>${notice.content}</p>
    `;
    noticeContainer.appendChild(div);
  });
}
```

### 3. 에러 처리
```javascript
async function safeAdminAction(action) {
  try {
    await action();
  } catch (error) {
    if (error.response?.status === 403) {
      alert('관리자 권한이 필요합니다');
    } else if (error.response?.status === 401) {
      alert('로그인이 필요합니다');
      window.location.href = '/login';
    } else {
      alert('오류가 발생했습니다');
    }
  }
}
```

---

## 📞 문의 및 지원

API 사용 중 문제가 발생하거나 추가 기능이 필요한 경우, 백엔드 팀에 문의해주세요.

**Swagger UI**: `http://localhost:8080/swagger-ui.html`

