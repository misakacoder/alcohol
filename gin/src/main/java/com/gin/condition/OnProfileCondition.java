package com.gin.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Profiles;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;

import java.util.List;

public class OnProfileCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        MultiValueMap<String, Object> multiValueMap = metadata.getAllAnnotationAttributes(ConditionalOnProfile.class.getName());
        if (multiValueMap != null) {
            List<Object> valueList = multiValueMap.get("value");
            for (Object value : valueList) {
                if (context.getEnvironment().acceptsProfiles(Profiles.of((String[]) value))) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }
}
