package com.nampd.apigateway;

import com.nampd.apigateway.client.PermissionVO;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.*;

@Component
public class JwtAuthenticationGlobalFilter implements GlobalFilter {

    private final JwtService jwtService;
    private final RestTemplate restTemplate;

    public JwtAuthenticationGlobalFilter(JwtService jwtService, RestTemplate restTemplate) {
        this.jwtService = jwtService;
        this.restTemplate = restTemplate;
    }

    private static final String[] WHITE_LIST_URL = {
            "/api/v1/auth/authenticate",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/api-docs",
            "/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"
    };

    private static final Map<String, String> ACTION_TO_METHOD_MAP = new HashMap<>();
    static {
        ACTION_TO_METHOD_MAP.put("CREATE", "POST");
        ACTION_TO_METHOD_MAP.put("READ", "GET");
        ACTION_TO_METHOD_MAP.put("UPDATE", "PUT");
        ACTION_TO_METHOD_MAP.put("DELETE", "DELETE");
    }
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final String path = exchange.getRequest().getPath().toString();
        final String method = exchange.getRequest().getMethod().toString();

        if (Arrays.stream(WHITE_LIST_URL).anyMatch(path::contains)) {
            return chain.filter(exchange);
        }

        final String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            final String jwt = authHeader.substring(7);

            if (jwtService.isTokenValid(jwt)) {
                try {
                    Claims claims = jwtService.extractAllClaims(jwt);
                    String role = (String) claims.get("role");

                    // Kiểm tra quyền của người dùng
                    if (hasPermission(role, path, method)) {
                        return chain.filter(exchange);
                    } else {
                        // Không có permission
                        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                        return exchange.getResponse().setComplete();
                    }
                } catch (Exception e) {
                    // Token k hợp lệ, trả về lỗi 401 Unauthorized
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }
            }
        } else {
            // Không có token trong header
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        return null;
    }

    private boolean hasPermission(String role, String path, String method) {
        try {

            Set<PermissionVO> permissionVOSet = restTemplate.exchange(
                    "/permissions/for-role/{role}",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Set<PermissionVO>>() {},
                    role
            ).getBody();

            if (permissionVOSet != null) {
                for (PermissionVO permission : permissionVOSet) {
                    String permissionDescription = permission.getDescription();
                    String permissionAction = permission.getName().split("\\.")[1];

                    String permissionMethod = ACTION_TO_METHOD_MAP.get(permissionAction.toUpperCase());

                    System.out.println(permissionDescription);
                    System.out.println(permissionMethod);

                    // So sánh method và path với phần tử permission
                    if (method.equalsIgnoreCase(permissionMethod) && path.contains(permissionDescription)) {
                        return true;
                    }
                }
            }
        } catch (RestClientException e) {
            // Xử lý lỗi khi gọi endpoint lấy permissions
            e.printStackTrace();
        }
        return false;
    }
}