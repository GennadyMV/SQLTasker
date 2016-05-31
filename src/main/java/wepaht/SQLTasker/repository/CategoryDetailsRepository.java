package wepaht.SQLTasker.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import wepaht.SQLTasker.domain.CategoryDetails;

public interface CategoryDetailsRepository extends JpaRepository<CategoryDetails, Long>{
    
}
