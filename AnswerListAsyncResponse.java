package data;

import java.util.ArrayList;

import model.Questions;

public interface AnswerListAsyncResponse {
    void processFinished (ArrayList<Questions> questionsArrayList);
}
