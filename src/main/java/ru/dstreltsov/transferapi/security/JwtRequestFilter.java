package ru.dstreltsov.transferapi.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final MyUserDetailsService myUserDetailsService;
    private final JwtTokenReader jwtTokenReader;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        final String requestURI = request.getRequestURI();
        if ("/v1/auth/login".equals(requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        final String jwtToken = getTokenFromRequest(request);
        if (jwtToken == null || !jwtTokenReader.isAccessTokenValid(jwtToken)) {
            chain.doFilter(request, response);
            return;
        }

        final Long userId = jwtTokenReader.getUserIdFromToken(jwtToken);
        final String email = jwtTokenReader.getEmailFromToken(jwtToken);

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.info(String.format("Request made by user [%d] with email [%s]", userId, email));
            final UserDetails userDetails = myUserDetailsService.loadUserByUsername(email);
            final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        chain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        final String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        return bearerToken != null && bearerToken.startsWith("Bearer ")
                ? bearerToken.substring(7)
                : null;
    }
}
