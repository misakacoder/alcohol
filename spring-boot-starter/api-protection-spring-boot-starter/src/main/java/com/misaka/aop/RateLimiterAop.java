package com.misaka.aop;

import com.misaka.annotation.RateLimiter;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.StringJoiner;

@Aspect
public class RateLimiterAop {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    @Qualifier("rateLimiterScript")
    private DefaultRedisScript<Long> rateLimiterScript;

    @Before("@annotation(rateLimiter)")
    public void before(RateLimiter rateLimiter) {
        String key = getKey(rateLimiter);
        int count = rateLimiter.count();
        int time = rateLimiter.time();
        Long current = redisTemplate.execute(rateLimiterScript, Collections.singletonList(key), count, time);
        if (current == null || current > count) {
            throw new RuntimeException("Access limit exceeded, please try again later");
        }
    }

    @Bean("rateLimiterScript")
    public DefaultRedisScript<Long> rateLimiterScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource("redis/rate_limiter.lua")));
        script.setResultType(Long.class);
        return script;
    }

    private String getKey(RateLimiter rateLimiter) {
        String key = rateLimiter.key();
        RateLimiter.Type type = rateLimiter.type();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes != null) {
            StringJoiner joiner = new StringJoiner(":");
            joiner.add(key);
            HttpServletRequest request = servletRequestAttributes.getRequest();
            if (type == RateLimiter.Type.IP) {
                joiner.add(request.getRemoteAddr());
            }
            joiner.add(request.getServletPath());
            joiner.add(request.getMethod());
            key = joiner.toString();
        }
        return key;
    }
}
