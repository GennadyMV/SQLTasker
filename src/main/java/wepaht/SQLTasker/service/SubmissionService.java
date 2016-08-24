package wepaht.SQLTasker.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import static org.springframework.data.jpa.domain.Specifications.where;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import static wepaht.SQLTasker.constant.ConstantString.ATTRIBUTE_MESSAGES;
import static wepaht.SQLTasker.constant.ConstantString.ATTRIBUTE_SUBMISSIONS;
import static wepaht.SQLTasker.constant.ConstantString.MESSAGE_UNAUTHORIZED_ACCESS;
import static wepaht.SQLTasker.constant.ConstantString.REDIRECT_DEFAULT;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.CategoryDetail;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.domain.Submission;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.domain.TmcAccount;
import static wepaht.SQLTasker.specification.SubmissionSpecification.searchSubmissions;
import wepaht.SQLTasker.repository.SubmissionRepository;
import wepaht.SQLTasker.specification.SubmissionSpecification;
import wrapper.SubmissionSearchWrapper;

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

    @Autowired
    private TaskService taskService;

    @Autowired
    private CategoryService catService;

    @Autowired
    private CourseService courseService;

    public void createNewSubmisson(Task task, String query, Boolean correct, Category category, Course course) {
        repository.save(new Submission(accountService.getAuthenticatedUser(), task, category, course, query, correct));
    }

    public List<Submission> listAllSubmissions() {
        return repository.findAll();
    }

    public Integer getAccountPoints(TmcAccount account) {
        return repository.getPointsByAccount(account);
    }

    public List getAccountSubmissions(TmcAccount account) {
        return repository.getTaskNameAndPointsByAccount(account);
    }

    public List getAllPoints() {
        return repository.getAllPoints();
    }

    public List exportAllPoints() {
        return repository.exportAllPoints();
    }

    int getAccountCoursePoints(TmcAccount account, Course course) {
        return repository.getPointsByAccountAndCourse(account, course);
    }

    Integer getAccountCourseCategoryPoints(TmcAccount account, Course course, Category category) {
        return repository.countPointsByAccountAndCourseAndCategory(account, course, category).intValue();
    }

    List<Submission> getSubmissionByAccountAndCourseAndCategoryAndTaskAndPoints(TmcAccount account, Course course, Category category, Task task, Boolean points) {
        return repository.findByAccountAndCourseAndCategoryAndTaskAndPoints(account, course, category, task, points);
    }

    public Boolean createNewSubmissionAndCheckPoints(Task task, String query, Category category, Course course) {
        Boolean isCorrect = false;

        if (course != null) {
            isCorrect = isCourseActive(true, course);

            if (task.getSolution() != null) {
                isCorrect = isCorrect && taskResultService.evaluateSubmittedQueryResult(task, query);
            } else {
                isCorrect = false;
            }
            if (category != null) {
                isCorrect = isCategoryActive(isCorrect, course, category);
            }
        }

        createNewSubmisson(task, query, isCorrect, category, course);

        return isCorrect;
    }

    public Boolean createNewSubmissionAndCheckPointsWithFeedback(Task task, String query, Category category, Course course, List<String> messages) {
        Boolean isCorrect = false;

        if (course != null) {
            isCorrect = isCourseActive(true, course);

            if (task.getSolution() != null) {
                isCorrect = isCorrect && taskResultService.evaluateSubmittedQueryResultWithFeedback(task, query, messages);
            } else {
                isCorrect = false;
            }
            if (category != null) {
                isCorrect = isCategoryActive(isCorrect, course, category);
            }
        }

        createNewSubmisson(task, query, isCorrect, category, course);

        return isCorrect;
    }

    private Boolean isCourseActive(Boolean isCorrect, Course course) {
        LocalDate now = LocalDate.now();
        if (course.getStarts() != null) {
            isCorrect = isCorrect && (course.getStarts().isBefore(now) || course.getStarts().equals(now));
        }
        if (course.getExpires() != null) {
            isCorrect = isCorrect && (course.getExpires().isAfter(now) || course.getExpires().equals(now));
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

    public String getSubmissions(Model model, RedirectAttributes redirAttr) {
        if (accountService.isUserStudent()) {
            redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACCESS);
            return REDIRECT_DEFAULT;
        }
        model.addAttribute(ATTRIBUTE_SUBMISSIONS, repository.findAll(searchSubmissions(null, null, null, null, null, null, null)));

        return "submissions";
    }

    public Long getCoursePoints(TmcAccount account, Course course) {
        return repository.countPointsByAccountAndCourse(account, course);
    }

    public List<Submission> getSubmissions(String user, String task, String category, String course, LocalDate after, LocalDate before, Boolean awarded) {
        List<TmcAccount> account = null;
        List<Task> tasks = null;
        List<Category> categories = null;
        List<Course> courses = null;

        if (user != null && !user.isEmpty()) {
            account = accountService.getAllAccountsByUsername(user);
        }
        if (task != null && !task.isEmpty()) {
            tasks = taskService.getAllTasksByName(task);
        }
        if (category != null && !category.isEmpty()) {
            categories = catService.getAllCategoriesByName(category);
        }
        if (course != null && !course.isEmpty()) {
            courses = courseService.getAllCoursesByName(course);
        }

        LocalDateTime afterTime = null;
        if (after != null) {
            afterTime = after.atStartOfDay();
        }
        LocalDateTime beforeTime = null;
        if (before != null) {
            beforeTime = before.atStartOfDay();
        }

        return repository.findAll(searchSubmissions(courses, categories, tasks, account, awarded, afterTime, beforeTime));
    }

    public Page<Submission> getSubmissionsPaged(String user, String task, String category, String course, LocalDate after, LocalDate before, Boolean awarded, Long page) {
        List<TmcAccount> account = null;
        List<Task> tasks = null;
        List<Category> categories = null;
        List<Course> courses = null;

        if (user != null && !user.isEmpty()) {
            account = accountService.getAllAccountsByUsername(user);
        }
        if (task != null && !task.isEmpty()) {
            tasks = taskService.getAllTasksByName(task);
        }
        if (category != null && !category.isEmpty()) {
            categories = catService.getAllCategoriesByName(category);
        }
        if (course != null && !course.isEmpty()) {
            courses = courseService.getAllCoursesByName(course);
        }

        LocalDateTime afterTime = null;
        if (after != null) {
            afterTime = after.atStartOfDay();
        }
        LocalDateTime beforeTime = null;
        if (before != null) {
            beforeTime = before.atStartOfDay();
        }
        
        Pageable pageable = new PageRequest(page.intValue(), 25, Sort.Direction.DESC, "created");

        return repository.findAll(where(searchSubmissions(courses, categories, tasks, account, awarded, afterTime, beforeTime)), pageable);
    }

    public List<Submission> getSubmissions(SubmissionSearchWrapper wrapper) {
        Boolean awarded = null;
        if (wrapper.getAwarded() != null) {
            awarded = wrapper.awardedToBoolean();
        }
        return getSubmissions(wrapper.getUser(), wrapper.getTask(), wrapper.getCategory(), wrapper.getCourse(), wrapper.getAfter(), wrapper.getBefore(), awarded);
    }

    public SubmissionSearchWrapper getWrapper(SubmissionSearchWrapper searchWrapper) {
        if (searchWrapper == null) {
            return new SubmissionSearchWrapper();
        }
        return searchWrapper;
    }

    public Integer getPreviousPage(Long page) {
        if (page > 0) {
            return page.intValue() - 1;
        }
        return null;
    }

    public Integer getNextPage(Long page, Page<Submission> pages) {
        if (page.intValue() < pages.getTotalPages()) {
            return page.intValue() + 1;
        }
        return null;
    }

    public Page<Submission> getSubmissions(SubmissionSearchWrapper wrapper, Long page) {

        Boolean awarded = null;
        if (wrapper.getAwarded() != null) {
            awarded = wrapper.awardedToBoolean();
        }
        
        return getSubmissionsPaged(wrapper.getUser(), wrapper.getTask(), wrapper.getCategory(), wrapper.getCourse(), wrapper.getAfter(), wrapper.getBefore(), awarded, page);
    }
}
