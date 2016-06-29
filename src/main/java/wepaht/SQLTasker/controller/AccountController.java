package wepaht.SQLTasker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wepaht.SQLTasker.domain.LocalAccount;
import wepaht.SQLTasker.repository.LocalAccountRepository;
import wepaht.SQLTasker.service.PastQueryService;
import wepaht.SQLTasker.service.PointService;
import wepaht.SQLTasker.service.AccountService;

import java.util.ArrayList;
import java.util.List;
import wepaht.SQLTasker.domain.Account;
import wepaht.SQLTasker.domain.TmcAccount;
import static wepaht.SQLTasker.library.StringLibrary.*;

@Controller
public class AccountController {

    private String[] roles = {"STUDENT", "TEACHER", "ADMIN"};

    @Autowired
    AccountService userService;
    
    @Autowired
    PointService pointService;

    @Autowired
    PastQueryService pastQueryService;

    @RequestMapping(value = "users", method = RequestMethod.GET)
    public String list(Model model, RedirectAttributes redirAttr) {
        return userService.listAllAccounts(model, redirAttr);
    }

    @RequestMapping(value = "users/{id}", method = RequestMethod.GET)
    public String getUser(Model model, RedirectAttributes redirAttr, @PathVariable Long id) {
        return userService.getUser(model, redirAttr, id);
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
