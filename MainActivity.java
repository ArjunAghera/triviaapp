package com.example.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import data.AnswerListAsyncResponse;
import data.Questionbank;
import model.Questions;
import model.Score;
import util.Prefs;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String HIGHESTSCORE = "heighest_score";
    private Button trueButton;
    private Button falseButton;
    private TextView questionno;
    private TextView questiontext;
    private TextView scoretext;
    private TextView heighestscore;
    private ImageButton prevButton;
    private ImageButton nextButton;
    private Button shareButton;
    private int countofquestion = 0;
    private int correctans = 0;
    List<Questions> questionsList;

    private Score score;
    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        score = new Score();
        prefs = new Prefs(MainActivity.this);

        //find by id
        trueButton = findViewById(R.id.true_button);
        falseButton = findViewById(R.id.false_button);
        prevButton = findViewById(R.id.prev_button);
        nextButton = findViewById(R.id.next_button);
        questiontext = findViewById(R.id.questiontext);
        questionno = findViewById(R.id.questioncount);
        scoretext = findViewById(R.id.scoretext);
        heighestscore = findViewById(R.id.highest_scoretext);
        shareButton = findViewById(R.id.sharebutton);

        //click listeners
        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);
        heighestscore.setText(MessageFormat.format("Heighest Score: {0}",String.valueOf(prefs.getHighscore())));

        //getting questions using Async
        questionsList = new Questionbank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Questions> questionsArrayList) {
                questiontext.setText(questionsArrayList.get(countofquestion).getAnswer());
                questionno.setText( countofquestion +" out of " + questionsArrayList.size());
                Log.d("Main", "processFinished: "+questionsArrayList);
            }
        });

        countofquestion = prefs.getState();
        correctans = prefs.getScoreState();

    }

    //defining button jobs
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.true_button:
                checkanswer(true);
                updatequestion();
                break;
            case R.id.false_button:
                checkanswer(false);
                updatequestion();
                break;
            case R.id.next_button:
                countofquestion = (countofquestion + 1) % questionsList.size();
                updatequestion();
                updatequestioncount();
                break;
            case R.id.prev_button:
                if(countofquestion>0) {
                    countofquestion = (countofquestion - 1) % questionsList.size();
                }
                updatequestion();
                updatequestioncount();
                break;
            case R.id.sharebutton:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,"My current score is: "+ correctans + " And my heighest score is: " + heighestscore.getText());
                intent.putExtra(Intent.EXTRA_SUBJECT,"Hi I am currently playing Trivia.");
                startActivity(intent);
                break;
        }
    }
    //function to check answers
    private void checkanswer(boolean useranswer) {
        boolean correctanswer = questionsList.get(countofquestion).isAnswertrue();
        if(correctanswer == useranswer){
            fadeanimation();
            addscore();
            Toast.makeText(MainActivity.this,"Correct Answer!",Toast.LENGTH_SHORT).show();
        }
        else {
            shakeanimation();
            deductscore();
            Toast.makeText(MainActivity.this,"Wrong Answer!",Toast.LENGTH_SHORT).show();
        }
        scoretext.setText("Score = " + correctans);
    }

    //function to go to next question
    public void updatequestion(){

        String question = questionsList.get(countofquestion).getAnswer();
        questiontext.setText(question);
    }

    //function to go to previous question
    public void updatequestioncount(){
        String questioncount = countofquestion +" out of "+  questionsList.size();
        questionno.setText(questioncount);
    }

    //function to add score
    public void addscore(){
        correctans += 10;
        score.setScore(correctans);
        scoretext.setText(MessageFormat.format("Score = {0}", String.valueOf(score.getScore())));
    }

    //function to deduct score
    public void deductscore(){
        if(correctans>0) {
            correctans -= 10;
        }
        score.setScore(correctans);
        scoretext.setText(MessageFormat.format("Score = {0}", String.valueOf(score.getScore())));
    }

    private void goNext(){
        countofquestion = (countofquestion + 1) % questionsList.size();
        updatequestion();
    }

    @Override
    protected void onPause() {
        prefs.saveHighscore(score.getScore());
        prefs.setState(countofquestion);
        prefs.setScoreState(score.getScore());
        super.onPause();
    }

    //animations
    private void shakeanimation(){
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake_animation);
        final CardView cardView = findViewById(R.id.cardView);
        cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                goNext();
                updatequestioncount();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void fadeanimation(){
        final CardView cardView = findViewById(R.id.cardView);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f,0.0f);

        alphaAnimation.setDuration(400);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                goNext();
                updatequestioncount();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}