package wepaht.SQLTasker.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.CategoryDetail;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.repository.CategoryDetailsRepository;

@Service
public class CategoryDetailsService {
    
    @Autowired
    private CategoryDetailsRepository categoryDetailsRepository;
    
    public List<CategoryDetail> getCourseCategoryDetails(Course course) {
        return categoryDetailsRepository.findByCourseOrderByStartsAscExpiresDesc(course);
    }
    
    @Transactional
    public int saveCategoryDetailsList(List<CategoryDetail> categoryDetailsList) {
        int detailsSaved = 0;
        
        detailsSaved = categoryDetailsList.stream().filter((detail) 
                -> (saveCategoryDetails(detail))).map((_item) -> 1).reduce(detailsSaved, Integer::sum);
        
        return detailsSaved;
    }

    @Transactional
    private boolean saveCategoryDetails(CategoryDetail details) {
        details = findExisting(details);
        
        if (isValidDates(details)) {
            try {
                categoryDetailsRepository.save(details);
                return true;
            } catch (Exception e) {}            
        }
        
        return false;
    }

    private boolean isValidDates(CategoryDetail details) {
        LocalDate starts = details.getStarts();
        LocalDate expires = details.getExpires();
        
        if (starts != null && expires != null) {
            return starts.isBefore(expires);
        } 
        
        return true;
    }
    
    public List<CategoryDetail> categoriesToCategoryDetails(List<Category> categories, Course course) {
        List<CategoryDetail> categoryDetailsList = new ArrayList<>();
        
        categories.stream().forEach((category) -> {
            List<CategoryDetail> detail = categoryDetailsRepository.findByCourseAndCategory(course, category);
            
            categoryDetailsList.add(!detail.isEmpty() ? detail.get(0) : new CategoryDetail(course, category, null, null));            
        });
        
        return categoryDetailsList;
    }
    
    public List<CategoryDetail> allCategoryDetails() {
        return categoryDetailsRepository.findAll();
    }

    private CategoryDetail findExisting(CategoryDetail details) {
        List<CategoryDetail> existingList = categoryDetailsRepository.findByCourseAndCategory(details.getCourse(), details.getCategory());
        
        if(!existingList.isEmpty()) {
            CategoryDetail existing = existingList.get(0);
            existing.setStarts(details.getStarts());
            existing.setExpires(details.getExpires());
            details = existing;
        } 
        
        return details;
    }
}
