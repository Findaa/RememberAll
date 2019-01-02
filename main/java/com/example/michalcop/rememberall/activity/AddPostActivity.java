package com.example.michalcop.rememberall.activity;

import android.app.ActivityOptions;
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
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.michalcop.rememberall.R;
import com.example.michalcop.rememberall.database.Post;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Activity that allows us to add custom elements to room database.
 */
public class AddPostActivity extends AppCompatActivity {
    //Constants for intent names.
    public static final String EXTRA_TITLE = "com.findaa.TITLE";
    public static final String EXTRA_CONTENT = "com.findaa.CONTENT";
    public static final String EXTRA_DATEFOR = "com.findaa.DATEFOR";
    //Inject view model.
    PostViewModel postViewModel;
    //Create button animation.
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.4F);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        postViewModel = ViewModelProviders.of(this).get(PostViewModel.class);

    }

    public void submitPost(View v){
        Intent intent = new Intent(this, MainActivity.class);

        v.startAnimation(buttonClick);
        v.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);

        EditText titleElement   = findViewById(R.id.editTitle);
        EditText contentElement = findViewById(R.id.editContent);
        EditText dateForElement = findViewById(R.id.editDateFor);

        String title   = titleElement.getText().toString();
        String content = contentElement.getText().toString();
        String dateFor = dateForElement.getText().toString();

        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_CONTENT, content);
        intent.putExtra(EXTRA_DATEFOR, dateFor);

        try {
            postViewModel.savePost(new Post(title, content, formatDate(new Date()), dateFor));
        } catch (NullPointerException npe){
            System.out.println("NPE on Post element");
            postViewModel.savePost(new Post("fail", "This post has been failed in assigning", "fail", "fail"));
        }

        finishActivity(R.layout.activity_main);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    private String formatDate(Date date){
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    //Inner view.
    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        public TimePickerFragment(){
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
