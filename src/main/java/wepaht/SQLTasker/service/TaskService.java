package wepaht.SQLTasker.service;

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
    
    public Map<String, Table> performQueryToTask(List<String> messages, Long taskId, String query, Long categoryId) {
        messages.add("Query sent");
        Task task = taskRepository.findOne(taskId);
        
        if (task.getSolution() != null && taskResultService.evaluateSubmittedQueryResult(task, query)) {
            messages.add("Your answer is correct");
            pastQueryService.saveNewPastQuery(accountService.getAuthenticatedUser().getUsername(), task, query, true, categoryId);
        } else {
            pastQueryService.saveNewPastQuery(accountService.getAuthenticatedUser().getUsername(), task, query, false, categoryId);
        }
        
        return databaseService.performQuery(task.getDatabase().getId(), query);
    }
}
