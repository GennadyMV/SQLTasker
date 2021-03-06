package wepaht.SQLTasker.service;

import java.util.ArrayList;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.repository.CategoryRepository;
import java.util.List;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wepaht.SQLTasker.domain.Account;
import wepaht.SQLTasker.domain.Course;
import static wepaht.SQLTasker.constant.ConstantString.*;

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
    private DatabaseService dbService;

    @Autowired
    private TagService tagService;

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
    public void setCategoryToTask(Category category, Task task) {
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

    public Task getPreviousTask(Category category, Task task) {
        List<Task> categoryTasks = category.getTaskList();
        int taskIndex = categoryTasks.indexOf(task);
        int prevTaskIndex = taskIndex - 1;

        if (taskIndex > 0 && categoryTasks.contains(task)) {
            return categoryTasks.get(prevTaskIndex);
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

    public void setTaskToCategoriesIds(Task task, List<Long> categoryIds) {
        categoryIds.stream().forEach((id) -> {
            CategoryService.this.setCategoryToTask(id, task);
        });

    }
    
    public void setTaskToCategories(Task task, List<Category> categories) {
        categories.stream().forEach((cat) -> {
            setCategoryToTask(cat, task);
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
            if (!courses.isEmpty()) {
                courseService.removeCategoryFromCourses(courses, deleting);
            }
        }

        if (deleting.getSubmissions() != null) {
            deleting.getSubmissions().stream().forEach((sub) -> {
                sub.setCategory(null);
            });
        }

        try {
            deleting.setDeleted(true);
        } catch (Exception e) {
        }
    }

    public Category getCategoryById(Long categoryId) {
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

        if (user.getRole().equals(ROLE_STUDENT) && (category.getOwner() == null || !category.getOwner().equals(user))) {
            redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACCESS);
            redirAttr.addAttribute("id", categoryId);
            return "redirect:/categories/{id}";
        }

        return returnEditForm(model, category, user);
    }

    public String returnEditForm(Model model, Category category, Account user) {
        model.addAttribute(ATTRIBUTE_CATEGORY, category);
        
        List<Task> tasks;

        if (user.getRole().equals(ROLE_STUDENT)) {
            tasks = category.getTaskList();
        } else {
            tasks = taskService.findAllTasks();
        }
        model.addAttribute("allTasks", tasks);

        return VIEW_CATEGORY_EDIT;
    }

    @Transactional
    public String updateCategory(RedirectAttributes redirectAttributes, Long categoryId, String name, String description, List<Long> taskIds) {
        Account user = accountService.getAuthenticatedUser();
        List<String> messages = new ArrayList<>();
        Category editing = categoryRepository.findOne(categoryId);

        if (user.getRole().equals(ROLE_STUDENT) && (editing.getOwner() == null || !editing.getOwner().equals(user))) {
            if (redirectAttributes != null) {
                redirectAttributes.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACTION);
                redirectAttributes.addAttribute("id", categoryId);
            }
            return "redirect:/categories/{id}";
        }

        editing = editCategory(editing, name, description, taskIds);
        messages = saveCategory(editing, messages);

        if (redirectAttributes != null) {
            redirectAttributes.addAttribute("id", categoryId);
            redirectAttributes.addFlashAttribute(ATTRIBUTE_MESSAGES, messages);
        }
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

    @Transactional
    public Category editCategory(Category editing, String name, String description, List<Long> taskIds) {
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

        if (user.getRole().equals(ROLE_STUDENT) && (category.getOwner() == null || !category.getOwner().equals(user))) {
            redirectAttributes.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACTION);
        } else {
            setCategoryTaskFurther(category, task);
            redirectAttributes.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_SUCCESSFUL_ACTION);
        }

        redirectAttributes.addAttribute("categoryId", id);
        return "redirect:/categories/{categoryId}";
    }

    public String getCreateTaskToCategoryForm(Model model, RedirectAttributes redirAttr, Long categoryId, Task task) {
        Account user = accountService.getAuthenticatedUser();
        Category category = categoryRepository.findOne(categoryId);
        if (user.getRole().equals(ROLE_STUDENT) && (category == null || category.getOwner() == null || !category.getOwner().equals(user))) {
            redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACCESS);
            redirAttr.addAttribute("categoryId", categoryId);
            return "redirect:/categories/{categoryId}";
        }

        return returnCreateTaskForm(model, categoryId, category, task);
    }

    public String returnCreateTaskForm(Model model, Long categoryId, Category category, Task task) {
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("databases", dbService.getAllDatabases());
        model.addAttribute("category", Arrays.asList(category.getId()));
        model.addAttribute("task", task);
        return "taskForm";
    }

    @Transactional
    public String createTaskToCategory(RedirectAttributes redirAttr, Model model, Long categoryId, Task task) {
        Account user = accountService.getAuthenticatedUser();
        Category category = categoryRepository.findOne(categoryId);
        if (user.getRole().equals(ROLE_STUDENT) && (category == null || category.getOwner() == null || !category.getOwner().equals(user))) {
            redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACTION);
            redirAttr.addAttribute("categoryId", categoryId);
            return "redirect:/categories/{categoryId}";
        }

        try {
            task = taskService.createTask(task);
            if (task != null) {
                redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_SUCCESSFUL_ACTION);
            } else {
                model.addAttribute(ATTRIBUTE_MESSAGES, MESSAGE_FAILED_ACTION + ": invalid task solution");
                return getCreateTaskToCategoryForm(model, redirAttr, categoryId, new Task());
            }
        } catch (Exception e) {
            redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_FAILED_ACTION + ": " + e.toString());
        }

        setTaskToCategoriesIds(task, Arrays.asList(categoryId));

        return "redirect:/categories/{categoryId}";
    }

    public String listAllCategories(Model model) {
        Account user = accountService.getAuthenticatedUser();

        model.addAttribute("categories", categoryRepository.findAll());
        if (!user.getRole().equals(ROLE_STUDENT)) {
            model.addAttribute("tasks", taskService.findAllTasks());
        }
        return "categories";
    }

    @Transactional
    public String deleteTask(RedirectAttributes redirAttr, Long categoryId, Long taskId) {
        Account user = accountService.getAuthenticatedUser();
        Category category = categoryRepository.findOne(categoryId);
        Task task = taskService.getTaskById(taskId);

        taskService.deleteTask(user, category, task, redirAttr, taskId);

        redirAttr.addAttribute("categoryId", categoryId);
        return "redirect:/categories/{categoryId}";
    }

    public String getEditTaskForm(Model model, RedirectAttributes redirAttr, Long categoryId, Long taskId) {
        Account user = accountService.getAuthenticatedUser();
        Category category = categoryRepository.findOne(categoryId);
        Task task = taskService.getTaskById(taskId);

        String redirectAddress;

        if (user.getRole().equals(ROLE_STUDENT)) {
            if (accountService.isOwned(category, user) && accountService.isOwned(task, user)) {
                redirectAddress = taskService.setEditForm(model, task);
            } else {
                redirAttr.addAttribute("categoryId", categoryId);
                redirAttr.addAttribute("taskId", taskId);
                redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACCESS);
                redirectAddress = "redirect:/categories/{categoryId}/tasks/{taskId}";
            }
        } else {
            redirectAddress = taskService.setEditForm(model, task);
        }

        return redirectAddress;
    }

    public String editTask(RedirectAttributes redirAttr, Long categoryId, Long taskId, Long databaseId, String name, String solution, String description) {
        String redirectAddress = "redirect:/categories/{categoryId}/tasks/{taskId}";
        Account user = accountService.getAuthenticatedUser();
        Category category = categoryRepository.findOne(taskId);
        Task task = taskService.getTaskById(taskId);
        redirAttr.addAttribute("categoryId", categoryId);
        redirAttr.addAttribute("taskId", taskId);

        if (user.getRole().equals(ROLE_STUDENT) && (!accountService.isOwned(task, user) || !accountService.isOwned(category, user))) {
            redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACTION);
        } else {
            if (taskService.updateTask(task, solution, redirAttr, redirectAddress, description, name, databaseId)) {
                redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_SUCCESSFUL_ACTION + ": updated task " + task.getName());
            }
        }

        return redirectAddress;
    }

    public String performQueryToTask(RedirectAttributes redirectAttributes, String query, Long categoryId, Long taskId) {
        List<Object> messagesAndQueryResult = taskService.performQueryToTask(new ArrayList<String>(), taskId, query, categoryId, null);

        redirectAttributes.addFlashAttribute("messages", messagesAndQueryResult.get(0));
        redirectAttributes.addAttribute("taskId", taskId);
        redirectAttributes.addAttribute("categoryId", categoryId);
        redirectAttributes.addFlashAttribute("tables", messagesAndQueryResult.get(1));

        return "redirect:/categories/{categoryId}/tasks/{taskId}";
    }

    public String getTaskEditForm(Model model, RedirectAttributes redirAttr, Long categoryId, Long taskId) {
        Account user = accountService.getAuthenticatedUser();
        Task task = taskService.getTaskById(taskId);
        Category category = categoryRepository.findOne(taskId);

        if ((!user.getRole().equals(ROLE_STUDENT) || accountService.isOwned(task)) && categoryHasTask(category, task)) {
            return taskService.setEditForm(model, task);
        }

        redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACCESS);
        return "redirect:/categories";
    }

    public Task getNextTask(Long categoryId, Long taskId) {
        Task task = taskService.getTaskById(taskId);
        Category category = categoryRepository.findOne(categoryId);
        return getNextTask(category, task);
    }

    public Task getPreviousTask(Long categoryId, Long taskId) {
        Task task = taskService.getTaskById(taskId);
        Category category = categoryRepository.findOne(categoryId);
        return getPreviousTask(category, task);
    }

    public Integer getTaskCountByCourse(Course course) {
        return categoryRepository.getTaskCountByCategories(course.getCourseCategories()).intValue();
    }

    public Integer getTaskCount(Category category) {
        return categoryRepository.getTaskCountByCategory(category).intValue();
    }

    List<Category> getAllCategoriesByName(String name) {
        return categoryRepository.findAllByNameContainingAndDeletedFalse(name);
    }

}
