package wepaht.SQLTasker.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wepaht.SQLTasker.domain.Account;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.domain.Database;
import wepaht.SQLTasker.domain.Table;
import wepaht.SQLTasker.domain.Tag;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.domain.TmcAccount;
import wepaht.SQLTasker.constant.ConstantString;
import static wepaht.SQLTasker.constant.ConstantString.ATTRIBUTE_MESSAGES;
import static wepaht.SQLTasker.constant.ConstantString.MESSAGE_SUCCESSFUL_ACTION;
import static wepaht.SQLTasker.constant.ConstantString.MESSAGE_UNAUTHORIZED_ACCESS;
import static wepaht.SQLTasker.constant.ConstantString.MESSAGE_UNAUTHORIZED_ACTION;
import static wepaht.SQLTasker.constant.ConstantString.REDIRECT_DEFAULT;
import static wepaht.SQLTasker.constant.ConstantString.REDIRECT_TASKS;
import static wepaht.SQLTasker.constant.ConstantString.ROLE_STUDENT;
import static wepaht.SQLTasker.constant.ConstantString.VIEW_TASK;
import wepaht.SQLTasker.repository.TaskRepository;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TagService tagService;

    @Transactional
    public boolean removeTask(Long taskId) {
        Task removing = taskRepository.findOne(taskId);

        removeConnections(removing);

        try {
            removing.setDeleted(true);
            return true;
        } catch (Exception e) {
        }

        return false;
    }

    private void removeConnections(Task removing) {
        if (removing.getCategories() != null) {
            removing.getCategories().stream().forEach((cat) -> {
                cat.getTaskList().remove(removing);
            });
        }

        if (removing.getFeedback() != null) {
            removing.getFeedback().stream().forEach((fb) -> {
                fb.setTask(null);
            });
        }

        if (removing.getSubmissions() != null) {
            removing.getSubmissions().stream().forEach((sub) -> {
                sub.setTask(null);
            });
        }
    }

    public Task getTaskById(Long id) {
        return taskRepository.findOne(id);
    }

    /**
     * Performs query to given task.
     *
     * @param messages Messages which are set to RedirectAttributes
     * @param taskId Task id which the query is directed to
     * @param query Query which is made to task
     * @param categoryId Category Id in which task is part of
     * @param courseId
     * @return First index contains messages, second contains the query result,
     * third is boolean which tells was query correct.
     */
    public List<Object> performQueryToTask(List<String> messages, Long taskId, String query, Long categoryId, Long courseId) {
        messages.add("Query sent");
        Task task = taskRepository.findOne(taskId);
        Category category = categoryService.getCategoryById(categoryId);
        Course course = courseService.getCourseById(courseId);

        Boolean isCorrect = submissionService.createNewSubmissionAndCheckPoints(task, query, category, course);

        if (isCorrect) {
            messages.add("Your answer is correct");
        }

        return Arrays.asList(messages, databaseService.performQuery(task.getDatabase().getId(), query), isCorrect);
    }

    public Task createTask(Task task) {
        if (isInvalidSolution(task, task.getDatabase(), null)) {
            return null;
        }
        task.setOwner(accountService.getAuthenticatedUser());

        task = taskRepository.save(task);
        return task;
    }

    public String createTask(RedirectAttributes redirectAttributes,
            Task task,
            Long databaseId,
            List<Long> categoryIds,
            BindingResult result) {
        Account user = accountService.getAuthenticatedUser();
        if (user.getRole().equals(ROLE_STUDENT)) {
            if (redirectAttributes != null) {
                redirectAttributes.addFlashAttribute(ATTRIBUTE_MESSAGES,
                        MESSAGE_UNAUTHORIZED_ACTION + ": student can create tasks to their own categories");
            }
            return REDIRECT_TASKS;
        }

        if (isDatabaseNull(databaseId, redirectAttributes)) {
            return REDIRECT_TASKS;
        }
        if (isErrorsInTask(result, redirectAttributes)) {
            return REDIRECT_TASKS;
        }
        if (isTaskNull(task, redirectAttributes)) {
            return REDIRECT_TASKS;
        }

        Database db = databaseService.getDatabase(databaseId);
        task.setDatabase(db);
        if (isInvalidSolution(task, db, redirectAttributes)) {
            return REDIRECT_TASKS;
        }

        if (categoryIds != null) {
            categoryService.setTaskToCategories(task, categoryIds);
        }

        task.setOwner(accountService.getAuthenticatedUser());

        taskRepository.save(task);
        if (redirectAttributes != null) {
            redirectAttributes.addFlashAttribute("messages", "Task has been created");
        }

        return "redirect:/tasks";
    }

    private boolean isDatabaseNull(Long databaseId, RedirectAttributes redirectAttributes) {
        if (databaseId == null) {
            if (redirectAttributes != null) {
                redirectAttributes.addFlashAttribute("messages", "You didn't choose a database!");
            }
            return true;
        }
        return false;
    }

    private boolean isErrorsInTask(BindingResult result, RedirectAttributes redirectAttributes) {
        if (result != null) {
            if (result.hasErrors()) {
                List<Object> messages = Arrays.asList("Error!", result.getAllErrors());
                if (redirectAttributes != null) {
                    redirectAttributes.addFlashAttribute("messages", messages);
                }
                return true;
            }
        }
        return false;
    }

    private boolean isTaskNull(Task task, RedirectAttributes redirectAttributes) {
        if (task == null) {
            if (redirectAttributes != null) {
                redirectAttributes.addFlashAttribute("messages", "Task creation has failed");
            }
            return true;
        }
        return false;
    }

    private boolean isInvalidSolution(Task task, Database db, RedirectAttributes redirectAttributes) {
        if (task.getSolution() != null || !task.getSolution().isEmpty()) {
            if (!databaseService.isValidQuery(db, task.getSolution())) {
                if (redirectAttributes != null) {
                    redirectAttributes.addFlashAttribute("messages", "Task creation failed due to invalid solution");
                }
                return true;
            }
        }
        return false;
    }

    public Task save(Task task) {
        return taskRepository.save(task);
    }

    public List<Task> findAllTasks() {
        return taskRepository.findAll();
    }

    public String listTasks(Model model, RedirectAttributes redirAttr) {
        Account user = accountService.getAuthenticatedUser();
        if (user.getRole().equals(ROLE_STUDENT)) {
            redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACCESS);
            return REDIRECT_DEFAULT;
        }

        model.addAttribute("tasks", taskRepository.findAll());
        model.addAttribute("databases", databaseService.getAllDatabases());
        model.addAttribute("categories", categoryService.findAllCategories());

        return "tasks";
    }

    List<Task> findAllOwnTasks() {
        return taskRepository.findByOwnerAndDeletedFalse(accountService.getAuthenticatedUser());
    }

    @Transactional
    public String removeTask(RedirectAttributes redirectAttributes, Long id) {
        Account user = accountService.getAuthenticatedUser();
        Task task = taskRepository.findOne(id);
        if (user.getRole().equals(ROLE_STUDENT)) {
            redirectAttributes.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACTION);
        } else {
            task.setDeleted(Boolean.TRUE);
            redirectAttributes.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_SUCCESSFUL_ACTION + ": task " + task.getName() + " deleted");
        }

        return REDIRECT_DEFAULT;
    }

    public String getOneTask(Model model, RedirectAttributes redirAttr, Long id, Map<Long, String> queries) {
        Task task = taskRepository.findOne(id);
        Account user = accountService.getAuthenticatedUser();
        String redirectAddress = VIEW_TASK;

        if (!user.getRole().equals(ROLE_STUDENT)) {
            if (!queries.containsKey(id)) {
                model.addAttribute("queryResults", new Table("dummy"));
            }

            List<Tag> tags = tagService.getTagsByTask(task);
            model.addAttribute("owned", accountService.isOwned(task));
            model.addAttribute("tags", tags);
            model.addAttribute("task", task);
            model.addAttribute("database", task.getDatabase());
        } else {
            redirectAddress = REDIRECT_DEFAULT;
            redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACCESS);
        }

        return redirectAddress;
    }

    public String getEditForm(Model model, RedirectAttributes redirAttr, Long id) {
        Task task = taskRepository.findOne(id);
        Account user = accountService.getAuthenticatedUser();
        String redirectAddress;

        if (!user.getRole().equals(ROLE_STUDENT)) {
            redirectAddress = setEditForm(model, task);
        } else {
            redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACCESS);
            redirectAddress = REDIRECT_DEFAULT;
        }

        return redirectAddress;
    }

    public String setEditForm(Model model, Task task) {
        String redirectAddress;
        model.addAttribute("tags", tagService.getTagsByTask(task));
        model.addAttribute("task", task);
        model.addAttribute("databases", databaseService.getAllDatabases());
        redirectAddress = "editTask";
        return redirectAddress;
    }

    public String editTask(RedirectAttributes redirectAttributes, Long id, Long databaseId, String name, String solution, String description) {
        String redirectAddress = "redirect:/tasks/{id}";
        Account user = accountService.getAuthenticatedUser();
        Task task = taskRepository.findOne(id);

        if (!user.getRole().equals(ROLE_STUDENT)) {
            if (!updateTask(task, solution, redirectAttributes, redirectAddress, description, name, databaseId)) {
                return redirectAddress;
            }
            if (redirectAttributes != null) {
                redirectAttributes.addAttribute("id", id);
                redirectAttributes.addFlashAttribute(ATTRIBUTE_MESSAGES, "Task modified!");
            }
        } else {
            redirectAddress = REDIRECT_DEFAULT;
            if (redirectAttributes != null) redirectAttributes.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACCESS);
        }
        return redirectAddress;
    }

    @Transactional
    public boolean updateTask(Task task, String solution, RedirectAttributes redirectAttributes, String redirectAddress, String description, String name, Long databaseId) {
        Database db = databaseService.getDatabase(databaseId);
        if (solution != null || !solution.isEmpty()) {
            if (!databaseService.isValidQuery(db, solution)) {
                if (redirectAttributes != null) {
                    redirectAttributes.addFlashAttribute(ATTRIBUTE_MESSAGES, "Task update failed due to invalid solution");
                }
                return false;
            }
        }

        task.setDatabase(db);
        task.setDescription(description);
        task.setName(name);
        task.setSolution(solution);
        task.setDatabase(db);
        return true;
    }

    public String addTag(RedirectAttributes redirectAttributes, Long id, String name) {
        if (!accountService.isUserStudent()) {
            Tag tag = tagService.createTag(name, getTaskById(id));
            if (redirectAttributes != null) {
                redirectAttributes.addAttribute("id", id);
                redirectAttributes.addFlashAttribute("messages", "Tag added!");
            }
        }
        return "redirect:/tasks/{id}/edit";
    }

    public String deleteTag(RedirectAttributes redirectAttributes, Long id, String name) {
        if (!accountService.isUserStudent()) {
            Tag tag = tagService.getTagByNameAndTask(name, getTaskById(id));
            tagService.deleteTag(tag);
            if (redirectAttributes != null) {
                redirectAttributes.addAttribute("id", id);
                redirectAttributes.addFlashAttribute("messages", "Tag deleted!");
            }
        }
        return "redirect:/tasks/{id}/edit";
    }
}
