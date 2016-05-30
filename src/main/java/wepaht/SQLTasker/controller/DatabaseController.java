package wepaht.SQLTasker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wepaht.SQLTasker.domain.Database;
import wepaht.SQLTasker.domain.Table;
import wepaht.SQLTasker.repository.DatabaseRepository;
import wepaht.SQLTasker.service.DatabaseService;
import wepaht.SQLTasker.service.AccountService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("databases")
public class DatabaseController {

    @Autowired
    DatabaseService databaseService;

    @Autowired
    DatabaseRepository databaseRepository;

    @Autowired
    AccountService userService;

    @RequestMapping(method = RequestMethod.GET)
    public String listDatabases(Model model) {
        List<Database> databases = databaseRepository.findAll();
        model.addAttribute("user", userService.getAuthenticatedUser());
        model.addAttribute("databases", databases);

        return "databases";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String viewDatabase(Model model, @PathVariable Long id) throws Exception {
        Database database = databaseRepository.findOne(id);
        Map<String, Table> databaseTables = databaseService.performUpdateQuery(id, null);

        model.addAttribute("database", database);
        model.addAttribute("tables", databaseTables);

        return "database";
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String createDatabase(RedirectAttributes redirectAttributes, @ModelAttribute Database database) {
        if (databaseService.createDatabase(database.getName(), database.getDatabaseSchema())) {
            redirectAttributes.addFlashAttribute("messages", "Database created!");
        } else {
            redirectAttributes.addFlashAttribute("messages", "Database creation failed!");
        }

        return "redirect:/databases";
    }
}
