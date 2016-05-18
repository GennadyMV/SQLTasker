package wepaht.SQLTasker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import wepaht.SQLTasker.domain.AuthenticationToken;
import wepaht.SQLTasker.domain.PointHolder;
import wepaht.SQLTasker.repository.AuthenticationTokenRepository;
import wepaht.SQLTasker.service.PointService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("export")
public class RestExportController {

    @Autowired
    PointService pointService;

    @Autowired
    AuthenticationTokenRepository tokenRepository;

    @RequestMapping(value = "/points", method = RequestMethod.POST)
    public List<PointHolder> getAllPoints(@RequestParam String exportToken) {
        List<PointHolder> points = new ArrayList<>();

        if (isValidToken(exportToken)) {
            List<List<String>> rows = pointService.getAllPoints().getRows();
            for (List<String> row : rows) {
                PointHolder point = new PointHolder();
                point.setUsername(row.get(0));
                point.setPoints(Integer.valueOf(row.get(1)));

                points.add(point);
            }
        }

        return points;
    }

    @RequestMapping(value = "/points/{username}", method = RequestMethod.POST)
    public PointHolder getPointsByUsername(@PathVariable String username, @RequestParam String exportToken) {
        PointHolder points = new PointHolder();
        if (isValidToken(exportToken)) {
            points.setUsername(username);
            points.setPoints(pointService.getPointsByUsername(username));
        }

        return points;
    }

    private boolean isValidToken(String token) {
        AuthenticationToken foundToken = tokenRepository.findByToken(token);

        if ((token != null || foundToken != null) && !foundToken.getUser().getRole().equals("STUDENT")) {
            return true;
        }
        return false;
    }
}
