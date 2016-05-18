package wepaht.SQLTasker.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.domain.Account;
import wepaht.SQLTasker.repository.CategoryRepository;
import wepaht.SQLTasker.repository.TaskRepository;
import wepaht.SQLTasker.service.UserService;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        if (user.getRole().equals("TEACHER") ||user.getRole().equals("ADMIN")  ) {
            model.addAttribute("categories", categoryRepository.findAll());
        } else {
            List<Category> categoryList=categoryRepository.findByStartDateBefore(new Date());
            categoryList.addAll(categoryRepository.findByStartDate(new Date()));
            model.addAttribute("categories", categoryList);
        }

        model.addAttribute("tasks", taskRepository.findAll());
        return "categories";
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(method = RequestMethod.POST)
    public String createCategory(RedirectAttributes redirectAttributes,
                                 @Valid @ModelAttribute Category category,
                                 BindingResult result,
                                 @RequestParam(required = false) List<Long> taskIds) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("messages", "Errors in field! Check name, start date and expiration date.");

            return "redirect:/categories";
        }
        if (category == null) {
            redirectAttributes.addFlashAttribute("messages", "Category creation has failed");
            return "redirect:/categories";
        }

        if (category.getExpiredDate().before(category.getStartDate())) {
            redirectAttributes.addFlashAttribute("messages", "Expired date is before starting date! Creation has failed!");
            return "redirect:/categories";
        }
        List<Task> tasks = new ArrayList<>();
        if (taskIds != null) {
            for (Long taskId : taskIds) {
                tasks.add(taskRepository.findOne(taskId));
            }
        }
        category.setTaskList(tasks);
        categoryRepository.save(category);
        redirectAttributes.addFlashAttribute("messages", "Category has been created!");
        return "redirect:/categories";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String getCategory(@PathVariable Long id,
                              Model model) throws Exception {
        model.addAttribute("category", categoryRepository.findOne(id));
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
                                 @RequestParam Date startDate,
                                 @RequestParam Date expiredDate,
                                 @RequestParam String description,
                                 @RequestParam(required = false) List<Long> taskIds) {
        Category oldCategory = categoryRepository.findOne(id);
        oldCategory.setName(name);
        oldCategory.setDescription(description);
        oldCategory.setExpiredDate(expiredDate);
        oldCategory.setStartDate(startDate);
        List<Task> tasks = new ArrayList<>();
        if (taskIds != null) {
            for (Long taskId : taskIds) {
                tasks.add(taskRepository.findOne(taskId));
            }
        }
        oldCategory.setTaskList(tasks);
        categoryRepository.save(oldCategory);
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
        if(category.getStartDate().before(new Date()) || category.getStartDate().equals(new Date()) || user.getRole().equals("TEACHER") ||user.getRole().equals("ADMIN")) {
            model.addAttribute("task", taskRepository.findOne(taskId));
            model.addAttribute("category", category);
            return "task";
        }

        redirectAttributes.addFlashAttribute("messages", "You shall not pass here!");
        return "redirect:/categories/";
    }


    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(true);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
    }
}
