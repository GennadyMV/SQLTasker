package wepaht.SQLTasker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wepaht.SQLTasker.repository.TaskRepository;
import wepaht.SQLTasker.repository.LocalAccountRepository;
import wepaht.SQLTasker.service.PastQueryService;
import wepaht.SQLTasker.service.AccountService;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Controller
@RequestMapping("queries")
public class PastQueryController {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    PastQueryService pastQueryService;

    @Autowired
    AccountService userService;

    @Autowired
    LocalAccountRepository userRepository;

    @RequestMapping(method = RequestMethod.GET)
    public String getPage(Model model) {
        model.addAttribute("queries", taskRepository.findAll());
        model.addAttribute("users", userRepository.findAll());
        return "query";
    }

    @Transactional
    @RequestMapping(method = RequestMethod.POST)
    public String getPastQuery(RedirectAttributes redirectAttributes,
            @RequestParam(required = false) Long taskId,
            @RequestParam String username,
            @RequestParam String isCorrect) {

        List pastQueries = pastQueryService.returnQuery(username, taskId, isCorrect);
        if (pastQueries.isEmpty()) {
            redirectAttributes.addFlashAttribute("messages", "No queries!");
        } else {
            redirectAttributes.addFlashAttribute("messages", "Here are queries:");

        }
        redirectAttributes.addFlashAttribute("pastQueries", pastQueries);
        return "redirect:/queries";
    }

    @RequestMapping(value = "/student", method = RequestMethod.POST)
    public String getPastQueryByUsername(RedirectAttributes redirectAttributes) {
        String loggedUsername = userService.getAuthenticatedUser().getUsername();
        List pastQueries = pastQueryService.returnQueryOnlyByUsername(loggedUsername);

        if (pastQueries.isEmpty()) {
            redirectAttributes.addFlashAttribute("messages", "You have no past queries!");
        } else {
            redirectAttributes.addFlashAttribute("messages", "Here are your queries:");

        }
        redirectAttributes.addFlashAttribute("pastQueries", pastQueries);
        return "redirect:/queries";
    }

}
