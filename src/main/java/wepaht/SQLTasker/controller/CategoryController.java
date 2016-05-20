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
import wepaht.SQLTasker.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;

@Controller
@RequestMapping("categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public String getCategories(Model model) {
        Account user = userService.getAuthenticatedUser();
        if (user.getRole().equals("TEACHER") || user.getRole().equals("ADMIN")) {
            model.addAttribute("categories", categoryRepository.findAll());
        } else {
            List<Category> categoryList = categoryRepository.getActiveCategories(LocalDate.now());
            model.addAttribute("categories", categoryList);
        }

        model.addAttribute("tasks", taskRepository.findAll());
        return "categories";
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(method = RequestMethod.POST)
    public String createCategory(
            RedirectAttributes redirectAttributes,            
            @RequestParam String name,
            @RequestParam String starts,
            @RequestParam String expires,
            @RequestParam String description,
            @RequestParam(required = false) List<Long> taskIds) {

        if (LocalDate.parse(starts).isAfter(LocalDate.parse(expires))) {
            return redirectMessage("Error! Start date is after expiridation date!", redirectAttributes);            
        }

        List<Task> tasks = new ArrayList<>();
        if (taskIds != null) {
            for (Long taskId : taskIds) {
                tasks.add(taskRepository.findOne(taskId));
            }
        }

        try {
            Category category = new Category();
            category.setName(name);
            category.setStarts(LocalDate.parse(starts));
            category.setExpires(LocalDate.parse(expires));
            category.setDescription(description);
            category.setTaskList(tasks);
            categoryRepository.save(category);
        } catch (Exception e) {
            return redirectMessage("Category creation failed!", redirectAttributes);            
        }

        redirectAttributes.addFlashAttribute("messages", "Category has been created!");
        return "redirect:/categories";
    }

    @Transactional
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
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String removeCategory(@PathVariable Long id,
            RedirectAttributes redirectAttributes) throws Exception {
        categoryRepository.delete(id);
        redirectAttributes.addFlashAttribute("messages", "Category deleted!");
        return "redirect:/categories";
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/{id}/edit", method = RequestMethod.POST)
    public String updateCategory(@PathVariable Long id, RedirectAttributes redirectAttributes,
            @RequestParam String name,
            @RequestParam String startDate,
            @RequestParam String expiredDate,
            @RequestParam String description,
            @RequestParam(required = false) List<Long> taskIds) {
        Category oldCategory = categoryRepository.findOne(id);
        oldCategory.setName(name);
        oldCategory.setDescription(description);
        oldCategory.setExpires(LocalDate.parse(expiredDate));
        oldCategory.setStarts(LocalDate.parse(startDate));
        List<Task> tasks = new ArrayList<>();
        if (taskIds != null) {
            for (Long taskId : taskIds) {
                tasks.add(taskRepository.findOne(taskId));
            }
        }
        oldCategory.setTaskList(tasks);
        
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
        if (category.getStarts().isBefore(LocalDate.now()) || category.getStarts().equals(LocalDate.now()) || user.getRole().equals("TEACHER") || user.getRole().equals("ADMIN")) {
            model.addAttribute("task", taskRepository.findOne(taskId));
            model.addAttribute("category", category);
            return "task";
        }

        redirectAttributes.addFlashAttribute("messages", "You shall not pass here!");
        return "redirect:/categories/";
    }
    
    private String redirectMessage(String message, RedirectAttributes ra) {
        ra.addFlashAttribute("messages", message);
        return "redirect:/categories/";
    }
}
