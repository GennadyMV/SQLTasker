package wrapper;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public class SubmissionSearchWrapper {

    String user;
    String task;
    String category;
    String course;
    Integer awarded;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate before;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate after;

    public SubmissionSearchWrapper() {

    }

    public SubmissionSearchWrapper(String user, String task, String category, String course, Integer awarded, LocalDate before, LocalDate after) {
        this.user = user;
        this.task = task;
        this.category = category;
        this.course = course;
        this.awarded = awarded;
        this.before = before;
        this.after = after;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public Integer getAwarded() {
        return awarded;
    }

    public void setAwarded(Integer awarded) {
        this.awarded = awarded;
    }

    public LocalDate getBefore() {
        return before;
    }

    public void setBefore(LocalDate before) {
        this.before = before;
    }

    public LocalDate getAfter() {
        return after;
    }

    public void setAfter(LocalDate after) {
        this.after = after;
    }

    public Boolean awardedToBoolean() {
        if (awarded == null) {
            return null;
        }
        if (awarded == 0) {
            return false;
        }
        return true;
    }
}
