package com.saveops.common.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class CorrelationIdFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String correlationId = request.getHeader(CorrelationId.HEADER);
        CorrelationId.set(correlationId);
        MDC.put("correlationId", CorrelationId.currentOrNew());
        response.setHeader(CorrelationId.HEADER, CorrelationId.currentOrNew());
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("correlationId");
            CorrelationId.clear();
        }
    }
}

