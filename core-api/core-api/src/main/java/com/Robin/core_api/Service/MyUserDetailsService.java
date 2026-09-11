package com.Robin.core_api.Service;

import com.Robin.core_api.Exception.ResourceNotFoundException;
import com.Robin.core_api.Model.Entity.User;
import com.Robin.core_api.Model.UserPrincipal;
import com.Robin.core_api.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService  implements UserDetailsService {
    @Autowired
    private UserRepo repo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user =  repo.findByEmail(username).orElseThrow(()-> new ResourceNotFoundException("User not Found with email:" + username));
        return new UserPrincipal(user);
    }
}
