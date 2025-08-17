package org.example.post.config;

import io.micrometer.tracing.Tracer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TracingConfig {

    @Bean
    public FilterRegistrationBean<TracingFilter> tracingFilterRegistration(Tracer tracer) {
        FilterRegistrationBean<TracingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new TracingFilter(tracer));
        registration.addUrlPatterns("/*");
        registration.setName("tracingFilter");
        registration.setOrder(1);
        return registration;
    }
}
