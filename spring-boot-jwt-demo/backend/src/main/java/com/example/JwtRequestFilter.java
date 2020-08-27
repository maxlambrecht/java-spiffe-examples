package com.example;

import io.spiffe.bundle.BundleSource;
import io.spiffe.bundle.jwtbundle.JwtBundle;
import io.spiffe.exception.AuthorityNotFoundException;
import io.spiffe.exception.BundleNotFoundException;
import io.spiffe.exception.JwtSvidException;
import io.spiffe.svid.jwtsvid.JwtSvid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private BundleSource<JwtBundle> jwtBundle;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestHeader = request.getHeader("Authorization");
        String token = StringUtils.substringAfter(requestHeader, "Bearer ");

        if (StringUtils.isBlank(token)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            JwtSvid jwtSvid = JwtSvid.parseAndValidate(token, jwtBundle, Collections.singleton("backend1"));
        } catch (JwtSvidException | BundleNotFoundException | AuthorityNotFoundException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }

        filterChain.doFilter(request, response);
    }
}
