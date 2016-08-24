package wrapper;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import wepaht.SQLTasker.domain.Task;

public class TaskFeedbackSearchWrapper {
    
    String task;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate before;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate after;
    
    public TaskFeedbackSearchWrapper() {
        
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public LocalDate getBefore() {
        return before;
    }

    public void setBefore(LocalDate before) {
        this.before = before;
    }

    public LocalDate getAfter() {
        return after;
    }

    public void setAfter(LocalDate after) {
        this.after = after;
    }
    
    
}
