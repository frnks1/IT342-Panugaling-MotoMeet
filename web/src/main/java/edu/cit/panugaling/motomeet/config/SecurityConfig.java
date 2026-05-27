package edu.cit.panugaling.motomeet.config;

import edu.cit.panugaling.motomeet.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
    this.userRepository = userRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
    return username -> userRepository.findByEmail(username)
        .map(user -> User.withUsername(user.getEmail())
            .password(user.getPassword())
            .roles(normalizeRole(user.getRole()))
            .build())
        .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return "USER";
        }
        return role.trim().toUpperCase();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/", "/login", "/register", "/h2-console/**", "/css/**", "/js/**", "/images/**").permitAll()
                    .requestMatchers("/api/v1/auth/**", "/api/v1/mobile/**").permitAll()
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .requestMatchers("/feed/**", "/feed", "/rides/**", "/rides", "/meetups/**", "/meetups", "/garage/**", "/garage", "/marketplace/**", "/marketplace", "/notifications/**", "/notifications", "/profile", "/profile/**").hasRole("USER")
                    .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login")
            .loginProcessingUrl("/login")
            .usernameParameter("email")
            .passwordParameter("password")
            .successHandler((request, response, authentication) -> {
                boolean admin = authentication.getAuthorities().stream()
                        .map(grantedAuthority -> grantedAuthority.getAuthority())
                        .anyMatch(authority -> authority.equals("ROLE_ADMIN"));
                response.sendRedirect(admin ? "/admin" : "/feed");
            })
            .failureUrl("/login?error=true")
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login?logout=true")
        );

    http.headers(headers -> headers.frameOptions(frame -> frame.disable()));
        return http.build();
    }
}