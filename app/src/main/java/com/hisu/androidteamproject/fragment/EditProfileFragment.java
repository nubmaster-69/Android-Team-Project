package com.hisu.androidteamproject.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hisu.androidteamproject.R;
import com.hisu.androidteamproject.entity.User;

import java.util.Date;

public class EditProfileFragment extends Fragment {

    public static final String EDIT_USER_KEY = "_user_edit_profile";

    private ImageView newAvatar;
    private EditText edtNewAddress, edtNewEmail;
    private Spinner spNewGender;
    private TextView currentAddress, currentEmail, currentGender;
    private Button btnUpdate;
    private Uri newAvatarUri;

    ProgressDialog dia;

    private FirebaseFirestore fireStore;
    private StorageReference storage;

    public EditProfileFragment(User user) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EDIT_USER_KEY, user);
        setArguments(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstancesState) {

        View editProfileView = inflater.inflate(
                R.layout.fragment_edit_user_profile, container, false
        );

        User user = (User) getArguments().getSerializable(EDIT_USER_KEY);

        initFragmentUI(editProfileView);
        setFragmentData(user);

        ActivityResultLauncher resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK) {
                        newAvatarUri = result.getData().getData();
                        newAvatar.setImageURI(newAvatarUri);
                    }
                });

        newAvatar.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            resultLauncher.launch(intent);
        });

        btnUpdate.setOnClickListener(view -> {
            String newAddress = edtNewAddress.getText().toString().trim();

            if (preValidate(newAddress)) {

                newAddress = TextUtils.isEmpty(newAddress) ? user.getAddress() : newAddress;

                updateProfile(user, newAddress);
            }
        });

        return editProfileView;
    }

    private void initFragmentUI(View editProfileView) {
        newAvatar = editProfileView.findViewById(R.id.user_edit_profile_img);
        edtNewAddress = editProfileView.findViewById(R.id.user_edit_profile_address);
        spNewGender = editProfileView.findViewById(R.id.user_edit_profile_gender);
        btnUpdate = editProfileView.findViewById(R.id.btn_update_profile);
        currentAddress = editProfileView.findViewById(R.id.txt_current_address);
        currentGender = editProfileView.findViewById(R.id.txt_current_gender);

        dia = new ProgressDialog(getContext());

        fireStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
    }

    private void setFragmentData(User user) {
        Glide.with(newAvatar).load(user.getAvatar()).into(newAvatar);
        currentAddress.setText(user.getAddress());
        currentGender.setText(user.getGender());
    }

    private boolean preValidate(String address) {
        return true;
    }

    private void setError(String errorMsg, EditText field) {
        field.setError(errorMsg);
        field.requestFocus();
    }

    private void updateProfile(User user, String newAddress) {

        dia.setMessage("Đợi tý nhé \\(^3^)/");
        dia.show();

        user.setAddress(newAddress);
        user.setGender(spNewGender.getSelectedItem().toString());

        if (newAvatarUri != null) {
            StorageReference imageRef = storage.child(String.valueOf(new Date().getTime()));
            imageRef.putFile(newAvatarUri).addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage()
                    .getDownloadUrl().addOnCompleteListener(task -> {
                        updateUserInfo(user, task.getResult().toString());
                    }));
        } else
            updateUserInfo(user, user.getAvatar());
    }

    private void updateUserInfo(User user, String avatarURI) {
        fireStore.collection("Users").whereEqualTo("email", user.getEmail()).get()
                .addOnSuccessListener(snapshots -> {
                    String userID = snapshots.getDocuments().get(0).getId();
                    user.setAvatar(avatarURI);
                    fireStore.collection("Users").document(userID).set(user)
                            .addOnSuccessListener(unused -> {
                                dia.dismiss();
                                new AlertDialog.Builder(getContext())
                                        .setTitle("Cập nhật thông tin cá nhân")
                                        .setMessage("9(*3*)9 Yay! Cập nhật thành công!")
                                        .setPositiveButton("Xác nhận", (dialogInterface, i) ->
                                                getActivity().getSupportFragmentManager().popBackStack())
                                        .show();
                            });
                });
    }
}