package wepaht.SQLTasker.service;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.domain.TaskFeedback;
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
    
    public String getFeedbackForm(Model model, Long courseId, Long categoryId, Long taskId) {
        Course course = courseService.getCourseById(courseId);
        Category category = categoryService.getCategoryById(categoryId);
        Task task = taskService.getTaskById(taskId);
        TaskFeedback feedback = new TaskFeedback();
        feedback.setTask(task);
        
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
        List<String> messages = Arrays.asList("Feedback on task " + task.getName() + "has been sent");
        
        try {
            repository.save(taskFeedback);
        } catch (Exception e) {
            messages.add(e.toString());
        }
        
        redirAttr.addFlashAttribute("messages", messages);
        redirAttr.addAttribute("courseId", courseId);
        redirAttr.addAttribute("categoryId", categoryId);
        redirAttr.addAttribute("taskId", taskId);
        
        return "redirect:/courses/{courseId}/category/{categoryId}/task/{taskId}";
    }
    
}
