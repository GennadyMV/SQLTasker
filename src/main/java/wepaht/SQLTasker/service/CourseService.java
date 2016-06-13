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
import wepaht.SQLTasker.domain.Account;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.CategoryDetail;
import wepaht.SQLTasker.domain.CategoryDetailsWrapper;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.domain.Task;
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

        model.addAttribute("courses", repository.findAll());

        return "courses";
    }

    public String createCourse(RedirectAttributes redirectAttributes, String name, String starts, String expires, String description, List<Long> categoryIds) {
        String redirectAddress = redirectCourses;
        List<String> messages = new ArrayList<>();
        Course course = new Course();
        course.setName(name);
        course.setDescription(description);
        List<Category> categories = categoryService.findCategoriesByIds(categoryIds);
        course.setCourseCategories(categories);
        course = setDates(course, starts, expires, messages);
        redirectAddress = saveCourse(course, messages, redirectAttributes, redirectAddress);

        if (redirectAttributes != null) {
            redirectAttributes.addFlashAttribute("messages", messages);
        }

        return redirectAddress;
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

            if (expireDate.isAfter(startDate) || startDate == null) {
                course.setExpires(expireDate);
                messages.add("Expire-date added");
            }
        } catch (Exception e) {
        }

        return course;
    }

    public String courseCreateForm(Model model) {
        model.addAttribute("categories", categoryService.findAllCategories());
        model.addAttribute("actionURL", "/courses");
        return "courseForm";
    }

    @Transactional
    public String getCourse(Model model, Long courseId) {
        Course course = repository.findOne(courseId);
        List<CategoryDetail> details = categoryDetailsService.getCourseCategoryDetails(course);
        model.addAttribute("course", course);
        model.addAttribute("points", pointService.getCoursePoints(course));
        if (!details.isEmpty()) {
            model.addAttribute("details", details);
        }
        return "course";
    }

    @Transactional
    public String deleteCourse(RedirectAttributes redirectAttributes, Long course) {
        List<String> messages = new ArrayList<>();

        try {
            Course deletingCourse = repository.findOne(course);
            if (deletingCourse.getSubmissions() != null) {
                deletingCourse.getSubmissions().stream().forEach((sub) -> {
                    sub.setCourse(null);
                });
            }
            repository.delete(deletingCourse);
            messages.add("Course " + deletingCourse.getName() + " deleted");
        } catch (Exception e) {
            messages.add("Course deletion failed");
        }

        if (redirectAttributes != null) {
            redirectAttributes.addFlashAttribute("messages", messages);
        }

        return redirectCourses;
    }

    public String editForm(Model model, Long courseId) {
        Course course = repository.findOne(courseId);

        model.addAttribute("id", courseId);
        model.addAttribute("course", course);
        model.addAttribute("actionURL", "/courses/" + courseId + "/edit");
        model.addAttribute("categories", categoryService.findAllCategories());

        return "courseForm";
    }

    public String editCourse(RedirectAttributes redirectAttributes, Long id, String name, String starts, String expires, String description, List<Long> categoryIds) {
        String redirectAddress = redirectCourses + "/" + id;
        List<String> messages = new ArrayList<>();
        Course course = repository.findOne(id);
        course.setName(name);
        course.setDescription(description);
        course = setDates(course, starts, expires, messages);
        course.setCourseCategories(categoryService.findCategoriesByIds(categoryIds));

        redirectAddress = saveEditCourse(course, messages, redirectAddress, redirectAttributes);

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
        redirectAttributes.addFlashAttribute("messages", messages);
        return redirectAddress;
    }

    public List<Course> getCoursesByName(String name) {
        return repository.findByName(name);
    }

    @Transactional
    public void removeCategoryFromCourses(List<Course> courses, Category deleting) {
        courses.stream().forEach((course) -> {
            course.getCourseCategories().remove(deleting);
        });
    }

    @Transactional
    public String joinCourse(RedirectAttributes redirectAttributes, Long id) {
        Course course = repository.findOne(id);
        List<String> messages = new ArrayList<>();

        try {
            List<Account> students = course.getStudents();
            Account currentUser = accountService.getAuthenticatedUser();
            if (!students.contains(currentUser)) {
                course.getStudents().add(currentUser);
            }
            messages.add("Joined to course " + course.getName());
        } catch (Exception e) {
            messages.add("Could not join course " + course.getName());
        }

        if (redirectAttributes != null) {
            redirectAttributes.addFlashAttribute("messages", messages);
            redirectAttributes.addAttribute("id", id);
        }

        return redirectCourses + "/{id}";
    }

    public boolean addStudentToCourse(Account student, Course course) {
        try {
            if (course.getStudents() == null) {
                course.setStudents(Arrays.asList(student));
            } else {
                course.getStudents().add(student);
            }
            repository.save(course);
            return true;
        } catch (Exception e) {
        }

        return false;
    }

    @Transactional
    public String leaveCourse(RedirectAttributes redirectAttributes, Long id) {
        List<String> messages = new ArrayList<>();
        Course course = repository.findOne(id);

        try {
            Account student = accountService.getAuthenticatedUser();
            course.getStudents().remove(student);
            messages.add("You have left course " + course.getName());
        } catch (Exception e) {
            messages.add("You failed to leave course" + course.getName());
        }

        redirectAttributes.addFlashAttribute("messages", messages);
        return redirectCourses;
    }

    @Transactional
    public String getCategoryDetails(Model model, Long id) {
        Course course = repository.findOne(id);
        CategoryDetailsWrapper wrapper = new CategoryDetailsWrapper();
        wrapper.setCategoryDetailsList((ArrayList<CategoryDetail>) categoryDetailsService.categoriesToCategoryDetails(course.getCourseCategories(), course));

//        model.addAttribute("categoryDetailsList", categoryDetailsService.categoriesToCategoryDetails(course.getCourseCategories(), course));
        model.addAttribute("actionURL", "/courses/" + id + "/details");
        model.addAttribute("course", course);
        model.addAttribute("wrapper", wrapper);

        return "categoryDetails";
    }

    public String setCategoryDetails(RedirectAttributes redirectAttributes, List<CategoryDetail> categoryDetailsList, Long id) {
        String redirectAddress = redirectCourses + "/{id}";
        List<String> messages = new ArrayList<>();
        Course course = repository.findOne(id);
        int detailsSaved = categoryDetailsService.saveCategoryDetailsList(categoryDetailsList);
        messages.add(detailsSaved + " category details saved");

        redirectAttributes.addFlashAttribute("messages", messages);
        redirectAttributes.addFlashAttribute("id", id);

        return redirectAddress;
    }

    public String getCourseCategory(Model model, RedirectAttributes redirectAttributes, Long courseId, Long categoryId) {
        Course course = repository.findOne(courseId);
        Category category = categoryService.getCategoryById(categoryId);

        if (!courseHasCategory(course, category)) {
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

        if (!courseHasCategory(course, category)) {
            return noSuchCategoryInCourse(course, redirectAttr);
        }

        if (!categoryService.categoryHasTask(category, task)) {
            return noSuchTaskInCategory(course, category, redirectAttr);
        }

        model.addAttribute("course", course);
        model.addAttribute("category", category);
        model.addAttribute("task", task);

        return "task";
    }

    public boolean courseHasCategory(Course course, Category category) {
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
        Account loggedUser = accountService.getAuthenticatedUser();

        if (course.getStudents().contains(loggedUser)) {
            List<Object> messagesAndQueryResult = taskService.performQueryToTask(new ArrayList<>(), taskId, query, categoryId, courseId);
            redirectAttr.addFlashAttribute("tables", messagesAndQueryResult.get(1));
            redirectAttr.addFlashAttribute("messages", messagesAndQueryResult.get(0));
        } else {
            redirectAttr.addFlashAttribute("messages", "You have not joined course " + course.getName());
        }

        redirectAttr.addAttribute("taskId", taskId);
        redirectAttr.addAttribute("categoryId", categoryId);
        redirectAttr.addAttribute("courseId", courseId);

        return "redirect:/courses/{courseId}/category/{categoryId}/task/{taskId}";
    }

    public Course getCourseById(Long id) {
        return repository.findOne(id);
    }
}
