package com.planit.strategy.domain.trend.prompt;

import com.planit.strategy.domain.trend.dto.TrendGenerationInput;
import org.springframework.stereotype.Component;

@Component
public class TrendPromptBuilder {
    
    private static final int MAX_DESCRIPTION_LENGTH = 150;
    
    public String build(TrendGenerationInput input) {
        StringBuilder prompt = new StringBuilder();
        
        // 역할 정의
        prompt.append("""
당신은 다양한 분야의 글로벌 트렌드를 분석하는 AI 분석가입니다.
기술, 학습, 커리어뿐만 아니라 건강, 재테크, 취미, 라이프스타일 등 
모든 영역의 트렌드를 포괄적으로 분석합니다.

다음은 여러 카테고리별 최근 글로벌 뉴스입니다.

""");
        
        // 카테고리별 뉴스 나열
        for (TrendGenerationInput.CategoryNews categoryNews : input.getCategories()) {
            prompt.append("=== 카테고리 ID: ").append(categoryNews.getCategoryId())
                  .append(" (").append(categoryNews.getCategoryName()).append(") ===\n");
            
            for (TrendGenerationInput.NewsArticle article : categoryNews.getNews()) {
                prompt.append("- ").append(article.getTitle()).append("\n");
                
                if (article.getDescription() != null && !article.getDescription().isEmpty()) {
                    String description = article.getDescription();
                    // description 길이 제한 (최대 150자)
                    if (description.length() > MAX_DESCRIPTION_LENGTH) {
                        description = description.substring(0, MAX_DESCRIPTION_LENGTH) + "...";
                    }
                    prompt.append("  ").append(description).append("\n");
                }
            }
            prompt.append("\n");
        }
        
        // 트렌드 생성 지시사항
        prompt.append("""
위 뉴스를 분석하여 각 카테고리별로 2-3개의 주요 트렌드를 추출하고,
각 트렌드별로 실행 가능한 목표를 제안하라.

📋 트렌드 생성 기준:
- 뉴스 여러 개에서 반복적으로 등장하는 주제
- 개인의 성장과 발전에 도움이 되는 분야
- 해당 카테고리의 특성에 맞는 실용적 트렌드
- 단순 뉴스 요약이 아닌 흐름 분석

📊 카테고리별 목표 유형 가이드:
- 직무/커리어: 스킬 개발, 자격증 취득, 네트워킹, 이직 준비
- 어학/자격증: 언어 학습, 시험 준비, 인증서 취득
- 독서/학습: 지식 습득, 독서 계획, 온라인 강의 수강
- 건강/운동: 운동 루틴, 식단 관리, 건강 검진, 정신 건강
- 재테크/경제: 투자 학습, 가계부 관리, 부업 시작, 경제 공부
- 마인드/루틴: 습관 형성, 생산성 향상, 시간 관리, 명상
- 취미/관계: 새로운 취미, 인간관계 개선, 여행 계획, 문화 활동
- 기타: 라이프스타일 개선, 환경 보호, 사회 참여

📊 트렌드 생성 규칙:
- 각 카테고리마다 정확히 2~3개의 트렌드 생성
- main_keyword: 트렌드의 핵심 키워드 (간결하게)
- headline: 트렌드 제목 (한 문장)
- summary: 트렌드 요약 (1-2문장, 구체적으로)
- score: 트렌드 중요도 (0.0~1.0, 높을수록 중요)
- goals: 해당 트렌드 기반 실행 가능한 목표 (최소 1개)

⚠️ 중요: 반드시 다음 JSON 형식으로만 응답하라.
- 설명 문장 추가 금지
- 코드블록(```) 사용 금지
- JSON 외 텍스트 출력 금지
- category_id는 반드시 위에 제공된 카테고리 ID를 사용하라

응답 형식 예시:
{
  "category_trends": [
    {
      "category_id": 4,
      "trends": [
        {
          "main_keyword": "홈트레이닝",
          "headline": "집에서 하는 운동의 대중화",
          "summary": "코로나19 이후 홈트레이닝이 일상화되면서 다양한 온라인 피트니스 플랫폼이 인기를 끌고 있습니다. 개인 맞춤형 운동 프로그램에 대한 관심이 높아지고 있습니다.",
          "score": 0.88,
          "goals": [
            {
              "title": "매일 30분 홈트레이닝 루틴 만들기",
              "description": "유튜브나 앱을 활용한 개인 맞춤 운동 계획 수립"
            },
            {
              "title": "홈짐 구축하기",
              "description": "집에서 효과적으로 운동할 수 있는 기구와 공간 마련"
            }
          ]
        }
      ]
    }
  ]
}

위 형식을 정확히 따라 JSON만 출력하라.
""");
        
        return prompt.toString();
    }
}
