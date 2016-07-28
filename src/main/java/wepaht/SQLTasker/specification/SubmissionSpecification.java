package wepaht.SQLTasker.specification;

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

    public static Specification<Submission> courseAndCategoryAndTaskAndAccountAndCorrectEqualsIfGiven(Course course, Category category, Task task, TmcAccount account, Boolean correct) {
        return (Root<Submission> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {            
            return cb.and(
                    courseEqualsIfGiven(course, cb, root),
                    categoryEqualsIfGiven(category, cb, root),
                    taskEqualsIfGiven(task, cb, root),
                    pointsEqualsIfGiven(correct, cb, root),
                    accountEqualsIfGiven(account, cb, root)
            );
        };
    }

    private static Predicate courseEqualsIfGiven(Course course, CriteriaBuilder cb, Root<Submission> root) {
        if (course != null) {
            return cb.equal(root.<Course>get(Submission_.course), course);
        }
        return cb.or(cb.isNull(root.<Course>get(Submission_.course)),
                cb.isNotNull(root.<Course>get(Submission_.course)));
    }

    private static Predicate categoryEqualsIfGiven(Category category, CriteriaBuilder cb, Root<Submission> root) {
        if (category != null) {
            return cb.equal(root.<Category>get(Submission_.category), category);
        }
        return cb.or(cb.isNull(root.<Category>get(Submission_.category)),
                cb.isNotNull(root.<Category>get(Submission_.category)));
    }

    private static Predicate taskEqualsIfGiven(Task task, CriteriaBuilder cb, Root<Submission> root) {
        if (task != null) {
            return cb.equal(root.<Task>get(Submission_.task), task);
        }
        return cb.or(cb.isNull(root.<Task>get(Submission_.task)),
                cb.isNotNull(root.<Task>get(Submission_.task)));
    }

    private static Predicate accountEqualsIfGiven(TmcAccount account, CriteriaBuilder cb, Root<Submission> root) {
        if (account != null) {
            return cb.equal(root.<TmcAccount>get(Submission_.account), account);
        }
        return cb.or(cb.isNull(root.<TmcAccount>get(Submission_.account)),
                cb.isNotNull(root.<TmcAccount>get(Submission_.account)));
    }

    private static Predicate pointsEqualsIfGiven(Boolean correct, CriteriaBuilder cb, Root<Submission> root) {
        if (correct != null) {
            return cb.equal(root.<TmcAccount>get(Submission_.account), correct);
        }
        return cb.or(cb.isNull(root.<TmcAccount>get(Submission_.account)),
                cb.isNotNull(root.<TmcAccount>get(Submission_.account)));
    }
}
