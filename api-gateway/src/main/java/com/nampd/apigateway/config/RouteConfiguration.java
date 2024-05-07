//package com.nampd.apigateway.config;
//
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class RouteConfiguration {
//    @Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route("auth-service", r -> r.path("/api/v1/auth/**")
//                        .uri("http://localhost:8081"))
//                .route("user-service", r -> r.path("/api/v1/users/**", "/api/v1/departments/**", "/api/v1/roles/**")
//                        .uri("http://localhost:8080"))
//                .build();
//    }
//}
//
