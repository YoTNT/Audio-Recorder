package com.codepath.audiorecorder;

//Import the require libraries
/**********************************/
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.parse.ParseFile;
import java.util.List;
/**********************************/

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder>{

    public static final String TAG = "PostsAdapter";

    private Context context;
    private List<Post> posts;

    //Constructor
    /*******************************************************/
    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }
    /*******************************************************/

    //Override the methods for superclass
    /*************************************************************************************/
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_post,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post=posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
    /*************************************************************************************/


    // Clean all elements of the RecycleView
    /*********************************/
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }
    /*********************************/

    // Add a list of items to the RecycleView
    /************************************************/
    public void addAll(List<Post> posts) {
        posts.addAll(posts);
        notifyDataSetChanged();
    }
    /************************************************/

    //Set up information that are use to display on the RecycleView
    /*********************************************************************************/
    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView iuHandle;
        private TextView iSize;
        private TextView iDuration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iuHandle = itemView.findViewById(R.id.iuHandle);
            iSize = itemView.findViewById(R.id.iSize);
            iDuration = itemView.findViewById(R.id.iDuration);
        }

        public void bind(Post post){
            ParseFile audio = post.getAudio();

            if(audio != null) {
                String url = audio.getUrl();
                url = url.substring(0, 4) + "s" + url.substring(4, url.length());
                //Glide.with(context).load(url).into(ivImage);
                Log.d(TAG, "Audio found!" + url);
            }
            else{
                Log.d(TAG, "Audio not found!");
            }

            // Set iuHandle, the file name to be createAt
            String fileName = String.valueOf(post.getCreatedAt());
            iuHandle.setText((fileName));

            iSize.setText(post.getFileSize());
            iDuration.setText(post.getDuration());
        }

    }
    /*********************************************************************************/
}
