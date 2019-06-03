package com.codepath.audiorecorder.fragments;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.codepath.audiorecorder.Post;
import com.codepath.audiorecorder.PostsAdapter;
import com.codepath.audiorecorder.R;
import com.codepath.audiorecorder.RecordingActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListenFragment extends Fragment {

    public static final String TAG = "Listen Fragment";

    private RecyclerView rvPosts;
    private PostsAdapter adapter;
    private List<Post> mPosts;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_listen,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvPosts=view.findViewById(R.id.rvPosts);
        mPosts=new ArrayList<>();
        adapter=new PostsAdapter(getContext(),mPosts);

        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPosts.setAdapter(adapter);

        swipeRefreshLayout=view.findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRecyclerView(0);
            }
        });

        queryPosts();

    }

    private void refreshRecyclerView(int page) {
        adapter.clear();
        queryPosts();
        swipeRefreshLayout.setRefreshing(false);
    }

    protected void queryPosts() {
        Log.d(TAG,"queryPost called");
        ParseQuery<Post> postQuery = new ParseQuery<Post>(Post.class);
        postQuery.include(Post.KEY_USER);
        //postQuery.setLimit(20);
        //postQuery.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        postQuery.addDescendingOrder(Post.KEY_CREATED_AT);
        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                Log.d(TAG,"Done method is called");
                if (e != null){
                    Log.e(TAG, "Error with query");
                    e.printStackTrace();
                    return;
                }
                mPosts.addAll(posts);
                adapter.notifyDataSetChanged();
                Log.d(TAG,"notifychanged inside done called");

                for (int i = 0; i < posts.size(); i++){
                    Post post = posts.get(i);
                    Log.d(TAG, "Post: " + post.getName()+", username: " + post.getUser().getUsername());
                }

            }
        });
    }

    /* Recordign - Delete audio file from server and the local device
    private void deleteAudio(String fileName){
        File file = new File(fileName);
        ParseFile pFile = new ParseFile(file);

        ParseUser user = ParseUser.getCurrentUser();

        Post post = new Post();
        post.setUser(user);
        post.setAudio(pFile);
        post.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    Log.d(TAG, "Delete Error!");
                    e.printStackTrace();
                    return;
                }
                else{
                    Log.d(TAG, "Delete Success!");
                    Toast.makeText(MainActivity.this, "Audio file deleted!", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
}
