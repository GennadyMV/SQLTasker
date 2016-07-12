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
import static wepaht.SQLTasker.constant.ConstantString.*;

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
        return userService.getProfile(model);
    }

    @Transactional
    @RequestMapping(value = "users/{id}/edit", method = RequestMethod.POST)
    public String update(@PathVariable Long id, RedirectAttributes redirectAttributes,
            @RequestParam(required = false) String role) {
        return userService.editUser(redirectAttributes, id, role);
    }

    @RequestMapping(value = "profile/token", method = RequestMethod.POST)
    public String createToken(RedirectAttributes redirectAttributes) {
        if (!userService.isUserStudent()) {
            userService.createToken();
            redirectAttributes.addFlashAttribute("messages", "New token created successfully!");
        }
        return "redirect:/profile";
    }

    @RequestMapping(value = "users/{accountId}", method = RequestMethod.DELETE)
    public String delete(RedirectAttributes redirAttr, @PathVariable Long accountId) {

        return userService.deleteAccount(redirAttr, accountId);
    }
}
