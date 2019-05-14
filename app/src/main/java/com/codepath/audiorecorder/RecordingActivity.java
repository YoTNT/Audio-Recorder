package com.codepath.audiorecorder;

import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class RecordingActivity extends AppCompatActivity {

    private Button btnRecord;
    private Button btnFinish;
    private Button btnPlay;
    private Button btnStop;
    private Button btnUpload;
    private Button btnDelete;

    String AudioFilename = null;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    private static final int RequestPermissionCode = 1;

    private static final String TAG = "Hello";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        btnRecord=findViewById(R.id.btnRecord);
        btnFinish=findViewById(R.id.btnFinish);
        btnPlay=findViewById(R.id.btnPlay);
        btnStop=findViewById(R.id.btnStop);
        btnUpload=findViewById(R.id.btnUpload);
        btnDelete=findViewById(R.id.btnDelete);

        btnFinish.setEnabled(false);
        btnPlay.setEnabled(false);
        btnStop.setEnabled(false);
        btnUpload.setEnabled(false);
        btnDelete.setEnabled(false);

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermission()){
                    AudioFilename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                            + "AudioRecording.3gp";
                    SetUpMediaRecorder();
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    btnRecord.setEnabled(false);
                    btnFinish.setEnabled(true);

                    Toast.makeText(RecordingActivity.this, "Recording ...",
                            Toast.LENGTH_SHORT).show();
                } else {
                    requestPermission();
                }
            }
        });

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaRecorder.stop();
                btnRecord.setEnabled(true);
                btnFinish.setEnabled(false);
                btnPlay.setEnabled(true);
                btnUpload.setEnabled(true);
                btnDelete.setEnabled(true);

                Toast.makeText(RecordingActivity.this,"Record Completed", Toast.LENGTH_SHORT).show();

                SetUpMediaRecorder();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRecord.setEnabled(false);
                btnPlay.setEnabled(false);
                btnStop.setEnabled(true);
                btnUpload.setEnabled(false);
                btnDelete.setEnabled(false);

                mediaPlayer=new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(AudioFilename);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();
                Toast.makeText(RecordingActivity.this, "Audio Playing",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRecord.setEnabled(true);
                btnPlay.setEnabled(true);
                btnStop.setEnabled(false);
                btnUpload.setEnabled(true);
                btnDelete.setEnabled(true);

                if(mediaPlayer!=null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    SetUpMediaRecorder();
                }
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG,"Button upload was clicked");
                //Get the current user
                ParseUser user=ParseUser.getCurrentUser();

                if(AudioFilename==null){
                    Log.e(TAG,"No audio to upload!");
                    Toast.makeText(RecordingActivity.this,"No audio to upload!",Toast.LENGTH_SHORT).show();;
                    return;
                }
                savePost(user);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deleteFile()){
                    Toast.makeText(RecordingActivity.this, "Audio deleted", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(RecordingActivity.this, "Cannot find specify audio file", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void savePost(ParseUser parseUser){

        Log.d(TAG,"savePost function was called");
        //Calculate the audio file size
        File file=new File(AudioFilename);
        Log.d(TAG,"File path is "+AudioFilename);
        int file_size=Integer.parseInt(String.valueOf(file.length()/1024));

        //Calculate the audio file duration
        MediaMetadataRetriever mmr=new MediaMetadataRetriever();
        mmr.setDataSource(AudioFilename);
        String duration=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        mmr.release();

        Post post=new Post();
        post.setUser(parseUser);
        post.setAudio(new ParseFile(file));
        post.setFileSize(file_size);
        post.setDuration(duration);

        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null){
                    Log.d(TAG,"Error while saving");
                    e.printStackTrace();
                    return;
                }
                Log.d(TAG,"Save Success");
                Toast.makeText(RecordingActivity.this, "Upload Successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean deleteFile() {
        Log.d(TAG,"deleteFile function was called");
        if(AudioFilename==null){
            return false;
        }
        File file=new File(AudioFilename);
        boolean isDelete=file.delete();
        return isDelete;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(RecordingActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    private void SetUpMediaRecorder() {
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioFilename);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

}
