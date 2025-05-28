package com.roomate.app.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface CustomerUserDetailsSer {
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;
}
