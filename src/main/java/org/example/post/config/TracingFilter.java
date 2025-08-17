package org.example.post.config;

import io.micrometer.tracing.Tracer;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TracingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(TracingFilter.class);
    private final Tracer tracer;

    public TracingFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestUri = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        // Get current span (automatically created by Micrometer Tracing)
        var currentSpan = tracer.currentSpan();

        if (currentSpan != null) {
            String traceId = currentSpan.context().traceId();
            String spanId = currentSpan.context().spanId();

            // Add custom tags to the span
            currentSpan.tag("http.method", method);
            currentSpan.tag("http.url", requestUri);
            currentSpan.tag("service.name", "post-service");

            logger.info("Incoming request: {} {} [TraceId: {}, SpanId: {}]",
                    method, requestUri, traceId, spanId);

            try {
                // Continue with the request
                chain.doFilter(request, response);

                // Log response
                currentSpan.tag("http.status_code", String.valueOf(httpResponse.getStatus()));
                logger.info("Request completed: {} {} - Status: {} [TraceId: {}, SpanId: {}]",
                        method, requestUri, httpResponse.getStatus(), traceId, spanId);

            } catch (Exception e) {
                currentSpan.tag("error", "true");
                currentSpan.tag("error.message", e.getMessage());
                logger.error("Request failed: {} {} [TraceId: {}, SpanId: {}] - Error: {}",
                        method, requestUri, traceId, spanId, e.getMessage());
                throw e;
            }
        } else {
            logger.warn("No tracing span found for request: {} {}", method, requestUri);
            chain.doFilter(request, response);
        }
    }
}
