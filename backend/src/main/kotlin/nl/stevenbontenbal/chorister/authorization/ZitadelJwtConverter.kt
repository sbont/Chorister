package nl.stevenbontenbal.chorister.authorization

import nl.stevenbontenbal.chorister.domain.users.IUserAuthorizationService
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimNames
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.stream.Collectors

@Component
class ZitadelJwtConverter(val authService: IUserAuthorizationService): Converter<Jwt, AbstractAuthenticationToken> {
    private val defaultGrantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()

    override fun convert(source: Jwt): AbstractAuthenticationToken? {
        val defaultAuthorities: MutableCollection<GrantedAuthority?>? = defaultGrantedAuthoritiesConverter.convert(source)
        val zitadelRoles = extractRoles(source)
        val authorities = zitadelRoles?.union(defaultAuthorities?.asIterable() ?: setOf())?.filterNotNull()

        val principalClaimValue = getPrincipleClaimName(source)
        val authentication: AbstractAuthenticationToken = JwtAuthenticationToken(
            source,
            authorities,
            principalClaimValue
        )

        return authentication
    }

    private fun extractRoles(source: Jwt): Set<SimpleGrantedAuthority?>? {
        val userRoles = authService.getRolesFromJwt(source)

        val collect = userRoles.stream()
            .map { role ->
                val roleName = when (role) {
                    is Admin -> "ROLE_ADMIN"
                    is TenantUser -> "ROLE_" + role.accessLevel.name.uppercase()
                    else -> "UNKNOWN"
                }
                SimpleGrantedAuthority(roleName) }
            .collect(Collectors.toSet())
        return collect
    }

    private fun getPrincipleClaimName(jwt: Jwt): String? {
        val claim = JwtClaimNames.SUB
        return jwt.getClaim<String?>(claim)
    }
}

