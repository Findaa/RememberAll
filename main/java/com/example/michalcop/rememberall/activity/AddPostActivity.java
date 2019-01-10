package com.example.michalcop.rememberall.activity;

import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.michalcop.rememberall.R;
import com.example.michalcop.rememberall.database.Post;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Activity that allows us to add custom elements to room database.
 */
public class AddPostActivity extends AppCompatActivity implements View.OnClickListener {

    //Constants for intent names.
    public static final String EXTRA_TITLE = "com.findaa.TITLE";
    public static final String EXTRA_CONTENT = "com.findaa.CONTENT";
    public static final String EXTRA_DATEFOR = "com.findaa.DATEFOR";

    //Inject view model.
    PostViewModel postViewModel;

    //Create button animation.
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.4F);

    //create variables
    Button pickDate, pickTime;
    int mYear, mMonth, mDay, mHour, mMinute;

    //Required onCreate method called when Activity is created.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        postViewModel = ViewModelProviders.of(this).get(PostViewModel.class);

        pickDate = (Button) findViewById(R.id.pickDate);
        pickTime = (Button) findViewById(R.id.pickTime);
        pickDate.setOnClickListener(this);
        pickTime.setOnClickListener(this);
    }

    //onClick method for different buttons
    @Override
    public void onClick(View v) {
        if (v == pickDate) {                                                //If we click on pickDate button
            final Calendar c = Calendar.getInstance();                      //Create new calendar
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);                            //Define calendar variables
            pickDate.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP); //Button animation
            TextView dateTxt = (TextView) findViewById(R.id.editDateFor);

            //Create dialog window for picking the date.
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, monthOfYear, dayOfMonth) ->
                            dateTxt.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year), //months go from 0 to 11
                    mYear, mMonth, mDay);
            datePickerDialog.show();                                        //Displaying date dialog
        }

        //Same as previous but affecting time
        if (v == pickTime) {
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
            pickTime.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
            TextView timeTxt = (TextView) findViewById(R.id.editTimeFor);
            TimePickerDialog timePickerDialog = new TimePickerDialog
                    (this, (view, hourOfDay, minute) ->
                            timeTxt.setText(hourOfDay + ":" + minute),
                            mHour, mMinute, true); //Last boolean defines whether we want 24/12h time format.

            timePickerDialog.show();

        } else {
            System.out.println("Error onClickListener");    //If something goes wrong we get console info in return
        }
    }

    //Method called with submit button
    public void submitPost(View v) {
        Intent intent = new Intent(this, MainActivity.class);           //Create new intent as after adding post we want to see
                                                                                        //Updated list
        v.startAnimation(buttonClick);
        v.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP); //Define two animations

        EditText titleElement = findViewById(R.id.editTitle);
        EditText contentElement = findViewById(R.id.editContent);
        TextView dateForElement = findViewById(R.id.editDateFor);
        TextView timeForElement = findViewById(R.id.editTimeFor);                   //Assing variables to xml elements

        String title = titleElement.getText().toString();
        String content = contentElement.getText().toString();
        String dateFor = dateForElement.getText().toString();
        String timeFor = timeForElement.getText().toString();                       //Assign java data types to xml variables

        String completeDate = dateFor + " " + timeFor;                              //Merge date with time

        try {
            postViewModel.savePost(new Post(title, content, formatDate(new Date()), completeDate));
        } catch (NullPointerException npe) {
            System.out.println("NPE on Post element");
            postViewModel.savePost(new Post("fail", "This post has been failed in assigning", "fail", "fail"));
        }                                                                           //Try-catch in the only place we can get null

        finishActivity(R.layout.activity_main);                                     //Need to finish main activity so animations work.
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle()); //Start new activity with animation
    }
    private String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
}
