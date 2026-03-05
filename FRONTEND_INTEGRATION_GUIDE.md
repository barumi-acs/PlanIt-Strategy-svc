# 프론트엔드 연동 가이드

## 📋 목차
1. [빠른 시작](#빠른-시작)
2. [연동 방법 선택](#연동-방법-선택)
3. [수동 연동 (권장)](#수동-연동-권장)
4. [자동 코드 생성](#자동-코드-생성)
5. [테스트 방법](#테스트-방법)

---

## 🚀 빠른 시작

### 1. 백엔드 서버 실행
```bash
./gradlew bootRun
```

서버가 `http://localhost:8080`에서 실행됩니다.

### 2. Swagger UI로 API 확인
브라우저에서 다음 URL을 열어보세요:
```
http://localhost:8080/swagger-ui/index.html
```

### 3. 프론트엔드에서 API 호출 테스트
```bash
curl http://localhost:8080/api/v1/categories
```

---

## 🎯 연동 방법 선택

### 방법 1: 수동 연동 (권장) ⭐
- **장점**: 간단하고 빠름, 커스터마이징 쉬움
- **단점**: 타입 정의를 수동으로 관리
- **추천 대상**: 소규모 프로젝트, 빠른 프로토타이핑

### 방법 2: OpenAPI 자동 생성
- **장점**: 타입 안전성 보장, API 변경 시 자동 동기화
- **단점**: 초기 설정 필요, 생성된 코드가 복잡할 수 있음
- **추천 대상**: 대규모 프로젝트, 장기 유지보수

---

## 📦 수동 연동 (권장)

### Step 1: 타입 정의 복사

프로젝트 루트의 `frontend-types.ts` 파일을 프론트엔드 프로젝트로 복사:

```bash
# 프론트엔드 프로젝트 디렉토리에서
cp ../PlanIt-Strategy-svc/frontend-types.ts src/types/api.ts
```

### Step 2: API 클라이언트 설정

#### Axios 설치
```bash
npm install axios
# 또는
yarn add axios
```

#### API 클라이언트 생성 (`src/api/client.ts`)
```typescript
import axios from 'axios';

const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 30000, // 30초 (AI 처리 시간 고려)
});

// 요청 인터셉터
apiClient.interceptors.request.use(
  (config) => {
    // 필요시 인증 토큰 추가
    // const token = localStorage.getItem('token');
    // if (token) {
    //   config.headers.Authorization = `Bearer ${token}`;
    // }
    return config;
  },
  (error) => Promise.reject(error)
);

// 응답 인터셉터 - ApiResponse 언래핑
apiClient.interceptors.response.use(
  (response) => {
    // ApiResponse의 data 필드만 반환
    return response.data.data;
  },
  (error) => {
    const errorData = error.response?.data?.error;
    console.error('API Error:', errorData);
    return Promise.reject(errorData || error);
  }
);

export default apiClient;
```

### Step 3: API 함수 작성

#### 트렌드 API (`src/api/trends.ts`)
```typescript
import apiClient from './client';
import type { AllTrendsResponse, Trend } from '../types/api';

export const trendApi = {
  // 전체 트렌드 조회
  getAllTrends: async (): Promise<AllTrendsResponse> => {
    return apiClient.get('/trends');
  },

  // 카테고리별 트렌드 조회
  getTrendsByCategory: async (categoryId: number): Promise<Trend[]> => {
    return apiClient.get(`/categories/${categoryId}/trends`);
  },
};
```

#### 목표 API (`src/api/goals.ts`)
```typescript
import apiClient from './client';
import type { AllGoalsResponse, GoalTemplate } from '../types/api';

export const goalApi = {
  // 전체 목표 조회
  getAllGoals: async (): Promise<AllGoalsResponse> => {
    return apiClient.get('/goals');
  },

  // 트렌드별 목표 조회
  getGoalsByTrend: async (trendId: number): Promise<GoalTemplate[]> => {
    return apiClient.get(`/trends/${trendId}/goals`);
  },
};
```

#### 플랜 API (`src/api/plans.ts`)
```typescript
import apiClient from './client';
import type { PlanRequest, PlanResponse } from '../types/api';

export const planApi = {
  // AI 플랜 생성
  generatePlan: async (request: PlanRequest): Promise<PlanResponse> => {
    return apiClient.post('/strategy/plan/generate', request);
  },
};
```

#### 카테고리 API (`src/api/categories.ts`)
```typescript
import apiClient from './client';
import type { Category } from '../types/api';

export const categoryApi = {
  // 카테고리 목록 조회
  getAllCategories: async (): Promise<Category[]> => {
    return apiClient.get('/categories');
  },
};
```

### Step 4: React Query 훅 작성 (선택사항)

React Query를 사용하면 캐싱, 로딩 상태 관리가 쉬워집니다.

#### 설치
```bash
npm install @tanstack/react-query
# 또는
yarn add @tanstack/react-query
```

#### 훅 작성 (`src/hooks/useTrends.ts`)
```typescript
import { useQuery, useMutation } from '@tanstack/react-query';
import { trendApi } from '../api/trends';

export const useAllTrends = () => {
  return useQuery({
    queryKey: ['trends', 'all'],
    queryFn: trendApi.getAllTrends,
    staleTime: 5 * 60 * 1000, // 5분
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

#### 플랜 생성 훅 (`src/hooks/usePlan.ts`)
```typescript
import { useMutation } from '@tanstack/react-query';
import { planApi } from '../api/plans';
import type { PlanRequest } from '../types/api';

export const useGeneratePlan = () => {
  return useMutation({
    mutationFn: (request: PlanRequest) => planApi.generatePlan(request),
    onSuccess: (data) => {
      console.log('플랜 생성 완료:', data);
    },
    onError: (error) => {
      console.error('플랜 생성 실패:', error);
    },
  });
};
```

### Step 5: 컴포넌트에서 사용

```tsx
import { useAllTrends } from '../hooks/useTrends';
import { useGeneratePlan } from '../hooks/usePlan';

export const TrendDashboard = () => {
  const { data: trendsData, isLoading, error } = useAllTrends();
  const { mutate: generatePlan, isPending } = useGeneratePlan();

  if (isLoading) return <div>로딩 중...</div>;
  if (error) return <div>에러: {error.message}</div>;

  const handleGeneratePlan = (goalText: string) => {
    generatePlan({ goalText, weeks: 4 });
  };

  return (
    <div>
      <h1>트렌드 대시보드</h1>
      {trendsData?.categories.map((category) => (
        <div key={category.categoryId}>
          <h2>{category.categoryName}</h2>
          {category.trends.map((trend) => (
            <div key={trend.id}>
              <h3>{trend.mainKeyword}</h3>
              <p>{trend.summary}</p>
              <button onClick={() => handleGeneratePlan(trend.mainKeyword)}>
                플랜 생성
              </button>
            </div>
          ))}
        </div>
      ))}
    </div>
  );
};
```

---

## 🤖 자동 코드 생성

OpenAPI 스펙에서 TypeScript 코드를 자동 생성할 수 있습니다.

### 방법 1: openapi-typescript-codegen

#### 설치
```bash
npm install --save-dev @openapitools/openapi-generator-cli
```

#### 코드 생성
```bash
# OpenAPI JSON 다운로드
curl http://localhost:8080/v3/api-docs > openapi.json

# TypeScript 클라이언트 생성
npx openapi-generator-cli generate \
  -i openapi.json \
  -g typescript-axios \
  -o src/generated/api
```

### 방법 2: orval (React Query 자동 생성)

#### 설치
```bash
npm install --save-dev orval
```

#### 설정 파일 (`orval.config.ts`)
```typescript
module.exports = {
  planit: {
    input: 'http://localhost:8080/v3/api-docs',
    output: {
      mode: 'tags-split',
      target: 'src/generated/api.ts',
      schemas: 'src/generated/models',
      client: 'react-query',
      mock: true,
    },
  },
};
```

#### 실행
```bash
npx orval
```

---

## 🧪 테스트 방법

### 1. Swagger UI에서 테스트
```
http://localhost:8080/swagger-ui/index.html
```

### 2. curl로 테스트
```bash
# 카테고리 목록
curl http://localhost:8080/api/v1/categories

# 전체 트렌드
curl http://localhost:8080/api/v1/trends

# 플랜 생성
curl -X POST http://localhost:8080/api/v1/strategy/plan/generate \
  -H "Content-Type: application/json" \
  -d '{"goalText":"Python 배우기","weeks":4}'
```

### 3. Postman Collection

Postman을 사용한다면 OpenAPI 스펙을 import할 수 있습니다:
1. Postman 열기
2. Import > Link 선택
3. `http://localhost:8080/v3/api-docs` 입력
4. Import 클릭

---

## 🔧 환경별 설정

### 개발 환경
```typescript
// src/config/api.ts
export const API_BASE_URL = 
  process.env.NODE_ENV === 'production'
    ? 'https://api.planit.com/api/v1'
    : 'http://localhost:8080/api/v1';
```

### .env 파일
```env
VITE_API_BASE_URL=http://localhost:8080/api/v1
# 또는 (Create React App)
REACT_APP_API_BASE_URL=http://localhost:8080/api/v1
```

---

## 📝 주의사항

### 1. CORS 설정
백엔드에서 CORS가 설정되어 있습니다:
- `http://localhost:3000` (React)
- `http://localhost:5173` (Vite)
- `http://localhost:4200` (Angular)

다른 포트를 사용한다면 백엔드 팀에 요청하세요.

### 2. 타임아웃 설정
AI 처리 API는 시간이 오래 걸릴 수 있습니다:
- `/strategy/plan/generate`: 10-30초
- `/categories/trends/generate-all`: 1-2분

axios timeout을 충분히 설정하세요 (30초 이상 권장).

### 3. 에러 처리
모든 API는 `ApiResponse<T>` 형식으로 응답합니다:
```typescript
{
  success: boolean,
  data: T | null,
  error: { code: string, message: string } | null
}
```

### 4. 로딩 상태 관리
AI 생성 API는 시간이 걸리므로 로딩 UI를 반드시 표시하세요.

---

## 🆘 문제 해결

### CORS 에러
```
Access to XMLHttpRequest at 'http://localhost:8080/api/v1/trends' 
from origin 'http://localhost:3000' has been blocked by CORS policy
```

**해결**: 백엔드 서버를 재시작하세요. `WebConfig.java`에 CORS 설정이 추가되어 있습니다.

### 타임아웃 에러
```
Error: timeout of 5000ms exceeded
```

**해결**: axios timeout을 늘리세요:
```typescript
const apiClient = axios.create({
  timeout: 30000, // 30초
});
```

### 타입 에러
```
Property 'data' does not exist on type 'AxiosResponse'
```

**해결**: 응답 인터셉터가 제대로 설정되었는지 확인하세요.

---

## 📚 추가 자료

- [API 문서](./API_DOCUMENTATION.md)
- [Swagger UI](http://localhost:8080/swagger-ui/index.html)
- [OpenAPI Spec](http://localhost:8080/v3/api-docs)
- [React Query 문서](https://tanstack.com/query/latest)
- [Axios 문서](https://axios-http.com/)

---

## 💡 팁

1. **React Query 사용 권장**: 캐싱, 리페칭, 로딩 상태 관리가 자동화됩니다.
2. **타입 정의 동기화**: API가 변경되면 `frontend-types.ts`를 업데이트하세요.
3. **에러 바운더리**: API 에러를 처리할 Error Boundary를 설정하세요.
4. **개발자 도구**: React Query Devtools를 설치하면 디버깅이 쉬워집니다.

```bash
npm install @tanstack/react-query-devtools
```

```tsx
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';

function App() {
  return (
    <>
      {/* 앱 컴포넌트 */}
      <ReactQueryDevtools initialIsOpen={false} />
    </>
  );
}
```
