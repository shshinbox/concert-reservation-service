package me.songha.concert.shared.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String SECRET_KEY = "my-fixed-secret-key-my-fixed-secret-key";
    private static final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    private static final Set<String> VALID_ROLES = Set.of("ADMIN", "USER");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);

        if (request.getRequestURI().startsWith("/api/") && request.getMethod().equals("GET")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (token == null) {
            setErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Missing token");
            return;
        }

        try {
            Claims claims = validateAndExtractClaims(token);
            String role = claims.get("role", String.class);

            if (!isValidRole(role)) {
                setErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Forbidden: Insufficient permissions");
                return;
            }

            Authentication authentication = buildAuthentication(claims);
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (ExpiredJwtException e) {
            setErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Token expired");
            return;
        } catch (Exception e) {
            setErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Token validation failed");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        return bearerToken != null && bearerToken.startsWith("Bearer ") ? bearerToken.substring(7) : null;
    }

    private Claims validateAndExtractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isValidRole(String role) {
        return role != null && VALID_ROLES.contains(role);
    }

    private Authentication buildAuthentication(Claims claims) {
        Long userId = claims.get("userId", Long.class);
        String username = claims.get("username", String.class);
        String role = claims.get("role", String.class);

        JwtUser jwtUser = new JwtUser(
                userId, username, role, Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role)));

        return new UsernamePasswordAuthenticationToken(jwtUser, null, jwtUser.getAuthorities());
    }

    private void setErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"message\":\"%s\"}", message));
    }
}