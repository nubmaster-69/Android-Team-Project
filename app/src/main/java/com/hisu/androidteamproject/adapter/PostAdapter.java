package com.hisu.androidteamproject.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.hisu.androidteamproject.MainActivity;
import com.hisu.androidteamproject.R;
import com.hisu.androidteamproject.entity.Post;
import com.hisu.androidteamproject.entity.User;
import com.hisu.androidteamproject.fragment.AddPostFragment;
import com.hisu.androidteamproject.fragment.RegisterFragment;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;
    private final User user;
    private final FirebaseFirestore fireStore;
    private String currentUserID = "";

    public static final int VIEW_ON_NEWFEED = 0;
    public static final int VIEW_ON_PROFILE = 1;
    private int viewMode;
    private MainActivity mainActivity;
    private Context context;

    public PostAdapter(User user, Context context) {
        this.user = user;
        this.context = context;
        mainActivity = (MainActivity) context;
        fireStore = FirebaseFirestore.getInstance();
    }

    public void setViewMode(int viewMode) {
        this.viewMode = viewMode;
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
            notifyItemChanged(position);
        });

        //Default is invisible
        if (viewMode == VIEW_ON_PROFILE) {
            holder.btnEditPost.setVisibility(View.VISIBLE);

            holder.btnEditPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AddPostFragment postFragment = new AddPostFragment(user);
                    postFragment.setPost(post);
                    postFragment.setPostMode(AddPostFragment.POST_UPDATE_MODE);

                    mainActivity.getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
                            .replace(mainActivity.getFrmContainer().getId(), postFragment)
                            .commit();
                }
            });

            holder.btnDeletePost.setVisibility(View.VISIBLE);

            addActionForBtnDeletePost(holder.btnDeletePost, post, position);
        }
    }

    private void addActionForBtnDeletePost(ImageButton btnDeletePost, Post post, int index) {
        btnDeletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Cảnh báo")
                        .setMessage("Bạn chắc chắn muốn xoá bài đăng này?!")
                        .setPositiveButton("Xoá", (dialogInterface, i) ->
                                fireStore.collection("Posts")
                                        .whereEqualTo("id", post.getId()).get()
                                        .addOnSuccessListener(snaps -> {
                                            String postId = snaps.getDocuments().get(0).getId();

                                            fireStore.collection("Posts")
                                                    .document(postId).delete()
                                                    .addOnSuccessListener(result -> {
                                                        postList.remove(index);
                                                        notifyItemRemoved(index);
                                                    });

                                            Toast.makeText(context,
                                                    "Đã xoá bài đăng!", Toast.LENGTH_SHORT
                                            ).show();
                                        }))
                        .setNegativeButton("Huỷ", null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList == null ? 0 : postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtPostReact, txtPostStatus, txtUserName, txtUserAddress;
        private final ImageView postImage, postFavorite, postUserImage;
        private ImageButton btnDeletePost, btnEditPost;
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
            btnDeletePost = itemView.findViewById(R.id.btnDeletePost);
            btnEditPost = itemView.findViewById(R.id.btnEditPost);
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