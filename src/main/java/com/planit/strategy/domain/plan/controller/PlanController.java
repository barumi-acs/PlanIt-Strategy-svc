package com.planit.strategy.domain.plan.controller;

import com.planit.strategy.common.ApiResponse;
import com.planit.strategy.domain.plan.dto.PlanRequest;
import com.planit.strategy.domain.plan.dto.PlanResponse;
import com.planit.strategy.domain.plan.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Plan API", description = "AI 실행 계획 생성 API")
@Slf4j
@RestController
@RequestMapping("/api/v1/strategy/plan")
@RequiredArgsConstructor
public class PlanController {
    private final PlanService planService;

    /**
     * 실행 계획 생성 API
     * POST /api/v1/strategy/plan/generate
     */
    @Operation(summary = "AI 실행 계획 생성", description = "목표와 기간을 입력받아 주차별 실행 계획을 생성합니다")
    @PostMapping("/generate")
    public ApiResponse<PlanResponse> generatePlan(@RequestBody PlanRequest request) {
        log.info("[Plan Generation API] 요청 수신 - Goal: {}", request.getGoalText());
        
        PlanResponse planResponse = planService.generatePlan(request);
        
        return ApiResponse.success(planResponse);
    }
}
