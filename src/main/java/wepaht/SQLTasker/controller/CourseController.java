/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wepaht.SQLTasker.controller;

import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wrapper.CategoryDetailsWrapper;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.domain.TaskFeedback;
import wepaht.SQLTasker.service.CourseService;
import wepaht.SQLTasker.service.TaskFeedbackService;

@Controller
@RequestMapping("courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private TaskFeedbackService feedbackService;

    @RequestMapping(method = RequestMethod.GET)
    public String getCourses(Model model) {
        return courseService.courseListing(model);
    }

    @RequestMapping(value = {"/{id}"}, method = RequestMethod.GET)
    public String getCourse(Model model, RedirectAttributes redirAttr, @PathVariable Long id) {
        return courseService.getCourse(model, redirAttr, id);
    }
    
    @RequestMapping(value = {"/{id}/categories"}, method = RequestMethod.GET)
    public String getCourse(RedirectAttributes redirAttr, @PathVariable Long id) {
        redirAttr.addAttribute("id", id);
        return "redirect:/courses/{id}";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String createCourse(RedirectAttributes redirectAttributes,
            @RequestParam String name,
            @RequestParam(required = false) String starts,
            @RequestParam(required = false) String expires,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) List<Long> categoryIds) {

        return courseService.createCourse(redirectAttributes, name, starts, expires, description, categoryIds);
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String getCreateCourse(Model model, RedirectAttributes redirAttr) {
        return courseService.courseCreateForm(model, redirAttr);
    }

    @RequestMapping(value = "/{id}/delete", method = RequestMethod.DELETE)
    public String deleteCourse(RedirectAttributes redirectAttributes, @PathVariable Long id) {
        return courseService.deleteCourse(redirectAttributes, id);
    }

    @RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
    public String getEditCourse(Model model, RedirectAttributes redirAttr, @PathVariable Long id) {
        return courseService.editForm(model, redirAttr, id);
    }

    @RequestMapping(value = "/{id}/edit", method = RequestMethod.POST)
    public String editCourse(RedirectAttributes redirectAttributes,
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam(required = false) String starts,
            @RequestParam(required = false) String expires,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) List<Long> categoryIds) {

        return courseService.editCourse(redirectAttributes, id, name, starts, expires, description, categoryIds);
    }

    @RequestMapping(value = "/{id}/join", method = RequestMethod.POST)
    public String joinCourse(RedirectAttributes redirectAttributes,
            @PathVariable Long id) {
        return courseService.joinOrLeaveCourse(redirectAttributes, id);
    }

    @RequestMapping(value = "/{id}/details", method = RequestMethod.GET)
    public String getCategoryDetails(Model model, RedirectAttributes redirAttr, @PathVariable Long id, @ModelAttribute("wrapper") CategoryDetailsWrapper wrapper) {
        return courseService.getCategoryDetails(model, redirAttr, id);
    }

    @RequestMapping(value = "/{id}/details", method = RequestMethod.POST)
    public String postCategoryDetails(RedirectAttributes redirectAttributes, @PathVariable Long id, @ModelAttribute("wrapper") CategoryDetailsWrapper wrapper) {
        return courseService.setCategoryDetails(redirectAttributes, wrapper.getCategoryDetailsList(), id);
    }

    @Transactional
    @RequestMapping(value = {"/{courseId}/categories/{categoryId}"}, method = RequestMethod.GET)
    public String getCourseCategory(Model model, RedirectAttributes redirectAttributes, @PathVariable Long courseId, @PathVariable Long categoryId) {
        return courseService.getCourseCategory(model, redirectAttributes, courseId, categoryId);
    }
    
    @RequestMapping(value = {"/{courseId}/categories/{categoryId}/tasks"}, method = RequestMethod.GET)
    public String getCourseCategory(RedirectAttributes redirectAttributes, @PathVariable Long courseId, @PathVariable Long categoryId) {
        redirectAttributes.addAttribute("courseId", courseId);
        redirectAttributes.addAttribute("categoryId", categoryId);
        return "redirect:/courses/{courseId}/categories/{categoryId}";
    }

    @Transactional
    @RequestMapping(value = "/{courseId}/categories/{categoryId}/tasks/{taskId}", method = RequestMethod.GET)
    public String getCourseCategoryTask(
            Model model,
            RedirectAttributes redirectAttr,
            @PathVariable Long courseId,
            @PathVariable Long categoryId,
            @PathVariable Long taskId) {
        return courseService.getCourseCategoryTask(model, redirectAttr, courseId, categoryId, taskId);
    }

    @RequestMapping(value = "/{courseId}/categories/{categoryId}/tasks/{taskId}/next", method = RequestMethod.GET)
    public String getCourseCategoryNextTask(
            RedirectAttributes redirectAttr,
            @PathVariable Long courseId,
            @PathVariable Long categoryId,
            @PathVariable Long taskId) {
        return courseService.getCourseCategoryNextTask(redirectAttr, courseId, categoryId, taskId);
    }

    @RequestMapping(value = "/{courseId}/categories/{categoryId}/tasks/{taskId}/prev", method = RequestMethod.GET)
    public String getCourseCategoryPreviousTask(
            RedirectAttributes redirectAttr,
            @PathVariable Long courseId,
            @PathVariable Long categoryId,
            @PathVariable Long taskId) {
        return courseService.getCourseCategoryPreviousTask(redirectAttr, courseId, categoryId, taskId);
    }

    @RequestMapping(value = "/{courseId}/categories/{categoryId}/tasks/{taskId}", method = RequestMethod.DELETE)
    public String deleteCourseCategoryTask(RedirectAttributes redirAttr, @PathVariable Long courseId, @PathVariable Long categoryId, @PathVariable Long taskId) {
        return courseService.deleteCourseCategoryTask(redirAttr, courseId, categoryId, taskId);
    }

    @RequestMapping(value = "/{courseId}/categories/{categoryId}/tasks/{taskId}/query", method = RequestMethod.POST)
    public String postQuery(RedirectAttributes redirectAttr,
            @RequestParam String query,
            @PathVariable Long courseId,
            @PathVariable Long categoryId,
            @PathVariable Long taskId) {

        return courseService.createQuery(redirectAttr, query, courseId, categoryId, taskId);
    }

    @RequestMapping(value = "/{courseId}/categories/{categoryId}/tasks/{taskId}/feedback", method = RequestMethod.GET)
    public String getTaskFeedback(Model model,
            @PathVariable Long courseId,
            @PathVariable Long categoryId,
            @PathVariable Long taskId,
            @ModelAttribute("taskFeedback") TaskFeedback taskFeedback) {
        return feedbackService.getFeedbackForm(model, courseId, categoryId, taskId);
    }

    @RequestMapping(value = "/{courseId}/categories/{categoryId}/tasks/{taskId}/feedback", method = RequestMethod.POST)
    public String postTaskFeedback(RedirectAttributes redirAttr, @PathVariable Long courseId,
            @PathVariable Long categoryId,
            @PathVariable Long taskId,
            @ModelAttribute TaskFeedback taskFeedback) {
        return feedbackService.createFeedback(redirAttr, courseId, categoryId, taskId, taskFeedback);
    }

    @RequestMapping(value = "/{courseId}/categories/{categoryId}/tasks/{taskId}/edit", method = RequestMethod.GET)
    public String getCourseCategoryEditTaskForm(Model model, RedirectAttributes redirAttr, @PathVariable Long categoryId, @PathVariable Long taskId, @PathVariable Long courseId) {
        return courseService.getCategoryEditTaskForm(model, redirAttr, courseId, categoryId, taskId);
    }

    @RequestMapping(value = "/{courseId}/categories/{categoryId}/tasks/{taskId}/edit", method = RequestMethod.POST)
    public String editCourseCategoryTask(RedirectAttributes redirAttr, @PathVariable Long categoryId, @PathVariable Long taskId, @PathVariable Long courseId,
            @RequestParam Long databaseId,
            @RequestParam String name,
            @RequestParam String solution,
            @RequestParam String description) {
        return courseService.categoryeditTask(redirAttr, courseId, categoryId, taskId, databaseId, name, solution, description);
    }
    
    @RequestMapping(value = "/{courseId}/categories/{categoryId}", method = RequestMethod.DELETE)
    public String deleteCourseCategory(RedirectAttributes redirAttr, @PathVariable Long courseId, @PathVariable Long categoryId) {
        return courseService.deleteCourseCategory(redirAttr, courseId, categoryId);
    }
    
    @RequestMapping(value = "/{courseId}/categories/{categoryId}/edit", method = RequestMethod.GET)
    public String getCourseCategoryEdit(Model model, RedirectAttributes redirAttr, @PathVariable Long courseId, @PathVariable Long categoryId) {
        return courseService.getCourseCategoryEdit(model, redirAttr, courseId, categoryId);
    }
    
    @RequestMapping(value = "/{courseId}/categories/{categoryId}/edit", method = RequestMethod.POST)
    public String editCourseCategory(
            RedirectAttributes redirAttr, 
            @PathVariable Long courseId, 
            @PathVariable Long categoryId, 
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam(required = false) List<Long> taskIds) {
        return courseService.editCourseCategory(redirAttr, courseId, categoryId, name, description, taskIds);
    }
    
    @RequestMapping(value = "/{courseId}/categories/{categoryId}/tasks/create", method = RequestMethod.GET)
    public String getCourseCategoryCreateTask(Model model, RedirectAttributes redirAttr, @PathVariable Long courseId, @PathVariable Long categoryId, @ModelAttribute Task task) {
        return courseService.getCourseCategoryTaskCreateForm(model, redirAttr, courseId, categoryId, task);
    }
    
    @RequestMapping(value = "/{courseId}/categories/{categoryId}/tasks", method = RequestMethod.POST)
    public String createCourseCategoryTask(Model model, RedirectAttributes redirAttr, @PathVariable Long courseId, @PathVariable Long categoryId, @Valid @ModelAttribute Task task, BindingResult result) {
        return courseService.createTaskToCourseCategory(model, redirAttr, courseId, categoryId, task, result);
    }
    
    @RequestMapping(value = "/{courseId}/categories/{categoryId}", method = RequestMethod.POST)
    public String postReorderCourseCategoryTask(RedirectAttributes redirAttr, @PathVariable Long courseId, @PathVariable Long categoryId, @RequestParam Long taskId) {
        return courseService.reorderTasks(redirAttr, courseId, categoryId, taskId);
    }
}
