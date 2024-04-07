package com.misaka.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.misaka.annotation.processor.AnnotatedParameterProcessor;
import com.misaka.support.FeignFormatterRegistrar;
import com.misaka.support.Slf4jLogger;
import com.misaka.support.SpringMvcContract;
import feign.Contract;
import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FeignAutoConfiguration {

    @Autowired(required = false)
    private List<AnnotatedParameterProcessor> parameterProcessors = new ArrayList<>();

    @Autowired(required = false)
    private List<FeignFormatterRegistrar> feignFormatterRegistrars = new ArrayList<>();

    @Bean
    @ConditionalOnMissingBean
    public Request.Options options() {
        return new Request.Options(10, TimeUnit.SECONDS, 30, TimeUnit.SECONDS, true);
    }

    @Bean
    @ConditionalOnMissingBean
    public Retryer retryer() {
        return Retryer.NEVER_RETRY;
    }

    @Bean
    @ConditionalOnMissingBean
    public Contract contract() {
        FormattingConversionService conversionService = new DefaultFormattingConversionService();
        for (FeignFormatterRegistrar feignFormatterRegistrar : feignFormatterRegistrars) {
            feignFormatterRegistrar.registerFormatters(conversionService);
        }
        return new SpringMvcContract(parameterProcessors, conversionService, true);
    }

    @Bean
    @ConditionalOnMissingBean
    public Encoder encoder(ObjectMapper objectMapper) {
        return new JacksonEncoder(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public Decoder decoder(ObjectMapper objectMapper) {
        return new JacksonDecoder(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public Logger logger() {
        return new Slf4jLogger();
    }
}
