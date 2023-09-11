package com.paylinkfusion.gateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.paylinkfusion.gateway.models.dto.request.LoginRequest;
import com.paylinkfusion.gateway.models.dto.request.SignupRequest;
import com.paylinkfusion.gateway.models.dto.response.JwtResponse;
import com.paylinkfusion.gateway.models.dto.response.MessageResponse;

public interface LoginService {

    JwtResponse authUser(LoginRequest loginRequest);

    MessageResponse registerUser(SignupRequest signUpRequest) throws JsonProcessingException;
}
