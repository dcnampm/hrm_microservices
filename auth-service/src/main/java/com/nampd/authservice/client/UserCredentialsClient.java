//package com.nampd.authservice.user;
//
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//
//import java.util.List;
//
//@FeignClient(name = "business-service", url = "${application.config.user-url}")
//public interface UserCredentialsClient {
//    @GetMapping("/auth-service/{email}")
//    UserCredentials findUserByEmail(@PathVariable String email);
//}
