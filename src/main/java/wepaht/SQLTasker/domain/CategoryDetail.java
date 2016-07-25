package wepaht.SQLTasker.domain;

import java.time.LocalDate;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.format.annotation.DateTimeFormat;
import wepaht.SQLTasker.converter.LocalDatePersistenceConverter;

@Entity
public class CategoryDetail extends AbstractPersistable<Long> {

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    private Course course;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    private Category category;

    @Convert(converter = LocalDatePersistenceConverter.class)
    private LocalDate starts;
    
    @Convert(converter = LocalDatePersistenceConverter.class)
    private LocalDate expires;
    
    public CategoryDetail() {
        
    }
    
    public CategoryDetail(Course course, Category category, LocalDate starts, LocalDate expires) {
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
