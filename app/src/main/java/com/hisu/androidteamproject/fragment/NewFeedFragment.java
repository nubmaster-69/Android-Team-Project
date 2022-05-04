package com.hisu.androidteamproject.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.reflect.TypeToken;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.hisu.androidteamproject.R;
import com.hisu.androidteamproject.adapter.PostAdapter;
import com.hisu.androidteamproject.entity.Post;
import com.hisu.androidteamproject.entity.User;

import java.lang.reflect.Type;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class NewFeedFragment extends Fragment {

    public static final String USER_KEY = "_user";

    private RecyclerView postRecyclerView;
    private PostAdapter postAdapter;
    private ImageView imgUserAvatar;

    private FirebaseFirestore fireStore;

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

        return newFeedsView;
    }

    private void initFragmentUI(View newFeedsView) {
        fireStore = FirebaseFirestore.getInstance();
        postRecyclerView = newFeedsView.findViewById(R.id.post_recycler_view);
        imgUserAvatar = newFeedsView.findViewById(R.id.user_profile_avatar);
    }

    private void initFragmentData(User user) {
        Glide.with(imgUserAvatar)
                .load(user.getAvatar()).into(imgUserAvatar);
        initPostRecyclerView(user.getEmail());
    }

    private void initPostRecyclerView(String email) {
        postAdapter = new PostAdapter(getActivity());
        postRecyclerView.setAdapter(postAdapter);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        fireStore.collection("Posts").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Post> postList = new ArrayList<>();

                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Post post = snapshot.toObject(Post.class);
                        postList.add(post);
                    }

                    postAdapter.setPostList(postList);
                });
    }
}