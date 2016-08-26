package wepaht.SQLTasker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wepaht.SQLTasker.domain.Table;
import java.util.*;
import org.springframework.transaction.annotation.Transactional;
import wepaht.SQLTasker.domain.LocalAccount;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.CategoryDetail;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.wrapper.PointHolder;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.domain.TmcAccount;
import wepaht.SQLTasker.repository.TaskRepository;

@Service
public class PointService {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    SubmissionService submissionService;

    @Autowired
    AccountService accountService;
    
    @Autowired
    CategoryService catService;
    
    @Autowired
    CourseService courseService;

    public Table pointsTable;

    public Integer getPointsByUsername(String username) {
        TmcAccount account = accountService.getAccountByUsername(username);
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

    public int getUserCoursePoints(Course course) {
        TmcAccount account = accountService.getAuthenticatedUser();
        return submissionService.getAccountCoursePoints(account, course);
    }

    public Integer getCourseCategoryPoints(Course course, Category category) {
        TmcAccount account = accountService.getAuthenticatedUser();
        return submissionService.getAccountCourseCategoryPoints(account, course, category);
    }
    
    public boolean hasUserDoneTaskCorrectly(TmcAccount account, Course course, Category category, Task task) {
        return submissionService.getSubmissionByAccountAndCourseAndCategoryAndTaskAndPoints(account, course, category, task, (boolean) true).size() > 0;
    }
    
    public Double getCourseCategoryProgress(Course course, Category category) {
        Integer points = getCourseCategoryPoints(course, category);
        Integer taskCount = catService.getTaskCount(category);
        
        if (taskCount == 0) return (double) 0;
        
        return ((double)points / taskCount) * 100;
    }
    
    @Transactional
    public List<List<Object>> getCategoryTasksAndIsDone(Course course, Category category) {
        List<List<Object>> tasks = new ArrayList<>();
        
        for (Task task : category.getTaskList()) {
            tasks.add(Arrays.asList(task, hasUserDoneTaskCorrectly(accountService.getAuthenticatedUser(), course, category, task)));            
        }
        
        return tasks;
    }
    
    @Transactional
    public Map<CategoryDetail, Double> getCategoriesAndProgress(Course course) {
        HashMap<CategoryDetail, Double> categories = new HashMap<>();
        
        courseService.getActiveCategories(course).stream().forEach((cat) -> {
            categories.put(cat, getCourseCategoryProgress(course, cat.getCategory()));
        });
        
        return categories;
    }
    
    public Double getCourseProgress(Course course) {
        Integer taskCount = catService.getTaskCountByCourse(course);
        Integer points = submissionService.getAccountCoursePoints(accountService.getAuthenticatedUser(), course);
        
        return ((double) points / taskCount) * 100;
    }
    
    @Transactional
    public Map<Course, Double> getCoursesAndProgress(List<Course> courses) {
        HashMap<Course, Double> coursesAndProgress = new HashMap<>();
        
        courses.stream().forEach((course) -> {
            coursesAndProgress.put(course, getCourseProgress(course));
        });
        
        return coursesAndProgress;
    }
    
    public Map<String, List<?>> getCoursePointsByName(String courseName) {
        List<Course> courses = courseService.getCoursesByName(courseName);
        Map<String, List<?>> pointsByCourse = new HashMap<>();
        
        courses.stream().forEach((course) -> {
            pointsByCourse.put(course.getName(), submissionService.getCoursePoints(course));
        });
        
        return pointsByCourse;
    }
}
