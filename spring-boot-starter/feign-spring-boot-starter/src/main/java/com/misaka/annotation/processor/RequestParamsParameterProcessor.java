package com.misaka.annotation.processor;

import com.misaka.annotation.RequestParams;
import feign.MethodMetadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;


public class RequestParamsParameterProcessor implements AnnotatedParameterProcessor {

	private static final Class<RequestParams> ANNOTATION = RequestParams.class;

	@Override
	public Class<? extends Annotation> getAnnotationType() {
		return ANNOTATION;
	}

	@Override
	public boolean processArgument(AnnotatedParameterContext context, Method method, Annotation annotation) {
		MethodMetadata methodMetadata = context.getMethodMetadata();
		int paramIndex = context.getParamIndex();
		if (methodMetadata.queryMapIndex() == null) {
			methodMetadata.queryMapIndex(paramIndex);
		}
		return true;
	}

}
