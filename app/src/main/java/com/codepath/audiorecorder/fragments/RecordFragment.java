package com.codepath.audiorecorder.fragments;

import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.audiorecorder.Post;
import com.codepath.audiorecorder.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.parse.Parse.getApplicationContext;

public class RecordFragment extends Fragment {

    public static final String TAG = "Compose Fragment";

    //private EditText etName;
    private Button btnRecord;
    private Button btnSubmit;

    private String AudioFilename = null;
    private String fileName = null;
    private MediaRecorder mediaRecorder;

    private static final int RequestPermissionCode = 1;
    private static boolean isRecordable = true;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_record,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //etName=view.findViewById(R.id.etName);
        btnRecord=view.findViewById(R.id.btnRecord);
        btnSubmit=view.findViewById(R.id.btnSumbit);

        btnRecord.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                // Check if the button is set in record mode
                if(isRecordable){
                    // Check permission
                    if(checkPermission()){
                        // Get the current time and use for filename
                        String pattern="yyyy-MM-dd-HH-mm-ss";
                        SimpleDateFormat time_format = new SimpleDateFormat(pattern);
                        Date now = new Date();
                        fileName = time_format.format(now)+".3gp";
                        //fileName=etName.getText().toString();
                        AudioFilename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                                + fileName;
                        SetUpMediaRecorder();
                        try{
                            mediaRecorder.prepare();
                            mediaRecorder.start();

                            Toast.makeText(getContext(),"Start recording!", Toast.LENGTH_SHORT).show();
                        } catch (IllegalStateException e){
                            e.printStackTrace();
                        } catch (IOException e){
                            e.printStackTrace();
                        }

                        // Change button to stop mode
                        btnRecord.setBackgroundResource(R.drawable.ic_mic_off_black_24dp);
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

                    Toast.makeText(getContext(),"Finish Recording", Toast.LENGTH_SHORT).show();

                    // Change the feature of the button to be record
                    btnRecord.setBackgroundResource(R.drawable.ic_mic_black_24dp);
                    isRecordable = true;
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Uploading
                ParseUser user = ParseUser.getCurrentUser();

                //fileName=etName.getText().toString();
                //AudioFilename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName;

                if(AudioFilename == null){
                    Log.e(TAG, "Filename path Error!");
                    return;
                }
                savePost(user);
                Toast.makeText(getContext(),"Upload Succeed",Toast.LENGTH_SHORT).show();
                /* Refresh the list in the main screen
                mPosts.clear();
                Log.d(TAG,"clear called");
                queryPosts();
                adapter.notifyDataSetChanged();
                Log.d(TAG,"notifychanged outside done called");*/
            }
        });
    }
/*
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
    }*/

    // Recording - Checking permission
    private boolean checkPermission(){
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    // Recording - Requesting permission
    private void requestPermission(){
        ActivityCompat.requestPermissions(getActivity(), new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
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
        Log.d(TAG,"SavePost called， AudioFilename is "+AudioFilename);
        File audioFile = new File(AudioFilename);
        int file_size = Integer.parseInt(String.valueOf(audioFile.length()/1024));

        // Calulating the audio duration
        MediaMetadataRetriever mmr=new MediaMetadataRetriever();
        mmr.setDataSource(AudioFilename);
        String duration=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        mmr.release();

        int ms=Integer.parseInt(duration);
        int total_second=ms/1000;
        int h=total_second/3600;
        int m=total_second/60-h*60;
        int s=total_second-m*60;
        String time=new String();
        if(h!=0){
            time += Integer.toString(h) + ":";
        }
        time += Integer.toString(m) + ":" + Integer.toString(s);


        // Upload to the server
        Post post = new Post();
        post.setUser(parseUser);
        Log.d(TAG,"fileName is "+fileName);
        post.setName(fileName);
        post.setAudio(new ParseFile(audioFile));
        post.setFileSize(file_size);
        post.setDuration((time));

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
    //    mPosts.add(0,post);
    //    adapter.notifyItemInserted(0);
    }
}
