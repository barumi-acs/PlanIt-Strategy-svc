# PlanIt Strategy API 문서

## 기본 정보

- **Base URL**: `http://localhost:8080/api/v1`
- **Content-Type**: `application/json`
- **응답 형식**: 모든 API는 `ApiResponse<T>` 래퍼로 감싸져 있습니다

### 공통 응답 구조

```json
{
  "success": true,
  "data": { /* 실제 데이터 */ },
  "error": null
}
```

에러 발생 시:
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "ERROR_CODE",
    "message": "에러 메시지"
  }
}
```

---

## API 엔드포인트

### 1. 카테고리 API

#### 1.1 카테고리 목록 조회
```http
GET /api/v1/categories
```

**응답 예시:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "IT/프로그래밍",
      "description": "소프트웨어 개발, 프로그래밍 언어, 개발 도구"
    },
    {
      "id": 2,
      "name": "어학",
      "description": "외국어 학습, 언어 능력 향상"
    }
  ]
}
```

---

### 2. 트렌드 API

#### 2.1 전체 트렌드 조회
```http
GET /api/v1/trends
```

**설명**: 모든 카테고리의 최신 트렌드를 조회합니다.

**응답 예시:**
```json
{
  "success": true,
  "data": {
    "categories": [
      {
        "categoryId": 1,
        "categoryName": "IT/프로그래밍",
        "trends": [
          {
            "id": 1,
            "mainKeyword": "AI 개발",
            "headline": "생성형 AI 기술 급부상",
            "summary": "ChatGPT와 같은 생성형 AI가...",
            "score": 95.5,
            "newsKeyword": "artificial intelligence development",
            "createdAt": "2026-03-05T10:00:00"
          }
        ]
      }
    ]
  }
}
```

#### 2.2 카테고리별 트렌드 조회
```http
GET /api/v1/categories/{categoryId}/trends
```

**Path Parameters:**
- `categoryId` (Long, required): 카테고리 ID

**응답 예시:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "mainKeyword": "AI 개발",
      "headline": "생성형 AI 기술 급부상",
      "summary": "ChatGPT와 같은 생성형 AI가...",
      "score": 95.5,
      "newsKeyword": "artificial intelligence development",
      "createdAt": "2026-03-05T10:00:00"
    }
  ]
}
```

---

### 3. 목표 템플릿 API

#### 3.1 전체 목표 조회
```http
GET /api/v1/goals
```

**설명**: 모든 카테고리의 트렌드별 AI 생성 목표를 조회합니다.

**응답 예시:**
```json
{
  "success": true,
  "data": {
    "categories": [
      {
        "categoryId": 1,
        "categoryName": "IT/프로그래밍",
        "trends": [
          {
            "trendId": 1,
            "trendKeyword": "AI 개발",
            "goals": [
              {
                "id": 1,
                "goalText": "Python으로 간단한 챗봇 만들기",
                "difficulty": "초급",
                "estimatedWeeks": 4,
                "createdAt": "2026-03-05T10:00:00"
              }
            ]
          }
        ]
      }
    ]
  }
}
```

#### 3.2 트렌드별 목표 조회
```http
GET /api/v1/trends/{trendId}/goals
```

**Path Parameters:**
- `trendId` (Long, required): 트렌드 ID

**응답 예시:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "goalText": "Python으로 간단한 챗봇 만들기",
      "difficulty": "초급",
      "estimatedWeeks": 4,
      "createdAt": "2026-03-05T10:00:00"
    }
  ]
}
```

---

### 4. 플랜 생성 API

#### 4.1 AI 실행 계획 생성
```http
POST /api/v1/strategy/plan/generate
```

**설명**: 목표와 기간을 입력받아 주차별 실행 계획을 AI가 생성합니다.

**Request Body:**
```json
{
  "goalText": "Python으로 간단한 챗봇 만들기",
  "weeks": 4
}
```

**Request Body 스키마:**
- `goalText` (String, required): 달성하고자 하는 목표
- `weeks` (Integer, required): 계획 기간 (주 단위)

**응답 예시:**
```json
{
  "success": true,
  "data": {
    "goal": "Python으로 간단한 챗봇 만들기",
    "totalWeeks": 4,
    "weeklyGoals": [
      {
        "week": 1,
        "weekGoal": "Python 기초 문법 학습",
        "tasks": [
          {
            "taskName": "Python 설치 및 환경 설정",
            "description": "Python 3.x 설치하고 VS Code 설정하기",
            "estimatedHours": 2
          },
          {
            "taskName": "변수와 자료형 학습",
            "description": "기본 자료형(int, str, list, dict) 이해하기",
            "estimatedHours": 3
          }
        ]
      },
      {
        "week": 2,
        "weekGoal": "자연어 처리 기초",
        "tasks": [
          {
            "taskName": "문자열 처리 학습",
            "description": "split, join, replace 등 문자열 메서드 익히기",
            "estimatedHours": 3
          }
        ]
      }
    ],
    "generatedAt": "2026-03-05T10:00:00"
  }
}
```

---

### 5. 트렌드 생성 API (관리자용)

#### 5.1 전체 카테고리 트렌드 생성
```http
POST /api/v1/categories/trends/generate-all
```

**설명**: 모든 카테고리에 대해 뉴스 수집 → AI 분석 → 트렌드 및 목표 생성을 수행합니다.

**⚠️ 주의**: 이 API는 AI 처리 시간이 오래 걸립니다 (약 1-2분).

