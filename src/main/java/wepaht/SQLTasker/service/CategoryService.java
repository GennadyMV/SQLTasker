package wepaht.SQLTasker.service;

import java.util.ArrayList;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.repository.CategoryRepository;
import wepaht.SQLTasker.repository.TaskRepository;

import java.util.List;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wepaht.SQLTasker.domain.Account;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.domain.Submission;
import wepaht.SQLTasker.domain.TmcAccount;
import wepaht.SQLTasker.library.StringLibrary;
import static wepaht.SQLTasker.library.StringLibrary.ATTRIBUTE_CATEGORY;
import static wepaht.SQLTasker.library.StringLibrary.ATTRIBUTE_MESSAGES;
import static wepaht.SQLTasker.library.StringLibrary.MESSAGE_FAILED_ACTION;
import static wepaht.SQLTasker.library.StringLibrary.MESSAGE_SUCCESSFUL_ACTION;
import static wepaht.SQLTasker.library.StringLibrary.MESSAGE_UNAUTHORIZED_ACCESS;
import static wepaht.SQLTasker.library.StringLibrary.MESSAGE_UNAUTHORIZED_ACTION;
import static wepaht.SQLTasker.library.StringLibrary.REDIRECT_CATEGORIES;
import static wepaht.SQLTasker.library.StringLibrary.ROLE_STUDENT;
import static wepaht.SQLTasker.library.StringLibrary.VIEW_CATEGORY_EDIT;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TaskService taskService;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private AccountService accountService;

    @Autowired
    private CategoryDetailService categoryDetailService;
    /**
     * Adds task to categorys' task list.
     *
     * @param categoryId which category
     * @param task which task
     */
    @Transactional
    public void setCategoryToTask(Long categoryId, Task task) {
        Category category = categoryRepository.findOne(categoryId);
        List<Task> taskList = category.getTaskList();
        if (!taskList.contains(task)) {
            taskList.add(task);
            category.setTaskList(taskList);
        }
    }

    @Transactional
    public void setCategoryToTasks(Category category, List<Task> tasks) {
        category.setTaskList(tasks);
    }

    public Task getNextTask(Category category, Task task) {
        List<Task> categoryTasks = category.getTaskList();
        int taskIndex = categoryTasks.indexOf(task);
        int nextTaskIndex = taskIndex + 1;

        if (taskIndex < categoryTasks.size() - 1 && categoryTasks.contains(task)) {
            return categoryTasks.get(nextTaskIndex);
        }

        return null;
    }

    @Transactional
    public void setCategoryTaskFurther(Category category, Task task) {
        List<Task> categoryTasks = category.getTaskList();

        int taskIndex = categoryTasks.indexOf(task);

        if (taskIndex < categoryTasks.size() - 1 && categoryTasks.contains(task)) {
            Task next = categoryTasks.get(taskIndex + 1);
            categoryTasks.set(taskIndex, next);
            categoryTasks.set(taskIndex + 1, task);
        }
    }

    public void removeTaskFromCategory(Category category, Task task) {

        List<Task> taskList = category.getTaskList();
        taskList.remove(task);
        category.setTaskList(taskList);
        categoryRepository.save(category);

        taskService.save(task);
    }

    public void setTaskToCategories(Task task, List<Long> categoryIds) {
        categoryIds.stream().forEach((id) -> {
            setCategoryToTask(id, task);
        });

    }

    public List<Category> findCategoriesByIds(List<Long> categoryIds) {
        if (categoryIds != null) {
            ArrayList<Category> categories = new ArrayList<>();

            categoryIds.stream().forEach((categoryId) -> {
                categories.add(categoryRepository.findOne(categoryId));
            });

            return categories;
        }
        return null;
    }
    
    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category deleting = categoryRepository.findOne(id);
        List<Course> courses = deleting.getCourses();
        if (courses != null) {
            if (!courses.isEmpty()) courseService.removeCategoryFromCourses(courses, deleting);
        }
        
        if (deleting.getSubmissions() != null)deleting.getSubmissions().stream().forEach((sub) -> {
            sub.setCategory(null);
        });
        
        try {
            deleting.setDeleted(true);
        } catch (Exception e) {}        
    }

    Category getCategoryById(Long categoryId) {
        return categoryRepository.findOne(categoryId);
    }
    
    public boolean categoryHasTask(Category category, Task task) {
        return category.getTaskList().contains(task);
    }

    public String deleteCategory(RedirectAttributes redirectAttributes, Long id) {
        Account user = accountService.getAuthenticatedUser();
        
        if (user.getRole().equals(ROLE_STUDENT)) {
            redirectAttributes.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACTION);
            
            return REDIRECT_CATEGORIES;
        }
        
        deleteCategory(id);
        redirectAttributes.addFlashAttribute(ATTRIBUTE_MESSAGES, "Category deleted!");
        return REDIRECT_CATEGORIES;
    }

    public String getEditForm(Model model, RedirectAttributes redirAttr, Long categoryId) {
        Account user = accountService.getAuthenticatedUser();
        Category category = categoryRepository.findOne(categoryId);
        
        if (user.getRole().equals(ROLE_STUDENT) && (category.getOwner() == null||!category.getOwner().equals(user))) {
            redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACCESS);
            redirAttr.addAttribute("id", categoryId);
            return "redirect:/categories/{id}";
        }
        
        model.addAttribute(ATTRIBUTE_CATEGORY, category);
        model.addAttribute("allTasks", taskService.getAllTasks());
        
        return VIEW_CATEGORY_EDIT;
    }

    public String updateCategory(RedirectAttributes redirectAttributes, Long id, String name, String description, List<Long> taskIds) {
        Account user = accountService.getAuthenticatedUser();
        List<String> messages = new ArrayList<>();
        Category editing = categoryRepository.findOne(id);
        
        if (user.getRole().equals(ROLE_STUDENT) && !editing.getOwner().equals(user)) {
            redirectAttributes.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACTION);
            redirectAttributes.addAttribute("id", id);
            return "redirect:/categories/{id}";
        }
        
        editing = editCategory(editing, name, description, taskIds, id);
        messages = saveCategory(editing, messages);

        redirectAttributes.addAttribute("id", id);
        redirectAttributes.addFlashAttribute(ATTRIBUTE_MESSAGES, messages);
        return "redirect:/categories/{id}";
    }

    private List<String> saveCategory(Category editing, List<String> messages) {
        try {
            categoryRepository.save(editing);
            messages.add(MESSAGE_SUCCESSFUL_ACTION);
        } catch (Exception e) {
            messages.add(MESSAGE_FAILED_ACTION);
            messages.add(e.toString());
        }
        return messages;
    }

    private Category editCategory(Category editing, String name, String description, List<Long> taskIds, Long id) {
        editing.setName(name);
        editing.setDescription(description);
        List<Task> tasks = new ArrayList<>();
        if (taskIds != null) {
            for (Long taskId : taskIds) {
                tasks.add(taskService.getTaskById(taskId));
            }
        }
        setCategoryToTasks(editing, tasks);
        
        return editing;
    }

    public String reorderTask(RedirectAttributes redirectAttributes, Long id, Long taskId) {
        Account user = accountService.getAuthenticatedUser();
        Category category = categoryRepository.findOne(id);
        Task task = taskService.getTaskById(taskId);
        
        if (user.getRole().equals(ROLE_STUDENT) && (category.getOwner() == null||!category.getOwner().equals(user))) {
            redirectAttributes.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACTION);
        } else {
            setCategoryTaskFurther(category, task);
            redirectAttributes.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_SUCCESSFUL_ACTION);
        }
        
        redirectAttributes.addAttribute("categoryId", id);
        return "redirect:/categories/{categoryId}";
    }
}
