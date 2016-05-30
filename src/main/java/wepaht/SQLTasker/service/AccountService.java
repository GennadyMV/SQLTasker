package wepaht.SQLTasker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import wepaht.SQLTasker.domain.AuthenticationToken;
import wepaht.SQLTasker.domain.Account;
import wepaht.SQLTasker.repository.AuthenticationTokenRepository;
import wepaht.SQLTasker.repository.AccountRepository;


@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AuthenticationTokenRepository tokenRepository;

    public Account getAuthenticatedUser() {
        return accountRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    public void customLogout() {
        SecurityContextHolder.clearContext();
    }

    public void createToken() {
        Account user = getAuthenticatedUser();
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
        Account user = getAuthenticatedUser();
        if (user.getRole().equals("TEACHER") || user.getRole().equals("ADMIN")) {
            return tokenRepository.findByUser(user);
        }
        return null;
    }
}
