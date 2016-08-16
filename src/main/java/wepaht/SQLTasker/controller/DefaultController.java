/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wepaht.SQLTasker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wepaht.SQLTasker.service.CourseService;
import wepaht.SQLTasker.service.SampleCourseService;
import wepaht.SQLTasker.service.SubmissionService;

@Controller
public class DefaultController {
    
    @Autowired
    CourseService courseService;
    
    @Autowired
    SubmissionService subService;
    
    @Autowired
    SampleCourseService sampleService;
    
    @RequestMapping(value="/", method=RequestMethod.GET)
    public String hello(Model model){
        courseService.getCourses(model);
        
        return "index";
    }
    
    @RequestMapping(value = "/submissions", method = RequestMethod.GET)
    public String getSubmissions(Model model, RedirectAttributes redirAttr) {
        return subService.getSubmissions(model, redirAttr);
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
