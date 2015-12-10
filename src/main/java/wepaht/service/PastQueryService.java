package wepaht.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wepaht.domain.PastQuery;
import wepaht.repository.PastQueryRepository;

import java.util.Date;
import java.util.List;


@Service
public class PastQueryService {

    @Autowired
    PastQueryRepository pastQueryRepository;

    public void saveNewPastQuery(String username, Long taskId, String query, boolean correctness) {
        PastQuery pastQuery = new PastQuery();
        pastQuery.setPastQuery(query);
        pastQuery.setUsername(username);
        pastQuery.setTaskId(taskId);
        pastQuery.setCorrectness(correctness);
        pastQuery.setDate(new Date());
        pastQueryRepository.save(pastQuery);

    }


    public List returnQuery(String username, Long taskId, String isCorrect) {

        if (taskId != null && !isCorrect.equals("allAnswers") && !username.equals("allUsers")) {
            return pastQueryRepository.findByTaskIdAndCorrectnessAndUsername(taskId, correctnessChecker(isCorrect), username);
        }

        if (taskId != null && !isCorrect.equals("allAnswers")) {
            return pastQueryRepository.findByTaskIdAndCorrectness(taskId, correctnessChecker(isCorrect));
        }

        if (taskId != null && !username.equals("allUsers")) {
            return pastQueryRepository.findByTaskIdAndUsername(taskId, username);
        }

        if (!username.equals("allUsers") && !isCorrect.equals("allAnswers")) {
            return pastQueryRepository.findByCorrectnessAndUsername(correctnessChecker(isCorrect), username);
        }

        if (!isCorrect.equals("allAnswers")) {
            return pastQueryRepository.findByCorrectness(correctnessChecker(isCorrect));
        }

        if (!username.equals("allUsers")) {
            return pastQueryRepository.findByUsername(username);
        }
        if (taskId != null) {
            return pastQueryRepository.findByTaskId(taskId);
        }

        return pastQueryRepository.findAll();
    }


    public List returnQueryOnlyByUsername(String username) {
        return pastQueryRepository.findByUsername(username);
    }
    
    public void deleteAllPastQueries(){
        pastQueryRepository.deleteAll();
    }

    private boolean correctnessChecker(String isCorrect) {
        if (isCorrect.equals("true")) {
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public void changeQueriesesUsernames(String oldUsername, String newUsername) {
        List<PastQuery> pastQueries = pastQueryRepository.findByUsername(oldUsername);

        for (PastQuery query: pastQueries) {
            query.setUsername(newUsername);
        }
    }
}

