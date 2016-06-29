package wepaht.SQLTasker.service;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wepaht.SQLTasker.domain.AuthenticationToken;
import wepaht.SQLTasker.domain.TmcAccount;
import static wepaht.SQLTasker.library.StringLibrary.*;
import wepaht.SQLTasker.repository.AuthenticationTokenRepository;
import wepaht.SQLTasker.repository.TmcAccountRepository;


@Service
public class AccountService {
    
    @Autowired
    private TmcAccountRepository tmcRepo;

    @Autowired
    private AuthenticationTokenRepository tokenRepository;
    
    @Autowired
    private PointService pointService;

    public TmcAccount getAuthenticatedUser() {
        Authentication auth = null;
        try {
            auth = SecurityContextHolder.getContext().getAuthentication();
        } catch (Exception e) {
            return null;
        }
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

    public String listAllAccounts(Model model, RedirectAttributes redirAttr) {
        if (isUserStudent()) {
            redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED);
            return REDIRECT_DEFAULT;
        }
        
        model.addAttribute(ATTRIBUTE_STUDENTS, tmcRepo.findByAccountRoleAndDeletedFalse(ROLE_STUDENT));
        model.addAttribute(ATTRIBUTE_TEACHERS, tmcRepo.findByAccountRoleAndDeletedFalse(ROLE_TEACHER));
        model.addAttribute(ATTRIBUTE_ADMINS, tmcRepo.findByAccountRoleAndDeletedFalse(ROLE_ADMIN));
        
        return VIEW_USERS;
    }

    public String getUser(Model model, RedirectAttributes redirAttr, Long id) {
        if (isUserStudent()) {
            redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED);
            return REDIRECT_DEFAULT;
        }
        
        model.addAttribute("editedUser", getUserById(id));
        model.addAttribute("roles", Arrays.asList(ROLE_STUDENT, ROLE_TEACHER, ROLE_ADMIN));
        
        return VIEW_USER;
    }
    
    public TmcAccount getUserById(Long accountId) {
        return tmcRepo.findOne(accountId);
    }
    
    public Boolean isUserStudent() {
        TmcAccount user = getAuthenticatedUser();
        
        if (user != null) return user.getRole().equals(ROLE_STUDENT);
        
        return true;
    }
}
