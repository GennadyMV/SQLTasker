package wepaht.SQLTasker.domain;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.jpa.domain.AbstractPersistable;
import wepaht.SQLTasker.converter.LocalDatePersistenceConverter;

@Entity
public class Course extends AbstractPersistable<Long>{
    
    @ManyToMany
    private List<Category> courseCategories;
    
    @NotBlank
    private String name;
    
    private String description;
    
    @Convert(converter = LocalDatePersistenceConverter.class)
    private LocalDate starts;
    
    @Convert(converter = LocalDatePersistenceConverter.class)
    private LocalDate expires;
    
    @ManyToMany
    private List<Account> students;
    
    @OneToMany(mappedBy = "course", orphanRemoval = true)
    private List<CategoryDetail> details;

    public List<Category> getCourseCategories() {
        return courseCategories;
    }

    public void setCourseCategories(List<Category> courseCategories) {
        this.courseCategories = courseCategories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStarts() {
        return starts;
    }

    public void setStarts(LocalDate starts) {
        this.starts = starts;
    }

    public LocalDate getExpires() {
        return expires;
    }

    public void setExpires(LocalDate expires) {
        this.expires = expires;
    }

    public List<Account> getStudents() {
        return students;
    }

    public void setStudents(List<Account> students) {
        this.students = students;
    }

    public List<CategoryDetail> getDetails() {
        return details;
    }

    public void setDetails(List<CategoryDetail> details) {
        this.details = details;
    }
}
