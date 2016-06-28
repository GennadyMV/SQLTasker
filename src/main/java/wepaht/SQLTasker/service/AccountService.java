package wepaht.SQLTasker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import wepaht.SQLTasker.domain.Account;
import wepaht.SQLTasker.domain.AuthenticationToken;
import wepaht.SQLTasker.domain.LocalAccount;
import wepaht.SQLTasker.domain.TmcAccount;
import wepaht.SQLTasker.repository.AuthenticationTokenRepository;
import wepaht.SQLTasker.repository.LocalAccountRepository;
import wepaht.SQLTasker.repository.TmcAccountRepository;


@Service
public class AccountService {

    @Autowired
    private LocalAccountRepository accountRepository;
    
    @Autowired
    private TmcAccountRepository tmcRepo;

    @Autowired
    private AuthenticationTokenRepository tokenRepository;

    public TmcAccount getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        TmcAccount user = tmcRepo.findByUsernameAndDeletedFalse(auth.getPrincipal().toString());
        if (user == null) {
            user = new TmcAccount();
            user.setUsername(auth.getPrincipal().toString());
            user.setRole("STUDENT");
            user = tmcRepo.save((TmcAccount) user);
        }
        
        return user;
    }

    public void customLogout() {
        SecurityContextHolder.clearContext();
    }

    public void createToken() {
        TmcAccount user = getAuthenticatedUser();
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
        TmcAccount user = getAuthenticatedUser();
        if (user.getRole().equals("TEACHER") || user.getRole().equals("ADMIN")) {
            return tokenRepository.findByUser(user);
        }
        return null;
    }

    TmcAccount getAccountByUsername(String username) {
        return tmcRepo.findByUsernameAndDeletedFalse(username);
    }
}
