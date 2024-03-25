package com.misaka.config;

import com.misaka.processor.TransactionLockAnnotationBeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

public class LockAutoConfiguration {

    @Bean
    @ConditionalOnClass(PlatformTransactionManager.class)
    public static TransactionLockAnnotationBeanPostProcessor transactionLockAnnotationBeanPostProcessor() {
        return new TransactionLockAnnotationBeanPostProcessor();
    }
}
