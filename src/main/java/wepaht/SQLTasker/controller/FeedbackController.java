package wepaht.SQLTasker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import static wepaht.SQLTasker.constant.ConstantString.ATTRIBUTE_MESSAGES;
import static wepaht.SQLTasker.constant.ConstantString.MESSAGE_UNAUTHORIZED_ACCESS;
import static wepaht.SQLTasker.constant.ConstantString.REDIRECT_DEFAULT;
import static wepaht.SQLTasker.constant.ConstantString.ROLE_STUDENT;
import wepaht.SQLTasker.domain.Account;
import wepaht.SQLTasker.domain.TaskFeedback;
import wepaht.SQLTasker.service.AccountService;
import wepaht.SQLTasker.service.TaskFeedbackService;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {
    
    @Autowired
    private TaskFeedbackService feedbackService;
    
    @Autowired
    private AccountService accountService;
    
    @RequestMapping(method = RequestMethod.GET)
    public String getFeedback() {
        return "redirect:/feedback/0";
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/{page}")
    public String getFeedbackPage(Model model, RedirectAttributes redirAttr, @PathVariable Long page) {
        Account user = accountService.getAuthenticatedUser();
        if (user.getRole().equals(ROLE_STUDENT)) {
            redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACCESS);
            return REDIRECT_DEFAULT;
        }
        
        Page<TaskFeedback> pages = feedbackService.listAllFeedbackPaged(page);
        model.addAttribute("currentPage", page);
        model.addAttribute("feedback", pages);
        model.addAttribute("nextPage", feedbackService.getNextPage(page, pages));
        model.addAttribute("prevPage", feedbackService.getPreviousPage(page, pages));
        model.addAttribute("pageCount", pages.getTotalPages());
        
        return "feedbackList";
    }
}
