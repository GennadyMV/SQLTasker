/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wepaht.SQLTasker.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wepaht.SQLTasker.service.CourseService;

/**
 *
 * @author mcsieni
 */
@Controller
@RequestMapping("courses")
public class CourseController {
    
    @Autowired
    private CourseService courseService;
    
    @RequestMapping(method = RequestMethod.GET)
    public String getCourses(Model model) {
        return courseService.courseListing(model);
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String createCourse(RedirectAttributes redirectAttributes,
            @RequestParam String name,
            @RequestParam(required = false) String starts,
            @RequestParam(required = false) String expires,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) List<Long> categoryIds) {
        
        return courseService.createCourse(redirectAttributes, name, starts, expires, description, categoryIds);
    }
}
