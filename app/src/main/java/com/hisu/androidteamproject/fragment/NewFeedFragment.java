package com.hisu.androidteamproject.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hisu.androidteamproject.R;
import com.hisu.androidteamproject.adapter.PostAdapter;
import com.hisu.androidteamproject.entity.Post;
import com.hisu.androidteamproject.entity.User;

import java.net.URI;
import java.util.List;

public class NewFeedFragment extends Fragment {

    public static final String USER_KEY = "_user";

    private RecyclerView postRecyclerView;
    private PostAdapter postAdapter;
    private ImageView imgUserAvatar;

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
        postRecyclerView = newFeedsView.findViewById(R.id.post_recycler_view);
        imgUserAvatar = newFeedsView.findViewById(R.id.user_profile_avatar);
    }

    private void initFragmentData(User user) {
//        imgUserAvatar.setImageURI(Uri.parse(user.getAvatar()));
        Glide.with(imgUserAvatar)
                .load(user.getAvatar()).into(imgUserAvatar);
        initPostRecyclerView();
    }

    private void initPostRecyclerView() {
        postAdapter = new PostAdapter(getPosts(), getActivity());
        postRecyclerView.setAdapter(postAdapter);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    //    Todo: Update code để load post từ firebase hoặc roomdb
    private List<Post> getPosts() {
        return List.of(
                new Post(323, R.drawable.demo_bg, "Trời xanh mây trắng\n" +
                        "Em say nắng chứ không say anh"),
                new Post(232, R.drawable.demo_bg, "Những lời đàm tiếu qua loa linh tinh\n" +
                        "Không thể làm khó được Xúc xích sô dum 100% từ thịt")
        );
    }
}