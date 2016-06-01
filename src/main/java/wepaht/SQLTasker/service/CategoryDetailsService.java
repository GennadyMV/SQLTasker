package wepaht.SQLTasker.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.CategoryDetails;
import wepaht.SQLTasker.domain.CategoryDetailsList;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.repository.CategoryDetailsRepository;

@Service
public class CategoryDetailsService {
    
    @Autowired
    private CategoryDetailsRepository categoryDetailsRepository;
    
    public List<CategoryDetails> getCourseCategoryDetails(Course course) {
        return categoryDetailsRepository.findByCourse(course);
    }
    
    public int saveCategoryDetailsList(List<CategoryDetails> categoryDetailsList) {
        int detailsSaved = 0;
        
        for (CategoryDetails details : categoryDetailsList) {
            if (saveCategoryDetails(details)) {
                detailsSaved ++;
            }
        }
        
        return detailsSaved;
    }

    private boolean saveCategoryDetails(CategoryDetails details) {
        
        if (isValidDates(details)) {
            categoryDetailsRepository.save(details);
            return true;
        }
        
        return false;
    }

    private boolean isValidDates(CategoryDetails details) {
        LocalDate starts = details.getStarts();
        LocalDate expires = details.getExpires();
        
        if (starts != null && expires != null) {
            return starts.isBefore(expires);
        } 
        
        return (starts != null || expires != null);
    }
    
    public List<CategoryDetails> categoriesToCategoryDetails(List<Category> categories, Course course) {
        List<CategoryDetails> categoryDetailsList = new ArrayList<>();
        
        categories.stream().forEach((category) -> {
            categoryDetailsList.add(new CategoryDetails(course, category, null, null));
        });
        
        return categoryDetailsList;
    }
}
