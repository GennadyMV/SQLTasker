package wepaht.SQLTasker.specification;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.domain.Submission;
import wepaht.SQLTasker.domain.Submission_;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.domain.TmcAccount;

public class SubmissionSpecification {

    private SubmissionSpecification() {

    }

    public static Specification<Submission> searchSubmissions(
            List<Course> courses, 
            List<Category> categories, 
            List<Task> tasks, 
            List<TmcAccount> account, 
            Boolean correct,
            LocalDateTime after,
            LocalDateTime before) {
        return (Root<Submission> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {            
            return cb.and(
                    courseEqualsIfGiven(courses, cb, root),
                    categoryEqualsIfGiven(categories, cb, root),
                    taskEqualsIfGiven(tasks, cb, root),
                    pointsEqualsIfGiven(correct, cb, root),
                    accountEqualsIfGiven(account, cb, root),
                    dateIsAfterIfGiven(after, cb, root),
                    dateIsBeforeIfGiven(before, cb, root)
            );
        };
    }

    private static Predicate courseEqualsIfGiven(List<Course> courses, CriteriaBuilder cb, Root<Submission> root) {
        if (courses != null && !courses.isEmpty()) {
            Predicate pre = null;
            for (Course course : courses) {
                if (pre == null) {
                    pre = cb.equal(root.<Course>get(Submission_.course), course);
                } else {
                    pre = cb.or(pre, cb.equal(root.<Course>get(Submission_.course), course));
                }                
            }    
            return pre;
        }
        return cb.or(cb.isNull(root.<Course>get(Submission_.course)),
                cb.isNotNull(root.<Course>get(Submission_.course)));
    }

    private static Predicate categoryEqualsIfGiven(List<Category> categories, CriteriaBuilder cb, Root<Submission> root) {
        if (categories != null && !categories.isEmpty()) {
            Predicate pre = null;
            for (Category cat : categories) {
                if (pre == null) {
                    pre = cb.equal(root.<Category>get(Submission_.category), cat);
                } else {
                    pre = cb.or(pre, cb.equal(root.<Category>get(Submission_.category), cat));
                }
            }
            return pre;
        }
        return cb.or(cb.isNull(root.<Category>get(Submission_.category)),
                cb.isNotNull(root.<Category>get(Submission_.category)));
    }

    private static Predicate taskEqualsIfGiven(List<Task> tasks, CriteriaBuilder cb, Root<Submission> root) {
        if (tasks != null && !tasks.isEmpty()) {
            Predicate pre = null;
            for (Task task : tasks) {
                if (pre == null) {
                    pre = cb.equal(root.<Task>get(Submission_.task), task);
                } else {
                    pre = cb.or(pre, cb.equal(root.<Task>get(Submission_.task), task));
                }
            }
            return pre;
        }
        return cb.or(cb.isNull(root.<Task>get(Submission_.task)),
                cb.isNotNull(root.<Task>get(Submission_.task)));
    }

    private static Predicate accountEqualsIfGiven(List<TmcAccount> accounts, CriteriaBuilder cb, Root<Submission> root) {
        if (accounts != null && !accounts.isEmpty()) {
            Predicate pre = null;
            for (TmcAccount account : accounts) {
                if (pre == null) {
                    pre = cb.equal(root.<TmcAccount>get(Submission_.account), account);
                } else {
                    pre = cb.or(pre, cb.equal(root.<TmcAccount>get(Submission_.account), account));
                }
            }
            return pre;
        }
        return cb.or(cb.isNull(root.<TmcAccount>get(Submission_.account)),
                cb.isNotNull(root.<TmcAccount>get(Submission_.account)));
    }

    private static Predicate pointsEqualsIfGiven(Boolean correct, CriteriaBuilder cb, Root<Submission> root) {

        if (correct != null) {
            return cb.equal(root.<Boolean>get(Submission_.points), correct);
        }
        return cb.or(cb.isNull(root.<Boolean>get(Submission_.points)),
                cb.isNotNull(root.<Boolean>get(Submission_.points)));
    }
    
    private static Predicate dateIsAfterIfGiven(LocalDateTime after, CriteriaBuilder cb, Root<Submission> root) {
        if (after != null) {
            return cb.greaterThanOrEqualTo(root.<LocalDateTime>get(Submission_.created), after);
        }
        return cb.or(cb.isNull(root.<LocalDateTime>get(Submission_.created)),
                cb.isNotNull(root.<LocalDateTime>get(Submission_.created)));
    }
    
    private static Predicate dateIsBeforeIfGiven(LocalDateTime before, CriteriaBuilder cb, Root<Submission> root) {
        if (before != null) {
            return cb.lessThanOrEqualTo(root.<LocalDateTime>get(Submission_.created), before);
        }
        return cb.or(cb.isNull(root.<LocalDateTime>get(Submission_.created)),
                cb.isNotNull(root.<LocalDateTime>get(Submission_.created)));
    }        
}
