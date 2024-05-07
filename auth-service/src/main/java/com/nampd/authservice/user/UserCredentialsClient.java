package com.nampd.authservice.user;

import com.nampd.authservice.GenericResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "business-service", url = "${application.config.users-url}")
public interface UserCredentialsClient {
    @GetMapping("/auth-service/{email}")
    Optional<UserCredentials> findUserByEmail(@PathVariable String email);
}
