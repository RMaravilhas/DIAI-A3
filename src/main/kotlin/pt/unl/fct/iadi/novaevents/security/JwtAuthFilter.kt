package pt.unl.fct.iadi.novaevents.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtService {
    // Generate a secure key (HS256 requires at least 256 bits/32 bytes)
    private val key: SecretKey = Jwts.SIG.HS256.key().build()
    private val validityInMilliseconds: Long = 3600000 // 1 hour

    fun generateToken(username: String, roles: List<String>): String {
        val now = Date()
        val validity = Date(now.time + validityInMilliseconds)

        return Jwts.builder()
            .claim("roles", roles)
            .subject(username)
            .issuedAt(now)
            .expiration(validity)
            .signWith(key)
            .compact()
    }

    fun validateToken(token: String): Claims? {
        return try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: JwtException) {
            null
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}

@Component
class JwtAuthFilter(private val jwtService: JwtService) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val jwtCookie = request.cookies?.find { it.name == "jwt" }
        val token = jwtCookie?.value

        if (token != null) {
            val claims = jwtService.validateToken(token)
            if (claims != null) {
                val username = claims.subject
                val rolesClaim = claims["roles"] as? List<*>
                val authorities = rolesClaim?.map { SimpleGrantedAuthority(it.toString()) } ?: emptyList()

                val auth = UsernamePasswordAuthenticationToken(username, null, authorities)
                SecurityContextHolder.getContext().authentication = auth
            }
        }

        filterChain.doFilter(request, response)
    }
}
