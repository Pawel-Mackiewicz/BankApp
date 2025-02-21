package info.mackiewicz.bankapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import info.mackiewicz.bankapp.service.CustomUserDetailsService;
import info.mackiewicz.bankapp.service.AdminUserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final AdminUserService adminUserService;
    
    public SecurityConfig(CustomUserDetailsService userDetailsService, 
                         AdminUserService adminUserService) {
        this.userDetailsService = userDetailsService;
        this.adminUserService = adminUserService;
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    // Security chain for settings API endpoints (najwyższy priorytet)
    @Bean
    @Order(1)
    public SecurityFilterChain settingsSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/settings/**")
            .userDetailsService(userDetailsService)
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .authorizeHttpRequests(authz -> authz
                .anyRequest().authenticated()
            )
            .exceptionHandling(e -> e
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + 
                        authException.getMessage() + "\"}");
                }));
        
        return http.build();
    }
    
    // Security chain for admin API endpoints (średni priorytet)
    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**")
            .userDetailsService(adminUserService)
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/public/**"))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(authz -> authz
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> basic
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .exceptionHandling(e -> e
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + 
                        authException.getMessage() + "\"}");
                }));
        
        return http.build();
    }
    
    // Security chain for web endpoints (najniższy priorytet)
    @Bean
    @Order(3)
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // Tymczasowo wyłączone dla debugowania
            .userDetailsService(userDetailsService)
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/public/**", "/login", "/css/**", "/js/**", "/images/**", "/register", "/favicon.ico").permitAll()
                .requestMatchers("/dashboard/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .addLogoutHandler(new SecurityContextLogoutHandler())
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
            )
            .sessionManagement(sessions -> sessions
                .maximumSessions(1)
                .maxSessionsPreventsLogin(true)
                .sessionRegistry(sessionRegistry())
                .expiredUrl("/login?expired")
            )
            .exceptionHandling(e -> e
                .authenticationEntryPoint((request, response, authException) -> 
                    response.sendRedirect("/login")));
                    
        return http.build();
    }
}