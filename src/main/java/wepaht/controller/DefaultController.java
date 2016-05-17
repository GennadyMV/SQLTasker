/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wepaht.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import wepaht.domain.Account;
import wepaht.repository.UserRepository;
import wepaht.service.UserService;

@Controller
public class DefaultController {

    @Autowired
    UserService userService;
    
    @Autowired
    UserRepository userRepository;
    
    @RequestMapping(value="/", method=RequestMethod.GET)
    public String hello(Model model){
        
        model.addAttribute("user", userService.getAuthenticatedUser());
        
        return "index";
    }
}
