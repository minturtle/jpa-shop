package jpabook.jpashop.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jpabook.jpashop.security.UidUserDetailsService;
import jpabook.jpashop.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@Component
@RequiredArgsConstructor
public class JwtAuthenticateFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UidUserDetailsService userDetailsService;


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

            Authentication authentication = createAuthentication(userDetails);
            SecurityContextHolder.getContext().setAuthentication(authentication);


        }catch (Exception e){

        }finally {
            filterChain.doFilter(request, response);
        }
    }

    private static Authentication createAuthentication(UserDetails userDetails) {
        return UsernamePasswordAuthenticationToken.authenticated(userDetails, null, userDetails.getAuthorities());
    }

    private static boolean isBearerTokenExists(HttpServletRequest request) {
        return request.getHeader("Authorization") != null && request.getHeader("Authorization").startsWith("Bearer");
    }
}
