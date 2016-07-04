package wepaht.SQLTasker.service;

import java.util.Arrays;
import java.util.List;
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
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.domain.TmcAccount;
import wepaht.SQLTasker.library.StringLibrary;
import static wepaht.SQLTasker.library.StringLibrary.ATTRIBUTE_MESSAGES;
import static wepaht.SQLTasker.library.StringLibrary.MESSAGE_UNAUTHORIZED_ACCESS;
import static wepaht.SQLTasker.library.StringLibrary.REDIRECT_DEFAULT;
import static wepaht.SQLTasker.library.StringLibrary.ROLE_STUDENT;
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

    public String createTask(RedirectAttributes redirectAttributes,
            Task task,
            Long databaseId,
            List<Long> categoryIds,
            BindingResult result) {
        if (isDatabaseNull(databaseId, redirectAttributes)) {
            return "redirect:/tasks";
        }
        if (isErrorsInTask(result, redirectAttributes)) {
            return "redirect:/tasks";
        }
        if (isTaskNull(task, redirectAttributes)) {
            return "redirect:/tasks";
        }

        Database db = databaseService.getDatabase(databaseId);
        task.setDatabase(db);
        if (isInvalidSolution(task, db, redirectAttributes)) {
            return "redirect:/tasks";
        }

        if (categoryIds != null) {
            categoryService.setTaskToCategories(task, categoryIds);
        }

        TmcAccount user = accountService.getAuthenticatedUser();
        task.setOwner(user);

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
        model.addAttribute("databases", databaseService.findAllDatabases());
        model.addAttribute("categories", categoryService.findAllCategories());
        
        return "tasks";
    }
}
