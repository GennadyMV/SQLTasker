package wepaht.SQLTasker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import wepaht.SQLTasker.domain.CustomExportToken;
import wepaht.SQLTasker.domain.PointHolder;
import wepaht.SQLTasker.repository.CustomExportTokenRepository;
import wepaht.SQLTasker.service.PointService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("export")
public class RestExportController {

    @Autowired
    PointService pointService;

    @Autowired
    CustomExportTokenRepository tokenRepository;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity hello(@RequestParam String exportToken) {
        if (isValidToken(exportToken)) {
            return new ResponseEntity(HttpStatus.OK);
        }
        
        return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/points", method = RequestMethod.POST)
    public List<PointHolder> getAllPoints(@RequestParam String exportToken) {
        List<PointHolder> points = new ArrayList<>();

        if (isValidToken(exportToken)) {
            points = pointService.exportAllPoints();
        }

        return points;
    }

    @RequestMapping(value = "/points/{username}", method = RequestMethod.POST)
    public PointHolder getPointsByUsername(@PathVariable String username, @RequestParam String exportToken) {
        PointHolder points = new PointHolder(null, null);
        if (isValidToken(exportToken)) {
            points = new PointHolder(username, pointService.getPointsByUsername(username).longValue());
        }

        return points;
    }

    private boolean isValidToken(String token) {
        CustomExportToken foundToken = tokenRepository.findByToken(token);

        if (foundToken != null && !foundToken.getUser().getRole().equals("STUDENT")) {
            return true;
        }
        return false;
    }
}
