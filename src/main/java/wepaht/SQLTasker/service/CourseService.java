package wepaht.SQLTasker.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import static wepaht.SQLTasker.constant.ConstantString.ATTRIBUTE_COURSES;
import static wepaht.SQLTasker.constant.ConstantString.ATTRIBUTE_MESSAGES;
import static wepaht.SQLTasker.constant.ConstantString.MESSAGE_SUCCESSFUL_ACTION;
import wepaht.SQLTasker.domain.Account;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.CategoryDetail;
import wepaht.SQLTasker.domain.CategoryDetailsWrapper;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.domain.TmcAccount;
import static wepaht.SQLTasker.constant.ConstantString.MESSAGE_UNAUTHORIZED_ACCESS;
import static wepaht.SQLTasker.constant.ConstantString.MESSAGE_UNAUTHORIZED_ACTION;
import static wepaht.SQLTasker.constant.ConstantString.ROLE_STUDENT;
import wepaht.SQLTasker.repository.CourseRepository;

@Service
public class CourseService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private CourseRepository repository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryDetailService categoryDetailsService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private PointService pointService;

    private final String redirectCourses = "redirect:/courses";

    public String courseListing(Model model) {

        getCourses(model);

        return "courses";
    }

    public void getCourses(Model model) {
        Account user = accountService.getAuthenticatedUser();
        if (user == null || user.getRole().equals("STUDENT")) {
            model.addAttribute(ATTRIBUTE_COURSES, getActiveCourses());
        } else {
            model.addAttribute(ATTRIBUTE_COURSES, repository.findAll());
        }
    }

    public String createCourse(RedirectAttributes redirectAttributes, String name, String starts, String expires, String description, List<Long> categoryIds) {
        Account user = accountService.getAuthenticatedUser();
        if (user.getRole().equals(ROLE_STUDENT)) {
            if (redirectAttributes != null) {
                redirectAttributes.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACTION);
            }
            return redirectCourses;
        }

        String redirectAddress = redirectCourses;
        List<String> messages = new ArrayList<>();

        Course course = setCourseAttributes(name, description, categoryIds, starts, expires, messages);

        redirectAddress = saveCourse(course, messages, redirectAttributes, redirectAddress);

        if (redirectAttributes != null) {
            redirectAttributes.addFlashAttribute(ATTRIBUTE_MESSAGES, messages);
        }

        return redirectAddress;
    }

    public Course setCourseAttributes(String name, String description, List<Long> categoryIds, String starts, String expires, List<String> messages) {
        Course course = new Course();
        course.setName(name);
        course.setDescription(description);
        List<Category> categories = categoryService.findCategoriesByIds(categoryIds);
        course.setCourseCategories(categories);
        course = setDates(course, starts, expires, messages);
        return course;
    }

    private String saveCourse(Course course, List<String> messages, RedirectAttributes redirectAttributes, String redirectAddress) {
        try {
            course = repository.save(course);
            messages.add("New course created");

            if (repository.getCourseCategories(course).size() > 0) {
                redirectAddress = redirectCourses + "/{id}/details";
                redirectAttributes.addAttribute("id", course.getId());
            }
        } catch (Exception e) {
            messages.add("Course creation failed");
            redirectAttributes.addAttribute("course", course);
            redirectAddress = "redirect:/courses/create";
        }
        return redirectAddress;
    }

    public Course saveCourse(Course course) {
        return repository.save(course);
    }

    private Course setDates(Course course, String starts, String expires, List<String> messages) {
        LocalDate startDate = null;
        LocalDate expireDate = null;

        try {
            startDate = LocalDate.parse(starts);
            course.setStarts(startDate);
            messages.add("Start-date added");
        } catch (Exception e) {
        }

        try {
            expireDate = LocalDate.parse(expires);

            if (startDate == null || expireDate.isAfter(startDate)) {
                course.setExpires(expireDate);
                messages.add("Expire-date added");
            }
        } catch (Exception e) {
        }

        return course;
    }

    public String courseCreateForm(Model model, RedirectAttributes redirAttr) {
        Account user = accountService.getAuthenticatedUser();

        if (user.getRole().equals(ROLE_STUDENT)) {
            redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACCESS);
            return "redirect:/courses";
        }

        model.addAttribute("categories", categoryService.findAllCategories());
        model.addAttribute("actionURL", "/courses");
        return "courseForm";
    }

    @Transactional
    public String getCourse(Model model, RedirectAttributes redirAttr, Long courseId) {
        Course course = repository.findOne(courseId);
        List<CategoryDetail> details = getActiveCategories(course);
        Account user = accountService.getAuthenticatedUser();

        if (course == null || !isActiveCourse(user, course)) {
            redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACCESS);
            return "redirect:/courses";
        }

        model.addAttribute("course", course);
        model.addAttribute("points", pointService.getCoursePoints(course));
        model.addAttribute("joined", course.getStudents().contains(accountService.getAuthenticatedUser()));
        if (!details.isEmpty()) {
            model.addAttribute("details", details);
        }
        return "course";
    }

    @Transactional
    public String deleteCourse(RedirectAttributes redirectAttributes, Long course) {
        List<String> messages = new ArrayList<>();
        Account user = accountService.getAuthenticatedUser();

        if (user.getRole().equals(ROLE_STUDENT)) {
            messages.add(MESSAGE_UNAUTHORIZED_ACTION);
            if (redirectAttributes != null) {
                redirectAttributes.addFlashAttribute(ATTRIBUTE_MESSAGES, messages);
                redirectAttributes.addAttribute("courseId", course);
            }

            return "redirect:/courses/{courseId}";
        }

        try {
            Course deletingCourse = repository.findOne(course);
            if (deletingCourse.getSubmissions() != null) {
                deletingCourse.getSubmissions().stream().forEach((sub) -> {
                    sub.setCourse(null);
                });
            }
            deletingCourse.setDeleted(true);
            messages.add("Course " + deletingCourse.getName() + " deleted");
        } catch (Exception e) {
            messages.add("Course deletion failed");
        }

        if (redirectAttributes != null) {
            redirectAttributes.addFlashAttribute("messages", messages);
        }

        return redirectCourses;
    }

    public String editForm(Model model, RedirectAttributes redirAttr, Long courseId) {
        Account user = accountService.getAuthenticatedUser();
        if (user.getRole().equals(ROLE_STUDENT)) {
            if (redirAttr != null) {
                redirAttr.addAttribute("courseId", courseId);
                redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACCESS);
            }
            return "redirect:/courses/{courseId}";
        }
        Course course = repository.findOne(courseId);

        model.addAttribute("id", courseId);
        model.addAttribute("course", course);
        model.addAttribute("actionURL", "/courses/" + courseId + "/edit");
        model.addAttribute("categories", categoryService.findAllCategories());

        return "courseForm";
    }

    public String editCourse(RedirectAttributes redirAttr, Long id, String name, String starts, String expires, String description, List<Long> categoryIds) {
        Account user = accountService.getAuthenticatedUser();
        if (user.getRole().equals(ROLE_STUDENT)) {
            if (redirAttr != null) {
                redirAttr.addAttribute("courseId", id);
                redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACCESS);
            }
            return "redirect:/courses/{courseId}";
        }
        String redirectAddress = redirectCourses + "/" + id;
        List<String> messages = new ArrayList<>();
        Course course = repository.findOne(id);
        course.setName(name);
        course.setDescription(description);
        course = setDates(course, starts, expires, messages);
        course.setCourseCategories(categoryService.findCategoriesByIds(categoryIds));

        redirectAddress = saveEditCourse(course, messages, redirectAddress, redirAttr);

        return redirectAddress;
    }

    private String saveEditCourse(Course course, List<String> messages, String redirectAddress, RedirectAttributes redirectAttributes) {
        try {
            if (course.getCourseCategories() != null && !course.getCourseCategories().isEmpty()) {
                redirectAddress = redirectAddress + "/details";
            }
            repository.save(course);
            messages.add("Course edited");
        } catch (Exception e) {
            messages.add("Course edit failed");
            redirectAddress = redirectAddress + "/edit";
        }
        if (redirectAttributes != null) {
            redirectAttributes.addFlashAttribute("messages", messages);
        }
        return redirectAddress;
    }

    public List<Course> getCoursesByName(String name) {
        return repository.findByNameAndDeletedFalse(name);
    }

    @Transactional
    public void removeCategoryFromCourses(List<Course> courses, Category deleting) {
        courses.stream().forEach((course) -> {
            course.getCourseCategories().remove(deleting);
        });
    }

    @Transactional
    public String joinOrLeaveCourse(RedirectAttributes redirAttr, Long id) {
        Course course = repository.findOne(id);
        
        if (course.getStudents().contains(accountService.getAuthenticatedUser())) {
            return leaveCourse(redirAttr, id);            
        } else {
            return joinCourse(redirAttr, id);
        }                
    }
    
    @Transactional
    public String joinCourse(RedirectAttributes redirAttr, Long id) {
        Course course = repository.findOne(id);
        List<String> messages = new ArrayList<>();
        Account user = accountService.getAuthenticatedUser();
        if (user.getRole().equals(ROLE_STUDENT) && !isActiveCourse(user, course)) {
            if (redirAttr != null) {
                redirAttr.addAttribute("courseId", id);
                redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACTION);
            }
            return "redirect:/courses/{courseId}";
        }

        try {
            if (!course.getStudents().contains(accountService.getAuthenticatedUser())) {
                course.getStudents().add(accountService.getAuthenticatedUser());
                messages.add("Joined to course " + course.getName());
            }
        } catch (Exception e) {
            System.out.println(course.getName());
            messages.add("Could not join course " + course.getName());
        }

        if (redirAttr != null) {
            redirAttr.addFlashAttribute("messages", messages);
            redirAttr.addAttribute("id", id);
        }

        return redirectCourses + "/{id}";
    }

    @Transactional
    public String leaveCourse(RedirectAttributes redirectAttributes, Long id) {
        List<String> messages = new ArrayList<>();
        Course course = repository.findOne(id);

        try {
            course.getStudents().remove(accountService.getAuthenticatedUser());
            messages.add("You have left course " + course.getName());
        } catch (Exception e) {
            messages.add("You failed to leave course" + course.getName());
        }

        if (redirectAttributes != null) {
            redirectAttributes.addFlashAttribute("messages", messages);
        }
        return redirectCourses;
    }

    @Transactional
    public String getCategoryDetails(Model model, RedirectAttributes redirAttr, Long id) {
        Account user = accountService.getAuthenticatedUser();
        if (user.getRole().equals(ROLE_STUDENT)) {
            if (redirAttr != null) {
                redirAttr.addAttribute("courseId", id);
                redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACTION);
            }
            return "redirect:/courses/{courseId}";
        }
        Course course = repository.findOne(id);
        CategoryDetailsWrapper wrapper = new CategoryDetailsWrapper();
        wrapper.setCategoryDetailsList((ArrayList<CategoryDetail>) categoryDetailsService.categoriesToCategoryDetails(course.getCourseCategories(), course));

