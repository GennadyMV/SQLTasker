package wepaht.domain;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;


@Entity
public class Category extends AbstractPersistable<Long> {

    @NotBlank
    private String name;

    @ManyToMany
    private List<Task> taskList;

    @Temporal(TemporalType.DATE)
    @NotNull
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @NotNull
    private Date expiredDate;

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
    public Date getStartDate() {
        return startDate;
    }

    /**
     *
     * @param startDate set date when user can do tasks on category.
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     *
     * @return get the date when user can't get point from task which are in category.
     */
    public Date getExpiredDate() {
        return expiredDate;
    }

    /**
     *
     * @param expiredDate date when user can't get point from tasks which are in category.
     */
    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = expiredDate;
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
