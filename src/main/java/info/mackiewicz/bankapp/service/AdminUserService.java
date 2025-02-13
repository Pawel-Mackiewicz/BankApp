package info.mackiewicz.bankapp.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.repository.AdminUserRepository;

@Service("adminUserDetailsService")
public class AdminUserService implements UserDetailsService {
    
    private final AdminUserRepository adminUserRepository;
    
    public AdminUserService(AdminUserRepository adminUserRepository) {
        this.adminUserRepository = adminUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return adminUserRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Admin user not found: " + username));
    }
}
