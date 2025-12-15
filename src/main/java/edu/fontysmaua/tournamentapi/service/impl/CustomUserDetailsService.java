package edu.fontysmaua.tournamentapi.service.impl;

import edu.fontysmaua.tournamentapi.security.UserPrincipal;
import edu.fontysmaua.tournamentapi.domain.User;
import edu.fontysmaua.tournamentapi.mapper.UserMapper;
import edu.fontysmaua.tournamentapi.persistence.UserRepository;
import edu.fontysmaua.tournamentapi.persistence.entity.UserEntity;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

        if (userEntity == null) {
            throw new UsernameNotFoundException(email);
        }

        User user = userMapper.entityToUser(userEntity);
        return new UserPrincipal(user);
    }
}
