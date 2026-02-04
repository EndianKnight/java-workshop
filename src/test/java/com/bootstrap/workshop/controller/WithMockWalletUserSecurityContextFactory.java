package com.bootstrap.workshop.controller;

import com.bootstrap.workshop.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;

/**
 * Factory to create a SecurityContext with a custom User entity as the
 * principal.
 */
public class WithMockWalletUserSecurityContextFactory implements WithSecurityContextFactory<WithMockWalletUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockWalletUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        User user = new User(
                annotation.email(),
                annotation.name(),
                "password",
                "Test Bank",
                "ACC123",
                "123 Test St");
        user.setId(annotation.id());

        Authentication auth = new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of(new SimpleGrantedAuthority(annotation.role())));

        context.setAuthentication(auth);
        return context;
    }
}
