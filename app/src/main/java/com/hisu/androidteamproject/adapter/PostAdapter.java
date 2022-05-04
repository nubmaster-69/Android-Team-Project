package com.hisu.androidteamproject.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.hisu.androidteamproject.entity.Post;
import com.hisu.androidteamproject.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private final Context context;
    private List<Post> postList;

    public void setPostList(List<Post> postList) {
        this.postList = postList;
        notifyDataSetChanged();
    }

    public PostAdapter(Context context) {
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
        Post post = postList.get(position);
        holder.setPostData(post);
        holder.postFavorite.setOnClickListener(view -> {
            holder.toggleReactToPost(post);
        });
    }

    @Override
    public int getItemCount() {
        return postList == null ? 0 : postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtPostReact, txtPostStatus, txtUserName, txtUserAddress;
        private final ImageView postImage, postFavorite;
        private boolean isFavorite = false;

        private final FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        private final CollectionReference postCollection = fireStore.collection("Posts");

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPostReact = itemView.findViewById(R.id.post_react);
            txtPostStatus = itemView.findViewById(R.id.post_status);
            txtUserName = itemView.findViewById(R.id.user_name);
            txtUserAddress = itemView.findViewById(R.id.user_address);
            postImage = itemView.findViewById(R.id.post_img);
            postFavorite = itemView.findViewById(R.id.img_favorite);
        }

        private void setPostData(Post post) {
            Glide.with(postImage)
                    .load(post.getImageURL()).into(postImage);
            txtPostStatus.setText(post.getStatus());
            txtPostReact.setText(String.valueOf(post.getPostReact()));
        }

        private void toggleReactToPost(Post post) {
            isFavorite = !isFavorite;

            if (isFavorite) {
                post.setPostReact(post.getPostReact() + 1);
                postFavorite.setImageResource(R.drawable.ic_favorite_full);
            } else {
                post.setPostReact(post.getPostReact() - 1);
                postFavorite.setImageResource(R.drawable.ic_favorite);
            }

            postCollection.whereEqualTo("id", post.getId()).get()
                    .addOnCompleteListener(task -> {
                        for (QueryDocumentSnapshot document : task.getResult())
                            postCollection.document(document.getId()).set(post)
                                    .addOnSuccessListener(unused -> txtPostReact.setText(
                                            String.valueOf(post.getPostReact())
                                    ));
                    });
        }
    }
}