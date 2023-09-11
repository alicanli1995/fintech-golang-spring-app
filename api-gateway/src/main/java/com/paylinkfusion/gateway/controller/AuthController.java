package com.paylinkfusion.gateway.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.paylinkfusion.gateway.models.dto.request.LoginRequest;
import com.paylinkfusion.gateway.models.dto.request.SignupRequest;
import com.paylinkfusion.gateway.models.dto.response.JwtResponse;
import com.paylinkfusion.gateway.models.dto.response.MessageResponse;
import com.paylinkfusion.gateway.service.LoginService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final LoginService loginService;

    @PostMapping("/signing")
    @CircuitBreaker(name = "beCommon")
    @RateLimiter(name = "beCommon")
    @Retry(name = "beCommon")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        final var loginResp = loginService.authUser(loginRequest);
        return ResponseEntity.ok(loginResp);
    }

    @PostMapping("/signup")
    @CircuitBreaker(name = "beCommon")
    @RateLimiter(name = "beCommon")
    @Retry(name = "beCommon")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignupRequest signUpRequest)
            throws JsonProcessingException {
        final var registerResp = loginService.registerUser(signUpRequest);
        return ResponseEntity.ok(registerResp);
    }
}
