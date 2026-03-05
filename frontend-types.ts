// PlanIt Strategy API TypeScript 타입 정의
// 프론트엔드 프로젝트의 src/types/ 폴더에 복사해서 사용하세요

// ============================================
// 공통 타입
// ============================================

export interface ApiResponse<T> {
    success: boolean;
    data: T | null;
    error: ErrorResponse | null;
}

export interface ErrorResponse {
    code: string;
    message: string;
}

// ============================================
// 카테고리 관련 타입
// ============================================

export interface Category {
    id: number;
    name: string;
    description: string;
}

// ============================================
// 트렌드 관련 타입
// ============================================

export interface Trend {
    id: number;
    mainKeyword: string;
    headline: string;
    summary: string;
    score: number;
    newsKeyword: string;
    createdAt: string;
}

export interface CategoryTrends {
    categoryId: number;
    categoryName: string;
    trends: Trend[];
}

export interface AllTrendsResponse {
    categories: CategoryTrends[];
}

// ============================================
// 목표 템플릿 관련 타입
// ============================================

export interface GoalTemplate {
    id: number;
    goalText: string;
    difficulty: string;
    estimatedWeeks: number;
    createdAt: string;
}

export interface TrendGoals {
    trendId: number;
    trendKeyword: string;
    goals: GoalTemplate[];
}

export interface CategoryGoals {
    categoryId: number;
    categoryName: string;
    trends: TrendGoals[];
}

export interface AllGoalsResponse {
    categories: CategoryGoals[];
}

// ============================================
// 플랜 생성 관련 타입
// ============================================

export interface PlanRequest {
    goalText: string;
    weeks: number;
}

export interface Task {
    taskName: string;
    description: string;
    estimatedHours: number;
}

export interface WeekGoal {
    week: number;
    weekGoal: string;
    tasks: Task[];
}

export interface PlanResponse {
    goal: string;
    totalWeeks: number;
    weeklyGoals: WeekGoal[];
    generatedAt: string;
}

// ============================================
// 트렌드 생성 관련 타입 (관리자용)
// ============================================

export interface TrendGenerationSummary {
    batchId: number;
    totalCategories: number;
    totalTrends: number;
    totalGoals: number;
    executionTimeMs: number;
    generatedAt: string;
}

// ============================================
// API 클라이언트 타입
// ============================================

export interface TrendApi {
    getAllTrends: () => Promise<AllTrendsResponse>;
    getTrendsByCategory: (categoryId: number) => Promise<Trend[]>;
}

export interface GoalApi {
    getAllGoals: () => Promise<AllGoalsResponse>;
    getGoalsByTrend: (trendId: number) => Promise<GoalTemplate[]>;
}

export interface PlanApi {
    generatePlan: (request: PlanRequest) => Promise<PlanResponse>;
}

export interface CategoryApi {
    getAllCategories: () => Promise<Category[]>;
}

export interface AdminApi {
    generateAllTrends: () => Promise<TrendGenerationSummary>;
}
