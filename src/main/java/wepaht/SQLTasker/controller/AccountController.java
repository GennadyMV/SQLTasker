package wepaht.SQLTasker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wepaht.SQLTasker.domain.Account;
import wepaht.SQLTasker.repository.AccountRepository;
import wepaht.SQLTasker.service.PastQueryService;
import wepaht.SQLTasker.service.PointService;
import wepaht.SQLTasker.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AccountController {

    private String[] roles = {"STUDENT", "TEACHER", "ADMIN"};

    @Autowired
    AccountRepository userRepository;

    @Autowired
    UserService userService;
    
    @Autowired
    PointService pointService;

    @Autowired
    PastQueryService pastQueryService;

    @Secured("ROLE_TEACHER")
    @RequestMapping(value = "users", method = RequestMethod.GET)
    public String list(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("roles", roles);
        model.addAttribute("students", userRepository.findByRole("STUDENT"));
        model.addAttribute("teachers", userRepository.findByRole("TEACHER"));
        model.addAttribute("admins", userRepository.findByRole("ADMIN"));
        return "users";
    }

    @Secured("ROLE_TEACHER")
    @RequestMapping(value = "users/{id}", method = RequestMethod.GET)
    public String getUser(Model model, @PathVariable Long id) {
        Account editedUser = userRepository.findOne(id);
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

    @RequestMapping(value = "register", method = RequestMethod.POST)
    public String create(@Valid @ModelAttribute Account newUser, RedirectAttributes redirectAttributes, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addAttribute("messages", bindingResult.getAllErrors());
            return "redirect:/register";
        }

        if (newUser.getRole() == null) newUser.setRole("STUDENT");
        userRepository.save(newUser);
        redirectAttributes.addFlashAttribute("messages", "User created succesfully, please log in.");
        if (userService.getAuthenticatedUser() != null && userService.getAuthenticatedUser().getRole().equals("ADMIN")) {
            return "redirect:/users";
        }
        return "redirect:/";
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "users/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Account user = userService.getAuthenticatedUser();
        if (user.getId() == id) {
            redirectAttributes.addFlashAttribute("messages", "Admin users cannot delete themselves.");
            return "redirect:/users";
        }
        userRepository.delete(id);
        redirectAttributes.addFlashAttribute("messages", "User deleted.");

        return "redirect:/users";
    }


    @Transactional
    @RequestMapping(value = "users/{id}/edit", method = RequestMethod.POST)
    public String update(@PathVariable Long id, RedirectAttributes redirectAttributes,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String repassword) {

        String redirectAddress = "redirect:/";
        List<String> messages = new ArrayList<>();
        
        if (password != null && !password.equals(repassword)) {
            redirectAttributes.addFlashAttribute("messages", "Passwords didn't match");
            return redirectAddress;
        }

        Account loggedUser = userService.getAuthenticatedUser();
        Account userToBeEdited = userRepository.getOne(id);

        String loggedRole = loggedUser.getRole();

        if (loggedRole.equals("ADMIN") || loggedUser.getId().equals(userToBeEdited.getId())) {
            if(loggedUser.getRole().equals("ADMIN") && loggedUser.getId().equals(userToBeEdited.getId()) && !loggedUser.getRole().equals(role)){
                redirectAttributes.addFlashAttribute("messages", "Admins cannot demote themselves");
            }

            if (loggedUser.getRole().equals("ADMIN")) {
                userToBeEdited.setRole(role);
            }

            if (password != null || !password.isEmpty()) {
                userToBeEdited.setPassword(password);
            }

            if (!username.equals(userToBeEdited.getUsername()) && (username != null || !username.isEmpty())) {
                String oldUsername = userToBeEdited.getUsername();
                userToBeEdited.setUsername(username);
                pastQueryService.changeQueriesesUsernames(oldUsername, username);
                messages.add("Username modified. Please log in.");
                userService.customLogout();
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

    @Secured("ROLE_TEACHER")
    @RequestMapping(value = "profile/token", method = RequestMethod.POST)
    public String createToken(RedirectAttributes redirectAttributes) {
        userService.createToken();
        redirectAttributes.addFlashAttribute("messages", "New token created successfully!");
        return "redirect:/profile";
    }
}
