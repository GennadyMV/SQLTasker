package wepaht.SQLTasker.service;

import java.util.ArrayList;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.repository.CategoryRepository;
import wepaht.SQLTasker.repository.TaskRepository;

import java.util.List;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.domain.Submission;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private CourseService courseService;

    @Autowired
    private CategoryDetailService categoryDetailService;
    /**
     * Adds task to categorys' task list.
     *
     * @param categoryId which category
     * @param task which task
     */
    @Transactional
    public void setCategoryToTask(Long categoryId, Task task) {
        Category category = categoryRepository.findOne(categoryId);
        List<Task> taskList = category.getTaskList();
        if (!taskList.contains(task)) {
            taskList.add(task);
            category.setTaskList(taskList);
        }
    }

    @Transactional
    public void setCategoryToTasks(Category category, List<Task> tasks) {
        category.setTaskList(tasks);
    }

    public Task getNextTask(Category category, Task task) {
        List<Task> categoryTasks = category.getTaskList();
        int taskIndex = categoryTasks.indexOf(task);
        int nextTaskIndex = taskIndex + 1;

        if (taskIndex < categoryTasks.size() - 1 && categoryTasks.contains(task)) {
            return categoryTasks.get(nextTaskIndex);
        }

        return null;
    }

    @Transactional
    public void setCategoryTaskFurther(Category category, Task task) {
        List<Task> categoryTasks = category.getTaskList();

        int taskIndex = categoryTasks.indexOf(task);

        if (taskIndex < categoryTasks.size() - 1 && categoryTasks.contains(task)) {
            Task next = categoryTasks.get(taskIndex + 1);
            categoryTasks.set(taskIndex, next);
            categoryTasks.set(taskIndex + 1, task);
        }
    }

    public void removeTaskFromCategory(Category category, Task task) {

        List<Task> taskList = category.getTaskList();
        taskList.remove(task);
        category.setTaskList(taskList);
        categoryRepository.save(category);

        taskRepository.save(task);
    }

    public void setTaskToCategories(Task task, List<Long> categoryIds) {
        categoryIds.stream().forEach((id) -> {
            setCategoryToTask(id, task);
        });

    }

    public List<Category> findCategoriesByIds(List<Long> categoryIds) {
        if (categoryIds != null) {
            ArrayList<Category> categories = new ArrayList<>();

            categoryIds.stream().forEach((categoryId) -> {
                categories.add(categoryRepository.findOne(categoryId));
            });

            return categories;
        }
        return null;
    }
    
    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category deleting = categoryRepository.findOne(id);
        List<Course> courses = deleting.getCourses();
        if (courses != null) {
            if (!courses.isEmpty()) courseService.removeCategoryFromCourses(courses, deleting);
        }
        
        if (deleting.getSubmissions() != null)deleting.getSubmissions().stream().forEach((sub) -> {
            sub.setCategory(null);
        });
        
        try {
            deleting.setDeleted(true);
        } catch (Exception e) {}        
    }

    Category getCategoryById(Long categoryId) {
        return categoryRepository.findOne(categoryId);
    }
    
    public boolean categoryHasTask(Category category, Task task) {
        return category.getTaskList().contains(task);
    }
}
