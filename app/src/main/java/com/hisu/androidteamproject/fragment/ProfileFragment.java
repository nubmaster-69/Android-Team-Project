package com.hisu.androidteamproject.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hisu.androidteamproject.MainActivity;
import com.hisu.androidteamproject.R;
import com.hisu.androidteamproject.adapter.PostAdapter;
import com.hisu.androidteamproject.entity.User;

import java.util.Date;

public class ProfileFragment extends Fragment {

    private ImageView profileImage, btnEditAva;
    private TextView profileUsername, profileAddress, profileEmail, profileGender;
    private Button btnLogout, btnEditProfile, btnNewPost;
    private MainActivity containerActivity;
    private Uri newAvatarUri;
    private MainActivity mainActivity;
    private FrameLayout viewContainer;
    private ConstraintLayout constraintLayout;

    private FirebaseFirestore fireStore;
    private StorageReference storage;

    public static final int VIEW_MY_PROFILE = 0;
    public static final int VIEW_OTHERS_PROFILE = 1;
    private int viewProfileMode;

    public ProfileFragment(User user, int viewProfileMode) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(AddPostFragment.USER_POST, user);
        setArguments(bundle);

        this.viewProfileMode = viewProfileMode;
    }

    public void setViewProfileMode(int viewProfileMode) {
        this.viewProfileMode = viewProfileMode;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstancesState) {

        mainActivity = (MainActivity) getActivity();

        View profileView = inflater.inflate(
                R.layout.fragment_user_profile, container, false
        );

        initFragmentUI(profileView);

        User user = (User) getArguments().getSerializable(AddPostFragment.USER_POST);

        initFragmentData(user);

        if(isConnectionAvailable()) {
            addActionWhenNetworkAvailable(user);
        } else {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(viewContainer.getId(), new OfflineProfileFragment()).commit();
        }

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

        viewContainer = profileView.findViewById(R.id.frlUserPostsContainer);

        constraintLayout = profileView.findViewById(R.id.constraintLayout4);

        fireStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();

        if (viewProfileMode == VIEW_OTHERS_PROFILE){
            btnEditAva.setVisibility(View.INVISIBLE);
            btnLogout.setVisibility(View.INVISIBLE);
            btnEditProfile.setVisibility(View.INVISIBLE);
            btnNewPost.setVisibility(View.INVISIBLE);
            constraintLayout.setVisibility(View.GONE);
        }
    }

    private void initFragmentData(User user) {
        Glide.with(profileImage).load(user.getAvatar()).into(profileImage);
        profileUsername.setText(user.getUsername());
        profileAddress.setText(user.getAddress());
        profileEmail.setText(user.getEmail());
        profileGender.setText(user.getGender());
    }

    private void addActionWhenNetworkAvailable(User user) {
        addActionForChangeAvatarButton(user);
        addActionForButtonLogOut();

        btnEditProfile.setOnClickListener(view -> switchToEditProfileScreen(user));
        btnNewPost.setOnClickListener(view -> switchToAddPostScreen(user));

        if (viewProfileMode == PostAdapter.VIEW_MY_PROFILE){
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(viewContainer.getId(), new UserPostsFragment(user, VIEW_MY_PROFILE))
                    .commit();
        }
        else if (viewProfileMode == PostAdapter.VIEW_OTHERS_PROFILE){
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(viewContainer.getId(), new UserPostsFragment(user, VIEW_OTHERS_PROFILE))
                    .commit();
        }


    }

    private void addActionForChangeAvatarButton(User user) {
        ActivityResultLauncher resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK) {
                        newAvatarUri = result.getData().getData();
                        new AlertDialog.Builder(getContext())
                                .setIcon(R.drawable.ic_alert)
                                .setTitle("C???p nh???t ???nh ?????i di???n")
                                .setMessage("B???n c?? ch???c mu???n thay ?????i ???nh ?????i di???n ch????!")
                                .setPositiveButton("OK", (dialogInterface, i) -> updateProfile(user))
                                .setNegativeButton("Hu???", null)
                                .show();
                    }
                });

        btnEditAva.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            resultLauncher.launch(intent);
        });
    }

    private void addActionForButtonLogOut() {
        btnLogout.setOnClickListener(view -> {
            new AlertDialog.Builder(getContext())
                    .setIcon(R.drawable.ic_alert)
                    .setTitle("????ng xu???t")
                    .setMessage("B???n c?? th???c s??? mu???n ????ng xu???t kh??ng?!")
                    .setPositiveButton("OK", (dialogInterface, i) -> mainActivity.logOut())
                    .setNegativeButton("Hu???", null)
                    .show();
        });
    }

    private void switchToAddPostScreen(User user) {
        containerActivity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
                .replace(containerActivity.getFrmContainer().getId(), new AddPostFragment(user))
                .addToBackStack("add_post")
                .commit();
    }

    private void switchToEditProfileScreen(User user) {
        containerActivity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
                .replace(containerActivity.getFrmContainer().getId(), new EditProfileFragment(user))
                .addToBackStack("edit_profile")
                .commit();
    }

    private void updateProfile(User user) {
        ProgressDialog dia = new ProgressDialog(getContext());
        dia.setMessage("?????i t?? nh?? \\(^3^)/");
        dia.show();

        StorageReference imageRef = storage.child(String.valueOf(new Date().getTime()));
        imageRef.putFile(newAvatarUri).addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage()
                .getDownloadUrl().addOnCompleteListener(task -> {
                    fireStore.collection("Users").whereEqualTo("email", user.getEmail()).get()
                            .addOnSuccessListener(snapshots -> {

                                String userID = snapshots.getDocuments().get(0).getId();
                                user.setAvatar(task.getResult().toString());

                                fireStore.collection("Users").document(userID).set(user)
                                        .addOnSuccessListener(unused -> {
                                            dia.dismiss();
                                            profileImage.setImageURI(newAvatarUri);
                                        });
                            });
                }));
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