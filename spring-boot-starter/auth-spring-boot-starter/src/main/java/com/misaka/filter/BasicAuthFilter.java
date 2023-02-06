package com.misaka.filter;

import com.misaka.annotation.BasicAuth;
import com.misaka.annotation.DisableBasicAuth;
import com.misaka.properties.BasicAuthProperties;
import org.apache.catalina.connector.RequestFacade;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Order(Integer.MIN_VALUE)
public class BasicAuthFilter implements InitializingBean, Filter {

    private static final String AUTH_FAILED_MESSAGE = "authentication failure";
    private static final String BASIC_AUTH_URL_FORMAT = "%s %s";
    private static final List<String> BASIC_AUTH_URL_LIST = new ArrayList<>();

    @Autowired
    private BasicAuthProperties basicAuthProperties;

    @Resource
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = requestMappingHandlerMapping.getHandlerMethods();
        handlerMethodMap.forEach((k, v) -> {
            Class<?> controllerClass = v.getMethod().getDeclaringClass();
            boolean basicAuth = v.hasMethodAnnotation(BasicAuth.class) || controllerClass.getAnnotation(BasicAuth.class) != null;
            boolean disableBasicAuth = v.hasMethodAnnotation(DisableBasicAuth.class);
            if (basicAuth && !disableBasicAuth) {
                RequestMethodsRequestCondition methodsCondition = k.getMethodsCondition();
                PatternsRequestCondition patternsCondition = k.getPatternsCondition();
                if (patternsCondition != null) {
                    RequestMethod method = getFirst(methodsCondition.getMethods());
                    String path = getFirst(patternsCondition.getPatterns());
                    BASIC_AUTH_URL_LIST.add(String.format(BASIC_AUTH_URL_FORMAT, method, path));
                }
            }
        });
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        RequestFacade requestFacade = (RequestFacade) servletRequest;
        String basicAuthUrl = String.format(BASIC_AUTH_URL_FORMAT, requestFacade.getMethod(), requestFacade.getServletPath());
        if (BASIC_AUTH_URL_LIST.contains(basicAuthUrl)) {
            String username = basicAuthProperties.getUsername();
            String password = basicAuthProperties.getPassword();
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

    private <T> T getFirst(Set<T> set) {
        T element = null;
        if (set != null) {
            Iterator<T> iterator = set.iterator();
            if (iterator.hasNext()) {
                element = iterator.next();
            }
        }
        return element;
    }
}
