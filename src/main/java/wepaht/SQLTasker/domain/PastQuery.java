package wepaht.SQLTasker.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

/**
 * Users queries from tasks. Controls which tasks user can get points from.
 */
@Entity
public class PastQuery extends AbstractPersistable<Long> {

    private String username;

    @ManyToOne(fetch = FetchType.EAGER)
    private Task task;

    @Lob
    private String query;

    private boolean awarded;


    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    private boolean correct;

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
     * @return user's query.
     */
    public String getQuery() {
        return query;
    }

    /**
     *
     * @param query set user's query.
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     *
     * @return if query is correct.
     */
    public boolean getCorrectness() {
        return correct;
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
     * @param correct set if query is right.
     */
    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    /**
     * Sets if user  can get points from query he or she made.
     * @param canGet set true if user made query between tasks expire date and start date.
     */
    public void setAwarded(boolean canGet) {
        this.awarded = canGet;
    }

    /**
     *
     * @return true if user can get points from query, false if not.
     */
    public boolean getCanGetPoint() {
        return awarded;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
    
    
}
