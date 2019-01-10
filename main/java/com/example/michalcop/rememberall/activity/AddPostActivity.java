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

    @Override
    public void onClick(View v) {
        if (v == pickDate) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            pickDate.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
            TextView dateTxt = (TextView) findViewById(R.id.editDateFor);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, monthOfYear, dayOfMonth) ->
                            dateTxt.setText(
                                    dayOfMonth + "-" + (monthOfYear + 1) + "-" + year), mYear, mMonth, mDay);
            
            datePickerDialog.show();
        }
        
        if (v == pickTime) {
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
            pickTime.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
            TextView timeTxt = (TextView) findViewById(R.id.editTimeFor);
            TimePickerDialog timePickerDialog = new TimePickerDialog
                    (this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    timeTxt.setText(hourOfDay + ":" + minute);
                                }
                            }, mHour, mMinute, false);
            
            timePickerDialog.show();
            
        } else {
            System.out.println("Error onClickListener");
        }
    }

    public void submitPost(View v) {
        Intent intent = new Intent(this, MainActivity.class);

        v.startAnimation(buttonClick);
        v.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);

        EditText titleElement = findViewById(R.id.editTitle);
        EditText contentElement = findViewById(R.id.editContent);
        TextView dateForElement = findViewById(R.id.editDateFor);
        TextView timeForElement = findViewById(R.id.editTimeFor);

        String title = titleElement.getText().toString();
        String content = contentElement.getText().toString();
        String dateFor = dateForElement.getText().toString();
        String timeFor = timeForElement.getText().toString();

        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_CONTENT, content);
        intent.putExtra(EXTRA_DATEFOR, dateFor + " " + timeFor);

        String completeDate = dateFor + " " + timeFor;

        try {
            postViewModel.savePost(new Post(title, content, formatDate(new Date()), completeDate));
        } catch (NullPointerException npe) {
            System.out.println("NPE on Post element");
            postViewModel.savePost(new Post("fail", "This post has been failed in assigning", "fail", "fail"));
        }

        finishActivity(R.layout.activity_main);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    //Inner view.
    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {
        public TimePickerFragment() {
            onAttachFragment(this);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker.
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it.
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user

        }

        public void showTimePickerDialog(View v) {
            DialogFragment newFragment = new TimePickerFragment();
            newFragment.show(getFragmentManager(), "timePicker");
        }
    }
}
