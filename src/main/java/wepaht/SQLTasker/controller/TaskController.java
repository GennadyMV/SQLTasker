/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wepaht.SQLTasker.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wepaht.SQLTasker.repository.CategoryRepository;
import wepaht.SQLTasker.repository.DatabaseRepository;
import wepaht.SQLTasker.repository.TaskRepository;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import javax.validation.Valid;
import wepaht.SQLTasker.domain.LocalAccount;
import wepaht.SQLTasker.domain.Database;
import wepaht.SQLTasker.domain.Table;
import wepaht.SQLTasker.domain.Tag;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.domain.TmcAccount;

import wepaht.SQLTasker.repository.TagRepository;
import wepaht.SQLTasker.service.CategoryService;
import wepaht.SQLTasker.service.DatabaseService;
import wepaht.SQLTasker.service.PastQueryService;
import wepaht.SQLTasker.service.TaskResultService;
import wepaht.SQLTasker.service.TaskService;
import wepaht.SQLTasker.service.AccountService;

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
    AccountService userService;

    @Autowired
    PastQueryService pastQueryService;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    CategoryService categoryService;

    @Autowired
    CategoryRepository categoryRepository;
    
    @Autowired
    TaskService taskService;

    @PostConstruct
    public void init() {
        queries = new HashMap();
    }

    @RequestMapping(method = RequestMethod.GET)
    public String listAll(Model model, RedirectAttributes redirAttr) {
        return taskService.listTasks(model, redirAttr);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String create(RedirectAttributes redirectAttributes,
                             @Valid @ModelAttribute Task task,
                             @RequestParam(required = false) Long databaseId,
                             @RequestParam(required= false) List<Long> categoryIds,
                             BindingResult result) {

        return taskService.createTask(redirectAttributes, task, databaseId, categoryIds, result);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String getOne(@PathVariable Long id, Model model) throws Exception {
        Task task = taskRepository.findOne(id);

        if (queries.containsKey(id)) {

        } else {
            model.addAttribute("queryResults", new Table("dummy"));
        }
        List<Tag> tags = tagRepository.findByTaskId(id);
        model.addAttribute("tags", tags);        
        model.addAttribute("task", task);
        model.addAttribute("database", task.getDatabase());

        return "task";
    }

    @RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
    public String getEditForm(@PathVariable Long id, Model model) {
        List<Tag> tags = tagRepository.findByTaskId(id);
        model.addAttribute("tags", tags);
        model.addAttribute("task", taskRepository.findOne(id));
        model.addAttribute("databases", databaseRepository.findAll());
        model.addAttribute("user", userService.getAuthenticatedUser());

        return "editTask";
    }

    @Secured("ROLE_ADMIN")
    @Transactional
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String removeTask(@PathVariable Long id, RedirectAttributes redirectAttributes) throws Exception {

        taskService.removeTask(id);
        
        redirectAttributes.addFlashAttribute("messages", "Task deleted!");
        return "redirect:/tasks";
    }

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
        
        List<Object> messagesAndQueryResult = taskService.performQueryToTask(new ArrayList<String>(), id, query, categoryId, null);
        
        redirectAttributes.addFlashAttribute("messages", messagesAndQueryResult.get(0));
        redirectAttributes.addAttribute("id", id);
        redirectAttributes.addFlashAttribute("tables", messagesAndQueryResult.get(1));
        return "redirect:/categories/{categoryId}/tasks/{id}";
    }
    
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
