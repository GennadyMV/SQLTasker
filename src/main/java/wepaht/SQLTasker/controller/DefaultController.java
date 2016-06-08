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
import wepaht.SQLTasker.repository.AccountRepository;
import wepaht.SQLTasker.service.AccountService;
import wepaht.SQLTasker.service.SubmissionService;

@Controller
public class DefaultController {

    @Autowired
    AccountService userService;
    
    @Autowired
    AccountRepository userRepository;
    
    @Autowired
    SubmissionService submissionService;
    
    @RequestMapping(value="/", method=RequestMethod.GET)
    public String hello(Model model){
        
        model.addAttribute("user", userService.getAuthenticatedUser());
        model.addAttribute("submissions", submissionService.listAllSubmissions());
        
        return "index";
    }
}
