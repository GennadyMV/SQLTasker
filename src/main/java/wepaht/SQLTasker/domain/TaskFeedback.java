package wepaht.SQLTasker.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class TaskFeedback extends AbstractPersistable<Long>{

    @ManyToOne(fetch = FetchType.EAGER)
    private Task task;
    
    private int difficulty;
    private int educational;
    private int effort;
    private boolean deleted;
    
    public TaskFeedback() {
        difficulty = 2;
        educational = 2;
        effort = 2;
        deleted = false;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int rating) {
        this.difficulty = betweenZeroAndFour(rating);
    }

    public int getEducational() {
        return educational;
    }

    public void setEducational(int rating) {
        this.educational = betweenZeroAndFour(rating);
    }

    public int getEffort() {
        return effort;
    }

    public void setEffort(int rating) {
        this.effort = betweenZeroAndFour(rating);
    }
    
    private int betweenZeroAndFour(int rating) {
        if (rating < 0) return 0;
        if (rating > 4) return 4;
        return rating;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
