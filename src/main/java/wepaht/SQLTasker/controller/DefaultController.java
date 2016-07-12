/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wepaht.SQLTasker.controller;

import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import wepaht.SQLTasker.domain.Account;
import static wepaht.SQLTasker.constant.ConstantString.ROLE_STUDENT;
import wepaht.SQLTasker.repository.LocalAccountRepository;
import wepaht.SQLTasker.service.AccountService;
import wepaht.SQLTasker.service.CourseService;
import wepaht.SQLTasker.service.SubmissionService;
import wepaht.SQLTasker.service.TaskFeedbackService;

@Controller
public class DefaultController {
    
    @Autowired
    CourseService courseService;
    
    @RequestMapping(value="/", method=RequestMethod.GET)
    public String hello(Model model){
        courseService.getCourses(model);
        
        return "index";
    }
}
