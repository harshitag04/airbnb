package com.example.airbnb.AirBnB.security;

import com.example.airbnb.AirBnB.DTOs.LoginDTO;
import com.example.airbnb.AirBnB.DTOs.SignUpDTO;
import com.example.airbnb.AirBnB.DTOs.UserDTO;
import com.example.airbnb.AirBnB.entities.User;
import com.example.airbnb.AirBnB.enums.Roles;
import com.example.airbnb.AirBnB.exceptions.ResourceNotFoundException;
import com.example.airbnb.AirBnB.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public UserDTO signUp(SignUpDTO signupdto){

        User user = userRepository.findByEmail(signupdto.getEmail()).orElse(null);

        if(user != null){
            throw new RuntimeException("User is already present with same email id");
        }

        User newUser = modelMapper.map(signupdto, User.class);
        newUser.setRoles(Set.of(Roles.GUEST));
        newUser.setPassword(passwordEncoder.encode(signupdto.getPassword()));

        User savedUser = userRepository.save(newUser);
        return modelMapper.map(savedUser, UserDTO.class);
    }

    public String[] login(LoginDTO loginDTO){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));

        User user = (User)authentication.getPrincipal();
        String[] arr = new String[2];
        arr[0] = jwtService.generateAccessToken(user);
        arr[1] = jwtService.generateRefreshToken(user);

        return arr;
    }

    public String refreshToken(String refreshToken) {
        Long id = jwtService.getUserIdFromToken(refreshToken);

        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: "+id));
        return jwtService.generateAccessToken(user);
    }
}
