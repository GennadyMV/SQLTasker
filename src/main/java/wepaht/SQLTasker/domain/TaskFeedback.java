package wepaht.SQLTasker.domain;

import java.time.LocalDateTime;
import java.util.Map;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class TaskFeedback extends AbstractPersistable<Long>{

    @ManyToOne(fetch = FetchType.EAGER)
    private Task task;
    
    @ElementCollection
    private Map<String, String> feedback;
    
    private final LocalDateTime created;
    
    private boolean deleted;
    
    public TaskFeedback() {   
        created = LocalDateTime.now();
        deleted = false;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        if (task == null) {
            setDeleted(true);
        } else {
            setDeleted(false);
        }
        this.task = task;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Map<String, String> getFeedback() {
        return feedback;
    }

    public void setFeedback(Map<String, String> feedback) {
        this.feedback = feedback;
    }
}
