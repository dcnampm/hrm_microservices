package com.nampd.authservice.auth;

import com.nampd.authservice.jwt.JwtService;
import com.nampd.authservice.client.UserCredentials;
import com.nampd.authservice.model.entity.Role;
import com.nampd.authservice.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final RestTemplate restTemplate;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        UserCredentials foundUser = restTemplate.getForObject("/users/auth-service/{email}", UserCredentials.class, authenticationRequest.getUsername());
//        var foundUser = userCredentialsClient.findUserByEmail(authenticationRequest.getUsername());

        if (foundUser != null) {
            if (passwordEncoder.matches(authenticationRequest.getPassword(), foundUser.getPassword())) {

                var accessToken = jwtService.generateToken(foundUser);
                return new AuthenticationResponse(accessToken);
            } else {
                throw new IllegalArgumentException("Wrong password");
            }
        } else {
            throw new IllegalArgumentException("User does not exist");
        }
    }
}


