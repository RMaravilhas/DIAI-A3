package pt.unl.fct.iadi.novaevents.controller

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import pt.unl.fct.iadi.novaevents.security.JwtService

@Controller
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService
) {

    @GetMapping("/login")
    fun showLoginForm(
        @RequestParam(required = false) error: String?,
        model: Model
    ): String {
        if (error != null) {
            model.addAttribute("loginError", true)
        }
        model.addAttribute("pageTitle", "Login")
        return "login"
    }

    @PostMapping("/login")
    fun performLogin(
        @RequestParam username: String,
        @RequestParam password: String,
        request: jakarta.servlet.http.HttpServletRequest,
        response: HttpServletResponse,
        model: Model
    ): String {
        try {
            val auth = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(username, password)
            )
            
            val roles = auth.authorities.map { it.authority }
            val token = jwtService.generateToken(auth.name, roles)
            
            val cookie = Cookie("jwt", token)
            cookie.isHttpOnly = true
            cookie.path = "/"
            cookie.maxAge = 3600 // 1 hour
            // Note: Secure flag should ideally be set in production
            response.addCookie(cookie)
            
            val requestCache = org.springframework.security.web.savedrequest.CookieRequestCache()
            val savedRequest = requestCache.getRequest(request, response)
            val targetUrl = savedRequest?.redirectUrl ?: "/clubs"
            
            return "redirect:$targetUrl"
        } catch (e: AuthenticationException) {
            model.addAttribute("loginError", true)
            model.addAttribute("pageTitle", "Login")
            return "login" // Return to form directly so we don't break CSRF state issues (since sess stateless)
        }
    }

    @PostMapping("/logout")
    fun logout(response: HttpServletResponse): String {
        val cookie = Cookie("jwt", "")
        cookie.isHttpOnly = true
        cookie.path = "/"
        cookie.maxAge = 0
        response.addCookie(cookie)
        
        return "redirect:/clubs"
    }
}
