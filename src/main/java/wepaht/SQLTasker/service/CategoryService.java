package wepaht.SQLTasker.service;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.repository.CategoryRepository;
import wepaht.SQLTasker.repository.TaskRepository;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TaskRepository taskRepository;

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
        
        if(taskIndex < categoryTasks.size() - 1 && categoryTasks.contains(task)) {
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
        for (Long id : categoryIds) {
            setCategoryToTask(id, task);
        }

    }

}
