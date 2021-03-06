package data;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import controller.AppController;
import model.Questions;

public class Questionbank {
    ArrayList<Questions> questionArrayList = new ArrayList<>();
    private String url = "https://raw.githubusercontent.com/curiousily/simple-quiz/master/script/statements-data.json";

    public List<Questions> getQuestions(final AnswerListAsyncResponse callBack) {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                (JSONArray) null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                Questions question = new Questions();
                                question.setAnswer(response.getJSONArray(i).get(0).toString());
                                question.setAnswertrue(response.getJSONArray(i).getBoolean(1));



                                //Add question objects to list
                                questionArrayList.add(question);
                                //Log.d("Hello", "onResponse: " + question.getAnswer());


                                // Log.d("JSON", "onResponse: " + response.getJSONArray(i).get(0));
                                //Log.d("JSON2", "onResponse: " + response.getJSONArray(i).getBoolean(1));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (null != callBack) callBack.processFinished(questionArrayList);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(jsonArrayRequest);


        return questionArrayList;

    }
}

