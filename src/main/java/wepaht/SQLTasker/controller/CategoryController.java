package wepaht.SQLTasker.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.domain.Account;
import wepaht.SQLTasker.repository.CategoryRepository;
import wepaht.SQLTasker.repository.TaskRepository;
import wepaht.SQLTasker.service.AccountService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import wepaht.SQLTasker.service.CategoryService;

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

    @RequestMapping(method = RequestMethod.GET)
    @Transactional
    public String getCategories(Model model) {
        Account user = userService.getAuthenticatedUser();

        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("tasks", taskRepository.findAll());
        return "categories";
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(method = RequestMethod.POST)
    public String createCategory(
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
    public String getCategory(@PathVariable Long id,
            Model model) throws Exception {
        Category category = categoryRepository.findOne(id);
        List<Task> taskList = category.getTaskList();
        model.addAttribute("category", category);
        model.addAttribute("taskList", taskList);
        return "category";
    }

    @Secured("ROLE_ADMIN")
    @Transactional
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String removeCategory(@PathVariable Long id,
            RedirectAttributes redirectAttributes) throws Exception {
        
        categoryService.deleteCategory(id);
        redirectAttributes.addFlashAttribute("messages", "Category deleted!");
        return "redirect:/categories";
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/{id}/edit", method = RequestMethod.POST)
    public String updateCategory(@PathVariable Long id, RedirectAttributes redirectAttributes,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam(required = false) List<Long> taskIds) {
        Category oldCategory = categoryRepository.findOne(id);
        oldCategory.setName(name);
        oldCategory.setDescription(description);
        List<Task> tasks = new ArrayList<>();
        if (taskIds != null) {
            for (Long taskId : taskIds) {
                tasks.add(taskRepository.findOne(taskId));
            }
        }
        categoryService.setCategoryToTasks(oldCategory, tasks);

        try {
            categoryRepository.save(oldCategory);
        } catch (Exception e) {
            return redirectMessage("Category update failed", redirectAttributes);
        }

        redirectAttributes.addFlashAttribute("messages", "Category modified!");
        return "redirect:/categories/{id}";
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
    public String getEditCategoryPage(@PathVariable Long id,
            Model model) {
        model.addAttribute("category", categoryRepository.findOne(id));
        model.addAttribute("allTasks", taskRepository.findAll());
        return "categoryEdit";
    }

    @RequestMapping(value = "/{id}/tasks/{taskId}", method = RequestMethod.GET)
    public String getCategoryTask(@PathVariable Long id,
            @PathVariable Long taskId,
            Model model, RedirectAttributes redirectAttributes) {

        Category category = categoryRepository.findOne(id);
        Account user = userService.getAuthenticatedUser();
        LocalDate now = LocalDate.now();

        Task task = taskRepository.findOne(taskId);
        model.addAttribute("task", task);
        model.addAttribute("database", task.getDatabase());
        model.addAttribute("category", category);
        model.addAttribute("next", categoryService.getNextTask(category, task));
        return "task";
    }

    @Secured("ROLE_TEACHER")
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public String changeCategoryTaskOrder(RedirectAttributes redirectAttributes,
            @PathVariable Long id,
            @RequestParam Long taskId) {

        Category category = categoryRepository.findOne(id);
        Task task = taskRepository.findOne(taskId);
        categoryService.setCategoryTaskFurther(category, task);

        redirectAttributes.addAttribute("categoryId", id);

        return "redirect:/categories/{categoryId}";
    }

    private String redirectMessage(String message, RedirectAttributes ra) {
        ra.addFlashAttribute("messages", message);
        return "redirect:/categories/";
    }
}
