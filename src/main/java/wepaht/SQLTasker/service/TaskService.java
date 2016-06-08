package wepaht.SQLTasker.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wepaht.SQLTasker.domain.Table;
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
     * @return First index contains messages, second contains the query result.
     */
    public List<Object> performQueryToTask(List<String> messages, Long taskId, String query, Long categoryId) {
        messages.add("Query sent");
        Task task = taskRepository.findOne(taskId);
        
        if (task.getSolution() != null && taskResultService.evaluateSubmittedQueryResult(task, query)) {
            messages.add("Your answer is correct");
            pastQueryService.saveNewPastQuery(accountService.getAuthenticatedUser().getUsername(), task, query, true, categoryId);
        } else {
            pastQueryService.saveNewPastQuery(accountService.getAuthenticatedUser().getUsername(), task, query, false, categoryId);
        }
        
        return Arrays.asList(messages, databaseService.performQuery(task.getDatabase().getId(), query));
    }
}
