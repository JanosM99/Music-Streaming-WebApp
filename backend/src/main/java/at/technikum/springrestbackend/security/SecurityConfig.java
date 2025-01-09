package at.technikum.springrestbackend.security;

import at.technikum.springrestbackend.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final CustomUserDetailService customUserDetailService;

    @Bean
    public SecurityFilterChain applicationSecurity(HttpSecurity http) throws Exception {
        // Add JWT filter to the security chain
        http
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http
                // Disable CORS and CSRF
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                // Set sessions to stateless
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Disable formLogin
                .formLogin(AbstractHttpConfigurer::disable)
                // Makes the configuration works on the whole application
                .securityMatcher("/**")
                .authorizeHttpRequests(registry -> registry
                        .requestMatchers("/api").permitAll()
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("api/users/create").permitAll()
                        .requestMatchers("api/users/deleteUser/{id}").hasRole("ADMIN")
                        .requestMatchers("api/country/all").permitAll()
                        .requestMatchers("api/songs/get-all").permitAll()
                        .requestMatchers("api/songs/{id}").permitAll()
                        .requestMatchers("api/files/songs/{id}").permitAll()
                        .requestMatchers("api/playlists/user/{id}").permitAll()
                        .requestMatchers("api/playlists/get-all").permitAll()
                        .requestMatchers("api/playlists/{id}").permitAll()
                        .requestMatchers(
                                // Swagger UI
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/v3/api-docs/**",
                                "/api/swagger-config"
                        ).permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        var builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder
                .userDetailsService(customUserDetailService)
                .passwordEncoder(passwordEncoder());
        return builder.build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:4200") // Allow requests from this origin
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }

}
