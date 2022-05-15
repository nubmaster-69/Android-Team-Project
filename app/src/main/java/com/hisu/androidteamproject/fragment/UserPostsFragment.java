package com.hisu.androidteamproject.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.hisu.androidteamproject.R;
import com.hisu.androidteamproject.adapter.PostAdapter;
import com.hisu.androidteamproject.entity.Post;
import com.hisu.androidteamproject.entity.User;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class UserPostsFragment extends Fragment {
    private User user;
    private PostAdapter postAdapter;
    private RecyclerView recyclerView;
    private FirebaseFirestore firestore;

    public UserPostsFragment(User user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_posts, container, false);


        initFrmComponents(view);

        postAdapter = new PostAdapter(user);


        loadPostsOfUser();


        recyclerView.setAdapter(postAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    private void initFrmComponents(View view){
        firestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.rcvUserPostsContainer);
    }

    private void loadPostsOfUser(){
        firestore.collection("Users")
                .whereEqualTo("email", user.getEmail()).get()
                .addOnSuccessListener(snaps -> {
                    String userId = snaps.getDocuments().get(0).getId();

                    firestore.collection("Posts")
                            .whereEqualTo("userID", userId).get()
                            .addOnSuccessListener(snapshots -> {
                                List<Post> posts = new ArrayList<>();

                                for (QueryDocumentSnapshot snapshot : snapshots) {
                                    posts.add(
                                            snapshot.toObject(Post.class)
                                    );
                                }

                                postAdapter.setViewMode(PostAdapter.VIEW_ON_PROFILE);
                                postAdapter.setPostList(posts);
                            });
                });
    }
}