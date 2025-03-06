package com.dtest.drools.global.security;

import com.dtest.drools.global.apipayload.code.status.ErrorStatus;
import com.dtest.drools.global.apipayload.exception.ExceptionResponse;
import com.dtest.drools.global.apipayload.exception.GeneralException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (GeneralException e) {
            setResponse(response);
        }
    }

    private void setResponse(HttpServletResponse response) throws IOException{
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(ErrorStatus.FORBIDDEN.getHttpStatus().value());

        ExceptionResponse errorResponse = ExceptionResponse.of(ErrorStatus.TOKEN_INVALID);
        String errorJson = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(errorJson);
    }
}
