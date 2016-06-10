package wepaht.SQLTasker.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wepaht.SQLTasker.domain.Account;
import wepaht.SQLTasker.domain.Category;
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
}
