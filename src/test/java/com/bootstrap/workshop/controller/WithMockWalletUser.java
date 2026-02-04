package com.bootstrap.workshop.controller;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Custom annotation for mocking a wallet user in security tests.
 * This sets up the SecurityContext with a User entity as the principal.
 */
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockWalletUserSecurityContextFactory.class)
public @interface WithMockWalletUser {
    long id() default 1L;

    String email() default "test@example.com";

    String name() default "Test User";

    String role() default "ROLE_USER";
}
