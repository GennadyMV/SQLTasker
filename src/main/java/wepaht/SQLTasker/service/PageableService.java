package wepaht.SQLTasker.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import wepaht.SQLTasker.domain.Submission;

@Service
public class PageableService {
    
    public Integer getPreviousPage(Long page) {
        if (page > 0) {
            return page.intValue() - 1;
        }
        return null;
    }

    public Integer getNextPage(Long page, Page<?> pages) {
        if (page.intValue() < pages.getTotalPages()) {
            return page.intValue() + 1;
        }
        return null;
    }
}
