package wepaht.SQLTasker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.repository.TaskRepository;

@Service
public class TaskService {

    @Autowired
    TaskRepository taskRepository;
    
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
}
