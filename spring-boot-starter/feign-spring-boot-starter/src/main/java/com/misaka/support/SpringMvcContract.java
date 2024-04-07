package com.misaka.support;

import com.misaka.annotation.CollectionFormat;
import com.misaka.annotation.processor.*;
import feign.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class SpringMvcContract extends Contract.BaseContract {

    private static final Logger log = LoggerFactory.getLogger(SpringMvcContract.class);
    private static final TypeDescriptor STRING_TYPE_DESCRIPTOR = TypeDescriptor.valueOf(String.class);
    private static final TypeDescriptor ITERABLE_TYPE_DESCRIPTOR = TypeDescriptor.valueOf(Iterable.class);
    private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    private final boolean decodeSlash;
    private final ResourceLoader resourceLoader;
    private final ConversionService conversionService;
    private final Map<Class<? extends Annotation>, AnnotatedParameterProcessor> annotatedArgumentProcessors;

    public SpringMvcContract() {
        this(new ArrayList<>(), new DefaultConversionService(), true);
    }

    public SpringMvcContract(List<AnnotatedParameterProcessor> annotatedParameterProcessors, ConversionService conversionService, boolean decodeSlash) {
        Assert.notNull(annotatedParameterProcessors, "Annotated parameter processor can not be null.");
        Assert.notNull(conversionService, "ConversionService can not be null.");
        this.decodeSlash = decodeSlash;
        this.resourceLoader = new DefaultResourceLoader();
        this.conversionService = conversionService;
        annotatedParameterProcessors.addAll(getDefaultAnnotatedArgumentsProcessors());
        this.annotatedArgumentProcessors = annotatedParameterProcessors.stream().collect(Collectors.toMap(AnnotatedParameterProcessor::getAnnotationType, p -> p));
    }

    protected void processAnnotationOnClass(MethodMetadata methodMetadata, Class<?> cls) {
        RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(cls, RequestMapping.class);
        if (requestMapping != null) {
            log.error("Cannot process class: {}. @RequestMapping annotation is not allowed on @Feign interfaces.", cls.getName());
            throw new IllegalArgumentException("@RequestMapping annotation not allowed on @Feign interfaces");
        } else {
            CollectionFormat collectionFormat = AnnotatedElementUtils.findMergedAnnotation(cls, CollectionFormat.class);
            if (collectionFormat != null) {
                methodMetadata.template().collectionFormat(collectionFormat.value());
            }
        }
    }

    protected void processAnnotationOnMethod(MethodMetadata methodMetadata, Annotation annotation, Method method) {
        if (annotation instanceof CollectionFormat) {
            CollectionFormat collectionFormat = AnnotatedElementUtils.findMergedAnnotation(method, CollectionFormat.class);
            methodMetadata.template().collectionFormat(collectionFormat.value());
        }
        if (annotation instanceof RequestMapping || annotation.annotationType().isAnnotationPresent(RequestMapping.class)) {
            RequestMapping methodMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
            RequestMethod[] methods = methodMapping.method();
            if (methods.length == 0) {
                methods = new RequestMethod[]{RequestMethod.GET};
            }
            methodMetadata.template().method(Request.HttpMethod.valueOf(methods[0].name()));
            String[] values = methodMapping.value();
            if (values.length > 0) {
                String pathValue = Util.emptyToNull(values[0]);
                if (pathValue != null) {
                    pathValue = resolve(pathValue);
                    if (!pathValue.startsWith("/") && !methodMetadata.template().path().endsWith("/")) {
                        pathValue = "/" + pathValue;
                    }
                    methodMetadata.template().uri(pathValue, true);
                    if (methodMetadata.template().decodeSlash() != decodeSlash) {
                        methodMetadata.template().decodeSlash(decodeSlash);
                    }
                }
            }
            parseProduces(methodMetadata, methodMapping);
            parseConsumes(methodMetadata, methodMapping);
            parseHeaders(methodMetadata, methodMapping);
            methodMetadata.indexToExpander(new LinkedHashMap<>());
        }
    }

    protected boolean processAnnotationsOnParameter(MethodMetadata methodMetadata, Annotation[] annotations, int paramIndex) {
        boolean isHttpAnnotation = false;
        AnnotatedParameterProcessor.AnnotatedParameterContext context = new SimpleAnnotatedParameterContext(methodMetadata, paramIndex);
        Method method = methodMetadata.method();
        for (Annotation annotation : annotations) {
            AnnotatedParameterProcessor processor = annotatedArgumentProcessors.get(annotation.annotationType());
            if (processor != null) {
                Annotation synthesizeAnnotation = synthesizeWithMethodParameterNameAsFallbackValue(method, annotation, paramIndex);
                isHttpAnnotation |= processor.processArgument(context, method, synthesizeAnnotation);
            }
        }
        if (!isMultipartFormData(methodMetadata) && isHttpAnnotation && methodMetadata.indexToExpander().get(paramIndex) == null) {
            TypeDescriptor typeDescriptor = createTypeDescriptor(method, paramIndex);
            if (conversionService.canConvert(typeDescriptor, STRING_TYPE_DESCRIPTOR)) {
                Param.Expander expander = value -> {
                    Object converted = conversionService.convert(value, typeDescriptor, STRING_TYPE_DESCRIPTOR);
                    return (String) converted;
                };
                methodMetadata.indexToExpander().put(paramIndex, expander);
            }
        }
        return isHttpAnnotation;
    }

    private List<AnnotatedParameterProcessor> getDefaultAnnotatedArgumentsProcessors() {
        List<AnnotatedParameterProcessor> annotatedArgumentResolvers = new ArrayList<>();
        annotatedArgumentResolvers.add(new CookieValueParameterProcessor());
        annotatedArgumentResolvers.add(new MatrixVariableParameterProcessor());
        annotatedArgumentResolvers.add(new PathVariableParameterProcessor());
        annotatedArgumentResolvers.add(new RequestBodyParameterProcessor());
        annotatedArgumentResolvers.add(new RequestHeaderParameterProcessor());
        annotatedArgumentResolvers.add(new RequestParamParameterProcessor());
        annotatedArgumentResolvers.add(new RequestParamsParameterProcessor());
        annotatedArgumentResolvers.add(new RequestPartParameterProcessor());
        return annotatedArgumentResolvers;
    }

    private String resolve(String value) {
        return StringUtils.hasText(value) && resourceLoader instanceof ConfigurableApplicationContext ? ((ConfigurableApplicationContext) resourceLoader).getEnvironment().resolvePlaceholders(value) : value;
    }

    private void parseProduces(MethodMetadata methodMetadata, RequestMapping annotation) {
        String[] produces = annotation.produces();
        String accept = produces.length == 0 ? null : Util.emptyToNull(produces[0]);
        if (accept != null) {
            methodMetadata.template().header(HttpHeaders.ACCEPT, accept);
        }

    }

    private void parseConsumes(MethodMetadata methodMetadata, RequestMapping annotation) {
        String[] consumes = annotation.consumes();
        String consume = consumes.length == 0 ? null : Util.emptyToNull(consumes[0]);
        if (consume != null) {
            methodMetadata.template().header(HttpHeaders.CONTENT_TYPE, consume);
        }
    }

    private void parseHeaders(MethodMetadata methodMetadata, RequestMapping annotation) {
        String[] headers = annotation.headers();
        for (String header : headers) {
            int index = header.indexOf('=');
            if (!header.contains("!=") && index >= 0) {
                methodMetadata.template().header(resolve(header.substring(0, index)), resolve(header.substring(index + 1).trim()));
            }
        }
    }

    private Annotation synthesizeWithMethodParameterNameAsFallbackValue(Method method, Annotation annotation, int paramIndex) {
        Map<String, Object> attributes = AnnotationUtils.getAnnotationAttributes(annotation);
        Object defaultValue = AnnotationUtils.getDefaultValue(annotation);
        if (defaultValue instanceof String && defaultValue.equals(attributes.get(AnnotationUtils.VALUE))) {
            Type[] parameterTypes = method.getGenericParameterTypes();
            String[] parameterNames = PARAMETER_NAME_DISCOVERER.getParameterNames(method);
            if (shouldAddParameterName(parameterTypes, parameterNames, paramIndex)) {
                attributes.put(AnnotationUtils.VALUE, parameterNames[paramIndex]);
            }
        }
        return AnnotationUtils.synthesizeAnnotation(attributes, annotation.annotationType(), null);
    }

    private boolean shouldAddParameterName(Type[] parameterTypes, String[] parameterNames, int paramIndex) {
        return parameterTypes != null && parameterTypes.length > paramIndex && parameterNames != null && parameterNames.length > paramIndex;
    }

    private boolean isMultipartFormData(MethodMetadata methodMetadata) {
        Collection<String> contentTypes = methodMetadata.template().headers().get(HttpHeaders.CONTENT_TYPE);
        if (!CollectionUtils.isEmpty(contentTypes)) {
            String contentType = contentTypes.iterator().next();
            try {
                return MediaType.MULTIPART_FORM_DATA.equals(MediaType.valueOf(contentType));
            } catch (InvalidMediaTypeException ignored) {
                return false;
            }
        }
        return false;
    }

    private static TypeDescriptor createTypeDescriptor(Method method, int paramIndex) {
        Parameter parameter = method.getParameters()[paramIndex];
        MethodParameter methodParameter = MethodParameter.forParameter(parameter);
        TypeDescriptor typeDescriptor = new TypeDescriptor(methodParameter);
        if (typeDescriptor.isAssignableTo(ITERABLE_TYPE_DESCRIPTOR)) {
            TypeDescriptor elementTypeDescriptor = getElementTypeDescriptor(typeDescriptor);
            Util.checkState(elementTypeDescriptor != null, "Could not resolve element type of Iterable type %s. Not declared?", typeDescriptor);
            typeDescriptor = elementTypeDescriptor;
        }
        return typeDescriptor;
    }

    private static TypeDescriptor getElementTypeDescriptor(TypeDescriptor typeDescriptor) {
        TypeDescriptor elementTypeDescriptor = typeDescriptor.getElementTypeDescriptor();
        if (elementTypeDescriptor == null && Iterable.class.isAssignableFrom(typeDescriptor.getType())) {
            ResolvableType resolvableType = typeDescriptor.getResolvableType().as(Iterable.class).getGeneric(0);
            if (resolvableType.resolve() == null) {
                return null;
            }
            return new TypeDescriptor(resolvableType, null, typeDescriptor.getAnnotations());
        }
        return elementTypeDescriptor;
    }

    private class SimpleAnnotatedParameterContext implements AnnotatedParameterProcessor.AnnotatedParameterContext {

        private final MethodMetadata methodMetadata;
        private final int paramIndex;

        SimpleAnnotatedParameterContext(MethodMetadata methodMetadata, int paramIndex) {
            this.methodMetadata = methodMetadata;
            this.paramIndex = paramIndex;
        }

        public MethodMetadata getMethodMetadata() {
            return methodMetadata;
        }

        public int getParamIndex() {
            return paramIndex;
        }

        public void setParameterName(String name) {
            SpringMvcContract.this.nameParam(methodMetadata, name, paramIndex);
        }

        public Collection<String> setTemplateParameter(String name, Collection<String> params) {
            params = Optional.ofNullable(params).map(ArrayList::new).orElse(new ArrayList<>());
            params.add(String.format("{%s}", name));
            return params;
        }
    }
}
