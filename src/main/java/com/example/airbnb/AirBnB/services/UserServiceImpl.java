package com.example.airbnb.AirBnB.services;

import com.example.airbnb.AirBnB.DTOs.ProfileUpdateRequestDTO;
import com.example.airbnb.AirBnB.DTOs.UserDTO;
import com.example.airbnb.AirBnB.entities.User;
import com.example.airbnb.AirBnB.exceptions.ResourceNotFoundException;
import com.example.airbnb.AirBnB.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.example.airbnb.utils.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: "+id));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElse(null);
    }

    @Override
    public void updateProfile(ProfileUpdateRequestDTO profileUpdateRequestDTO) {
        User user = getCurrentUser();

        if(profileUpdateRequestDTO.getDateOfBirth() != null) user.setDateOfBirth(profileUpdateRequestDTO.getDateOfBirth());
        if(profileUpdateRequestDTO.getGender() != null) user.setGender(profileUpdateRequestDTO.getGender());
        if (profileUpdateRequestDTO.getName() != null) user.setName(profileUpdateRequestDTO.getName());

        userRepository.save(user);
    }

    @Override
    public UserDTO getMyProfile() {
        User user = getCurrentUser();
        log.info("Getting the profile for user with id: {}", user.getId());
        return modelMapper.map(user, UserDTO.class);
    }
}
