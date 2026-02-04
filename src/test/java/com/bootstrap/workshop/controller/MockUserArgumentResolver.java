package com.bootstrap.workshop.controller;

import com.bootstrap.workshop.entity.User;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Custom argument resolver for testing that injects a mock User.
 * This is used when security filters are disabled in @WebMvcTest.
 */
public class MockUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final User mockUser;

    public MockUserArgumentResolver() {
        this.mockUser = new User("test@example.com", "Test User", "password", "Test Bank", "ACC123", "123 Test St");
        this.mockUser.setId(1L);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthenticationPrincipal.class) &&
                User.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        return mockUser;
    }
}
