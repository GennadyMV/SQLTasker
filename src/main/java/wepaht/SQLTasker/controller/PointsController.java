package wepaht.SQLTasker.controller;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wepaht.SQLTasker.domain.Table;
import wepaht.SQLTasker.service.PointService;
import wepaht.SQLTasker.service.UserService;

@Controller
@RequestMapping("points")
public class PointsController {

    @Autowired
    PointService pointService;
    
    @Autowired
    UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public String listPoints(Model model, RedirectAttributes redirectAttributes) {
        if(userService.getAuthenticatedUser().getRole().equals("STUDENT")){
            return "redirect:/points/student";
        }
        
        Table pointsTable = pointService.getAllPoints();
        if (!pointsTable.getRows().isEmpty()) {
            Map<String, Table> tables = new HashMap<>();
            tables.put("Points of all users", pointsTable);
            model.addAttribute("tables", tables);
            return "points";
        }
        model.addAttribute("messages", "No points available.");
        return "points";
    } 
    
    @Transactional
    @RequestMapping(value="/student",method = RequestMethod.GET)
    public String listPointsAndExercises(Model model, RedirectAttributes redirectAttributes) {
        String username = userService.getAuthenticatedUser().getUsername();
        if (!pointService.getExercisesAndAwardedByUsername(username).getRows().isEmpty()) {
            Table pointsTable = pointService.getExercisesAndAwardedByUsername(username);
            Map<String, Table> tables = new HashMap<>();
            tables.put("Points from exercises:", pointsTable);
            model.addAttribute("tables", tables);
            return "points";
        }
        model.addAttribute("messages", "No points available.");
        return "points";
    }   
}
