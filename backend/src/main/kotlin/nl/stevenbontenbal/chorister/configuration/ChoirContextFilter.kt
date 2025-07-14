package nl.stevenbontenbal.chorister.configuration

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import nl.stevenbontenbal.chorister.service.ChoirContext
import nl.stevenbontenbal.chorister.service.UserService
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException


class ChoirContextFilter(val userService: UserService) : OncePerRequestFilter() {
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val currentChoir = userService.getCurrentChoirId()
        currentChoir?.let { ChoirContext.setCurrentChoirId(it) }

        try {
            filterChain.doFilter(request, response)
        } finally {
            ChoirContext.clear()
        }
    }
}
