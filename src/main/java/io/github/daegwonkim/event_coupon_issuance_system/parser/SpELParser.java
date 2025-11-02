package io.github.daegwonkim.event_coupon_issuance_system.parser;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Component
public class SpELParser {

    /**
     * SpEL을 사용해 동적인 락 키 생성
     *
     * @param parameterNames 메서드 파라미터 이름
     * @param args 실제 전달된 인수 값
     * @param keyExpression 동적 키 표현식
     * @return 생선된 키 이름
     */
    public String createDynamicKey(String[] parameterNames, Object[] args, String keyExpression) {
        // SpEL 파서와 컨텍스트 생성
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();

        // 메서드 파라미터를 SpEL 컨텍스트에 등록
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        // SpEL 표현식 파싱
        return parser.parseExpression(keyExpression)
                .getValue(context, String.class);
    }
}
