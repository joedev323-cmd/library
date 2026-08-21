package com.example.libback.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
            .cors(cors ->
                cors.configurationSource(corsConfigurationSource())
            )

            .csrf(csrf ->
                csrf.ignoringRequestMatchers("/api/**", "/h2/**")
            )

            .authorizeHttpRequests(auth -> auth

                // -----------------------------------------
                // PUBLIC
                // -----------------------------------------

                .requestMatchers(
                    "/",
                    "/index",
                    "/login",
                    "/css/**",
                    "/js/**",
                    "/img/**",
                    "/h2/**"
                ).permitAll()

                // Public catalogue
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/catalogue/**"
                ).permitAll()

                .requestMatchers(
                    "/catalog/**"
                ).permitAll()


                // -----------------------------------------
                // SUPERADMIN ONLY
                // -----------------------------------------

                .requestMatchers(
                    "/api/users/**",
                    "/admin/users/**"
                ).hasRole("SUPERADMIN")


                // -----------------------------------------
                // LIBRARIAN + SUPERADMIN
                // -----------------------------------------

                .requestMatchers(
                    "/api/books/**",
                    "/api/categories/**",
                    "/api/accessions/**",
                    "/api/members/**",
                    "/api/loans/**",
                    "/api/reports/**"
                ).hasAnyRole(
                    "SUPERADMIN",
                    "LIBRARIAN"
                )

                .requestMatchers(
                    "/admin/books/**",
                    "/admin/categories/**",
                    "/admin/accessions/**",
                    "/admin/members/**",
                    "/admin/circulation/**",
                    "/admin/reports/**"
                ).hasAnyRole(
                    "SUPERADMIN",
                    "LIBRARIAN"
                )


                // -----------------------------------------
                // EVERYTHING ELSE
                // -----------------------------------------

                .anyRequest().authenticated()
            )

            .formLogin(form ->
                form
                    .loginPage("/login")
                    .loginProcessingUrl("/login")
                    .defaultSuccessUrl("/dashboard", true)
                    .permitAll()
            )

            .logout(logout ->
                logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/index")
                    .permitAll()
            )

            .headers(headers ->
                headers.frameOptions(frame ->
                    frame.sameOrigin()
                )
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
            List.of("http://localhost:5173")
        );

        configuration.setAllowedMethods(
            List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
            )
        );

        configuration.setAllowedHeaders(
            List.of("*")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
            "/**",
            configuration
        );

        return source;
    }
}
