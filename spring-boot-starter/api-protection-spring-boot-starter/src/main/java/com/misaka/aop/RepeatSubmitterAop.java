package com.misaka.aop;

import com.misaka.annotation.RepeatSubmitter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

@Aspect
public class RepeatSubmitterAop {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Around("@annotation(repeatSubmitter)")
    public Object around(ProceedingJoinPoint pjp, RepeatSubmitter repeatSubmitter) throws Throwable {
        StringJoiner joiner = new StringJoiner(":");
        joiner.add(repeatSubmitter.key());
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        String methodName = method.getName();
        String controllerName = method.getDeclaringClass().getName();
        joiner.add(controllerName.replace(".", ":"));
        joiner.add(methodName);
        String repeat = repeatSubmitter.repeat();
        if (StringUtils.hasText(repeat)) {
            String[] parameterNames = methodSignature.getParameterNames();
            Object[] args = pjp.getArgs();
            EvaluationContext context = new StandardEvaluationContext();
            for (int i = 0; i < args.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
            ExpressionParser parser = new SpelExpressionParser();
            Expression expression = parser.parseExpression(repeat);
            String value = expression.getValue(context, String.class);
            joiner.add(value);
        }
        String key = joiner.toString();
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(key, true, repeatSubmitter.time(), TimeUnit.SECONDS);
        if (acquired == null || !acquired) {
            throw new RuntimeException("Please do not submit repeatedly");
        }
        try {
            return pjp.proceed();
        } finally {
            redisTemplate.delete(key);
        }
    }
}
