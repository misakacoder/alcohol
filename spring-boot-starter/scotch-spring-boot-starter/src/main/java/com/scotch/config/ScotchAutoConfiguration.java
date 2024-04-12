package com.scotch.config;

import com.kir.decoder.JacksonDecoder;
import com.kir.encoder.JacksonEncoder;
import com.kir.http.Kir;
import com.scotch.kir.ScotchAuthInterceptor;
import com.scotch.kir.ScotchServer;
import com.scotch.properties.ScotchProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

@ComponentScan("com.scotch")
@ConditionalOnProperty(name = "scotch.enable", havingValue = "true")
public class ScotchAutoConfiguration {

    @Autowired
    private ScotchProperties scotchProperties;

    @Autowired
    private ScotchAuthInterceptor scotchAuthInterceptor;

    @Bean
    public ScotchServer scotchServer() {
        return Kir.builder()
                .url(scotchProperties.getUrl())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .interceptors(List.of(scotchAuthInterceptor))
                .target(ScotchServer.class);
    }
}