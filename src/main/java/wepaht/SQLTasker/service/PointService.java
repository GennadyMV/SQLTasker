package wepaht.SQLTasker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wepaht.SQLTasker.domain.Table;
import java.util.*;
import wepaht.SQLTasker.domain.Account;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.domain.PointHolder;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.repository.TaskRepository;

@Service
public class PointService {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    SubmissionService submissionService;

    @Autowired
    AccountService accountService;

    public Table pointsTable;

    public Integer getPointsByUsername(String username) {
        Account account = accountService.getAccountByUsername(username);
        return submissionService.getAccountPoints(account);
    }

    public Table getExercisesAndAwardedByUsername(String username) {
        pointsTable = new Table("points");
        List<String> columns = new ArrayList<>();
        columns.add("exercise");
        columns.add("points");
        pointsTable.setColumns(columns);
        pointsTable.setRows(submissionService.getAccountSubmissions(accountService.getAccountByUsername(username)));

        return pointsTable;
    }

    public Table getAllPoints() {
        pointsTable = new Table("points");
        List<String> columns = new ArrayList<>();
        columns.add("username");
        columns.add("points");
        pointsTable.setColumns(columns);
        pointsTable.setRows(submissionService.getAllPoints());

        return pointsTable;
    }

    public List<PointHolder> exportAllPoints() {
        return submissionService.exportAllPoints();
    }

    public int getCoursePoints(Course course) {
        Account account = accountService.getAuthenticatedUser();
        return submissionService.getAccountCoursePoints(account, course);
    }

    public Integer getCourseCategoryPoints(Course course, Category category) {
        Account account = accountService.getAuthenticatedUser();
        return submissionService.getAccountCourseCategoryPoints(account, course, category);
    }
    
    public boolean hasUserDoneTaskCorrectly(Account account, Course course, Category category, Task task) {
        return submissionService.getSubmissionByAccountAndCourseAndCategoryAndTaskAndPoints(account, course, category, task, (boolean) true).size() > 0;
    }
}
