package com.misaka.processor;

import com.misaka.annotation.TransactionLock;
import com.misaka.lock.Lock;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.SpringTransactionAnnotationParser;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class TransactionLockAnnotationBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        Method[] methods = targetClass.getDeclaredMethods();
        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers()) && method.getAnnotation(TransactionLock.class) != null) {
                ProxyFactory proxyFactory = new ProxyFactory();
                proxyFactory.setTarget(bean);
                proxyFactory.addAdvice((MethodInterceptor) this::methodInterceptor);
                return proxyFactory.getProxy();
            }
        }
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private Object methodInterceptor(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        TransactionLock transactionalLock = method.getAnnotation(TransactionLock.class);
        Transactional transactional = null;
        Class<? extends Lock> lockType = null;
        if (transactionalLock != null && (lockType = transactionalLock.lock()) != null && (transactional = transactionalLock.transactional()) != null) {
            Lock lock = applicationContext.getBean(lockType);
            lock.lock();
            try {
                SpringTransactionAnnotationParser transactionAnnotationParser = new SpringTransactionAnnotationParser();
                TransactionStatus status = transactionManager.getTransaction(transactionAnnotationParser.parseTransactionAnnotation(transactional));
                try {
                    Object result = invocation.proceed();
                    transactionManager.commit(status);
                    return result;
                } catch (Exception e) {
                    if (rollback(transactional, e)) {
                        transactionManager.rollback(status);
                    } else {
                        transactionManager.commit(status);
                    }
                    throw e;
                }
            } finally {
                lock.unlock();
            }
        }
        return invocation.proceed();
    }

    private boolean rollback(Transactional transactional, Exception e) {
        if (e instanceof RuntimeException) {
            boolean rollback = validateException(transactional.rollbackFor(), transactional.rollbackForClassName(), e);
            boolean noRollback = validateException(transactional.noRollbackFor(), transactional.noRollbackForClassName(), e);
            return rollback && !noRollback;
        }
        return false;
    }

    private boolean validateException(Class<? extends Throwable>[] classes, String[] classNames, Exception e) {
        Stream<? extends Class<?>> classNameStream = Arrays.stream(classNames).map(p -> {
            Class<?> cls = null;
            try {
                cls = ClassLoader.getSystemClassLoader().loadClass(p);
            } catch (ClassNotFoundException ignored) {

            }
            return cls;
        });
        return Stream.concat(Arrays.stream(classes), classNameStream)
                .filter(Objects::nonNull)
                .distinct()
                .anyMatch(p -> p.isAssignableFrom(e.getClass()));
    }
}
