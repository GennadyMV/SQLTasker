package wepaht.SQLTasker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wepaht.SQLTasker.service.TaskFeedbackService;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {
    
    @Autowired
    private TaskFeedbackService feedbackService;
    
    @RequestMapping(method = RequestMethod.GET)
    public String getAllFeedback(Model model, RedirectAttributes redirAttr) {
        return feedbackService.getAllFeedback(model, redirAttr);
    }
}
