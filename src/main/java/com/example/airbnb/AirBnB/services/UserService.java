package com.example.airbnb.AirBnB.services;

import com.example.airbnb.AirBnB.DTOs.ProfileUpdateRequestDTO;
import com.example.airbnb.AirBnB.DTOs.UserDTO;
import com.example.airbnb.AirBnB.entities.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService {

    User getUserById(Long id);

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    void updateProfile(ProfileUpdateRequestDTO profileUpdateRequestDTO);

    UserDTO getMyProfile();
}
