package jpabook.jpashop.filter;

import jakarta.servlet.*;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;



@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MDCLoggingFilter implements Filter {

    public static final String REQUEST_ID = "request_id";



    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        UUID uuid = UUID.randomUUID();

        MDC.put(REQUEST_ID, uuid.toString());
        chain.doFilter(request, response);
        MDC.clear();
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

}