//        model.addAttribute("categoryDetailsList", categoryDetailsService.categoriesToCategoryDetails(course.getCourseCategories(), course));
        model.addAttribute("actionURL", "/courses/" + id + "/details");
        model.addAttribute("course", course);
        model.addAttribute("wrapper", wrapper);

        return "categoryDetails";
    }

    public String setCategoryDetails(RedirectAttributes redirAttr, List<CategoryDetail> categoryDetailsList, Long id) {
        Account user = accountService.getAuthenticatedUser();
        if (user.getRole().equals(ROLE_STUDENT)) {
            if (redirAttr != null) {
                redirAttr.addAttribute("courseId", id);
                redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACCESS);
            }
            return "redirect:/courses/{courseId}";
        }
        String redirectAddress = redirectCourses + "/{id}";
        List<String> messages = new ArrayList<>();
        Course course = repository.findOne(id);
        int detailsSaved = categoryDetailsService.saveCategoryDetailsList(categoryDetailsList);
        messages.add(detailsSaved + " category details saved");

        redirAttr.addFlashAttribute("messages", messages);
        redirAttr.addFlashAttribute("id", id);

        return redirectAddress;
    }

    public String getCourseCategory(Model model, RedirectAttributes redirectAttributes, Long courseId, Long categoryId) {
        Course course = repository.findOne(courseId);
        Category category = categoryService.getCategoryById(categoryId);

        boolean hasCategory = courseHasCategory(course, category);
        boolean categoryIsActive = isCategoryActive(course, category);

        if (!hasCategory || !categoryIsActive) {
            return noSuchCategoryInCourse(course, redirectAttributes);
        }

        model.addAttribute("points", pointService.getCourseCategoryPoints(course, category));
        model.addAttribute("course", course);
        model.addAttribute("category", category);
        model.addAttribute("taskList", category.getTaskList());
        return "category";
    }

    private String noSuchCategoryInCourse(Course course, RedirectAttributes redirectAttributes) {
        List<String> messages = Arrays.asList("No such category in course " + course.getName());
        redirectAttributes.addFlashAttribute("messages", messages);
        redirectAttributes.addAttribute("id", course.getId());
        return "redirect:/courses/{id}";
    }

    public String getCourseCategoryTask(Model model, RedirectAttributes redirectAttr, Long courseId, Long categoryId, Long taskId) {
        Course course = repository.findOne(courseId);
        Category category = categoryService.getCategoryById(categoryId);
        Task task = taskService.getTaskById(taskId);

        boolean courseHasCategory = courseHasCategory(course, category);
        boolean categoryIsActive = isCategoryActive(course, category);

        if (!courseHasCategory || !categoryIsActive) {
            return noSuchCategoryInCourse(course, redirectAttr);
        }

        if (!categoryService.categoryHasTask(category, task)) {
            return noSuchTaskInCategory(course, category, redirectAttr);
        }

        if (model != null) {
            model.addAttribute("course", course);
            model.addAttribute("category", category);
            model.addAttribute("task", task);
            model.addAttribute("owned", accountService.isOwned(task));
            model.addAttribute("database", task.getDatabase());
            model.addAttribute("next", categoryService.getNextTask(category, task));
            model.addAttribute("prev", categoryService.getPreviousTask(category, task));
        }

        return "task";
    }

    public boolean courseHasCategory(Course course, Category category) {
//        return course.getCourseCategories().stream().filter(cat -> cat.getId().equals(category.getId())).findAny().isPresent();
        return course.getCourseCategories().contains(category);
    }

    private String noSuchTaskInCategory(Course course, Category category, RedirectAttributes redirectAttr) {
        List<String> messages = Arrays.asList("No such task in category " + category.getName());
        redirectAttr.addFlashAttribute("messages", messages);
        redirectAttr.addAttribute("courseId", course.getId());
        redirectAttr.addAttribute("categoryId", category.getId());
        return "redirect:/courses/{courseId}/category/{categoryId}";
    }

    public String createQuery(RedirectAttributes redirectAttr, String query, Long courseId, Long categoryId, Long taskId) {
        Course course = repository.findOne(courseId);
        TmcAccount loggedUser = accountService.getAuthenticatedUser();
        String redirectAddress = "redirect:/courses/{courseId}/category/{categoryId}/tasks/{taskId}";

        if (!loggedUser.getRole().equals(ROLE_STUDENT) || course.getStudents().contains(loggedUser)) {
            List<Object> messagesAndQueryResult = taskService.performQueryToTask(new ArrayList<>(), taskId, query, categoryId, courseId);
            if (redirectAttr != null) {
                redirectAttr.addFlashAttribute("query", query);
                redirectAttr.addFlashAttribute("tables", messagesAndQueryResult.get(1));
                redirectAttr.addFlashAttribute("messages", messagesAndQueryResult.get(0));
            }
            if ((Boolean) messagesAndQueryResult.get(2)) {
                redirectAddress = redirectAddress + "/feedback";
            }
        } else {
            if (redirectAttr != null) {
                redirectAttr.addFlashAttribute("messages", "You have not joined course " + course.getName());
            }
        }

        if (redirectAttr != null) {
            redirectAttr.addAttribute("taskId", taskId);
            redirectAttr.addAttribute("categoryId", categoryId);
            redirectAttr.addAttribute("courseId", courseId);
        }

        return redirectAddress;
    }

    public Course getCourseById(Long id) {
        if (id == null) {
            return null;
        }
        return repository.findOne(id);
    }

    private List<Course> getActiveCourses() {
        LocalDate date = LocalDate.now();
        return repository.findByStartsBeforeAndExpiresAfter(date);
    }

    private boolean isActiveCourse(Account user, Course course) {
        LocalDate now = LocalDate.now();
        if (user.getRole().equals("STUDENT")) {
            return (course.getStarts() == null || (course.getStarts() != null && (course.getStarts().isBefore(now) || course.getStarts().equals(now))))
                    && (course.getExpires() == null || (course.getExpires() != null && (course.getExpires().isAfter(now) || course.getExpires().equals(now))));
        }

        return true;
    }

    private List<CategoryDetail> getActiveCategories(Course course) {
        TmcAccount user = accountService.getAuthenticatedUser();
        if (user.getRole().equals("STUDENT")) {
            return categoryDetailsService.getActiveCategoryDetailsBycourse(course);
        }

        return categoryDetailsService.getCourseCategoryDetails(course);
    }

    private boolean isCategoryActive(Course course, Category category) {
        TmcAccount account = accountService.getAuthenticatedUser();

        if (account.getRole().equals("STUDENT")) {
            return categoryDetailsService.isCategoryActive(course, category);
        }

        return true;
    }

    public String deleteCourseCategoryTask(RedirectAttributes redirAttr, Long courseId, Long categoryId, Long taskId) {
        Account user = accountService.getAuthenticatedUser();
        Course course = repository.findOne(courseId);
        Category category = categoryService.getCategoryById(categoryId);
        Task task = taskService.getTaskById(taskId);

        if (courseHasCategory(course, category)) {
            taskService.deleteTask(user, category, task, redirAttr, taskId);
        }

        return "redirect:/courses/{courseId}/category/{categoryId}";
    }

    public String getCategoryEditTaskForm(Model model, RedirectAttributes redirAttr, Long courseId, Long categoryId, Long taskId) {
        Account user = accountService.getAuthenticatedUser();
        Task task = taskService.getTaskById(taskId);

        if (!user.getRole().equals(ROLE_STUDENT)) {
            return taskService.setEditForm(model, task);
        }
        
        redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACCESS);
        return "redirect:/courses";
    }

    public String categoryeditTask(RedirectAttributes redirAttr, Long courseId, Long categoryId, Long taskId, Long databaseId, String name, String solution, String description) {
        Account user = accountService.getAuthenticatedUser();
        
        if (!user.getRole().equals(ROLE_STUDENT)) {
            Task task = taskService.getTaskById(taskId);
            if (taskService.updateTask(task, solution, redirAttr, "", description, name, databaseId)) {
                redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_SUCCESSFUL_ACTION + ": updated task " + task.getName());
            }
            return "redirect:/courses/{courseId}/category/{categoryId}/tasks/{taskId}";
        }
        
        redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACTION);
        return "redirect:/courses";
    }

    public String getCourseCategoryNextTask(RedirectAttributes redirectAttr, Long courseId, Long categoryId, Long taskId) {
        Task next = categoryService.getNextTask(categoryId, taskId);
        redirectAttr.addAttribute("courseId", courseId);
        redirectAttr.addAttribute("categoryId", categoryId);
        redirectAttr.addAttribute("nextId", next.getId());
        return "redirect:/courses/{courseId}/category/{categoryId}/tasks/{nextId}";
    }

    public String getCourseCategoryPreviousTask(RedirectAttributes redirectAttr, Long courseId, Long categoryId, Long taskId) {
        Task previous = categoryService.getPreviousTask(categoryId, taskId);
        redirectAttr.addAttribute("courseId", courseId);
        redirectAttr.addAttribute("categoryId", categoryId);
        redirectAttr.addAttribute("prevId", previous.getId());
        return "redirect:/courses/{courseId}/category/{categoryId}/tasks/{prevId}";
    }
    
    public List<Course> getCoursesByAccount(Account account) {
        return repository.findByStudentsAndDeletedFalse((TmcAccount) account);
    }
    
    public List<Course> getCoursesByCurrentUser() {
        return repository.findByStudentsAndDeletedFalse(accountService.getAuthenticatedUser());
    }
}
