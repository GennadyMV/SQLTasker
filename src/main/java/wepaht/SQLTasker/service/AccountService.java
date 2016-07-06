package wepaht.SQLTasker.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wepaht.SQLTasker.domain.Account;
import wepaht.SQLTasker.domain.AuthenticationToken;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.domain.TmcAccount;
import static wepaht.SQLTasker.library.ConstantString.*;
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getPrincipal().toString();

        if (!auth.isAuthenticated() || username.equals("anonymousUser")) {
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
            redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACCESS);
            return REDIRECT_DEFAULT;
        }

        model.addAttribute(ATTRIBUTE_STUDENTS, tmcRepo.findByAccountRoleAndDeletedFalse(ROLE_STUDENT));
        model.addAttribute(ATTRIBUTE_TEACHERS, tmcRepo.findByAccountRoleAndDeletedFalse(ROLE_TEACHER));
        model.addAttribute(ATTRIBUTE_ADMINS, tmcRepo.findByAccountRoleAndDeletedFalse(ROLE_ADMIN));

        return VIEW_USERS;
    }

    public String getUser(Model model, RedirectAttributes redirAttr, Long id) {
        TmcAccount user = getAuthenticatedUser();

        if (user.getRole().equals(ROLE_STUDENT)) {
            redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACCESS);
            return REDIRECT_DEFAULT;
        }

        TmcAccount fetched = getUserById(id);
        model.addAttribute("editedUser", fetched);

        if (user.getRole().equals(ROLE_ADMIN)) {
            model.addAttribute(ATTRIBUTE_ROLES, Arrays.asList(ROLE_STUDENT, ROLE_TEACHER, ROLE_ADMIN));
        }
        if (user.getRole().equals(ROLE_TEACHER)) {
            model.addAttribute(ATTRIBUTE_ROLES, Arrays.asList(ROLE_STUDENT, ROLE_TEACHER));
        }

        return VIEW_USER;
    }

    public TmcAccount getUserById(Long accountId) {
        return tmcRepo.findOne(accountId);
    }

    public Boolean isUserStudent() {
        TmcAccount user = getAuthenticatedUser();

        if (user != null) {
            return user.getRole().equals(ROLE_STUDENT);
        }

        return true;
    }

    public String editUser(RedirectAttributes redirAttr, Long accountId, String role) {
        List<String> messages = new ArrayList<>();
        TmcAccount user = getAuthenticatedUser();
        TmcAccount fetched = getUserById(accountId);

        if (user.getRole().equals(ROLE_STUDENT)) {
            redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACCESS);
            return REDIRECT_DEFAULT;
        }

        if (hasLowerAuthority(user, fetched)) {
            messages.add(MESSAGE_UNAUTHORIZED_ACTION + ": editing admin is not allowed");
        } else {
            fetched.setRole(role);
            tmcRepo.save(fetched);
            messages.add(MESSAGE_SUCCESSFUL_ACTION + " for user " + fetched.getUsername());
        }

        redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, messages);

        return REDIRECT_DEFAULT;
    }

    private boolean hasLowerAuthority(TmcAccount user, TmcAccount fetched) {
        return user.getRole().equals(ROLE_TEACHER) && fetched.getRole().equals(ROLE_ADMIN);
    }

    public String getProfile(Model model) {
        TmcAccount user = getAuthenticatedUser();

        if (user.getRole().equals(ROLE_ADMIN)) {
            model.addAttribute(ATTRIBUTE_ROLES, Arrays.asList(ROLE_STUDENT, ROLE_TEACHER));
        } else if (user.getRole().equals(ROLE_TEACHER)) {
            model.addAttribute(ATTRIBUTE_ROLES, ROLE_STUDENT);
        }
        model.addAttribute(ATTRIBUTE_EDITEDUSER, user);
        model.addAttribute(ATTRIBUTE_TOKEN, getToken());

        return VIEW_USER;
    }

    public String deleteAccount(RedirectAttributes redirAttr, Long accountId) {
        TmcAccount user = getAuthenticatedUser();
        TmcAccount deleting = getUserById(accountId);

        if (user.equals(deleting) || user.getRole().equals(ROLE_TEACHER) || user.getRole().equals(ROLE_ADMIN)) {
            deleting.setDeleted(Boolean.TRUE);
            tmcRepo.save(deleting);

            if (user.equals(deleting)) {
                customLogout();
            }

            redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_SUCCESSFUL_ACTION + ": user " + deleting.getUsername() + " deleted");
        } else {
            redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACTION);
        }

        return REDIRECT_DEFAULT;
    }

    @Transactional
    void addUserToCourse(Course course) {
        TmcAccount user = getAuthenticatedUser();
        if (course.getStudents() == null) {
            course.setStudents(new ArrayList<>());
        }
        if (!course.getStudents().contains(user)) {
            course.getStudents().add(user);
        }
    }

    @Transactional
    void removeUserFromCourse(Course course) {
        TmcAccount student = getAuthenticatedUser();
        if (course.getStudents() != null && course.getStudents().contains(student)) {
            course.getStudents().remove(student);
        }        
    }
}
