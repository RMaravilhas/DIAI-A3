package pt.unl.fct.iadi.novaevents.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler
import pt.unl.fct.iadi.novaevents.security.JwtAuthFilter

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    private val jwtAuthFilter: JwtAuthFilter
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(authConfig: AuthenticationConfiguration): AuthenticationManager {
        return authConfig.authenticationManager
    }

    @Bean
    @Order(1)
    fun apiSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .securityMatcher("/api/**")
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { it.anyRequest().authenticated() }
            .exceptionHandling { handling ->
                handling.authenticationEntryPoint(org.springframework.security.web.authentication.HttpStatusEntryPoint(org.springframework.http.HttpStatus.UNAUTHORIZED))
            }
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    @Order(2)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // Use Cookie-based CSRF token repository so session isn't required for CSRF
            .csrf { csrf ->
                csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .csrfTokenRequestHandler(CsrfTokenRequestAttributeHandler())
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .requestCache { cache -> 
                cache.requestCache(org.springframework.security.web.savedrequest.CookieRequestCache())
            }
            .exceptionHandling { handling ->
                handling.authenticationEntryPoint(org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint("/login"))
            }
            .authorizeHttpRequests { auth ->
                auth
                    // Specific path security evaluated first
                    .requestMatchers(HttpMethod.GET, "/clubs/{clubId}/events/new", "/clubs/{clubId}/events/{eventId}/edit").hasAnyRole("EDITOR", "ADMIN")
                    .requestMatchers(HttpMethod.POST, "/clubs/{clubId}/events").hasAnyRole("EDITOR", "ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/clubs/{clubId}/events/{eventId}").hasAnyRole("EDITOR", "ADMIN")
                    .requestMatchers("/clubs/{clubId}/events/{eventId}/delete").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/clubs/{clubId}/events/{eventId}").hasRole("ADMIN")

                    // Public endpoints evaluated after specific overrides
                    .requestMatchers("/", "/login", "/logout", "/error", "/css/**", "/js/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/clubs", "/clubs/{id}", "/events", "/clubs/{clubId}/events/{eventId}").permitAll()
                    
                    .anyRequest().authenticated()
            }
            // Form login is disabled here because we are handling /login in our controller manually
            .formLogin { it.disable() }
            .logout { it.disable() } // Handled in AuthController

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
