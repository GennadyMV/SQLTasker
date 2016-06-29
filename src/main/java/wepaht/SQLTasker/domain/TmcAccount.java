/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wepaht.SQLTasker.domain;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import org.springframework.data.jpa.domain.AbstractPersistable;
import static wepaht.SQLTasker.library.StringLibrary.*;

/**
 *
 * @author mcraty
 */
@Entity
public class TmcAccount extends AbstractPersistable<Long> implements Account {

    @ManyToMany(mappedBy = "students")
    private List<Course> courses;

    @Column(unique = true)
    private String username;

    private String accountRole;

    private Boolean deleted;

    public TmcAccount() {
        deleted = false;
    }

    @Override
    public List<Course> getCourses() {
        return courses;
    }

    @Override
    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getRole() {
        return accountRole;
    }

    @Override
    public void setRole(String role) {
        if (role.equals(ROLE_STUDENT) || role.equals(ROLE_TEACHER) || role.equals(ROLE_ADMIN)) {
            this.accountRole = role;
        }
    }

    @Override
    public Boolean getDeleted() {
        return deleted;
    }

    @Override
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
