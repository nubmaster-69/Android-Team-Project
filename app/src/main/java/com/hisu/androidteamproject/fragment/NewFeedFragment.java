package com.hisu.androidteamproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.hisu.androidteamproject.MainActivity;
import com.hisu.androidteamproject.R;
import com.hisu.androidteamproject.adapter.PostAdapter;
import com.hisu.androidteamproject.entity.Post;
import com.hisu.androidteamproject.entity.User;

import java.util.ArrayList;
import java.util.List;

public class NewFeedFragment extends Fragment {

    public static final String USER_KEY = "_user";

    private RecyclerView postRecyclerView;
    private PostAdapter postAdapter;
    private ImageView imgUserAvatar;

    private FirebaseFirestore fireStore;
    private CollectionReference postCollection;
    private MainActivity containerActivity;

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

        imgUserAvatar.setOnClickListener(view -> switchToProfileScreen());

        return newFeedsView;
    }

    private void initFragmentUI(View newFeedsView) {
        fireStore = FirebaseFirestore.getInstance();
        postRecyclerView = newFeedsView.findViewById(R.id.post_recycler_view);
        imgUserAvatar = newFeedsView.findViewById(R.id.user_profile_avatar);
        containerActivity = (MainActivity) getActivity();
    }

    private void initFragmentData(User user) {
        Glide.with(imgUserAvatar)
                .load(user.getAvatar()).into(imgUserAvatar);
        initPostRecyclerView(user);
    }

    private void initPostRecyclerView(User user) {
        postAdapter = new PostAdapter(getActivity(), user);
        postRecyclerView.setAdapter(postAdapter);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        fireStore.collection("Posts").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Post> postList = new ArrayList<>();
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots)
                        postList.add(snapshot.toObject(Post.class));
                    postAdapter.setPostList(postList);
                });

        fireStore.collection("Posts").addSnapshotListener((value, error) -> {
            List<Post> postList = new ArrayList<>();
            for (DocumentSnapshot document : value.getDocuments())
                postList.add(document.toObject(Post.class));
            postAdapter.setPostList(postList);
        });
    }

    private void switchToProfileScreen() {
        containerActivity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
                .replace(containerActivity.getFrmContainer().getId(), new ProfileFragment())
                .addToBackStack("user_profile")
                .commit();
    }
}