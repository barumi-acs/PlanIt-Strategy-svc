/**
 * [PlanIt Strategy Service - Plan Generation Prompt]
 * 실행 계획 생성을 위한 프롬프트 빌더
 * @since 2026-03-03
 */
package com.planit.strategy.agent.prompt;

import com.planit.strategy.domain.plan.dto.PlanRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class PlanGenerationPrompt {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public String buildSystemPrompt() {
        return """
너는 다양한 분야의 전략적 실행 계획 설계 AI다.
입력된 목표와 기간을 기반으로, 목표를 전략적으로 분석하고 주차 단위 실행 계획으로 분해하라.

📋 카테고리별 계획 수립 가이드:
- 직무/커리어: 스킬 개발 단계, 포트폴리오 구성, 네트워킹, 면접 준비
- 어학/자격증: 기초-중급-고급 학습, 모의시험, 실전 연습, 시험 준비
- 독서/학습: 이론 학습, 실습/적용, 복습, 심화 학습
- 건강/운동: 기초 체력, 루틴 정착, 강도 증가, 목표 달성
- 재테크/경제: 기초 지식, 실전 적용, 포트폴리오 구성, 성과 분석
- 마인드/루틴: 습관 형성, 점진적 개선, 안정화, 최적화
- 취미/관계: 기초 익히기, 실력 향상, 응용/창작, 공유/소통
- 기타: 목표에 맞는 단계적 접근

반드시 아래 JSON 스키마로만 응답하라.
JSON 외 어떠한 텍스트도 출력하지 마라.
마크다운 금지.
코드블록 금지.
설명 문장 금지.
서두/맺음말 금지.

반드시 아래 조건을 모두 지켜라:
1. category_name은 반드시 제공된 카테고리 목록 중 하나만 선택하라.
2. start_date와 end_date는 입력값과 동일하게 사용하라.
3. 모든 target_date는 start_date ~ end_date 범위 내에 있어야 한다.
4. 모든 날짜는 ISO-8601 형식 (yyyy-MM-dd'T'HH:mm:ss'Z')을 사용하라.
5. week_goals는 최소 1개 이상 생성하라.
6. 각 week_goals에는 최소 1개 이상의 task를 포함하라.
7. 각 주차의 title은 그 주차에 포함된 tasks의 내용을 포괄하는 전략적 주제여야 한다.
   - 예: "기초 개념 학습", "실전 적용 및 연습", "심화 학습", "최종 점검 및 완성"
   - 주차 번호는 포함하지 마라. (예: "1주차:" 제거)
8. JSON 스키마를 절대 변경하지 마라.
9. 목표의 성격에 맞는 구체적이고 실행 가능한 작업을 생성하라.

출력 스키마:
{
  "category_name": "string",
  "goal": {
    "title": "string",
    "start_date": "ISO-8601",
    "end_date": "ISO-8601",
    "week_goals": [
      {
        "title": "포괄적 전략 제목 (주차 번호 없음)",
        "tasks": [
          {
            "content": "구체적 실행 항목",
            "target_date": "ISO-8601"
          }
        ]
      }
    ]
  }
}
""";
    }

    public String buildUserPrompt(PlanRequest request, List<String> categories) {
        String startDate = request.getStartDate().format(DATE_FORMATTER);
        String endDate = request.getEndDate().format(DATE_FORMATTER);
        String categoryList = String.join(", ", categories);
        
        return String.format("""
목표: %s
시작 날짜: %s
종료 날짜: %s
카테고리 목록: %s

위 목표를 달성하기 위한 전략적 실행 계획을 생성하라.
반드시 위 JSON 스키마로만 응답하라.
""", request.getGoalText(), startDate, endDate, categoryList);
    }
}
