package com.cristaline.cristal.service;

import com.cristaline.cristal.exception.UserNotFoundException;
import com.cristaline.cristal.model.User;
import com.cristaline.cristal.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) { this.userRepository = userRepository; }


    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public void register(String username, String email, String password)
    {
        if (!userRepository.existsByEmail(email))
        {
            // Sha-256 hash for the password
            MessageDigest digest = null;
            try {
                digest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            byte[] encodedPass = digest.digest(
                    password.getBytes(StandardCharsets.UTF_8));
            User user = new User(username, email, bytesToHex(encodedPass));
            userRepository.save(user);
        }
    }

    public void login(String username, String password)
    {
        if (userRepository.existsByUsername(username))
        {
            MessageDigest digest = null;
            try {
                digest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            byte[] encodedPass = digest.digest(
                    password.getBytes(StandardCharsets.UTF_8));
            User foundUser = userRepository.findUserByUsername(username).isPresent() ? userRepository.findUserByUsername(username).get() : null;
            if (foundUser != null && bytesToHex(encodedPass).matches(foundUser.getPassword()))
            {
                // login successful
            }
        }
        else
        {
            throw new UserNotFoundException("User does not exist.");
        }
    }
}
