package pt.unl.fct.iadi.novaevents.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

class RequestLoggingInterceptor : HandlerInterceptor {
    private val logger = LoggerFactory.getLogger(RequestLoggingInterceptor::class.java)

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        val principalName = request.userPrincipal?.name ?: "anonymous"
        val method = request.method
        val uri = request.requestURI
        val status = response.status
        
        logger.info("[$principalName] $method $uri [$status]")
    }
}

@Configuration
class WebConfig : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(RequestLoggingInterceptor())
    }
}
