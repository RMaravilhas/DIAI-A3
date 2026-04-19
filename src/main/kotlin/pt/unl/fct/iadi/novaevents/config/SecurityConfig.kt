package pt.unl.fct.iadi.novaevents.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
            .authorizeHttpRequests { auth ->
                auth
                    // Public endpoints
                    .requestMatchers("/", "/login", "/logout", "/error", "/css/**", "/js/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/clubs", "/clubs/{id}", "/events", "/clubs/{clubId}/events/{eventId}").permitAll()
                    
                    // Specific path security (method security handles finer grain like Owner)
                    // The instructions ask for:
                    // Public All read operations (club list, club detail, event list, event detail)
                    // EDITOR or ADMIN Creating and editing events (forms and submissions)
                    // ADMIN only Deleting events (confirmation page and deletion)
                    // Wait, the plan was to just require auth here and rely on PreAuthorize for deletes?
                    // Let's use hasAnyRole for endpoints as instructed:
                    .requestMatchers(HttpMethod.GET, "/clubs/{clubId}/events/new", "/clubs/{clubId}/events/{eventId}/edit").hasAnyRole("EDITOR", "ADMIN")
                    .requestMatchers(HttpMethod.POST, "/clubs/{clubId}/events").hasAnyRole("EDITOR", "ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/clubs/{clubId}/events/{eventId}").hasAnyRole("EDITOR", "ADMIN")
                    
                    // As agreed to avoid contradiction with method-level, we just require auth here,
                    // but wait, "ADMIN only Deleting events" could mean URL only ADMIN, and method level adds Owner.
                    // Oh, if I use `hasAnyRole("EDITOR", "ADMIN")` for everything modifying, then method level can say `owner OR admin`.
                    // Yes! Since Editor can be owner, we should allow Editor into the delete endpoints if owner.
                    // So URL rule for delete should also be hasAnyRole("EDITOR", "ADMIN") OR just authenticated(), and let method level handle Owner vs Admin!
                    // Oh wait, if Editor can be owner, then Editor MUST be able to reach the DELETE endpoint.
                    // The exercise explicitly says: 
                    // "ADMIN only Deleting events (confirmation page and deletion)" in the table.
                    // And method level: "An event can be deleted by its owner or by an admin."
                    // If an Editor is the owner, do they bypass the URL rule? No, Spring Security applies URL rules first.
                    // If they are an Editor and Owner, the URL rule "ADMIN only" would block them.
                    // I will use authenticated() for the delete URL so that @PreAuthorize can evaluate Owner.
                    .requestMatchers("/clubs/{clubId}/events/{eventId}/delete").authenticated()
                    .requestMatchers(HttpMethod.DELETE, "/clubs/{clubId}/events/{eventId}").authenticated()
                    
                    .anyRequest().authenticated()
            }
            // Form login is disabled here because we are handling /login in our controller manually
            .formLogin { it.disable() }
            .logout { it.disable() } // Handled in AuthController

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
