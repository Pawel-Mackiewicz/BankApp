package info.mackiewicz.bankapp.shared.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Interceptor for logging HTTP requests and measuring execution time.
 * Provides consistent logging for all controller methods.
 */
@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTRIBUTE = "startTime";

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());
        log.debug("Received {} request to {}", request.getMethod(), request.getRequestURI());
        return true;
    }

    @Override
    public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, @Nullable ModelAndView modelAndView) {
        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        long executionTime = System.currentTimeMillis() - startTime;
        log.info("{} request to {} completed with status {} in {}ms",
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                executionTime);
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, @Nullable Exception ex) {
        if (ex != null) {
            log.error("Request to {} failed", request.getRequestURI(), ex);
        }
    }
}