package wepaht.SQLTasker.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wepaht.SQLTasker.domain.CustomExportToken;
import wepaht.SQLTasker.repository.CustomExportTokenRepository;

@Service
public class RestExportService {
    @Autowired
    CustomExportTokenRepository tokenRepository;
    
    @Autowired
    PointService pointService;
    
    public CustomExportToken getTokenByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public Map<String, List<?>> getCoursePoints(String courseName) {
        return pointService.getCoursePointsByName(courseName);
    }
}
