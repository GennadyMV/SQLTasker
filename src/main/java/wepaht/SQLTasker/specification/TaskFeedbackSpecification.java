package wepaht.SQLTasker.specification;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.domain.TaskFeedback;
import wepaht.SQLTasker.domain.TaskFeedback_;

public class TaskFeedbackSpecification {

    private TaskFeedbackSpecification() {

    }
    
    public static Specification<TaskFeedback> searchFeedback(
            List<Task> tasks,
            LocalDateTime after,
            LocalDateTime before) {
        
        return (Root<TaskFeedback> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            return cb.and(dateIsAfterIfGiven(after, cb, root),
                    dateIsBeforeIfGiven(before, cb, root),
                    taskEqualsIfGiven(tasks, cb, root));
        };
    }
    
    private static Predicate dateIsAfterIfGiven(LocalDateTime after, CriteriaBuilder cb, Root<TaskFeedback> root) {
        if (after != null) {
            return cb.greaterThanOrEqualTo(root.<LocalDateTime>get(TaskFeedback_.created), after);
        }
        return cb.or(cb.isNull(root.<LocalDateTime>get(TaskFeedback_.created)),
                cb.isNotNull(root.<LocalDateTime>get(TaskFeedback_.created)));
    }
    
    private static Predicate dateIsBeforeIfGiven(LocalDateTime before, CriteriaBuilder cb, Root<TaskFeedback> root) {
        if (before != null) {
            return cb.lessThanOrEqualTo(root.<LocalDateTime>get(TaskFeedback_.created), before);
        }
        return cb.or(cb.isNull(root.<LocalDateTime>get(TaskFeedback_.created)),
                cb.isNotNull(root.<LocalDateTime>get(TaskFeedback_.created)));
    }
    
    private static Predicate taskEqualsIfGiven(List<Task> tasks, CriteriaBuilder cb, Root<TaskFeedback> root) {
        if (tasks != null && !tasks.isEmpty()) {
            Predicate pre = null;
            for (Task task : tasks) {
                if (pre == null) {
                    pre = cb.equal(root.<Task>get(TaskFeedback_.task), task);
                } else {
                    pre = cb.or(pre, cb.equal(root.<Task>get(TaskFeedback_.task), task));
                }
            }
            return pre;
        }
        return cb.or(cb.isNull(root.<Task>get(TaskFeedback_.task)),
                cb.isNotNull(root.<Task>get(TaskFeedback_.task)));
    }
}
