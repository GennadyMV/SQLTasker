package wepaht.SQLTasker.service;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.repository.TaskRepository;

@Service
public class TaskService {

    @Autowired
    TaskRepository taskRepository;
    
    @Autowired
    TaskResultService taskResultService;
    
    @Autowired
    PastQueryService pastQueryService;
    
    @Autowired
    AccountService accountService;
    
    @Autowired
    DatabaseService databaseService;
    
    @Autowired
    CategoryService categoryService;
    
    @Autowired
    CourseService courseService;
    
    @Autowired
    SubmissionService submissionService;
    
    public boolean removeTask(Long taskId) {
        Task removing = taskRepository.findOne(taskId);
        
        removing.getCategories().stream().forEach((cat) -> {
            cat.getTaskList().remove(removing);
        });
        
        try {
            taskRepository.delete(taskId);
            return true;
        } catch (Exception e) {
        }
        
        return false;
    }
    
    public Task getTaskById(Long id) {
        return taskRepository.findOne(id);
    }
    
    /**
     * Performs query to given task.
     * @param messages Messages  which are set to RedirectAttributes
     * @param taskId Task id which the query is directed to
     * @param query Query which is made to task
     * @param categoryId Category Id in which task is part of
     * @param courseId
     * @return First index contains messages, second contains the query result.
     */
    public List<Object> performQueryToTask(List<String> messages, Long taskId, String query, Long categoryId, Long courseId) {
        messages.add("Query sent");
        Task task = taskRepository.findOne(taskId);
        Category category = categoryService.getCategoryById(categoryId);
        Course course = courseService.getCourseById(courseId);
        Boolean isCorrect;
        
         if (task.getSolution() != null) {
             isCorrect = taskResultService.evaluateSubmittedQueryResult(task, query);
         } else {
             isCorrect = false;
         }
        
        if (isCorrect) {
            messages.add("Your answer is correct");
//            pastQueryService.saveNewPastQuery(accountService.getAuthenticatedUser().getUsername(), task, query, true, categoryId);
            
        } else {
//            pastQueryService.saveNewPastQuery(accountService.getAuthenticatedUser().getUsername(), task, query, false, categoryId);
        }
        
        submissionService.createNewSubmisson(task, query, isCorrect, category, course);
        
        return Arrays.asList(messages, databaseService.performQuery(task.getDatabase().getId(), query));
    }
}
