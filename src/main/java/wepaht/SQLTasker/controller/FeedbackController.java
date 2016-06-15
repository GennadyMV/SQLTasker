package wepaht.SQLTasker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import wepaht.SQLTasker.service.TaskFeedbackService;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {
    
    @Autowired
    private TaskFeedbackService feedbackService;
    
    @RequestMapping(method = RequestMethod.GET)
    public String getAllFeedback(Model model) {
        return feedbackService.getAllFeedback(model);
    }
}
