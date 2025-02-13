package info.mackiewicz.bankapp.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

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
    
    // Security chain for API endpoints (priorytet wysoki)
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**")
            .userDetailsService(adminUserService)  // Używamy AdminUserService dla API
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()
            )
            .httpBasic(withDefaults())
            .exceptionHandling(e -> e
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Unauthorized\"}");
                })
            );
        return http.build();
    }
    
    // Security chain for web endpoints (priorytet niższy)
    @Bean
    @Order(2)
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .userDetailsService(userDetailsService)
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/public/**", "/login", "/css/**", "/js/**", "/images/**", "/register").permitAll()
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
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
            )
            .sessionManagement(sessions -> sessions
                .maximumSessions(1)
                .maxSessionsPreventsLogin(true)
            )
            .exceptionHandling(e -> e
                .authenticationEntryPoint((request, response, authException) -> response.sendRedirect("/login"))
            );
        return http.build();
    }
}