**응답 예시:**
```json
{
  "success": true,
  "data": {
    "batchId": 123,
    "totalCategories": 5,
    "totalTrends": 25,
    "totalGoals": 75,
    "executionTimeMs": 120000,
    "generatedAt": "2026-03-05T10:00:00"
  }
}
```

---

## CORS 설정

현재 백엔드는 CORS가 설정되어 있지 않을 수 있습니다. 프론트엔드 개발 시 CORS 에러가 발생하면 백엔드 팀에 요청하세요.

---

## 에러 코드

| 에러 코드 | 설명 |
|----------|------|
| `CATEGORY_NOT_FOUND` | 카테고리를 찾을 수 없음 |
| `TREND_NOT_FOUND` | 트렌드를 찾을 수 없음 |
| `GOAL_NOT_FOUND` | 목표를 찾을 수 없음 |
| `AGENT_EXECUTION_ERROR` | AI Agent 실행 중 오류 발생 |
| `EXTERNAL_API_ERROR` | 외부 API 호출 실패 |

---

## 개발 환경 설정

### 로컬 서버 실행
```bash
./gradlew bootRun
```

서버 주소: `http://localhost:8080`

### Swagger UI
```
http://localhost:8080/swagger-ui/index.html
```

### OpenAPI Spec JSON
```
http://localhost:8080/v3/api-docs
```

---

## 프론트엔드 연동 예시 (React + Axios)

```typescript
// api/client.ts
import axios from 'axios';

const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

// 응답 인터셉터로 ApiResponse 언래핑
apiClient.interceptors.response.use(
  (response) => response.data.data, // ApiResponse의 data 필드만 반환
  (error) => {
    const errorData = error.response?.data?.error;
    return Promise.reject(errorData || error);
  }
);

export default apiClient;
```

```typescript
// api/trends.ts
import apiClient from './client';

export interface Trend {
  id: number;
  mainKeyword: string;
  headline: string;
  summary: string;
  score: number;
  newsKeyword: string;
  createdAt: string;
}

export const trendApi = {
  // 전체 트렌드 조회
  getAllTrends: () => apiClient.get('/trends'),
  
  // 카테고리별 트렌드 조회
  getTrendsByCategory: (categoryId: number) => 
    apiClient.get<Trend[]>(`/categories/${categoryId}/trends`),
};
```

```typescript
// api/plans.ts
import apiClient from './client';

export interface PlanRequest {
  goalText: string;
  weeks: number;
}

export interface PlanResponse {
  goal: string;
  totalWeeks: number;
  weeklyGoals: WeekGoal[];
  generatedAt: string;
}

export const planApi = {
  generatePlan: (request: PlanRequest) => 
    apiClient.post<PlanResponse>('/strategy/plan/generate', request),
};
```

---

## React Query 사용 예시

```typescript
// hooks/useTrends.ts
import { useQuery } from '@tanstack/react-query';
import { trendApi } from '../api/trends';

export const useAllTrends = () => {
  return useQuery({
    queryKey: ['trends', 'all'],
    queryFn: trendApi.getAllTrends,
  });
};

export const useTrendsByCategory = (categoryId: number) => {
  return useQuery({
    queryKey: ['trends', 'category', categoryId],
    queryFn: () => trendApi.getTrendsByCategory(categoryId),
    enabled: !!categoryId,
  });
};
```

```typescript
// hooks/usePlan.ts
import { useMutation } from '@tanstack/react-query';
import { planApi, PlanRequest } from '../api/plans';

export const useGeneratePlan = () => {
  return useMutation({
    mutationFn: (request: PlanRequest) => planApi.generatePlan(request),
  });
};
```

---

## 사용 예시 (컴포넌트)

```tsx
// components/TrendList.tsx
import { useAllTrends } from '../hooks/useTrends';

export const TrendList = () => {
  const { data, isLoading, error } = useAllTrends();

  if (isLoading) return <div>로딩 중...</div>;
  if (error) return <div>에러 발생: {error.message}</div>;

  return (
    <div>
      {data.categories.map((category) => (
        <div key={category.categoryId}>
          <h2>{category.categoryName}</h2>
          {category.trends.map((trend) => (
            <div key={trend.id}>
              <h3>{trend.mainKeyword}</h3>
              <p>{trend.summary}</p>
            </div>
          ))}
        </div>
      ))}
    </div>
  );
};
```

```tsx
// components/PlanGenerator.tsx
import { useState } from 'react';
import { useGeneratePlan } from '../hooks/usePlan';

export const PlanGenerator = () => {
  const [goalText, setGoalText] = useState('');
  const [weeks, setWeeks] = useState(4);
  const { mutate, data, isLoading } = useGeneratePlan();

  const handleSubmit = () => {
    mutate({ goalText, weeks });
  };

  return (
    <div>
      <input 
        value={goalText} 
        onChange={(e) => setGoalText(e.target.value)}
        placeholder="목표를 입력하세요"
      />
      <input 
        type="number" 
        value={weeks} 
        onChange={(e) => setWeeks(Number(e.target.value))}
      />
      <button onClick={handleSubmit} disabled={isLoading}>
        {isLoading ? '생성 중...' : '플랜 생성'}
      </button>

      {data && (
        <div>
          <h2>{data.goal}</h2>
          {data.weeklyGoals.map((week) => (
            <div key={week.week}>
              <h3>Week {week.week}: {week.weekGoal}</h3>
              <ul>
                {week.tasks.map((task, idx) => (
                  <li key={idx}>{task.taskName}</li>
                ))}
              </ul>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
```
