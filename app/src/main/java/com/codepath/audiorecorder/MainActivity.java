package com.codepath.audiorecorder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

// Import recording relative libraries
/************************************************/
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
/************************************************/
// Import date relative libraries
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "Main Activity";

    private RecyclerView rvPosts;
    private PostsAdapter adapter;
    private List<Post> mPosts;

    String AudioFilename = null;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    private static final int RequestPermissionCode = 1;
    private static boolean isRecordable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setContentView(R.layout.fragment_posts);

        rvPosts=findViewById(R.id.rvPosts);
        mPosts=new ArrayList<>();
        adapter=new PostsAdapter(this,mPosts);

        rvPosts.setLayoutManager(new LinearLayoutManager(this));
        rvPosts.setAdapter(adapter);

        queryPosts();

        // Create button that are able to switch from record and stop mode
        final Button btnRecord = findViewById(R.id.btnRecord);
        btnRecord.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                // Check if the button is set in record mode
                if(isRecordable){
                    // Check permission
                    if(checkPermission()){
                        // Get the current time and use for filename
                        SimpleDateFormat time_format = new SimpleDateFormat("yy_MM_dd_HH_mm_ss", Locale.US);
                        Date now = new Date();
                        String fileName = time_format.format(now) + ".3gp";

                        AudioFilename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName;
                        SetUpMediaRecorder();
                        try{
                            mediaRecorder.prepare();
                            mediaRecorder.start();

                            Toast.makeText(MainActivity.this,"Start recording!", Toast.LENGTH_SHORT).show();
                        } catch (IllegalStateException e){
                            e.printStackTrace();
                        } catch (IOException e){
                            e.printStackTrace();
                        }

                        // Change button to stop mode
                        btnRecord.setBackgroundResource(R.drawable.stop);
                        isRecordable = false;
                    } else {
                        requestPermission();
                    }
                }

                // Button is in Stop mode and begin to upload
                else{
                    // Stop recording
                    mediaRecorder.stop();
                    // Reset recorder
                    SetUpMediaRecorder();

                    // Uploading
                    ParseUser user = ParseUser.getCurrentUser();
                    if(AudioFilename == null){
                        Log.e(TAG, "Upload Error!");
                        return;
                    }
                    savePost(user);
                    Toast.makeText(MainActivity.this,"Record stop and uploaded!", Toast.LENGTH_SHORT).show();

                    // Change the feature of the button to be record
                    btnRecord.setBackgroundResource(R.drawable.record);
                    isRecordable = true;

                    // Refresh the list in the main screen
                    mPosts.clear();
                    queryPosts();
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    protected void queryPosts() {
        ParseQuery<Post> postQuery = new ParseQuery<Post>(Post.class);
        postQuery.include(Post.KEY_USER);
        postQuery.setLimit(20);
        postQuery.addDescendingOrder(Post.KEY_AUDIO);
        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null){
                    Log.e(TAG, "Error with query");
                    e.printStackTrace();
                    return;
                }
                mPosts.addAll(posts);
                adapter.notifyDataSetChanged();

                for (int i = 0; i < posts.size(); i++){
                    Post post = posts.get(i);
                    Log.d(TAG, "Post: " + ", username: " + post.getUser().getUsername());
                }

            }
        });
    }

    // Recording - Checking permission
    private boolean checkPermission(){
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    // Recording - Requesting permission
    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    // Recording - Set up for the recording
    private void SetUpMediaRecorder(){
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioFilename);
    }

    // Recording - Save the audio
    private void savePost(ParseUser parseUser){
        // Calculating the file size
        File file = new File(AudioFilename);
        int file_size = Integer.parseInt(String.valueOf(file.length()/1024));

        // Calulating the audio duration
        MediaMetadataRetriever mmr=new MediaMetadataRetriever();
        mmr.setDataSource(AudioFilename);
        String duration=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        mmr.release();

        // Upload to the server
        Post post = new Post();
        post.setUser(parseUser);
        post.setAudio(new ParseFile(file));
        post.setFileSize(file_size);
        post.setDuration((duration));

        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    Log.e(TAG, "Upload Issue!");
                    e.printStackTrace();
                    return;
                }
                Log.d(TAG, "Upload Success!");
            }
        });
    }

    // Recordign - Delete audio file from server and the local device
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
        });
    }
}
