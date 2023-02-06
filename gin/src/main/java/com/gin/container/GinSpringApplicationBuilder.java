package com.gin.container;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class GinSpringApplicationBuilder extends SpringApplicationBuilder {

    public GinSpringApplicationBuilder(Class<?>... sources) {
        super(sources);
    }

    public GinSpringApplicationBuilder registerByParent(Class<?>... componentClasses) {
        AnnotationConfigApplicationContext parentContext = new AnnotationConfigApplicationContext(componentClasses);
        this.parent(parentContext);
        return this;
    }
}
