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

            // =====================================================
            // CORS
            // =====================================================
            .cors(cors ->
                cors.configurationSource(corsConfigurationSource())
            )

            // =====================================================
            // CSRF
            // =====================================================
            .csrf(csrf ->
                csrf.ignoringRequestMatchers(
                    "/api/**",
                    "/h2/**"
                )
            )

            // =====================================================
            // AUTHORIZATION
            // =====================================================
            .authorizeHttpRequests(auth -> auth

                // -------------------------------------------------
                // PUBLIC PAGES
                // -------------------------------------------------

                .requestMatchers(
                    "/",
                    "/index",
                    "/login"
                ).permitAll()

                // Static resources
                .requestMatchers(
                    "/css/**",
                    "/js/**",
                    "/img/**",
                    "/favicon.ico"
                ).permitAll()


                // -------------------------------------------------
                // PUBLIC CATALOGUE
                // -------------------------------------------------

                // Public catalogue pages
                .requestMatchers(
                    HttpMethod.GET,
                    "/cantalog/**"
                ).permitAll()

                // Public catalogue API
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/catalogue/**"
                ).permitAll()


                // -------------------------------------------------
                // H2 CONSOLE
                // -------------------------------------------------

                .requestMatchers(
                    "/h2/**"
                ).permitAll()


                // -------------------------------------------------
                // PUBLIC API
                // -------------------------------------------------

                /*
                 * If your entire API is intentionally public,
                 * keep this.
                 *
                 * Otherwise remove "/api/**" from the PUBLIC
                 * section and secure individual API endpoints below.
                 */
                .requestMatchers(
                    "/api/**"
                ).permitAll()


                // -------------------------------------------------
                // SUPERADMIN ONLY
                // -------------------------------------------------

                .requestMatchers(
                    "/api/users/**",
                    "/admin/users/**"
                ).hasRole("SUPER_ADMIN")


                // -------------------------------------------------
                // LIBRARIAN + SUPERADMIN
                // -------------------------------------------------

                .requestMatchers(
                    "/api/books/**",
                    "/api/categories/**",
                    "/api/accessions/**",
                    "/api/members/**",
                    "/api/loans/**",
                    "/api/reports/**"
                ).hasAnyRole(
                    "SUPER_ADMIN",
                    "LIBRARIAN"
                )

                .requestMatchers(
                    "/admin/catalog/**",
                    "/admin/books/**",
                    "/admin/categories/**",
                    "/admin/accessions/**",
                    "/admin/members/**",
                    "/admin/circulation/**",
                    "/admin/reports/**"
                ).hasAnyRole(
                    "SUPER_ADMIN",
                    "LIBRARIAN"
                )


                // -------------------------------------------------
                // STAFF PAGES
                // -------------------------------------------------

                .requestMatchers(
                    "/dashboard",
                    "/circulation",
                    "/reports"
                ).hasAnyRole(
                    "SUPER_ADMIN",
                    "LIBRARIAN"
                )


                // -------------------------------------------------
                // EVERYTHING ELSE
                // -------------------------------------------------

                .anyRequest().authenticated()
            )

            // =====================================================
            // FORM LOGIN
            // =====================================================
            .formLogin(form ->
                form
                    .loginPage("/login")
                    .loginProcessingUrl("/login")
                    .defaultSuccessUrl("/dashboard", true)
                    .permitAll()
            )

            // =====================================================
            // LOGOUT
            // =====================================================
            .logout(logout ->
                logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/index")
                    .permitAll()
            )

            // =====================================================
            // H2 CONSOLE
            // =====================================================
            .headers(headers ->
                headers.frameOptions(frame ->
                    frame.sameOrigin()
                )
            );

        return http.build();
    }


    // =============================================================
    // CORS CONFIGURATION
    // =============================================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
            List.of(
                "http://localhost:5173"
            )
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
