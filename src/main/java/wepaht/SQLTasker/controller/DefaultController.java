/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wepaht.SQLTasker.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import static wepaht.SQLTasker.constant.ConstantString.ATTRIBUTE_MESSAGES;
import static wepaht.SQLTasker.constant.ConstantString.MESSAGE_UNAUTHORIZED_ACCESS;
import static wepaht.SQLTasker.constant.ConstantString.REDIRECT_DEFAULT;
import wepaht.SQLTasker.domain.Submission;
import wepaht.SQLTasker.service.AccountService;
import wepaht.SQLTasker.service.CourseService;
import wepaht.SQLTasker.service.SampleCourseService;
import wepaht.SQLTasker.service.SubmissionService;
import wrapper.SubmissionSearchWrapper;

@Controller
public class DefaultController {
    
    @Autowired
    CourseService courseService;
    
    @Autowired
    SubmissionService subService;
    
    @Autowired
    SampleCourseService sampleService;
    
    @Autowired
    AccountService accountService;
    
    @RequestMapping(value="/", method=RequestMethod.GET)
    public String hello(Model model){
        courseService.getCourses(model);
        
        return "index";
    }
    
    @RequestMapping(value = "/submissions", method = RequestMethod.GET)
    public String getSubmissions(RedirectAttributes redirAttr) {
        return "redirect:/submissions/0";
    }
    
    @RequestMapping(value = "/submissions/{page}", method = RequestMethod.GET)
    public String getSubmissionsPage(Model model, RedirectAttributes redirAttr, 
            @ModelAttribute("wrapper") SubmissionSearchWrapper wrapper,
            @PathVariable Long page) {
        if (accountService.isUserStudent()) {

            redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACCESS);
            return REDIRECT_DEFAULT;
        }

        Page<Submission> pages = subService.getSubmissions(wrapper, page);
        model.addAttribute("prevPage", subService.getPreviousPage(page));
        model.addAttribute("pageCount", pages.getTotalPages());
        model.addAttribute("nextPage", subService.getNextPage(page, pages));
        model.addAttribute("searchWrapper", subService.getWrapper(wrapper));
        model.addAttribute("submissions", subService.getSubmissions(wrapper, page));
        model.addAttribute("currentPage", page);
        return "submissions";
    }
    
    @RequestMapping(value = "/admin/coursetemplate")
    public String postCourseTemplate(RedirectAttributes redirAttr) {
        if (sampleService.initCourse()) {
            redirAttr.addFlashAttribute("messages", "Template course successfully created");
        } else {
            redirAttr.addFlashAttribute("messages", "Action failed");
        }
        return "redirect:/courses";
    }
}
