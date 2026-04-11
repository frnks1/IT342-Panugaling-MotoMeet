package edu.cit.panugaling.motomeet.service;

import edu.cit.panugaling.motomeet.model.User;
import edu.cit.panugaling.motomeet.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CurrentUserService {
    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return Optional.empty();
        }

        return userRepository.findByEmail(authentication.getName());
    }

    public User getRequiredUser(Authentication authentication) {
        return findUser(authentication)
                .orElseThrow(() -> new AuthenticatedUserMissingException("Authenticated user is missing in database."));
    }
}
