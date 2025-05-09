package com.smartcontact.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smartcontact.entities.User;
import com.smartcontact.repository.UserRepository;



public class CustomeUserDetailsService implements UserDetailsService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // fetching user details from database
        User user = userRepository.getUserByUserName(username);
        if(user == null){
            throw new UsernameNotFoundException("user could not found!!");
        }
        CustomUserDetails userDetails = new CustomUserDetails(user);
        return userDetails;
    }
    
}
