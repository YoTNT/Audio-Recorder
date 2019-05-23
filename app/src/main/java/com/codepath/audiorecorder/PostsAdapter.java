package codepath.com.simple_instagram;

import android.content.Context;
import android.support.annotation.NonNull;
//import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

//import com.bumptech.glide.Glide;
import com.codepath.audiorecorder.Post;
import com.codepath.audiorecorder.R;
import com.parse.ParseFile;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder>{

    public static final String TAG = "PostsAdapt";

    private Context context;
    private List<Post> posts;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

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
            // TODO: bind the view elements to the post
            iuHandle.setText(post.getUser().getUsername());
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

            iDuration.setText(post.getDuration());
        }

        // Clean all elements of the recycler
        public void clear() {
            posts.clear();
            notifyDataSetChanged();
        }

        // Add a list of items -- change to type used
        public void addAll(List<Post> list) {
            posts.addAll(list);
            notifyDataSetChanged();
        }
    }
}
