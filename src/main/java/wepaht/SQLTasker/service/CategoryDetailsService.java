package wepaht.SQLTasker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wepaht.SQLTasker.repository.CategoryDetailsRepository;

@Service
public class CategoryDetailsService {
    
    @Autowired
    private CategoryDetailsRepository categoryDetailsRepository;
}
