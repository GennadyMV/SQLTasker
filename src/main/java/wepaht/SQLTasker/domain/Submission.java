package wepaht.SQLTasker.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class Submission extends AbstractPersistable<Long> {
    
    @NotNull
    @ManyToOne
    private TmcAccount account;
    
    @ManyToOne
    private Task task;
    
    @ManyToOne
    private Category category;
    
    @ManyToOne
    private Course course;
    
    private String query;
    private Boolean points;
    private final LocalDateTime created;

    public Boolean getPoints() {
        return points;
    }

    public void setPoints(Boolean points) {
        this.points = points;
    }

    public LocalDateTime getCreated() {
        return created;
    }
    
    public Submission() {
        this.created = LocalDateTime.now();
    }
    
    public Submission(TmcAccount account, Task task, Category category, Course course, String query, Boolean points) {
        this.account = account;
        this.task = task;
        this.category = category;
        this.course = course;
        this.query = query;
        this.points = points;
        this.created = LocalDateTime.now();
    }

    public TmcAccount getAccount() {
        return account;
    }

    public void setAccount(TmcAccount account) {
        this.account = account;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public boolean isPoints() {
        return points;
    }

    public void setPoints(boolean points) {
        this.points = points;
    }
}
