package jpabook.jpashop.controller.common.argumentResolvers;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jpabook.jpashop.controller.common.annotations.LoginedUserUid;
import jpabook.jpashop.exception.user.UserExceptonMessages;
import jpabook.jpashop.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;



@Component
@RequiredArgsConstructor
public class LoginedUserUidArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 파라미터의 타입이 이 Resolver이 처리할 수 있는 타입인지 확인
        boolean isParameterTypeSupports = parameter.getParameterType().isAssignableFrom(String.class);

        // 이 ArgumentResolver가 적용되는 Annotation 지정
        boolean hasAnnotation = parameter.hasParameterAnnotation(LoginedUserUid.class);

        return isParameterTypeSupports && hasAnnotation;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        String token = getJwtToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        return jwtTokenProvider.verify(token);

    }

    private String getJwtToken(String tokenHeader) {
            String[] tokenHeaderValues = tokenHeader.split(" ");
            if(!tokenHeaderValues[0].equals("Bearer")){
                throw new JwtException(UserExceptonMessages.INVALID_TOKEN.getMessage());
            }
            return tokenHeaderValues[1];
    }
}
