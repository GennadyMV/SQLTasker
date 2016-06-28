package wepaht.SQLTasker.domain;

import java.util.List;


public interface Account {
    public List<Course> getCourses();

    public void setCourses(List<Course> courses);

    public String getUsername();    

    public void setUsername(String username);
    

    public String getRole();

    public void setRole(String role);

    public Boolean getDeleted();

    public void setDeleted(Boolean deleted);
}
