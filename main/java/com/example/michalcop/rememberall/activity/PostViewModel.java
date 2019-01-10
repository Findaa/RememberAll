package com.example.michalcop.rememberall.activity;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.michalcop.rememberall.database.Post;
import com.example.michalcop.rememberall.database.PostDao;
import com.example.michalcop.rememberall.database.PostsDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * viewModel is class that makes View data-aware. Here we just implement Room queries in ViewModel language
 */
public class PostViewModel extends AndroidViewModel {

    //Include thread services and data access interface.
    private PostDao postDao;
    private ExecutorService executorService;

    //constructor implementing dao in application.
    public PostViewModel(@NonNull Application application) {
        super(application);
        postDao = PostsDatabase.getInstance(application).postDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    //Using livedata allows to use its lifecycle in real time rendering.
    LiveData<List<Post>> getAllPosts() {
        return postDao.findAll();
    }

    void savePost(Post post) { executorService.execute(() -> postDao.save(post)); }

    void deletePost(Post post) {
        executorService.execute(() -> postDao.delete(post));
    }

    void deleteAll() {executorService.execute(() -> postDao.deleteAll());}
}
