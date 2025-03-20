package info.mackiewicz.bankapp.shared.infrastructure.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Interceptor that provides comprehensive logging for all HTTP requests in the application.
 *
 * <p>This interceptor tracks and logs:
 * <ul>
 *   <li>Request initiation with method and URI (DEBUG level)</li>
 *   <li>Request completion with status code and execution time (INFO level)</li>
 *   <li>Request failures with full exception details (ERROR level)</li>
 * </ul></p>
 *
 * <p>The interceptor measures request execution time by storing the start time
 * in the request attributes and calculating the difference in postHandle.</p>
 *
 * <p>Thread-safe: This class is thread-safe as it uses thread-local request objects
 * and has no shared mutable state.</p>
 *
 * @see HandlerInterceptor
 * @see HttpServletRequest
 * @see HttpServletResponse
 */
@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTRIBUTE = "startTime";

    /**
     * Called before the actual handler is executed.
     * Logs the incoming request and stores the start time for performance measurement.
     *
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler chosen handler to execute, for type and/or instance examination
     * @return true to continue processing, false to stop processing
     * @see HandlerInterceptor#preHandle
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());
        log.debug("Received {} request to {}", request.getMethod(), request.getRequestURI());
        return true;
    }

    /**
     * Called after the handler is executed but before the view is rendered.
     * Calculates and logs the request execution time along with response status.
     *
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler the handler that was executed
     * @param modelAndView the ModelAndView that the handler returned (can be null)
     * @see HandlerInterceptor#postHandle
     */
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

    /**
     * Called after the complete request has been finished and view was rendered.
     * Logs any exceptions that occurred during the request processing.
     *
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler the handler that was executed
     * @param ex exception thrown during processing (if any)
     * @see HandlerInterceptor#afterCompletion
     */
    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, @Nullable Exception ex) {
        if (ex != null) {
            log.error("Request to {} failed", request.getRequestURI(), ex);
        }
    }
}