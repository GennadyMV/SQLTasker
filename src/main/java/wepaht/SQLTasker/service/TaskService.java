package wepaht.SQLTasker.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.CategoryDetail;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.repository.TaskRepository;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskResultService taskResultService;

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private CategoryDetailService categoryDetailService;

    @Transactional
    public boolean removeTask(Long taskId) {
        Task removing = taskRepository.findOne(taskId);

        removeConnections(removing);

        try {
            taskRepository.delete(taskId);
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
}
