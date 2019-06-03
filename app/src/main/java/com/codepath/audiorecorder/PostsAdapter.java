package com.codepath.audiorecorder;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseFile;

import java.io.IOException;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder>{

    public static final String TAG = "PostsAdapter";

    private Context context;
    private List<Post> posts;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    //Override the methods for superclass
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_post,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post=posts.get(position);
        final String fileName=post.getName();
        Log.d(TAG,"You clicked on "+fileName);
        holder.bind(post);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer mediaPlayer=new MediaPlayer();
                String AudioFilename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+fileName;
                Log.d(TAG,"The Audiofilename is "+AudioFilename);
                try {
                    mediaPlayer.setDataSource(AudioFilename);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();
                Toast.makeText(context,"Audio Playing",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }


    // Clean all elements of the RecycleView
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items to the RecycleView
    public void addAll(List<Post> posts) {
        posts.addAll(posts);
        notifyDataSetChanged();
    }

    //Set up information that are use to display on the RecycleView
    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvName;
        private LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvListenName);
            linearLayout = itemView.findViewById(R.id.linearLayout);
        }

        public void bind(Post post){
            ParseFile audio = post.getAudio();

            if(audio != null) {
                String url = audio.getUrl();
                url = url.substring(0, 4) + "s" + url.substring(4, url.length());
                Log.d(TAG, "Audio found!" + url);
            }
            else{
                Log.d(TAG, "Audio not found!");
            }

            String fileName = post.getName();
            tvName.setText((fileName));
        }
    }
}
