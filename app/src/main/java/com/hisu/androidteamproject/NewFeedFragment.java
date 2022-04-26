package com.hisu.androidteamproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NewFeedFragment extends Fragment {

    private RecyclerView postRecyclerView;
    private PostAdapter postAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View newFeedsView = inflater
                .inflate(R.layout.fragment_new_feed, container, false);

        postRecyclerView = newFeedsView.findViewById(R.id.post_recycler_view);

        postAdapter = new PostAdapter(List.of(
                new Post(323, R.drawable.demo_bg, "Trời xanh mây trắng\n" +
                        "Em say nắng chứ không say anh"),
                new Post(232, R.drawable.demo_bg, "Những lời đàm tiếu qua loa linh tinh\n" +
                        "Không thể làm khó được Xúc xích sô dum 100% từ thịt")
        ), getActivity());

        postRecyclerView.setAdapter(postAdapter);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return newFeedsView;
    }
}