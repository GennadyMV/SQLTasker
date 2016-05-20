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

    
//    @JoinTable(name = "APP_USER_USER_PROFILE", 
//             joinColumns = { @JoinColumn(name = "USER_ID") }, 
//             inverseJoinColumns = { @JoinColumn(name = "USER_PROFILE_ID") })
    
    
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Task> taskList;    
    
    @NotNull
    @Convert(converter = LocalDatePersistenceConverter.class)
    private LocalDate starts;

    @NotNull
    @Convert(converter = LocalDatePersistenceConverter.class)
    private LocalDate expires;

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
     * @return date when user can do tasks on category.
     */
    public LocalDate getStarts() {
        return starts;
    }

    /**
     *
     * @param startDate set date when user can do tasks on category.
     */
    public void setStarts(LocalDate startDate) {
        this.starts = startDate;
    }

    /**
     *
     * @return get the date when user can't get point from task which are in category.
     */
    public LocalDate getExpires() {
        return expires;
    }

    /**
     *
     * @param expires date when user can't get point from tasks which are in category.
     */
    public void setExpires(LocalDate expires) {
        this.expires = expires;
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
}
