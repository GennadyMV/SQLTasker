package wepaht.SQLTasker.domain;

import java.time.LocalDate;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import wepaht.SQLTasker.converter.LocalDatePersistenceConverter;


@Entity
public class Category extends AbstractPersistable<Long> {

    @NotBlank
    private String name;
    
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Task> taskList;       
    
    @ManyToMany(mappedBy = "courseCategories")
    private List<Course> courses;
    
    @OneToMany(mappedBy = "category", orphanRemoval = true)
    private List<CategoryDetail> details;

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
}
