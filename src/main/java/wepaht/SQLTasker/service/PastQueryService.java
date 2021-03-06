package wepaht.SQLTasker.service;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wepaht.SQLTasker.domain.PastQuery;
import wepaht.SQLTasker.repository.CategoryRepository;
import wepaht.SQLTasker.repository.PastQueryRepository;

import java.util.Date;
import java.util.List;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.repository.TaskRepository;


@Service
public class PastQueryService {

    @Autowired
    private PastQueryRepository pastQueryRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private TaskRepository taskRepository;

    public void saveNewPastQuery(String username, Task task, String query, boolean correctness, Long categoryId) {
        PastQuery pastQuery = new PastQuery();
        pastQuery.setQuery(query);
        pastQuery.setUsername(username);
        pastQuery.setTask(task);
        pastQuery.setCorrect(correctness);
        pastQuery.setDate(new Date());
        pastQuery.setAwarded(true);
        pastQueryRepository.save(pastQuery);

    }

    /**
     * Return all queries by definition made by teacher.
     * @param username allUsers if all users is wanted. Else username of wanted user.
     * @param taskId null if all tasks are wanted. Else long id of wanted task.
     * @param isCorrect allAnswers if correct and incorrect answers is wanted. Else true or false.
     * @return wanted list of queries.
     */
    public List returnQuery(String username, Long taskId, String isCorrect) {
        
        Task task = null;
        
        if (taskId != null) {
            task = taskRepository.findOne(taskId);
        }

        if (taskId != null && !isCorrect.equals("allAnswers") && !username.equals("allUsers")) {
            return pastQueryRepository.findByTaskAndCorrectAndUsername(task, correctnessChecker(isCorrect), username);
        }

        if (taskId != null && !isCorrect.equals("allAnswers")) {
            return pastQueryRepository.findByTaskAndCorrect(task, correctnessChecker(isCorrect));
        }

        if (taskId != null && !username.equals("allUsers")) {
            return pastQueryRepository.findByTaskAndUsername(task, username);
        }

        if (!username.equals("allUsers") && !isCorrect.equals("allAnswers")) {
            return pastQueryRepository.findByCorrectAndUsername(correctnessChecker(isCorrect), username);
        }

        if (!isCorrect.equals("allAnswers")) {
            return pastQueryRepository.findByCorrect(correctnessChecker(isCorrect));
        }

        if (!username.equals("allUsers")) {
            return pastQueryRepository.findByUsername(username);
        }
        if (taskId != null) {
            return pastQueryRepository.findByTask(task);
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
     * @param task
     * @param query
     * @param correctness
     */
    public void saveNewPastQueryForTests(String username, Task task, String query, boolean correctness) {
        PastQuery pastQuery = new PastQuery();
        pastQuery.setQuery(query);
        pastQuery.setUsername(username);
        pastQuery.setTask(task);
        pastQuery.setCorrect(correctness);
        pastQuery.setDate(new Date());

        if(correctness){
            pastQuery.setAwarded(true);
        }else{
            pastQuery.setAwarded(false);
        }
        pastQueryRepository.save(pastQuery);
    }
    
    public List getPoints() {
        return pastQueryRepository.getPoints();
    }
    
    public List getExercisesAndAwardedByUsername(String username) {
        return pastQueryRepository.getExercisesAndAwardedByUsername(username);
    }
}

