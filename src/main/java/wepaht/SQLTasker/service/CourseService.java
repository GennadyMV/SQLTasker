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
import wepaht.SQLTasker.repository.CourseRepository;

@Service
public class CourseService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private CategoryDetailsService categoryDetailsService;

    private final String redirectCourses = "redirect:/courses";

    public String courseListing(Model model) {

        model.addAttribute("courses", courseRepository.findAll());

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
            course = courseRepository.save(course);
            messages.add("New course created");
            
            if (courseRepository.getCourseCategories(course).size() > 0) {
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
        return courseRepository.save(course);
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
        Course course = courseRepository.findOne(courseId);
        List<CategoryDetail> details = categoryDetailsService.getCourseCategoryDetails(course);
        model.addAttribute("course", course);
        model.addAttribute("courseCategories", courseRepository.getCourseCategories(course));        
        if (!details.isEmpty()) model.addAttribute("details", details);
        return "course";
    }

    public String deleteCourse(RedirectAttributes redirectAttributes, Long course) {
        List<String> messages = new ArrayList<>();

        try {
            Course deletingCourse = courseRepository.findOne(course);
            courseRepository.delete(deletingCourse);
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
        Course course = courseRepository.findOne(courseId);

        model.addAttribute("id", courseId);
        model.addAttribute("course", course);
        model.addAttribute("actionURL", "/courses/" + courseId + "/edit");
        model.addAttribute("categories", categoryService.findAllCategories());

        return "courseForm";
    }

    public String editCourse(RedirectAttributes redirectAttributes, Long id, String name, String starts, String expires, String description, List<Long> categoryIds) {
        String redirectAddress = redirectCourses + "/" + id;
        List<String> messages = new ArrayList<>();
        Course course = courseRepository.findOne(id);
        course.setName(name);
        course.setDescription(description);
        course = setDates(course, starts, expires, messages);
        course.setCourseCategories(categoryService.findCategoriesByIds(categoryIds));

        redirectAddress = saveEditCourse(course, messages, redirectAddress, redirectAttributes);

        return redirectAddress;
    }

    private String saveEditCourse(Course course, List<String> messages, String redirectAddress, RedirectAttributes redirectAttributes) {
        try {
            courseRepository.save(course);
            messages.add("Course edited");
        } catch (Exception e) {
            messages.add("Course edit failed");
            redirectAddress = redirectAddress + "/edit";
        }
        redirectAttributes.addFlashAttribute("messages", messages);
        return redirectAddress;
    }

    public List<Course> getCoursesByName(String name) {
        return courseRepository.findByName(name);
    }

    @Transactional
    public void removeCategoryFromCourses(List<Course> courses, Category deleting) {
        courses.stream().forEach((course) -> {
            course.getCourseCategories().remove(deleting);
        });
    }

    @Transactional
    public String joinCourse(RedirectAttributes redirectAttributes, Long id) {
        Course course = courseRepository.findOne(id);
        List<String> messages = new ArrayList<>();
        
        try {
            List<Account> students = course.getStudents();
            Account currentUser = accountService.getAuthenticatedUser();
            if (!students.contains(currentUser)) course.getStudents().add(currentUser);
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
            courseRepository.save(course);
            return true;
        } catch (Exception e) {}
        
        return false;
    }

    @Transactional
    public String leaveCourse(RedirectAttributes redirectAttributes, Long id) {
        List<String> messages = new ArrayList<>();
        Course course = courseRepository.findOne(id);
        
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
        Course course = courseRepository.findOne(id);
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
        Course course = courseRepository.findOne(id);
        int detailsSaved = categoryDetailsService.saveCategoryDetailsList(categoryDetailsList);
        messages.add(detailsSaved + " category details saved");
        
        redirectAttributes.addFlashAttribute("messages", messages);
        redirectAttributes.addFlashAttribute("id", id);
        
        return redirectAddress;
    }
    
    
}
