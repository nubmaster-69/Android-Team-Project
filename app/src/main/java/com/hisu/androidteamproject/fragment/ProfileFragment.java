package com.hisu.androidteamproject.fragment;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hisu.androidteamproject.MainActivity;
import com.hisu.androidteamproject.R;
import com.hisu.androidteamproject.entity.User;

public class ProfileFragment extends Fragment {

    private ImageView profileImage;
    private TextView profileUsername, profileAddress, profileEmail, profileGender;
    private RecyclerView userPostRecyclerView;
    private ImageButton btnEditAva;
    private Button btnLogout, btnEditProfile, btnNewPost;
    private MainActivity containerActivity;
    private Uri newAvatarUri;

    public ProfileFragment(User user) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(AddPostFragment.USER_POST, user);
        setArguments(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstancesState) {

        View profileView = inflater.inflate(
                R.layout.fragment_user_profile, container, false
        );

        initFragmentUI(profileView);

        User user = (User) getArguments().getSerializable(AddPostFragment.USER_POST);

        initFragmentData(user);

//        ActivityResultLauncher resultLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if(result.getResultCode() == getActivity().RESULT_OK) {
//                        newAvatarUri = result.getData().getData();
//                    }
//                });

        btnNewPost.setOnClickListener(view -> switchToAddPostScreen(user));

        return profileView;
    }

    private void initFragmentUI(View profileView) {
        containerActivity = (MainActivity) getActivity();

        profileImage = profileView.findViewById(R.id.profile_image);
        profileUsername = profileView.findViewById(R.id.profile_username);
        profileAddress = profileView.findViewById(R.id.txt_profile_address);
        profileEmail = profileView.findViewById(R.id.txt_profile_email);
        profileGender = profileView.findViewById(R.id.txt_profile_gender);
        btnEditAva = profileView.findViewById(R.id.ibtnEditAva);
        btnLogout = profileView.findViewById(R.id.ibtnLogout);
        btnEditProfile = profileView.findViewById(R.id.ibtnEditProfile);
        btnNewPost = profileView.findViewById(R.id.ibtnAdd);
    }

    private void initFragmentData(User user) {
        Glide.with(profileImage).load(user.getAvatar()).into(profileImage);
        profileUsername.setText(user.getUsername());
        profileAddress.setText(user.getAddress());
        profileEmail.setText(user.getEmail());
        profileGender.setText(user.getGender());
    }

    private void switchToAddPostScreen(User user) {
        containerActivity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
                .replace(containerActivity.getFrmContainer().getId(), new AddPostFragment(user))
                .addToBackStack("add_post")
                .commit();
    }
}
