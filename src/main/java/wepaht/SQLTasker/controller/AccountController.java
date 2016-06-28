package wepaht.SQLTasker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wepaht.SQLTasker.domain.LocalAccount;
import wepaht.SQLTasker.repository.LocalAccountRepository;
import wepaht.SQLTasker.service.PastQueryService;
import wepaht.SQLTasker.service.PointService;
import wepaht.SQLTasker.service.AccountService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import wepaht.SQLTasker.domain.Account;
import wepaht.SQLTasker.domain.TmcAccount;

@Controller
public class AccountController {

    private String[] roles = {"STUDENT", "TEACHER", "ADMIN"};

    @Autowired
    LocalAccountRepository userRepository;

    @Autowired
    AccountService userService;
    
    @Autowired
    PointService pointService;

    @Autowired
    PastQueryService pastQueryService;

    @RequestMapping(value = "users", method = RequestMethod.GET)
    public String list(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("roles", roles);
        model.addAttribute("students", userRepository.findByAccountRoleAndDeletedFalse("STUDENT"));
        model.addAttribute("teachers", userRepository.findByAccountRoleAndDeletedFalse("TEACHER"));
        model.addAttribute("admins", userRepository.findByAccountRoleAndDeletedFalse("ADMIN"));
        return "users";
    }

    @RequestMapping(value = "users/{id}", method = RequestMethod.GET)
    public String getUser(Model model, @PathVariable Long id) {
        LocalAccount editedUser = userRepository.findOne(id);
        model.addAttribute("editedUser", editedUser);
        model.addAttribute("roles", roles);
        model.addAttribute("points", pointService.getPointsByUsername(editedUser.getUsername()));
        return "user";
    }

    @RequestMapping(value = "profile", method = RequestMethod.GET)
    public String getProfile(Model model) {
        Account user = userService.getAuthenticatedUser();
        model.addAttribute("editedUser", user);
        model.addAttribute("roles", roles);
        model.addAttribute("user", user);
        model.addAttribute("points", pointService.getPointsByUsername(user.getUsername()));
        model.addAttribute("token", userService.getToken());

        return "user";
    }

    @Transactional
    @RequestMapping(value = "users/{id}/edit", method = RequestMethod.POST)
    public String update(@PathVariable Long id, RedirectAttributes redirectAttributes,
            @RequestParam(required = false) String role) {

        String redirectAddress = "redirect:/";
        List<String> messages = new ArrayList<>();

        TmcAccount loggedUser = userService.getAuthenticatedUser();
        LocalAccount userToBeEdited = userRepository.getOne(id);

        String loggedRole = loggedUser.getRole();

        if (loggedRole.equals("ADMIN") || loggedUser.getId().equals(userToBeEdited.getId())) {
            if(loggedUser.getRole().equals("ADMIN") && loggedUser.getId().equals(userToBeEdited.getId()) && !loggedUser.getRole().equals(role)){
                redirectAttributes.addFlashAttribute("messages", "Admins cannot demote themselves");
            }

            if (loggedUser.getRole().equals("ADMIN")) {
                userToBeEdited.setRole(role);
            }

            messages.add("User modified!");
        } else {
            messages.add("User modification failed!");
        }

        redirectAttributes.addFlashAttribute("messages", messages);
        return redirectAddress;
    }

    @RequestMapping(value = "register", method = RequestMethod.GET)
    public String viewRegister(Model model) {
        model.addAttribute("user", userService.getAuthenticatedUser());
        return "register";
    }

    @RequestMapping(value = "profile/token", method = RequestMethod.POST)
    public String createToken(RedirectAttributes redirectAttributes) {
        userService.createToken();
        redirectAttributes.addFlashAttribute("messages", "New token created successfully!");
        return "redirect:/profile";
    }
}
