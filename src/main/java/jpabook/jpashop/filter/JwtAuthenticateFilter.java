package jpabook.jpashop.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jpabook.jpashop.controller.api.common.response.ErrorResponse;
import jpabook.jpashop.exception.user.CannotFindUserException;
import jpabook.jpashop.exception.user.UserExceptonMessages;
import jpabook.jpashop.security.UidUserDetailsService;
import jpabook.jpashop.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;


@Component
@RequiredArgsConstructor
public class JwtAuthenticateFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UidUserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(!isBearerTokenExists(request)){
            filterChain.doFilter(request, response);
            return;
        }


        try{
            String accessToken = request.getHeader("Authorization").split("Bearer ")[1];
            String uid = jwtTokenProvider.verify(accessToken);

            UserDetails userDetails = userDetailsService.loadUserByUid(uid);

            Authentication authentication = createAuthentication(userDetails, request);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        }
        catch (ExpiredJwtException e){
            ErrorResponse errorResponse = new ErrorResponse(UserExceptonMessages.EXPIRED_TOKEN.getMessage(),  Arrays.toString(e.getStackTrace()));

            send401Response(response, errorResponse);
        }

        catch (JwtException | CannotFindUserException e){
            ErrorResponse errorResponse = new ErrorResponse(UserExceptonMessages.INVALID_TOKEN.getMessage(),  Arrays.toString(e.getStackTrace()));

            send401Response(response, errorResponse);
        }
    }

    private void send401Response(HttpServletResponse response, ErrorResponse errorResponse) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=utf-8");

        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }

    private static Authentication createAuthentication(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken.authenticated(userDetails, null, userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));


        return authenticationToken;
    }

    private static boolean isBearerTokenExists(HttpServletRequest request) {
        return request.getHeader("Authorization") != null && request.getHeader("Authorization").startsWith("Bearer");
    }
}
