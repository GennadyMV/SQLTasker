package wepaht.SQLTasker.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Users queries from tasks. Controls which tasks user can get points from.
 */
@Entity
public class PastQuery extends AbstractPersistable<Long> {

    private String username;

    private Long taskId;

    @Lob
    private String pastQuery;

    private boolean canGetPoint;


    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    private boolean correctness;

    /**
     *
     * @return get name of user who created query.
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username set username who created query.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return return id of the task which query is associated.
     */
    public Long getTaskId() {
        return taskId;
    }

    /**
     *
     * @param taskId set id of the task which query is associated.
     */
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    /**
     *
     * @return user's query.
     */
    public String getPastQuery() {
        return pastQuery;
    }

    /**
     *
     * @param pastQuery set user's query.
     */
    public void setPastQuery(String pastQuery) {
        this.pastQuery = pastQuery;
    }

    /**
     *
     * @return if query is correct.
     */
    public boolean getCorrectness() {
        return correctness;
    }

    /**
     *
     * @param date when query is made.
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     *
     * @return date when query is made.
     */
    public Date getDate() {
        return date;
    }

    /**
     *
     * @param correctness set if query is right.
     */
    public void setCorrectness(boolean correctness) {
        this.correctness = correctness;
    }

    /**
     * Sets if user  can get points from query he or she made.
     * @param canGet set true if user made query between tasks expire date and start date.
     */
    public void setCanGetPoint(boolean canGet) {
        this.canGetPoint = canGet;
    }

    /**
     *
     * @return true if user can get points from query, false if not.
     */
    public boolean getCanGetPoint() {
        return canGetPoint;
    }
}
