package wrapper;

import java.util.ArrayList;
import wepaht.SQLTasker.domain.CategoryDetail;

public class CategoryDetailsWrapper {
    
    private ArrayList<CategoryDetail> categoryDetailsList;

    public ArrayList<CategoryDetail> getCategoryDetailsList() {
        return categoryDetailsList;
    }

    public void setCategoryDetailsList(ArrayList<CategoryDetail> categoryDetailses) {
        this.categoryDetailsList = categoryDetailses;
    }
    
    
}
