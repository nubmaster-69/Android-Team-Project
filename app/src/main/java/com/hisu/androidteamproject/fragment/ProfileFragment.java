package com.hisu.androidteamproject.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hisu.androidteamproject.MainActivity;
import com.hisu.androidteamproject.R;
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

    private FirebaseFirestore fireStore;
    private StorageReference storage;

    public ProfileFragment(User user) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(AddPostFragment.USER_POST, user);
        setArguments(bundle);
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

        fireStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
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

        getChildFragmentManager()
                .beginTransaction()
                .replace(viewContainer.getId(), new UserPostsFragment(user))
                .commit();
    }

    private void addActionForChangeAvatarButton(User user) {
        ActivityResultLauncher resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK) {
                        newAvatarUri = result.getData().getData();
                        new AlertDialog.Builder(getContext())
                                .setTitle("Cập nhật ảnh đại diện")
                                .setMessage("Bạn có chắc muốn thay đổi ảnh đại diện chứ?!")
                                .setPositiveButton("OK", (dialogInterface, i) -> updateProfile(user))
                                .setNegativeButton("Huỷ", null)
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
                    .setTitle("Đăng xuất")
                    .setMessage("Bạn có thực sự muốn thoát không?!")
                    .setPositiveButton("OK", (dialogInterface, i) -> mainActivity.logOut())
                    .setNegativeButton("Huỷ", null)
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
        dia.setMessage("Đợi tý nhé \\(^3^)/");
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