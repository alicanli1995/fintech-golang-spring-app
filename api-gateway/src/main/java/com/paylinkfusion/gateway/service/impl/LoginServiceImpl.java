package com.paylinkfusion.gateway.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.paylinkfusion.gateway.exception.AuthServiceException;
import com.paylinkfusion.gateway.messaging.producer.UserRegisterProducer;
import com.paylinkfusion.gateway.models.Account;
import com.paylinkfusion.gateway.models.Role;
import com.paylinkfusion.gateway.models.dto.UserRegisterEvent;
import com.paylinkfusion.gateway.models.dto.request.LoginRequest;
import com.paylinkfusion.gateway.models.dto.request.SignupRequest;
import com.paylinkfusion.gateway.models.dto.response.JwtResponse;
import com.paylinkfusion.gateway.models.dto.response.MessageResponse;
import com.paylinkfusion.gateway.models.enums.ERole;
import com.paylinkfusion.gateway.repository.AccountRepository;
import com.paylinkfusion.gateway.repository.RoleRepository;
import com.paylinkfusion.gateway.security.jwt.JwtUtils;
import com.paylinkfusion.gateway.security.services.UserDetailsImpl;
import com.paylinkfusion.gateway.service.LoginService;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final AuthenticationManager authenticationManager;
    private final UserRegisterProducer userRegisterProducer;
    private final AccountRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    @Override
    public JwtResponse authUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        return JwtResponse.builder().token(jwt).id(userDetails.getId()).username(userDetails.getUsername())
                .email(userDetails.getEmail()).roles(roles).build();
    }


    @Override
    public MessageResponse registerUser(SignupRequest signUpRequest) throws JsonProcessingException {
        checkValidations(signUpRequest);

        Account user = getAccount(signUpRequest);
        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        setRoles(strRoles, roles);
        user.setRoles(roles);

        Account save = userRepository.save(user);

        userRegisterProducer.sendUserRegisterEventAsync(UserRegisterEvent.builder().accountId(save.getId()).build());

        return MessageResponse.builder().message("User registered successfully!").build();
    }

    private void checkValidations(SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername()) || userRepository.existsByEmail(
                signUpRequest.getEmail())) {
            throw new AuthServiceException("Error: Username is already taken!", HttpStatus.SC_NOT_ACCEPTABLE);
        }
    }

    private void setRoles(Set<String> strRoles, Set<Role> roles) {
        if (Objects.isNull(strRoles) || strRoles.isEmpty()) {
            Role userRole = getUserRole(ERole.ROLE_USER);
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                Role foundRole = switch (role) {
                    case "admin" -> getUserRole(ERole.ROLE_ADMIN);
                    case "mod" -> getUserRole(ERole.ROLE_MODERATOR);
                    default -> getUserRole(ERole.ROLE_USER);
                };
                roles.add(foundRole);
            });
        }
    }

    private Role getUserRole(ERole roleUser) {
        return roleRepository.findByName(roleUser)
                .orElseThrow(() -> new AuthServiceException("Role is not found.", HttpStatus.SC_NOT_FOUND));
    }

    private Account getAccount(SignupRequest signUpRequest) {
        return Account.builder().username(signUpRequest.getUsername()).email(signUpRequest.getEmail())
                .password(encoder.encode(signUpRequest.getPassword())).fullName(signUpRequest.getFullName())
                .phone(signUpRequest.getPhoneNumber()).build();
    }

}
