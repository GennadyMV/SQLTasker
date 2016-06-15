package wepaht.SQLTasker.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wepaht.SQLTasker.domain.Account;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.CategoryDetail;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.domain.Submission;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.repository.SubmissionRepository;

@Service
public class SubmissionService {
    
    @Autowired
    private SubmissionRepository repository;
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private CategoryDetailService detailService;

    @Autowired
    private TaskResultService taskResultService;

    public void createNewSubmisson(Task task, String query, Boolean correct, Category category, Course course) {
        repository.save(new Submission(accountService.getAuthenticatedUser(), task, category, course, query, correct));
    }
    
    public List<Submission> listAllSubmissions() {
        return repository.findAll();
    }
    
    public Integer getAccountPoints(Account account) {
        return repository.getPointsByAccount(account);
    }
    
    public List getAccountSubmissions(Account account) {
        return repository.getTaskNameAndPointsByAccount(account);
    }
    
    public List getAllPoints() {
        return repository.getAllPoints();
    }
    
    public List exportAllPoints() {
        return repository.exportAllPoints();
    }

    int getAccountCoursePoints(Account account, Course course) {
        return repository.getPointsByAccountAndCourse(account, course);
    }

    Integer getAccountCourseCategoryPoints(Account account, Course course, Category category) {
        return repository.getPointsByAccountAndCourseAncCategory(account, course, category);
    }

    List<Submission> getSubmissionByAccountAndCourseAndCategoryAndTaskAndPoints(Account account, Course course, Category category, Task task, Boolean points) {
        return repository.findByAccountAndCourseAndCategoryAndTaskAndPoints(account, course, category, task, points);
    }

    public Boolean createNewSubmissionAndCheckPoints(Task task, String query, Category category, Course course) {
        Boolean isCorrect;
        
        if (task.getSolution() != null) {
            isCorrect = taskResultService.evaluateSubmittedQueryResult(task, query);
        } else {
            isCorrect = false;
        }
        
        if (course != null) isCorrect = isCourseActive(isCorrect, course);
        
        if (category != null) isCorrect = isCategoryActive(isCorrect, course, category);
        
        createNewSubmisson(task, query, isCorrect, category, course);
        
        return isCorrect;
    }
    
    private Boolean isCourseActive(Boolean isCorrect, Course course) {
        if (course.getStarts() != null) {
            isCorrect = isCorrect && course.getStarts().isBefore(LocalDate.now());
        }
        if (course.getExpires() != null) {
            isCorrect = isCorrect && course.getExpires().isAfter(LocalDate.now());
        }
        return isCorrect;
    }

    private Boolean isCategoryActive(Boolean isCorrect, Course course, Category category) {
        List<CategoryDetail> details = detailService.getCategoryDetailsByCourseAndCategory(course, category);
        if (details.size() > 0) {
            CategoryDetail detail = details.get(0);
            if (detail.getStarts() != null) {
                isCorrect = isCorrect && detail.getStarts().isBefore(LocalDate.now());
            }
            if (detail.getExpires() != null) {
                isCorrect = isCorrect && detail.getExpires().isAfter(LocalDate.now());
            }
        }
        return isCorrect;
    }
}
