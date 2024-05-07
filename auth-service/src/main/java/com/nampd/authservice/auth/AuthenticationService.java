package com.nampd.authservice.auth;

import com.nampd.authservice.jwt.JwtService;
import com.nampd.authservice.user.UserCredentials;
import com.nampd.authservice.user.UserCredentialsClient;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserCredentialsClient userCredentialsClient;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );

        var foundUser = userCredentialsClient.findUserByEmail(authenticationRequest.getUsername());
        if (foundUser.isPresent()) {
            UserCredentials user = foundUser.get();
            var accessToken = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);

            return new AuthenticationResponse(accessToken, refreshToken);
        } else {
            throw new IllegalArgumentException("Invalid email or password");
        }
    }

    public AuthenticationResponse refreshToken(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String username;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        refreshToken = authHeader.substring(7);
        username = jwtService.extractUsername(refreshToken);

        if (username != null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                var accessToken = jwtService.generateToken(userDetails);
                var newRefreshToken = jwtService.generateRefreshToken(userDetails);
                return new AuthenticationResponse(accessToken, newRefreshToken);
            }
        }
        return null;
    }
}
