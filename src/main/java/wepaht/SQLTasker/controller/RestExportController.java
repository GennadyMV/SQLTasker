package wepaht.SQLTasker.controller;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import wepaht.SQLTasker.domain.CustomExportToken;
import wepaht.SQLTasker.wrapper.PointHolder;
import wepaht.SQLTasker.repository.CustomExportTokenRepository;
import wepaht.SQLTasker.service.PointService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import wepaht.SQLTasker.service.RestExportService;

@RestController
@RequestMapping("export")
public class RestExportController {

    @Autowired
    PointService pointService;

    @Autowired
    RestExportService restService;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)    
    public ResponseEntity hello(@RequestParam String exportToken) {
        if (isValidToken(exportToken)) {
            return new ResponseEntity(HttpStatus.OK);
        }        
        return new ResponseEntity(HttpStatus.FORBIDDEN);
    }
    
    @ResponseBody
    @RequestMapping(value = "/courses/{courseName}/points", method = RequestMethod.GET)    
    public Map<String, List<?>> getPointsByCourse(@RequestParam String exportToken, @PathVariable String courseName) {
        Map<String, List<?>> response = new HashMap();
        if (!isValidToken(exportToken)) {
            response.put("Unauthorized access", null);
            return response;
        }
        
        response = pointService.getCoursePointsByName(courseName);
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/points", method = RequestMethod.POST)
    public List<PointHolder> getAllPoints(@RequestParam String exportToken) {
        List<PointHolder> points = new ArrayList<>();

        if (isValidToken(exportToken)) {
            points = pointService.exportAllPoints();
        }

        return points;
    }

    @ResponseBody
    @RequestMapping(value = "/points/{username}", method = RequestMethod.POST)
    public PointHolder getPointsByUsername(@PathVariable String username, @RequestParam String exportToken) {
        PointHolder points = new PointHolder(null, null);
        if (isValidToken(exportToken)) {
            points = new PointHolder(username, pointService.getPointsByUsername(username).longValue());
        }

        return points;
    }

    private boolean isValidToken(String token) {
        CustomExportToken foundToken = restService.getTokenByToken(token);

        if (foundToken != null && !foundToken.getUser().getRole().equals("STUDENT")) {
            return true;
        }
        return false;
    }
    
    private Map<String, LocalDateTime> getTimeStamp() {
        Map<String, LocalDateTime> stamp = new HashMap<>();
        stamp.put("timeStamp", LocalDateTime.now());
        return stamp;
    }
}
