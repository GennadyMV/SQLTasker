package wepaht.SQLTasker.domain;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class CategoryDetails extends AbstractPersistable<Long> {

    @ManyToOne
    private Course course;

    @ManyToOne
    private Category category;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate starts;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate expires;
    
    public CategoryDetails() {
        
    }
    
    public CategoryDetails(Course course, Category category, LocalDate starts, LocalDate expires) {
        this.course = course;
        this.category = category;
        this.starts = starts;
        this.expires = expires;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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
}
