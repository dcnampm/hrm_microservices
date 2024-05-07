package com.nampd.apigateway.config;

import com.nampd.apigateway.JwtService;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationGlobalFilter implements GlobalFilter {

    private final JwtService jwtService;

    public JwtAuthenticationGlobalFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Lấy token từ request header
        String token = extractTokenFromRequest(exchange.getRequest());

        if (token != null && jwtService.isTokenValid(token)) {
            //tiếp tục chuỗi filter, cho phép request tiếp theo
            return chain.filter(exchange);
        } else {
            // Nếu token không hợp lệ, trả về lỗi 401 Unauthorized
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

//    @Override
//    public int getOrder() {
//        // Sắp xếp filter này có thứ tự thấp nhất để thực hiện trước các filter khác
//        return Ordered.HIGHEST_PRECEDENCE;
//    }

    private String extractTokenFromRequest(ServerHttpRequest request) {
        // Lấy token từ header Authorization
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}

