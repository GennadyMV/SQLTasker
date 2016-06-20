package wepaht.SQLTasker.domain;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.util.List;


@Entity
public class Category extends AbstractPersistable<Long> {

    @NotBlank
    private String name;
    
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Task> taskList;       
    
    @ManyToMany(mappedBy = "courseCategories")
    private List<Course> courses;
    
    @ManyToOne
    private Account owner;
    
    @OneToMany(mappedBy = "category", orphanRemoval = true)
    private List<CategoryDetail> details;
    
    @OneToMany(mappedBy = "category")
    private List<Submission> submissions;

    private String description;

    /**
     *
     * @return get name of the category.
     */
    public String getName() {
        return name;        
    }

    /**
     *
     * @param name set name of category.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return list of tasks which are on category.
     */
    public List<Task> getTaskList() {
        return taskList;
    }

    /**
     *
     * @param taskList set list of tasks to the category.
     */
    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    /**
     *
     * @return the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public List<CategoryDetail> getDetails() {
        return details;
    }

    public void setDetails(List<CategoryDetail> details) {
        this.details = details;
    }

    public List<Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(List<Submission> submissions) {
        this.submissions = submissions;
    }

    public Account getOwner() {
        return owner;
    }

    public void setOwner(Account owner) {
        this.owner = owner;
    }
}
