package com.hisu.androidteamproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hisu.androidteamproject.entity.Post;
import com.hisu.androidteamproject.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private final List<Post> posts;
    private final Context context;

    public PostAdapter(List<Post> posts, Context context) {
        this.posts = posts;
        this.context = context;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View postView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_post, parent, false);
        return new PostViewHolder(postView);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.PostViewHolder holder, int position) {
        Post post = posts.get(position);

        holder.setPostData(post);

        holder.postFavorite.setOnClickListener(view -> holder.toggleReactToPost());
    }

    @Override
    public int getItemCount() {
        return posts == null ? 0 : posts.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtPostReact, txtPostStatus;
        private final ImageView postImage, postFavorite;
        private boolean isFavorite = false;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPostReact = itemView.findViewById(R.id.post_react);
            txtPostStatus = itemView.findViewById(R.id.post_status);
            postImage = itemView.findViewById(R.id.post_img);
            postFavorite = itemView.findViewById(R.id.img_favorite);
        }

        private void setPostData(Post post) {
            postImage.setImageResource(post.getImageURL());
            txtPostStatus.setText(post.getStatus());
            txtPostReact.setText(String.valueOf(post.getPostReact()));
        }

        private void toggleReactToPost() {
            isFavorite = !isFavorite;
            if (isFavorite)
                postFavorite.setImageResource(R.drawable.ic_favorite_full);
            else
                postFavorite.setImageResource(R.drawable.ic_favorite);
        }
    }
}