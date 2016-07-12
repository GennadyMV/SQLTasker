package wepaht.SQLTasker.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wepaht.SQLTasker.domain.Account;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.domain.TaskFeedback;
import static wepaht.SQLTasker.constant.ConstantString.ATTRIBUTE_MESSAGES;
import static wepaht.SQLTasker.constant.ConstantString.MESSAGE_UNAUTHORIZED_ACCESS;
import static wepaht.SQLTasker.constant.ConstantString.REDIRECT_DEFAULT;
import static wepaht.SQLTasker.constant.ConstantString.ROLE_STUDENT;
import wepaht.SQLTasker.repository.TaskFeedbackRepository;

@Service
public class TaskFeedbackService {

    @Autowired
    CategoryService categoryService;

    @Autowired
    CourseService courseService;

    @Autowired
    TaskService taskService;

    @Autowired
    TaskFeedbackRepository repository;

    @Autowired
    AccountService accountService;

    @Autowired
    PointService pointService;

    public String getFeedbackForm(Model model, Long courseId, Long categoryId, Long taskId) {
        Course course = courseService.getCourseById(courseId);
        Category category = categoryService.getCategoryById(categoryId);
        Task task = taskService.getTaskById(taskId);
        
        TaskFeedback feedback = new TaskFeedback();
        feedback.setTask(task);
        feedback.setFeedback(new HashMap<>());

        model.addAttribute("taskFeedback", feedback);
        model.addAttribute("course", course);
        model.addAttribute("category", category);
        model.addAttribute("task", task);

        return "feedback";
    }

    public List<TaskFeedback> listAllFeedback() {
        return repository.findAll();
    }

    public String createFeedback(RedirectAttributes redirAttr, Long courseId, Long categoryId, Long taskId, TaskFeedback taskFeedback) {
        Task task = taskService.getTaskById(taskId);
        Course course = courseService.getCourseById(courseId);
        Category category = categoryService.getCategoryById(categoryId);

        List<String> messages = new ArrayList<>();

        if (pointService.hasUserDoneTaskCorrectly(accountService.getAuthenticatedUser(), course, category, task)) {
            try {
                saveFeedback(taskFeedback);
                messages.add("Feedback on task " + task.getName() + "has been sent");
            } catch (Exception e) {
                messages.add(e.toString());
            }
        } else {
            messages.add("You have not done task " + task.getName() + " in course " + course.getName());
        }

        redirAttr.addFlashAttribute("messages", messages);
        redirAttr.addAttribute("courseId", courseId);
        redirAttr.addAttribute("categoryId", categoryId);
        redirAttr.addAttribute("taskId", taskId);

        return "redirect:/courses/{courseId}/category/{categoryId}/task/{taskId}";
    }

    public void saveFeedback(TaskFeedback taskFeedback) {
        repository.save(taskFeedback);
    }

    public String getAllFeedback(Model model, RedirectAttributes redirAttr) {
        Account user = accountService.getAuthenticatedUser();
        if (user.getRole().equals(ROLE_STUDENT)) {
            redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACCESS);
            return REDIRECT_DEFAULT;
        }
        model.addAttribute("feedback", listAllFeedback());
        
        return "feedbackList";
    }

}
