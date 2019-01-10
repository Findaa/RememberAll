package com.example.michalcop.rememberall.activity;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;

import com.example.michalcop.rememberall.R;
import com.example.michalcop.rememberall.database.Post;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Main activity class is what represents the first view we get after opening the app.
 * It extends AppCompatActivity for the framework compatibility and implements
 * OnDeleteButtonClickListener from Post Adapter class which is required for RecyclerView,
 * which is the most commonly used view for database or list elements.
 */
public class MainActivity extends AppCompatActivity implements PostsAdapter.OnDeleteButtonClickListener {
    //Class creates two private objects, adapter and view model, both required to make it work.
    private PostsAdapter postsAdapter;
    private PostViewModel postViewModel;

    //Override AppCompatActivity onCreate method which is called, as it suggests, whenever
    //class, so the activity, is called. That is pretty much constructor in that framework.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);                 //Inject data from parcel using super onCreate method
        setContentView(R.layout.activity_main);             //Indicate which xml view is the one supported here
        Toolbar toolbar = findViewById(R.id.toolbar);       //Create additional toolbar
        setSupportActionBar(toolbar);                       //Set that toolbar to be used within this view.

        postsAdapter = new PostsAdapter(this, this);                               //Adapter is required for RecyclerView. It defines one element.
        postViewModel = ViewModelProviders.of(this).get(PostViewModel.class);              //View model is class that has room query methods.
        postViewModel.getAllPosts().observe(this, posts -> postsAdapter.setData(posts));    //Set observer for data changes.
        //Create and configure RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rvPostsLis);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(postsAdapter);
    }

    //Override menu onCreate method. It inflates the bar with our-defined menu.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);   //Inflate menu element with R.menu.main_menu xml.
        return true;

    }
    //Upper method creates Menu element, here we define inner menu Elements called menu items.
    //Define how elements should behave making method actively a listener.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

            //One of the button adds example element. THAT DESTROYS BUTTON COLORING.
        if (item.getItemId() == R.id.addPost) {
            postViewModel.savePost(new Post("Example", "You should probably click the other +",
                    new SimpleDateFormat("yyyy-MM-dd").format(new Date()), "2019-01-02"));
            return true;

            //Another button starts activity that allows us to create custom element. It opens up another activity and animates it.
        } else if (item.getItemId() == R.id.addMine) {
            Intent intent = new Intent(this, AddPostActivity.class);    //Also creates intent that transfers back to main activity.
            this.finishActivity(R.layout.activity_main); //It is required to restart MainActivity to rebind items to RecyclerView.
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            return true;
        } else if (item.getItemId() == R.id.deleteAll){
                onBackPressed();
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Delete Everything")
                .setMessage("Are you sure you want to wipe this?")
                .setPositiveButton("Yes", (dialog, which) -> postViewModel.deleteAll())
                .setNegativeButton("No", null)
                .show();
    }

    //A simple listener method that is implemented in that activity.
    //It allows adapter to make a delete button work. Two additional animations implemented.
    //bgColorFilter needs to be applied in Adapter too, this one overrides all buttons color.
    @Override
    public void onDeleteButtonClicked(Post post) {
        findViewById(R.id.btnDelete).getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP); //change button bg animation.
        findViewById(R.id.btnDelete).startAnimation(new AlphaAnimation(1F, 0.4F)); //change button alpha animation.
        postViewModel.deletePost(post); //Delete the Post from database using custom-created method.


    }

}
