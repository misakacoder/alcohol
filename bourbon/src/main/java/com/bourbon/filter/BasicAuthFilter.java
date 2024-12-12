package com.bourbon.filter;

import com.bourbon.properties.BasicAuth;
import com.bourbon.properties.BourbonProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Order(Integer.MIN_VALUE)
public class BasicAuthFilter implements Filter {

    private static final String AUTH_FAILED_MESSAGE = "authentication failure";

    @Autowired
    private BourbonProperties bourbonProperties;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        BasicAuth basicAuth = bourbonProperties.getBasic();
        if (basicAuth != null && basicAuth.isEnabled()) {
            String username = basicAuth.getUsername();
            String password = basicAuth.getPassword();
            byte[] bytes = String.format("%s:%s", username, password).getBytes(StandardCharsets.UTF_8);
            String authorization = "Basic " + Base64Utils.encodeToString(bytes);
            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            String header = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
            if (!authorization.equals(header)) {
                HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
                httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpServletResponse.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=" + AUTH_FAILED_MESSAGE);
                httpServletResponse.getWriter().write(AUTH_FAILED_MESSAGE);
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
