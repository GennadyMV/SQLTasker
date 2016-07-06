package wepaht.SQLTasker.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.domain.LocalAccount;
import wepaht.SQLTasker.repository.CategoryRepository;
import wepaht.SQLTasker.repository.TaskRepository;
import wepaht.SQLTasker.service.AccountService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import wepaht.SQLTasker.service.CategoryService;
import wepaht.SQLTasker.service.DatabaseService;

@Controller
@RequestMapping("categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private AccountService userService;

    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private DatabaseService dbService;

    @RequestMapping(method = RequestMethod.GET)
    @Transactional
    public String listAll(Model model) {
        return categoryService.listAllCategories(model);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String create(
            RedirectAttributes redirectAttributes,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam(required = false) List<Long> taskIds) {

        List<Task> tasks = new ArrayList();
        if (taskIds != null) {
            taskIds.stream().forEach((taskId) -> {
                tasks.add(taskRepository.findOne(taskId));
            });
        }

        try {
            Category category = new Category();
            category.setName(name);
            category.setDescription(description);
            category.setOwner(userService.getAuthenticatedUser());
            categoryService.setCategoryToTasks(category, tasks);
            categoryRepository.save(category);
        } catch (Exception e) {
            return redirectMessage("Category creation failed!", redirectAttributes);
        }

        redirectAttributes.addFlashAttribute("messages", "Category has been created!");
        return "redirect:/categories";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String getOne(@PathVariable Long id,
            Model model) throws Exception {
        Category category = categoryRepository.findOne(id);
        List<Task> taskList = category.getTaskList();
        model.addAttribute("category", category);
        model.addAttribute("taskList", taskList);
        return "category";
    }

    @Transactional
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable Long id,
            RedirectAttributes redirectAttributes) throws Exception {
        
        return categoryService.deleteCategory(redirectAttributes, id);
    }

    @RequestMapping(value = "/{id}/edit", method = RequestMethod.POST)
    public String update(@PathVariable Long id, RedirectAttributes redirectAttributes,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam(required = false) List<Long> taskIds) {
        
        return categoryService.updateCategory(redirectAttributes, id, name, description, taskIds);
    }

    @RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
    public String getEditForm(@PathVariable Long id,
            Model model, RedirectAttributes redirAttr) {
        return categoryService.getEditForm(model, redirAttr, id);
    }

    @RequestMapping(value = "/{id}/tasks/{taskId}", method = RequestMethod.GET)
    public String getCategoryTask(@PathVariable Long id,
            @PathVariable Long taskId,
            Model model, RedirectAttributes redirectAttributes) {

        Category category = categoryRepository.findOne(id);
        LocalDate now = LocalDate.now();

        Task task = taskRepository.findOne(taskId);
        model.addAttribute("task", task);
        model.addAttribute("database", task.getDatabase());
        model.addAttribute("category", category);
        model.addAttribute("next", categoryService.getNextTask(category, task));
        return "task";
    }
    
    @RequestMapping(value = "/{categoryId}/tasks/create", method = RequestMethod.GET)
    public String getTaskForm(Model model, 
            RedirectAttributes redirAttr, 
            @PathVariable Long categoryId, 
            @ModelAttribute Task task) {
        return categoryService.getCreateTaskToCategoryForm(model, redirAttr, categoryId, task);
    }
    
    @RequestMapping(value="/{categoryId}/tasks", method = RequestMethod.POST)
    public String createTask(RedirectAttributes redirAttr, Model model, @PathVariable Long categoryId, @Valid @ModelAttribute Task task, BindingResult result) {
        if (result.hasErrors()) {
            return categoryService.getCreateTaskToCategoryForm(model, redirAttr, categoryId, task);
        }
        return categoryService.createTaskToCategory(redirAttr, model, categoryId, task);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public String reorderTasks(RedirectAttributes redirectAttributes,
            @PathVariable Long id,
            @RequestParam Long taskId) {

        Category category = categoryRepository.findOne(id);
        Task task = taskRepository.findOne(taskId);
        categoryService.setCategoryTaskFurther(category, task);

        redirectAttributes.addAttribute("categoryId", id);

        return categoryService.reorderTask(redirectAttributes, id, taskId);
    }

    private String redirectMessage(String message, RedirectAttributes ra) {
        ra.addFlashAttribute("messages", message);
        return "redirect:/categories/";
    }
}
