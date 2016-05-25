package wepaht.SQLTasker.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.repository.CourseRepository;

@Service
public class CourseService {
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private CategoryService categoryService;
    
    private String redirectCourses = "redirect:/courses"; 
    
    public String courseListing(Model model) {
        
        model.addAttribute("courses", courseRepository.findAll());
        
        return "courses";
    }

    public String createCourse(RedirectAttributes redirectAttributes, String name, String starts, String expires, String description, List<Long> categoryIds) {
        List<String> messages = new ArrayList<>();
        Course course = new Course();
        course.setName(name);
        course.setDescription(description);
        course.setCourseCategories(categoryService.findCategoriesByIds(categoryIds));
        course = setDates(course, starts, expires, messages);
        
        saveCourse(course, messages);
        redirectAttributes.addFlashAttribute("messages", messages);
        
        return redirectCourses;
    }
    
    private void saveCourse(Course course, List<String> messages) {
        try {
            courseRepository.save(course);
            messages.add("New course created");
        } catch (Exception e) {
            messages.add("Course creation failed");
        }
    }
    
    private Course setDates(Course course, String starts, String expires, List<String> messages) {
        LocalDate startDate = null;
        LocalDate expireDate = null;
        
        try {
            startDate = LocalDate.parse(starts);
            course.setStarts(startDate);
            messages.add("Start-date added");
        } catch (Exception e) {}
        
        try {
            expireDate = LocalDate.parse(expires);
            
            if (expireDate.isAfter(startDate) || startDate == null) {
                course.setExpires(expireDate);
                messages.add("Expire-date added");
            }
        } catch (Exception e) {}
        
        return course;
    }
}
