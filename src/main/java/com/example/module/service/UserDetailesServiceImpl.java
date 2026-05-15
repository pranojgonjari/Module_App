package com.example.module.service;

/**
 * DEPRECATED: This class has been replaced by CustomUserDetailsService.java
 *
 * This file used to have a typo in the interface name (UserDetailesService instead of UserDetailsService).
 * All UserDetailsService implementation has been moved to:
 * @see com.example.module.security.CustomUserDetailsService
 *
 * CustomUserDetailsService provides the complete implementation of:
 * - Loading users from MongoDB
 * - Converting user roles to Spring Security authorities
 * - Handling UsernameNotFoundException
 */
public class UserDetailesServiceImpl {
    // This class is deprecated - use CustomUserDetailsService instead
}
