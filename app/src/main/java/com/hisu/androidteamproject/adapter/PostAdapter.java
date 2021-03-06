package com.hisu.androidteamproject.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
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
import com.hisu.androidteamproject.fragment.ProfileFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;
    private final User user;
    private final FirebaseFirestore fireStore;
    private final FirebaseAuth firebaseAuth;
    private String currentUserID = "";

    public static final int VIEW_ON_NEWFEED = 0;
    public static final int VIEW_ON_PROFILE = 1;
    private int viewMode;

    public static final int VIEW_MY_PROFILE = 0;
    public static final int VIEW_OTHERS_PROFILE = 1;
    private int viewProfileMode;

    private MainActivity mainActivity;
    private Context context;

    public PostAdapter(User user, Context context) {
        this.user = user;
        this.context = context;
        mainActivity = (MainActivity) context;
        fireStore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void setViewMode(int viewMode) {
        this.viewMode = viewMode;
    }

    public void setViewProfileMode(int viewProfileMode) {
        this.viewProfileMode = viewProfileMode;
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

        addActionForAvatarImageInNewFeed(holder.postUserImage, post);

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
                            .addToBackStack("user_profile_edit")
                            .commit();
                }
            });

            holder.btnDeletePost.setVisibility(View.VISIBLE);

            addActionForBtnDeletePost(holder.btnDeletePost, post, position);
        }

        if (viewProfileMode == VIEW_OTHERS_PROFILE){
            holder.btnEditPost.setVisibility(View.INVISIBLE);
            holder.btnDeletePost.setVisibility(View.INVISIBLE);
        }
    }

    private void addActionForAvatarImageInNewFeed(ImageView avatar, Post post){
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fireStore.collection("Posts")
                        .whereEqualTo("id", post.getId()).get()
                        .addOnSuccessListener(snaps -> {
                            String userId = snaps.getDocuments().get(0).get("userID").toString();

                            fireStore.collection("Users")
                                    .document(userId).get()
                                    .addOnSuccessListener(sn -> {
                                        User clickedUser = sn.toObject(User.class);

                                        if (user.getEmail().equals(clickedUser.getEmail())){

                                            mainActivity.getSupportFragmentManager()
                                                    .beginTransaction()
                                                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
                                                    .replace(mainActivity.getFrmContainer().getId(), new ProfileFragment(user, ProfileFragment.VIEW_MY_PROFILE))
                                                    .addToBackStack("user_profile")
                                                    .commit();
                                        }
                                        else{
                                            fireStore.collection("Users")
                                                    .document(userId).get()
                                                    .addOnSuccessListener(snapshot -> {
                                                        User currentUser = snapshot.toObject(User.class);

                                                        mainActivity.getSupportFragmentManager()
                                                                .beginTransaction()
                                                                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
                                                                .replace(mainActivity.getFrmContainer().getId(), new ProfileFragment(currentUser, ProfileFragment.VIEW_OTHERS_PROFILE))
                                                                .addToBackStack("user_profile")
                                                                .commit();
                                                    });
                                        }
                                    });
                        });
            }
        });
    }

    private void addActionForBtnDeletePost(ImageButton btnDeletePost, Post post, int index) {
        btnDeletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("C???nh b??o")
                        .setMessage("B???n ch???c ch???n mu???n xo?? b??i ????ng n??y?!")
                        .setPositiveButton("Xo??", (dialogInterface, i) ->
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
                                                    "???? xo?? b??i ????ng!", Toast.LENGTH_SHORT
                                            ).show();
                                        }))
                        .setNegativeButton("Hu???", null)
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
        private final ImageButton btnDeletePost, btnEditPost;
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

            txtUserAddress.setText(String.format("%s - %s",
                    user.getAddress(), getDateFormat(post.getPostDate()))
            );
        }

        private String getDateFormat(Date date) {
            Duration duration = Duration.between(date.toInstant(), new Date().toInstant());
            long days = Math.abs(duration.toDays());
            long hours = Math.abs(duration.toHours());
            long min = Math.abs(duration.toMinutes());

            String time = "";
            if (days >= 1)
                time = days + " ng??y tr?????c.";
            else if (min < 4)
                time = " V???a xong.";
            else if (min < 60)
                time = min + " ph??t tr?????c.";
            else
                time = hours + " gi??? tr?????c.";

            return time;
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

        private void viewOthersProfile(Post post){

        }

    }
}