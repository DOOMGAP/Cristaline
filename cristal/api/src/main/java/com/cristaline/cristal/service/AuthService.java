package com.cristaline.cristal.service;

import com.cristaline.cristal.dto.AuthRequest;
import com.cristaline.cristal.dto.RegisterRequest;
import com.cristaline.cristal.exception.UserNotFoundException;
import com.cristaline.cristal.model.User;
import com.cristaline.cristal.repository.UserRepository;
import com.cristaline.cristal.security.CustomUserDetails;
import com.cristaline.cristal.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

        public String register(RegisterRequest request) {
            User newUser = new User(request.username(), request.email(), request.password());
            userRepository.save(newUser);
            CustomUserDetails customUserDetails = new CustomUserDetails(newUser);
            return jwtService.generateToken(customUserDetails);
        }

        public String login(AuthRequest request) {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
            var user = userRepository.findByUsername(request.username()).orElseThrow();

            return jwtService.generateToken(new CustomUserDetails(user));
        }
    }
