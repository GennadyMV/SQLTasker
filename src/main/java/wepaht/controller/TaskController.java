/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wepaht.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wepaht.domain.*;
import wepaht.repository.DatabaseRepository;
import wepaht.repository.TaskRepository;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import javax.validation.Valid;

import wepaht.repository.TagRepository;
import wepaht.service.*;

@Controller
@RequestMapping("tasks")
public class TaskController {

    private Map<Long, String> queries;
    private String selectRegex = "select ([a-zA-Z0-9*_]+){1}(, [[a-zA-Z0-9*_]+)* from [[a-zA-Z0-9*_]+( where [[a-zA-Z0-9*_]+='[[a-zA-Z0-9*_]+')?";

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    DatabaseRepository databaseRepository;

    @Autowired
    DatabaseService databaseService;

    @Autowired
    TaskResultService taskResultService;

    @Autowired
    UserService userService;

    @Autowired
    PastQueryService pastQueryService;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    CategoryService categoryService;
    
    @PostConstruct
    public void init() {
        queries = new HashMap<>();
    }

    @RequestMapping(method = RequestMethod.GET)
    public String listTasks(Model model) {
        model.addAttribute("tasks", taskRepository.findAll());
        model.addAttribute("databases", databaseRepository.findAll());

        return "tasks";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String createTask(RedirectAttributes redirectAttributes,
                             @Valid @ModelAttribute Task task,
                             @RequestParam(required = false) Long databaseId,
                             BindingResult result) {

        if(databaseId==null){
            redirectAttributes.addFlashAttribute("messages", "You didn't chose database!");

            return "redirect:/tasks";

        }
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("messages", "Error!");

            return "redirect:/tasks";

        }
        if (task == null) {
            redirectAttributes.addFlashAttribute("messages", "Task creation has failed");
            return "redirect:/tasks";
        }

        Database db = databaseRepository.findOne(databaseId);
        task.setDatabase(db);
        task.setCategoryList(new ArrayList<Category>());
        if (task.getSolution() != null || !task.getSolution().isEmpty()) {
            if (!databaseService.isValidQuery(db, task.getSolution())) {
                redirectAttributes.addFlashAttribute("messages", "Task creation failed due to invalid solution");
                return "redirect:/tasks";
            }
        }

        User user = userService.getAuthenticatedUser();
        if (user.getRole().equals("STUDENT")||user.getRole().equals("TEACHER")) {
            task.setDescription(task.getDescription()+" SUGGESTED BY "+user.getUsername());
            task.setName("SUGGESTION: " + task.getName());
            taskRepository.save(task);
            redirectAttributes.addFlashAttribute("messages", "Task has been suggested");

            return "redirect:/tasks";
        }

        taskRepository.save(task);
        redirectAttributes.addFlashAttribute("messages", "Task has been created");

        return "redirect:/tasks";
    }

    @Secured("ROLE_TEACHER")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String getTask(@PathVariable Long id, Model model) throws Exception {
        Task task = taskRepository.findOne(id);

        if (queries.containsKey(id)) {

        } else {
            model.addAttribute("queryResults", new Table("dummy"));
        }
        List<Tag> tags = tagRepository.findByTaskId(id);
        model.addAttribute("tags", tags);        
        model.addAttribute("task", task);
        model.addAttribute("categoryId", 1);
        return "task";
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
    public String getTaskEditor(@PathVariable Long id, Model model) {
        List<Tag> tags = tagRepository.findByTaskId(id);
        model.addAttribute("tags", tags);
        model.addAttribute("task", taskRepository.findOne(id));
        model.addAttribute("databases", databaseRepository.findAll());
        model.addAttribute("user", userService.getAuthenticatedUser());

        return "editTask";
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String removeTask(@PathVariable Long id, RedirectAttributes redirectAttributes) throws Exception {

        categoryService.removeTaskFromCategory(taskRepository.findOne(id));
        taskRepository.delete(id);
        redirectAttributes.addFlashAttribute("messages", "Task deleted!");
        return "redirect:/tasks";
    }

    @Secured("ROLE_ADMIN")
    @Transactional
    @RequestMapping(value = "/{id}/edit", method = RequestMethod.POST)
    public String updateTask(@PathVariable Long id, RedirectAttributes redirectAttributes,
            @RequestParam Long databaseId,
            @RequestParam String name,
            @RequestParam String solution,
            @RequestParam String description) {
        if (solution != null || !solution.isEmpty()) {
            if (!databaseService.isValidQuery(databaseRepository.findOne(databaseId), solution)) {
                redirectAttributes.addFlashAttribute("messages", "Task creation failed due to invalid solution");
                return "redirect:/tasks";
            }
        }
        Task oldtask = taskRepository.getOne(id);
        oldtask.setDatabase(databaseRepository.findOne(databaseId));
        oldtask.setDescription(description);
        oldtask.setName(name);
        oldtask.setSolution(solution);

        redirectAttributes.addAttribute("id", id);
        redirectAttributes.addFlashAttribute("messages", "Task modified!");
        return "redirect:/tasks/{id}";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{categoryId}/{id}/query")
    public String sendQuery(RedirectAttributes redirectAttributes, @RequestParam(required = false, defaultValue = "") String query,
                            @PathVariable Long categoryId,
                            @PathVariable Long id) {
        Task task = taskRepository.findOne(id);

        queries.put(id, query);
        redirectAttributes.addFlashAttribute("messages", "Query sent.");

        if (task.getSolution() != null && taskResultService.evaluateSubmittedQueryStrictly(task, query)) {
            RedirectAttributes messages = redirectAttributes.addFlashAttribute("messages", "Your answer is correct!");
            pastQueryService.saveNewPastQuery(userService.getAuthenticatedUser().getUsername(), task.getId(), query, true, categoryId);
        } else {
            pastQueryService.saveNewPastQuery(userService.getAuthenticatedUser().getUsername(), task.getId(), query, false, categoryId);
        }

        Map<String, Table> queryResult = databaseService.performQuery(task.getDatabase().getId(), query);

        redirectAttributes.addAttribute("id", id);
        redirectAttributes.addFlashAttribute("tables", queryResult);
        return "redirect:/categories/{categoryId}/tasks/{id}";
    }
    
    @Secured("ROLE_TEACHER")
    @RequestMapping(value = "/{id}/tags", method = RequestMethod.POST)
    public String addTag(@PathVariable Long id, @RequestParam() String name,
            RedirectAttributes redirectAttributes) throws Exception {
        Tag tag = new Tag();
        tag.setName(name);
        tag.setTaskId(id);
        tagRepository.save(tag);
        redirectAttributes.addAttribute("id", id);
        redirectAttributes.addFlashAttribute("messages", "Tag added!");
        return "redirect:/tasks/{id}/edit";
    }
    
    @Secured("ROLE_TEACHER")
    @RequestMapping(value = "/{id}/tags", method = RequestMethod.DELETE)
    public String removeTag(@PathVariable Long id, @RequestParam() String name,
            RedirectAttributes redirectAttributes) throws Exception {
        Tag tag = tagRepository.findByNameAndTaskId(name, id);
        tagRepository.delete(tag);
        redirectAttributes.addAttribute("id", id);
        redirectAttributes.addFlashAttribute("messages", "Tag deleted!");
        return "redirect:/tasks/{id}/edit";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/suggest")
    public String getSuggestionPage(Model model) {
        model.addAttribute("databases", databaseRepository.findAll());
        return "tasks";
    }
}
