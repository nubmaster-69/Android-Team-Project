package com.hisu.androidteamproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.hisu.androidteamproject.R;
import com.hisu.androidteamproject.entity.Post;
import com.hisu.androidteamproject.entity.User;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;
    private final User user;
    private final FirebaseFirestore fireStore;
    private String currentUserID = "";

    public PostAdapter(User user) {
        this.user = user;
        fireStore = FirebaseFirestore.getInstance();
    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
        notifyDataSetChanged();
    }

    public List<Post> getPostList() {
        return postList;
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

        fireStore.collection("Users")
                .whereEqualTo("email", user.getEmail()).get()
                .addOnSuccessListener(snapshots -> {

                    DocumentSnapshot snapshot = snapshots.getDocuments().get(0);

                    currentUserID = snapshot.getId();
                    List<String> likedPosts = snapshot.toObject(User.class).getLikedPosts();

                    if (likedPosts.contains(post.getId())) {
                        holder.isFavorite = true;
                        holder.postFavorite.setImageResource(R.drawable.ic_favorite_full);
                    } else {
                        holder.isFavorite = false;
                        holder.postFavorite.setImageResource(R.drawable.ic_favorite);
                    }
                });

        fireStore.collection("Users").document(post.getUserID()).get()
                .addOnSuccessListener(
                        snapshot -> holder.setPostData(post, snapshot.toObject(User.class))
                );

        holder.postFavorite.setOnClickListener(view -> {
            holder.toggleReactToPost(post, currentUserID);
        });
    }

    @Override
    public int getItemCount() {
        return postList == null ? 0 : postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtPostReact, txtPostStatus, txtUserName, txtUserAddress;
        private final ImageView postImage, postFavorite, postUserImage;
        private boolean isFavorite = false;

        private final FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        private final CollectionReference postCollection = fireStore.collection("Posts");
        private final CollectionReference userCollection = fireStore.collection("Users");

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPostReact = itemView.findViewById(R.id.post_react);
            txtPostStatus = itemView.findViewById(R.id.post_status);
            txtUserName = itemView.findViewById(R.id.user_name);
            txtUserAddress = itemView.findViewById(R.id.user_address);
            postImage = itemView.findViewById(R.id.post_img);
            postFavorite = itemView.findViewById(R.id.img_favorite);
            postUserImage = itemView.findViewById(R.id.post_user_img);
        }

        private void setPostData(Post post, User user) {
            Glide.with(postImage)
                    .load(post.getImageURL()).into(postImage);

            Glide.with(postUserImage)
                    .load(user.getAvatar()).into(postUserImage);

            txtPostStatus.setText(post.getStatus());
            txtPostReact.setText(String.valueOf(post.getPostReact()));
            txtUserName.setText(user.getUsername());
            txtUserAddress.setText(user.getAddress());
        }

        private void toggleReactToPost(Post post, String userID) {
            isFavorite = !isFavorite;

            if (!isFavorite) {
                post.setPostReact(post.getPostReact() - 1);
                userCollection.document(userID)
                        .update("likedPosts", FieldValue.arrayRemove(post.getId()))
                        .addOnSuccessListener(unused1 -> {
                        });
            } else {
                post.setPostReact(post.getPostReact() + 1);
                userCollection.document(userID)
                        .update("likedPosts", FieldValue.arrayUnion(post.getId()))
                        .addOnSuccessListener(unused2 -> {
                        });
            }

            postCollection.whereEqualTo("id", post.getId()).get()
                    .addOnCompleteListener(task -> {
                        for (QueryDocumentSnapshot document : task.getResult())
                            postCollection.document(document.getId()).set(post)
                                    .addOnSuccessListener(unused -> {
                                    });
                    });
        }
    }
}