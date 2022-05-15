package com.hisu.androidteamproject.fragment;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.hisu.androidteamproject.MainActivity;
import com.hisu.androidteamproject.R;
import com.hisu.androidteamproject.adapter.PostAdapter;
import com.hisu.androidteamproject.db.DBInit;
import com.hisu.androidteamproject.entity.Post;
import com.hisu.androidteamproject.entity.User;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class NewFeedFragment extends Fragment {

    public static final String USER_KEY = "_user";

    private RecyclerView postRecyclerView;
    private PostAdapter postAdapter;
    private ImageView imgUserAvatar, logoImage;

    private FirebaseFirestore fireStore;
    private MainActivity containerActivity;

    private DBInit dbInit;

    public NewFeedFragment(User user) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER_KEY, user);
        setArguments(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View newFeedsView = inflater
                .inflate(R.layout.fragment_new_feed, container, false);

        initFragmentUI(newFeedsView);
        User user = (User) getArguments().get(USER_KEY);
        initFragmentData(user);

        imgUserAvatar.setOnClickListener(view -> switchToProfileScreen(user));
        logoImage.setOnClickListener(view -> loadNewFeed(postAdapter));

        return newFeedsView;
    }

    private void initFragmentUI(View newFeedsView) {
        fireStore = FirebaseFirestore.getInstance();
        postRecyclerView = newFeedsView.findViewById(R.id.post_recycler_view);
        imgUserAvatar = newFeedsView.findViewById(R.id.user_profile_avatar);
        logoImage = newFeedsView.findViewById(R.id.img_logo);
        containerActivity = (MainActivity) getActivity();

        initLocalDB();
    }

    private void initFragmentData(User user) {
        Glide.with(imgUserAvatar)
                .load(user.getAvatar()).into(imgUserAvatar);
        initPostRecyclerView(user);
    }

    private void initPostRecyclerView(User user) {
        postAdapter = new PostAdapter(user);
        postRecyclerView.setAdapter(postAdapter);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        loadNewFeed(postAdapter);
    }

    private boolean filterPostDate(Instant postedDate) {
        return Math.abs(Duration.between(Instant.now(), postedDate).toDays()) < 3;
    }

    private void switchToProfileScreen(User user) {
        containerActivity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
                .replace(containerActivity.getFrmContainer().getId(), new ProfileFragment(user))
                .addToBackStack("user_profile")
                .commit();
    }

    private void loadNewFeed(PostAdapter postAdapter) {
        if (isConnectionAvailable()) {

            dbInit.postDao().clearTable();

            fireStore.collection("Posts").get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        List<Post> postList = new ArrayList<>();

                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                            Post post = snapshot.toObject(Post.class);

                            if (filterPostDate(post.getPostDate().toInstant())) {
                                postList.add(post);
                                dbInit.postDao().insertPost(post);
                            }
                        }

                        postAdapter.setPostList(postList);
                        postAdapter.setViewMode(PostAdapter.VIEW_ON_NEWFEED);
                        postRecyclerView.scrollToPosition(0);
                    });

            fireStore.collection("Posts").addSnapshotListener((value, error) -> {
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.MODIFIED) {

                        List<Post> postList = postAdapter.getPostList();
                        Post post = documentChange.getDocument().toObject(Post.class);

                        for (int i = 0; i < postList.size(); i++)
                            if (postList.get(i).getId().equalsIgnoreCase(post.getId())) {
                                postList.set(i, post);
                                postAdapter.notifyItemChanged(i);
                            }
                    }
                }
            });
        } else {
            Toast.makeText(getContext(),
                    "Không có kết nối mạng! Vui lòng kiểm tra lại!", Toast.LENGTH_LONG).show();
            postAdapter.setPostList(dbInit.postDao().getAllPost());
        }
    }

    private void initLocalDB() {
        dbInit = DBInit.getInstance(getContext());
    }

    private boolean isConnectionAvailable() {
        boolean isConnected = true;

        ConnectivityManager connectivityManager = getContext().getSystemService(ConnectivityManager.class);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null) {
            isConnected = false;
        }

        return isConnected;
    }
}