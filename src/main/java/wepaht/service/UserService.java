package wepaht.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import wepaht.domain.AuthenticationToken;
import wepaht.domain.User;
import wepaht.repository.AuthenticationTokenRepository;
import wepaht.repository.UserRepository;

import javax.transaction.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationTokenRepository tokenRepository;

    public User getAuthenticatedUser() {
        //return userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        return null;
    }

    public void customLogout() {
        SecurityContextHolder.clearContext();
    }

    public void createToken() {
        User user = getAuthenticatedUser();
        AuthenticationToken token = tokenRepository.findByUser(user);

        if (token == null) {
            token = new AuthenticationToken();
            token.setUser(user);
            token.setToken("");
            tokenRepository.save(token);
        } else {
            token.setToken("");
            tokenRepository.save(token);
        }
    }

    public AuthenticationToken getToken() {
        User user = getAuthenticatedUser();
        if (user.getRole().equals("TEACHER") || user.getRole().equals("ADMIN")) {
            return tokenRepository.findByUser(user);
        }
        return null;
    }
}
