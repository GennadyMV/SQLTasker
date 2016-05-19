package wepaht.SQLTasker.service;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.PastQuery;
import wepaht.SQLTasker.repository.CategoryRepository;
import wepaht.SQLTasker.repository.PastQueryRepository;

import java.util.Date;
import java.util.List;


@Service
public class PastQueryService {

    @Autowired
    PastQueryRepository pastQueryRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public void saveNewPastQuery(String username, Long taskId, String query, boolean correctness, Long categoryId) {
        PastQuery pastQuery = new PastQuery();
        pastQuery.setPastQuery(query);
        pastQuery.setUsername(username);
        pastQuery.setTaskId(taskId);
        pastQuery.setCorrectness(correctness);
        pastQuery.setDate(new Date());
        pastQuery.setCanGetPoint(compareExpirationDate(categoryRepository.findOne(categoryId).getExpiredDate()));
        pastQueryRepository.save(pastQuery);

    }

    private boolean compareExpirationDate(LocalDate expiredDate) {
        return expiredDate.isAfter(LocalDate.now());
    }

    /**
     * Return all queries by definition made by teacher.
     * @param username allUsers if all users is wanted. Else username of wanted user.
     * @param taskId null if all tasks are wanted. Else long id of wanted task.
     * @param isCorrect allAnswers if correct and incorrect answers is wanted. Else true or false.
     * @return wanted list of queries.
     */
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

    /**
     * Check if wanted query needs to be correct or not.
     * @param isCorrect
     * @return
     */
    private boolean correctnessChecker(String isCorrect) {
        if (isCorrect.equals("true")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Keeps track if user chance his or hers username.
     * @param oldUsername
     * @param newUsername
     */
    @Transactional
    public void changeQueriesesUsernames(String oldUsername, String newUsername) {
        List<PastQuery> pastQueries = pastQueryRepository.findByUsername(oldUsername);

        for (PastQuery query: pastQueries) {
            query.setUsername(newUsername);
        }
    }

    /**
     * Used only in test
     * @param username
     * @param taskId
     * @param query
     * @param correctness
     */
    public void saveNewPastQueryForTests(String username, long taskId, String query, boolean correctness) {
        PastQuery pastQuery = new PastQuery();
        pastQuery.setPastQuery(query);
        pastQuery.setUsername(username);
        pastQuery.setTaskId(taskId);
        pastQuery.setCorrectness(correctness);
        pastQuery.setDate(new Date());

        if(correctness){
            pastQuery.setCanGetPoint(true);
        }else{
            pastQuery.setCanGetPoint(false);
        }
        pastQueryRepository.save(pastQuery);
    }
}

